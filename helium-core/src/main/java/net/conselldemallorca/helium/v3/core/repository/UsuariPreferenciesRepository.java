/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.UsuariPreferencies;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a les preferències d'usuari que estan emmagatzemades
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariPreferenciesRepository extends JpaRepository<UsuariPreferencies, Long> {

	UsuariPreferencies findByCodi(String codi);

}
