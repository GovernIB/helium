/**
 * 
 */
package es.caib.helium.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persistence.entity.Parametre;

/**
 * Repositori per gestionar una entitat de base de dades del tipus Parametre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ParametreRepository extends JpaRepository<Parametre, Long> {
	
	Parametre findByCodi(String codi);
}
