package es.caib.helium.expedient.repository;

import es.caib.helium.expedient.domain.Tasca;
import es.caib.helium.ms.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TascaRepository extends BaseRepository<Tasca, String>  {

    @Modifying
    @Query("delete from Tasca where id = :id")
    void delete(@Param("id") String id);

    @Modifying
    @Query("delete from Tasca t where t in :tasques")
    void deleteAll(@Param("tasques") List<Tasca> tasques);
    
    /** Cerca per identificador de tasca
     * 
     * @param tascaId
     * @return 
     */
    Optional<Tasca> findByTascaId(String tascaId);

}
