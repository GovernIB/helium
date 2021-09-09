package es.caib.helium.expedient.repository;

import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.ms.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcesRepository extends BaseRepository<Proces, String>  {

    @Modifying
    @Query("delete from Proces where id = :id")
    void delete(@Param("id") String id);

    @Modifying
    @Query("delete from Proces p where p in :processos")
    void deleteAll(@Param("processos") List<Proces> processos);

    /** Cerca per identificador del procés */
	Optional<Proces> findByProcesId(String procesId);

}
