/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.Parametre;

/**
 * Repositori per gestionar una entitat de base de dades del tipus Parametre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ParametreRepository extends JpaRepository<Parametre, Long> {
	
	Parametre findByCodi(String codi);
}
