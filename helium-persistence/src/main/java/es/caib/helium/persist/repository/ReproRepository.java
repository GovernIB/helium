/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Repro;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un Repro que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReproRepository extends JpaRepository<Repro, Long> {
	List<Repro> findByUsuariAndExpedientTipusIdOrderByIdDesc(String usuari,Long expedientTipusId);
	
	
	@Query("from Repro r "
			+ " where"
			+ "	r.usuari = :usuari "
			+ "	and "
			+ "	r.expedientTipus = :expedientTipus "
			+ "	and ("
			+ "		( :tascaCodi = 'inici' and r.tascaCodi is null )"
			+ "		or r.tascaCodi = :tascaCodi )")
	List<Repro> findByUsuariAndExpedientTipusIdAndTascaCodiOrderByIdDesc(
			@Param("usuari") String usuari,
			@Param("expedientTipus") ExpedientTipus expedientTipus,
			@Param("tascaCodi") String tascaCodi);
}