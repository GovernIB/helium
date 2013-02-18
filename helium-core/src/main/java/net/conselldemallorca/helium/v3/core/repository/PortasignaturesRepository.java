/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un document enviat al portasignatures que està
 * emmagatzemat a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PortasignaturesRepository extends JpaRepository<Portasignatures, Long> {

}
