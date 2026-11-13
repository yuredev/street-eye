package yuredev.street_eye_backend.models.dto.auth;
import yuredev.street_eye_backend.models.Usuario;

public record ResponseAuthDTO(Usuario user, String token) {
}