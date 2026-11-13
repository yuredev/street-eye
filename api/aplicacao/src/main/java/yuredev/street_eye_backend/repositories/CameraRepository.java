package yuredev.street_eye_backend.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import yuredev.street_eye_backend.models.Camera;

@RepositoryRestResource(exported = false)
public interface CameraRepository extends JpaRepository<Camera, String> {
    List<Camera> findByComunidade_IdOrderByNome(String comunidadeId);
    Optional<Camera> findByIdAndComunidade_Id(String id, String comunidadeId);

    @Modifying(flushAutomatically = true)
    @Query("delete from Camera c where c.comunidade.id = :comunidadeId")
    int deleteByComunidadeId(@Param("comunidadeId") String comunidadeId);

    @Modifying(flushAutomatically = true)
    @Query("delete from Camera c where c.id = :id and c.comunidade.id = :comunidadeId")
    int deleteCamera(@Param("id") String id, @Param("comunidadeId") String comunidadeId);
}

