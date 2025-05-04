package yuredev.street_eye_backend.models.dto.auth;
import yuredev.street_eye_backend.models.User;

public record ResponseAuthDTO(User user, String token) {
}