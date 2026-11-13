package yuredev.street_eye_backend.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

import yuredev.street_eye_backend.models.Camera;
import yuredev.street_eye_authentication.models.Usuario;
import yuredev.street_eye_backend.models.dto.CameraInput;
import yuredev.street_eye_backend.models.dto.CameraResponse;
import yuredev.street_eye_backend.repositories.CameraRepository;
import yuredev.street_eye_backend.repositories.ComunidadeRepository;

@Service
@RequiredArgsConstructor
public class CameraService {
    private final CameraRepository cameraRepo;
    private final ComunidadeRepository comunidadeRepo;
    private final ComunidadeService comunidadeService;

    @Transactional(readOnly = true)
    public List<CameraResponse> cameras(String id, Usuario current) {
        comunidadeService.requireMember(id, current);
        return cameraRepo.findByComunidade_IdOrderByNome(id).stream()
                .map(c -> new CameraResponse(c.getId(), c.getNome(), c.getLatitude(), c.getLongitude()))
                .toList();
    }

    @Transactional
    public CameraResponse createCamera(String id, CameraInput input, Usuario current) {
        comunidadeService.requireMember(id, current);
        validateCoordinates(input);
        String name = requiredName(input.nome());
        Camera camera = new Camera();
        camera.setComunidade(comunidadeRepo.getReferenceById(id));
        camera.setNome(name);
        camera.setLatitude(input.latitude());
        camera.setLongitude(input.longitude());
        cameraRepo.save(camera);
        return new CameraResponse(camera.getId(), name, camera.getLatitude(), camera.getLongitude());
    }

    @Transactional
    public void updateCamera(String id, String cameraId, CameraInput input, Usuario current) {
        comunidadeService.requireMember(id, current);
        validateCoordinates(input);
        String name = requiredName(input.nome());
        Camera camera = cameraRepo.findByIdAndComunidade_Id(cameraId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        camera.setNome(name);
        camera.setLatitude(input.latitude());
        camera.setLongitude(input.longitude());
    }

    @Transactional
    public void deleteCamera(String id, String cameraId, Usuario current) {
        comunidadeService.requireMember(id, current);
        if (cameraRepo.deleteCamera(cameraId, id) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private String requiredName(String name) {
        if (name == null || name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome obrigatório");
        return name.trim();
    }

    private void validateCoordinates(CameraInput input) {
        if (input.latitude() == null || input.longitude() == null || !Double.isFinite(input.latitude())
                || !Double.isFinite(input.longitude()) || Math.abs(input.latitude()) > 90
                || Math.abs(input.longitude()) > 180)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenadas inválidas");
    }
}
