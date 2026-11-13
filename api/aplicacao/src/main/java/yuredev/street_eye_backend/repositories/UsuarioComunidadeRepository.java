package yuredev.street_eye_backend.repositories;

import java.util.List;
import java.util.Optional;
import yuredev.street_eye_backend.models.PapelComunidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import yuredev.street_eye_backend.models.UsuarioComunidade;

@RepositoryRestResource(exported = false)
public interface UsuarioComunidadeRepository extends JpaRepository<UsuarioComunidade, String> {
    Optional<UsuarioComunidade> findByComunidade_IdAndUsuario_Id(String comunidadeId, String usuarioId);

    List<UsuarioComunidade> findByComunidade_IdOrderByUsuario_Login(String comunidadeId);

    List<UsuarioComunidade> findByUsuario_IdOrderByComunidade_Nome(String usuarioId);

    long countByComunidade_IdAndPapel(String comunidadeId, PapelComunidade papel);

    @Query("select m.comunidade.id from UsuarioComunidade m where m.usuario.id = :usuarioId order by m.comunidade.id")
    List<String> findComunidadeIdsByUsuarioId(@Param("usuarioId") String usuarioId);

    boolean existsByComunidade_IdAndUsuario_Id(String comunidadeId, String usuarioId);
    boolean existsByComunidade_IdAndUsuario_IdAndPapel(String comunidadeId, String usuarioId, PapelComunidade papel);

    @Modifying(flushAutomatically = true)
    @Query("delete from UsuarioComunidade m where m.usuario.id = :usuarioId")
    int deleteByUsuarioId(@Param("usuarioId") String usuarioId);

    @Modifying(flushAutomatically = true)
    @Query("delete from UsuarioComunidade m where m.comunidade.id = :comunidadeId")
    int deleteByComunidadeId(@Param("comunidadeId") String comunidadeId);

    @Modifying(flushAutomatically = true)
    @Query("delete from UsuarioComunidade m where m.comunidade.id = :comunidadeId and m.usuario.id = :usuarioId")
    int deleteMembro(@Param("comunidadeId") String comunidadeId, @Param("usuarioId") String usuarioId);
}

