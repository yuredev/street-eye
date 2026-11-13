package yuredev.street_eye_backend.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.dto.CameraInput;
import yuredev.street_eye_backend.models.dto.CameraResponse;
import yuredev.street_eye_backend.services.CameraService;

@RestController
@RequestMapping("/api/comunidades/{id}/cameras")
@RequiredArgsConstructor
public class CameraController {
    private final CameraService cameraService;

    @GetMapping
    public List<CameraResponse> cameras(@PathVariable String id, @AuthenticationPrincipal Usuario current) {
        return cameraService.cameras(id, current);
    }

    @PostMapping
    public CameraResponse createCamera(@PathVariable String id, @RequestBody CameraInput input,
            @AuthenticationPrincipal Usuario current) {
        return cameraService.createCamera(id, input, current);
    }

    @PutMapping("/{cameraId}")
    public void updateCamera(@PathVariable String id, @PathVariable String cameraId, @RequestBody CameraInput input,
            @AuthenticationPrincipal Usuario current) {
        cameraService.updateCamera(id, cameraId, input, current);
    }

    @DeleteMapping("/{cameraId}")
    public void deleteCamera(@PathVariable String id, @PathVariable String cameraId,
            @AuthenticationPrincipal Usuario current) {
        cameraService.deleteCamera(id, cameraId, current);
    }
}
