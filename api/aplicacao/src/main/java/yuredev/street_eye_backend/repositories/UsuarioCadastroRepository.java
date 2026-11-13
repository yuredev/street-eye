package yuredev.street_eye_backend.repositories;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import yuredev.street_eye_authentication.models.Usuario;

@RepositoryRestResource(exported = false)
public interface UsuarioCadastroRepository extends JpaRepository<Usuario, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from usuario u where u.id = :id")
    Optional<Usuario> findByIdForUpdate(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from usuario u where u.login = :login")
    Optional<Usuario> findByLoginForUpdate(@Param("login") String login);

    Usuario findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, String id);
    List<Usuario> findAllByLogin(String login);

    @Query("select m.usuario from UsuarioComunidade m where m.comunidade.id = :comunidadeId order by m.usuario.login")
    List<Usuario> findMembrosByComunidadeId(@Param("comunidadeId") String comunidadeId);
}

