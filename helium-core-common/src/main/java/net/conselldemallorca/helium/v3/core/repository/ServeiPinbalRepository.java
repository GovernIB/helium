package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ServeiPinbalEntity;
import net.conselldemallorca.helium.v3.core.api.dto.PinbalServeiEnumDto;

public interface ServeiPinbalRepository extends JpaRepository<ServeiPinbalEntity, Long> {

	@Query(	"from ServeiPinbalEntity dp " +
			"where (:esNullFiltre = true or lower(dp.codi) like lower('%'||:filtre||'%') or lower(dp.nom) like lower('%'||:filtre||'%') ) "
			+ "and actiu=1")
	Page<ServeiPinbalEntity> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,
			Pageable pageable);	
	
	ServeiPinbalEntity findByCodi(PinbalServeiEnumDto codi);
	
	@Query(	"from ServeiPinbalEntity dp where dp.codi=:codi")
	ServeiPinbalEntity findByCodiString(@Param("codi") String codi);
}