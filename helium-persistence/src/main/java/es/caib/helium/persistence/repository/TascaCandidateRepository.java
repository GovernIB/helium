package es.caib.helium.persistence.repository;

import es.caib.helium.persistence.entity.ExpedientTasca;
import es.caib.helium.persistence.entity.TascaCandidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un candidat de tasques d'expedient que està emmagatzemat a dins la base
 * de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaCandidateRepository extends JpaRepository<TascaCandidate, Long> {
	public List<TascaCandidate> findByTasca(ExpedientTasca tasca);

	public void deleteByTasca(ExpedientTasca tasca);
}
