/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.entity.TerminiIniciat;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un termini que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiIniciatRepository extends JpaRepository<TerminiIniciat, Long> {

	Optional<TerminiIniciat> findById(
			Long Id);

	TerminiIniciat findByTerminiAndProcessInstanceId(
			Termini termini,
			String processInstanceId);
	
	List<TerminiIniciat> findByTaskInstanceId(String taskInstanceId);
	
	@Query("select e from TerminiIniciat e "
			+ "where e.taskInstanceId in (:taskInstanceIds)")
	List<TerminiIniciat> findByTaskInstanceIds(
			@Param("taskInstanceIds") Collection<String> taskInstanceIds);
	
	List<TerminiIniciat> findByProcessInstanceId(String processInstanceId);

	@Query("select e from TerminiIniciat e "
			+ "where e.dataAturada is null and e.dataCancelacio is null")
	List<TerminiIniciat> findIniciatsActius();
}
