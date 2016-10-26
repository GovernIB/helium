/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.Nofiticacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioRepository extends JpaRepository<Nofiticacio, Long> {

//	Nofiticacio findByNumeroAndRdsCodiAndRdsClave(
//			String numero,
//			Long rdsCodi,
//			String rdsClave);

}
