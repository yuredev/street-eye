package yuredev.street_eye_authentication.models.dto.auth;
import yuredev.street_eye_authentication.models.Usuario;

public record ResponseAuthDTO(Usuario user, String token) {
}
