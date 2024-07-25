package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ServeiPinbalEntity;

public interface ServeiPinbalRepository extends JpaRepository<ServeiPinbalEntity, Long> {

	@Query(	"from ServeiPinbalEntity dp " +
			"where (:esNullFiltre = true or lower(dp.codi) like lower('%'||:filtre||'%') or lower(dp.nom) like lower('%'||:filtre||'%') ) ")
	Page<ServeiPinbalEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);	
}
