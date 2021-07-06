/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.ExecucioMassiva;
import es.caib.helium.persist.entity.ExecucioMassiva.ExecucioMassivaTipus;

/**
 * Dao pels objectes del tipus ExecucioMassiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassiva, Long> {

	/** Llistat per usuari i entorn. */
	public List<ExecucioMassiva> findByUsuariAndEntornOrderByDataIniciDesc(String usuari, Long entorn, Pageable pageable);
	
	/** Llistat per administradors de l'entorn. */
	public List<ExecucioMassiva> findByEntornOrderByDataIniciDesc(Long entorn, Pageable pageable);	
	
	@Query("select min(id) " +
			"from 	ExecucioMassiva " +
			"where 	dataInici <= :ara " +
			"	and dataFi is null " +
			"	and id > :lastMassiu ")
	Long getNextMassiu(@Param("lastMassiu") Long lastMassiu, @Param("ara") Date ara);
	
	@Query("select min(id) " +
			"	from 	ExecucioMassiva " +
			"	where 	dataInici <= :ara " +
			"	and dataFi is null")
	Long getMinExecucioMassiva(@Param("ara") Date ara);
	
	@Query("select max(id) " +
			"	from 	ExecucioMassiva " +
			"	where 	expedientTipus.id = :expedientTipusId " +
			" 			and tipus = :tipus ")
	public Long getMinExecucioMassivaByExpedientTipusId(
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("tipus") ExecucioMassivaTipus altaMassiva);

}
