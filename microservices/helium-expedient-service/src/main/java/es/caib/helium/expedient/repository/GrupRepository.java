package es.caib.helium.expedient.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.caib.helium.expedient.domain.Grup;
import es.caib.helium.ms.repository.BaseRepository;

@Repository
public interface GrupRepository extends BaseRepository<Grup, Long>  {

    @Modifying
    @Query("delete from Grup where id = :id")
    void delete(@Param("id") Long id);
}
