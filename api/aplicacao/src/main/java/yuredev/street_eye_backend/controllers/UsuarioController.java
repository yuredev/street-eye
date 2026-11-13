package yuredev.street_eye_backend.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.dto.UsuarioInput;
import yuredev.street_eye_backend.models.dto.UsuarioResponse;
import yuredev.street_eye_backend.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/eu")
    public UsuarioResponse me(@AuthenticationPrincipal Usuario current) {
        return usuarioService.me(current);
    }

    @PutMapping("/{id}")
    public UsuarioResponse updateUser(@PathVariable String id, @RequestBody UsuarioInput input,
            @AuthenticationPrincipal Usuario current) {
        return usuarioService.updateUser(id, input, current);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        usuarioService.deleteUser(id, current);
    }
}
