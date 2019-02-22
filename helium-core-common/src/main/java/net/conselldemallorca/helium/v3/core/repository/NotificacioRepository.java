/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Notificacio;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentEnviamentEstatEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentNotificacioTipusEnumDto;

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
}