package yuredev.street_eye_backend.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.dto.ComunidadeInput;
import yuredev.street_eye_backend.models.dto.ComunidadeResponse;
import yuredev.street_eye_backend.models.dto.MembroInput;
import yuredev.street_eye_backend.models.dto.MembroResponse;
import yuredev.street_eye_backend.models.dto.PapelMembroInput;
import yuredev.street_eye_backend.services.ComunidadeService;

@RestController
@RequestMapping("/api/comunidades")
@RequiredArgsConstructor
public class ComunidadeController {
    private final ComunidadeService comunidadeService;

    @GetMapping
    public List<ComunidadeResponse> communities(@AuthenticationPrincipal Usuario current) {
        return comunidadeService.communities(current);
    }

    @PostMapping
    public ComunidadeResponse createCommunity(@RequestBody ComunidadeInput input,
            @AuthenticationPrincipal Usuario current) {
        return comunidadeService.createCommunity(input, current);
    }

    @PutMapping("/{id}")
    public void updateCommunity(@PathVariable String id, @RequestBody ComunidadeInput input,
            @AuthenticationPrincipal Usuario current) {
        comunidadeService.updateCommunity(id, input, current);
    }

    @DeleteMapping("/{id}")
    public void deleteCommunity(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        comunidadeService.deleteCommunity(id, current);
    }

    @GetMapping("/{id}/usuarios")
    public List<MembroResponse> communityUsers(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        return comunidadeService.communityUsers(id, current);
    }

    @PostMapping("/{id}/usuarios")
    public void addUser(@PathVariable String id, @RequestBody MembroInput input,
            @AuthenticationPrincipal Usuario current) {
        comunidadeService.addUser(id, input, current);
    }

    @DeleteMapping("/{id}/usuarios/{usuarioId}")
    public void removeUser(@PathVariable String id, @PathVariable String usuarioId,
            @AuthenticationPrincipal Usuario current) {
        comunidadeService.removeUser(id, usuarioId, current);
    }

    @PatchMapping("/{id}/usuarios/{usuarioId}/papel")
    public void updateRole(@PathVariable String id, @PathVariable String usuarioId,
            @RequestBody PapelMembroInput input, @AuthenticationPrincipal Usuario current) {
        comunidadeService.updateRole(id, usuarioId, input, current);
    }
}
