package es.caib.helium.expedient.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.caib.helium.expedient.domain.Expedient;

@Repository
public interface ExpedientRepository extends BaseRepository<Expedient, Long>  {

    // TODO: Investigar per què no funcionen les comandes de delete
    @Modifying
    @Query("delete from Expedient where id = ?1")
    void delete(Long expedientId);

}
