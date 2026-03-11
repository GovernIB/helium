/**
 * 
 */
package es.caib.helium.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.commons.dto.DocumentEnviamentEstatEnumDto;
import es.caib.helium.commons.dto.DocumentNotificacioTipusEnumDto;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.Notificacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<Notificacio, Long> {
	
	List<Notificacio> findByExpedientOrderByDataEnviamentDesc(Expedient expedient);
	
	List<Notificacio> findByExpedientAndTipusOrderByDataEnviamentDesc(Expedient expedient,  DocumentNotificacioTipusEnumDto tipus);
	
	List<Notificacio> findByEstatAndTipusOrderByDataEnviamentAsc(DocumentEnviamentEstatEnumDto estat,  DocumentNotificacioTipusEnumDto tipus);
	
	Notificacio findByRegistreNumeroAndRdsCodiAndRdsClau(String registreNumero, Long rdsCodi, String rdsClau);
		
	@Query(	"from Notificacio n " +
			"where " +
			"    (:esNullFiltre = true or lower(n.assumpte) like lower('%'||:filtre||'%') or lower(n.observacions) like lower('%'||:filtre||'%')) " +
			"order by n.dataEnviament DESC")
	Page<Notificacio> findByFiltrePaginat(
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
}
