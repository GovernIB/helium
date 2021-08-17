package es.caib.helium.expedient.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.caib.helium.expedient.domain.Proces;
import es.caib.helium.ms.repository.BaseRepository;

@Repository
public interface ProcesRepository extends BaseRepository<Proces, String>  {

    @Modifying
    @Query("delete from Proces where id = :id")
    void delete(@Param("id") String id);

}
