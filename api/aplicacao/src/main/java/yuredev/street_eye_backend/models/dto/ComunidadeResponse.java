package yuredev.street_eye_backend.models.dto;

import yuredev.street_eye_backend.models.PapelComunidade;

public record ComunidadeResponse(String id, String nome, String descricao, PapelComunidade papel) {}

