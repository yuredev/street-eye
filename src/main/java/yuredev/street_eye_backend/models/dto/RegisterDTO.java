package yuredev.street_eye_backend.models.dto;

import yuredev.street_eye_backend.models.UserRole;

public record RegisterDTO(String login, String password, UserRole role) {
    
}
