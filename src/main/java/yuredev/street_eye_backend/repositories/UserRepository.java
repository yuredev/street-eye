package yuredev.street_eye_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import yuredev.street_eye_backend.models.Usuario;

public interface UserRepository extends JpaRepository<Usuario, String> {
    UserDetails findByLogin(String login);
}
