package yuredev.street_eye_backend.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import yuredev.street_eye_backend.models.Comunidade;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.UsuarioComunidade;
import yuredev.street_eye_backend.models.dto.ComunidadeInput;
import yuredev.street_eye_backend.models.dto.ComunidadeResponse;
import yuredev.street_eye_backend.models.dto.MembroInput;
import yuredev.street_eye_backend.models.PapelComunidade;
import yuredev.street_eye_backend.models.dto.MembroResponse;
import yuredev.street_eye_backend.models.dto.PapelMembroInput;
import yuredev.street_eye_backend.repositories.CameraRepository;
import yuredev.street_eye_backend.repositories.ComunidadeRepository;
import yuredev.street_eye_backend.repositories.UsuarioCadastroRepository;
import yuredev.street_eye_backend.repositories.UsuarioComunidadeRepository;

@Service
@RequiredArgsConstructor
public class ComunidadeService {
    private final ComunidadeRepository comunidadeRepo;
    private final UsuarioComunidadeRepository usuarioComunidadeRepo;
    private final UsuarioCadastroRepository usuarioRepo;
    private final CameraRepository cameraRepo;

    public void requireMember(String comunidadeId, Usuario current) {
        if (!usuarioComunidadeRepo.existsByComunidade_IdAndUsuario_Id(comunidadeId, current.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem acesso à comunidade");
    }

    @Transactional(readOnly = true)
    public List<ComunidadeResponse> communities(Usuario current) {
        return usuarioComunidadeRepo.findByUsuario_IdOrderByComunidade_Nome(current.getId()).stream()
                .map(m -> new ComunidadeResponse(m.getComunidade().getId(), m.getComunidade().getNome(),
                        m.getComunidade().getDescricao(), m.getPapel()))
                .toList();
    }

    @Transactional
    public ComunidadeResponse createCommunity(ComunidadeInput input, @NonNull Usuario currentUser) {
        String name = required(input.nome(), "Nome");
        Usuario usuario = usuarioRepo.findByIdForUpdate(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Comunidade comunidade = new Comunidade();
        comunidade.setNome(name);
        comunidade.setDescricao(input.descricao());
        comunidadeRepo.save(comunidade);
        UsuarioComunidade membro = new UsuarioComunidade();
        membro.setUsuario(usuario);
        membro.setPapel(PapelComunidade.ADMINISTRADOR);
        membro.setComunidade(comunidade);
        usuarioComunidadeRepo.save(membro);
        return new ComunidadeResponse(comunidade.getId(), name,
                input.descricao() == null ? "" : input.descricao(), PapelComunidade.ADMINISTRADOR);
    }

    @Transactional
    public void updateCommunity(String id, ComunidadeInput input, Usuario current) {
        Comunidade comunidade = lockCommunity(id);
        requireAdministrator(id, current);
        comunidade.setNome(required(input.nome(), "Nome"));
        comunidade.setDescricao(input.descricao());
    }

    @Transactional
    public void deleteCommunity(String id, Usuario current) {
        lockCommunity(id);
        requireAdministrator(id, current);
        cameraRepo.deleteByComunidadeId(id);
        usuarioComunidadeRepo.deleteByComunidadeId(id);
        comunidadeRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MembroResponse> communityUsers(String id, Usuario current) {
        requireMember(id, current);
        return usuarioComunidadeRepo.findByComunidade_IdOrderByUsuario_Login(id).stream()
                .map(m -> new MembroResponse(m.getUsuario().getId(), m.getUsuario().getLogin(), m.getPapel()))
                .toList();
    }

    @Transactional
    public void addUser(@NonNull String id, MembroInput input, Usuario current) {
        requireAdministrator(id, current);
        // Bloquear o usuário antes da comunidade coordena a inclusão com a exclusão da conta.
        Usuario usuario = usuarioRepo.findByLoginForUpdate(required(input.login(), "Login"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        Comunidade comunidade = lockCommunity(id);
        requireAdministrator(id, current);
        if (usuarioComunidadeRepo.existsByComunidade_IdAndUsuario_Id(id, usuario.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já participa da comunidade");
        UsuarioComunidade membro = new UsuarioComunidade();
        membro.setUsuario(usuario);
        membro.setComunidade(comunidade);
        membro.setPapel(PapelComunidade.MEMBRO);
        usuarioComunidadeRepo.save(membro);
    }

    @Transactional
    public void removeUser(String id, String usuarioId, Usuario current) {
        lockCommunity(id);
        requireAdministrator(id, current);
        requireCanRemoveAdministrator(id, usuarioId);
        usuarioComunidadeRepo.deleteMembro(id, usuarioId);
    }

    @Transactional
    public void updateRole(String id, String usuarioId, PapelMembroInput input, Usuario current) {
        lockCommunity(id);
        requireAdministrator(id, current);
        if (input.papel() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Papel obrigatório");
        UsuarioComunidade membro = findMember(id, usuarioId);
        if (input.papel() != PapelComunidade.ADMINISTRADOR)
            requireCanRemoveAdministrator(id, usuarioId);
        membro.setPapel(input.papel());
    }

    public Comunidade lockCommunity(String id) {
        return comunidadeRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comunidade não encontrada"));
    }

    private UsuarioComunidade findMember(String id, String usuarioId) {
        return usuarioComunidadeRepo.findByComunidade_IdAndUsuario_Id(id, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não participa da comunidade"));
    }

    private void requireAdministrator(String id, Usuario current) {
        if (!usuarioComunidadeRepo.existsByComunidade_IdAndUsuario_IdAndPapel(
                id, current.getId(), PapelComunidade.ADMINISTRADOR))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem realizar esta ação");
    }

    // O usuário já deve estar bloqueado para impedir novos vínculos durante a exclusão.
    public void requireCanDeleteUser(String usuarioId) {
        for (String comunidadeId : usuarioComunidadeRepo.findComunidadeIdsByUsuarioId(usuarioId)) {
            if (comunidadeRepo.findByIdForUpdate(comunidadeId).isPresent()
                    && usuarioComunidadeRepo.existsByComunidade_IdAndUsuario_Id(comunidadeId, usuarioId))
                requireCanRemoveAdministrator(comunidadeId, usuarioId);
        }
    }

    // Deve ser chamado depois de bloquear a comunidade na mesma transação.
    public void requireCanRemoveAdministrator(String id, String usuarioId) {
        UsuarioComunidade membro = findMember(id, usuarioId);
        if (membro.getPapel() == PapelComunidade.ADMINISTRADOR
                && usuarioComunidadeRepo.countByComunidade_IdAndPapel(id, PapelComunidade.ADMINISTRADOR) <= 1)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A comunidade precisa de pelo menos um administrador");
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " obrigatório");
        return value.trim();
    }
}
