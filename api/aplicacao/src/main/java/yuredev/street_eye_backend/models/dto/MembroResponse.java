package yuredev.street_eye_backend.models.dto;

import yuredev.street_eye_backend.models.PapelComunidade;

public record MembroResponse(String id, String login, PapelComunidade papel) {}
