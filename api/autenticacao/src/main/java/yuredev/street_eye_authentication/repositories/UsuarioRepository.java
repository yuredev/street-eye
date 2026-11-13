package yuredev.street_eye_authentication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import yuredev.street_eye_authentication.models.Usuario;

@RepositoryRestResource(exported = false)
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Usuario findByLogin(String login);
}
