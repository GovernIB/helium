package es.caib.helium.base.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import es.caib.helium.base.domain.Exemple;

public interface ExempleRepository extends BaseRepository<Exemple, Long> {

    Optional<Exemple> findByCodi(String codi);
    List<Exemple> findByNom(String nom);

    Optional<Exemple> findByCodiAndNom(
            @Param("nom") String nom,
            @Param("codi") String codi);

}
