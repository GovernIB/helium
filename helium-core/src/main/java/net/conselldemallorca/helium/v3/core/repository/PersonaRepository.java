/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Persona;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una persona que està emmagatzemada a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PersonaRepository extends JpaRepository<Persona, Long> {

	public Persona findByCodi(String codi);
	
	@Query("select e from Persona e where UPPER(nomSencer) like '%'||UPPER(:nomSencer)||'%'")
	List<Persona> findLikeNomSencer(@Param("nomSencer") String nomSencer);
}
