package yuredev.street_eye_backend.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.dto.UsuarioInput;
import yuredev.street_eye_backend.models.dto.UsuarioResponse;
import yuredev.street_eye_backend.repositories.UsuarioCadastroRepository;
import yuredev.street_eye_backend.repositories.UsuarioComunidadeRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioCadastroRepository usuarioRepo;
    private final UsuarioComunidadeRepository usuarioComunidadeRepo;
    private final PasswordEncoder passwordEncoder;
    private final ComunidadeService comunidadeService;

    public UsuarioResponse me(Usuario current) {
        return new UsuarioResponse(current.getId(), current.getLogin());
    }

    @Transactional
    public UsuarioResponse updateUser(String id, UsuarioInput input, Usuario current) {
        if (!current.getId().equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        String login = required(input.login(), "Login");
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (usuarioRepo.existsByLoginAndIdNot(login, id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        usuario.setLogin(login);
        if (input.senha() != null && !input.senha().isBlank())
            usuario.setSenha(passwordEncoder.encode(input.senha()));
        return new UsuarioResponse(usuario.getId(), usuario.getLogin());
    }

    @Transactional
    public void deleteUser(String id, Usuario current) {
        if (!current.getId().equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        usuarioRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // A mesma ordem de bloqueio evita deadlocks ao excluir contas de várias comunidades.
        comunidadeService.requireCanDeleteUser(id);
        usuarioComunidadeRepo.deleteByUsuarioId(id);
        usuarioRepo.deleteById(id);
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " obrigatório");
        return value.trim();
    }
}
