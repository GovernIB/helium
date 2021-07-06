/**
 * 
 */
package es.caib.helium.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persist.entity.FormulariExtern;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un formulari extern que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FormulariExternRepository extends JpaRepository<FormulariExtern, Long> {
	
	FormulariExtern findByFormulariId(String formulariId);

}
