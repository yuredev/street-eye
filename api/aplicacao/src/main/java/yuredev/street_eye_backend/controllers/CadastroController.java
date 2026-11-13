package yuredev.street_eye_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import yuredev.street_eye_backend.models.Usuario;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CadastroController {
    private final JdbcTemplate db;
    private final PasswordEncoder passwords;

    public record PessoaInput(String login, String senha) {}
    public record ComunidadeInput(String nome, String descricao) {}
    public record MembroInput(String login) {}
    public record CameraInput(String nome, Double latitude, Double longitude) {}

    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " obrigatório");
        return value.trim();
    }

    private void member(String communityId, Usuario user) {
        Integer count = db.queryForObject(
                "SELECT count(*) FROM usuario_comunidade WHERE id_comunidade = ? AND id_usuario = ?",
                Integer.class, communityId, user.getId());
        if (count == null || count == 0) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem acesso à comunidade");
    }

    private void exists(String table, String id) {
        // table is only supplied as a fixed literal by this controller.
        Integer count = db.queryForObject("SELECT count(*) FROM " + table + " WHERE id = ?", Integer.class, id);
        if (count == null || count == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/pessoas/eu")
    public Map<String, Object> me(@AuthenticationPrincipal Usuario current) {
        return Map.of("id", current.getId(), "login", current.getLogin());
    }

    @PostMapping("/pessoas")
    public Map<String, Object> createPerson(@RequestBody PessoaInput input) {
        String login = required(input.login(), "Login");
        String password = required(input.senha(), "Senha");
        if (db.queryForObject("SELECT count(*) FROM usuario WHERE login = ?", Integer.class, login) > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        String id = UUID.randomUUID().toString();
        db.update("INSERT INTO usuario (id, login, senha, papel) VALUES (?, ?, ?, 1)", id, login, passwords.encode(password));
        return Map.of("id", id, "login", login);
    }

    @PutMapping("/pessoas/{id}")
    public Map<String, Object> updatePerson(@PathVariable String id, @RequestBody PessoaInput input,
            @AuthenticationPrincipal Usuario current) {
        if (!current.getId().equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        String login = required(input.login(), "Login");
        exists("usuario", id);
        if (db.queryForObject("SELECT count(*) FROM usuario WHERE login = ? AND id <> ?", Integer.class, login, id) > 0)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        if (input.senha() == null || input.senha().isBlank())
            db.update("UPDATE usuario SET login = ? WHERE id = ?", login, id);
        else
            db.update("UPDATE usuario SET login = ?, senha = ? WHERE id = ?", login, passwords.encode(input.senha()), id);
        return Map.of("id", id, "login", login);
    }

    @DeleteMapping("/pessoas/{id}")
    @Transactional
    public void deletePerson(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        if (!current.getId().equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        db.update("DELETE FROM usuario_comunidade WHERE id_usuario = ?", id);
        db.update("DELETE FROM usuario WHERE id = ?", id);
    }

    @GetMapping("/comunidades")
    public List<Map<String, Object>> communities(@AuthenticationPrincipal Usuario current) {
        return db.queryForList("SELECT c.id, c.nome, c.descricao FROM comunidade c JOIN usuario_comunidade uc "
                + "ON uc.id_comunidade = c.id WHERE uc.id_usuario = ? ORDER BY c.nome", current.getId());
    }

    @PostMapping("/comunidades")
    @Transactional
    public Map<String, Object> createCommunity(@RequestBody ComunidadeInput input, @AuthenticationPrincipal Usuario current) {
        String name = required(input.nome(), "Nome");
        String id = UUID.randomUUID().toString();
        db.update("INSERT INTO comunidade (id, nome, descricao) VALUES (?, ?, ?)", id, name, input.descricao());
        db.update("INSERT INTO usuario_comunidade (id, id_usuario, id_comunidade) VALUES (?, ?, ?)",
                UUID.randomUUID().toString(), current.getId(), id);
        return Map.of("id", id, "nome", name, "descricao", input.descricao() == null ? "" : input.descricao());
    }

    @PutMapping("/comunidades/{id}")
    public void updateCommunity(@PathVariable String id, @RequestBody ComunidadeInput input,
            @AuthenticationPrincipal Usuario current) {
        exists("comunidade", id);
        member(id, current);
        db.update("UPDATE comunidade SET nome = ?, descricao = ? WHERE id = ?", required(input.nome(), "Nome"), input.descricao(), id);
    }

    @DeleteMapping("/comunidades/{id}")
    @Transactional
    public void deleteCommunity(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        exists("comunidade", id);
        member(id, current);
        db.update("DELETE FROM camera WHERE id_comunidade = ?", id);
        db.update("DELETE FROM usuario_comunidade WHERE id_comunidade = ?", id);
        db.update("DELETE FROM comunidade WHERE id = ?", id);
    }

    @GetMapping("/comunidades/{id}/pessoas")
    public List<Map<String, Object>> communityPeople(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        member(id, current);
        return db.queryForList("SELECT u.id, u.login FROM usuario u JOIN usuario_comunidade uc ON uc.id_usuario = u.id "
                + "WHERE uc.id_comunidade = ? ORDER BY u.login", id);
    }

    @PostMapping("/comunidades/{id}/pessoas")
    public void addPerson(@PathVariable String id, @RequestBody MembroInput input, @AuthenticationPrincipal Usuario current) {
        member(id, current);
        List<String> ids = db.queryForList("SELECT id FROM usuario WHERE login = ?", String.class, required(input.login(), "Login"));
        if (ids.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada");
        if (db.queryForObject("SELECT count(*) FROM usuario_comunidade WHERE id_usuario = ? AND id_comunidade = ?",
                Integer.class, ids.get(0), id) > 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já é membro");
        db.update("INSERT INTO usuario_comunidade (id, id_usuario, id_comunidade) VALUES (?, ?, ?)",
                UUID.randomUUID().toString(), ids.get(0), id);
    }

    @DeleteMapping("/comunidades/{id}/pessoas/{personId}")
    public void removePerson(@PathVariable String id, @PathVariable String personId, @AuthenticationPrincipal Usuario current) {
        member(id, current);
        db.update("DELETE FROM usuario_comunidade WHERE id_comunidade = ? AND id_usuario = ?", id, personId);
    }

    @GetMapping("/comunidades/{id}/cameras")
    public List<Map<String, Object>> cameras(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        member(id, current);
        return db.queryForList("SELECT id, nome, latitude, longitude FROM camera WHERE id_comunidade = ? ORDER BY nome", id);
    }

    @PostMapping("/comunidades/{id}/cameras")
    public Map<String, Object> createCamera(@PathVariable String id, @RequestBody CameraInput input,
            @AuthenticationPrincipal Usuario current) {
        member(id, current);
        validateCoordinates(input);
        String name = required(input.nome(), "Nome");
        String cameraId = UUID.randomUUID().toString();
        db.update("INSERT INTO camera (id, id_comunidade, nome, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
                cameraId, id, name, input.latitude(), input.longitude());
        return Map.of("id", cameraId, "nome", name, "latitude", input.latitude(), "longitude", input.longitude());
    }

    @PutMapping("/comunidades/{id}/cameras/{cameraId}")
    public void updateCamera(@PathVariable String id, @PathVariable String cameraId, @RequestBody CameraInput input,
            @AuthenticationPrincipal Usuario current) {
        member(id, current);
        validateCoordinates(input);
        if (db.update("UPDATE camera SET nome = ?, latitude = ?, longitude = ? WHERE id = ? AND id_comunidade = ?",
                required(input.nome(), "Nome"), input.latitude(), input.longitude(), cameraId, id) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/comunidades/{id}/cameras/{cameraId}")
    public void deleteCamera(@PathVariable String id, @PathVariable String cameraId, @AuthenticationPrincipal Usuario current) {
        member(id, current);
        if (db.update("DELETE FROM camera WHERE id = ? AND id_comunidade = ?", cameraId, id) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private void validateCoordinates(CameraInput input) {
        if (input.latitude() == null || input.longitude() == null || !Double.isFinite(input.latitude())
                || !Double.isFinite(input.longitude()) || Math.abs(input.latitude()) > 90 || Math.abs(input.longitude()) > 180)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenadas inválidas");
    }
}
