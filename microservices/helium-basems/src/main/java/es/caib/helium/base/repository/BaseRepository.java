package es.caib.helium.base.repository;

import es.caib.helium.base.domain.Base;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BaseRepository extends _BaseRepository<Base, Long> {

    Optional<Base> findByCodi(String codi);
    List<Base> findByNom(String nom);

    @Query(	"from Base b " +
            "where " +
            "  	b.codi = :codi " +
            "  	and b.nom = :nom "
    )
    Optional<Base> findByCodiAndNom(
            @Param("nom") String nom,
            @Param("codi") String codi);

}
