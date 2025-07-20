package yuredev.street_eye_backend.models;

public enum PapelUsuario {
    ADMIN("admin"),
    USER("user");

    PapelUsuario(String role) {
        this.role = role;
    }

    private String role;

    public String get() {
        return role;
    }
}
