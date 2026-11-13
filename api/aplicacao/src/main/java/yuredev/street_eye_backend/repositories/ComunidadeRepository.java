package yuredev.street_eye_backend.repositories;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import yuredev.street_eye_backend.models.Comunidade;

@RepositoryRestResource(exported = false)
public interface ComunidadeRepository extends JpaRepository<Comunidade, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comunidade c where c.id = :id")
    Optional<Comunidade> findByIdForUpdate(@Param("id") String id);

    @Query("select m.comunidade from UsuarioComunidade m where m.usuario.id = :usuarioId order by m.comunidade.nome")
    List<Comunidade> findByUsuarioId(@Param("usuarioId") String usuarioId);
}

