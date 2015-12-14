/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un entorn que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntornRepository extends JpaRepository<Entorn, Long> {

}
