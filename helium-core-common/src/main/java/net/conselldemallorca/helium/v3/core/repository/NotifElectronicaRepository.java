/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.NotifElectronica;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotifElectronicaRepository extends JpaRepository<NotifElectronica, Long> {

	NotifElectronica findByNumeroAndRdsCodiAndRdsClave(
			String numero,
			Long rdsCodi,
			String rdsClave);

}
