/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Dao pels objectes del tipus ExecucioMassivaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaExpedientRepository extends JpaRepository<ExecucioMassivaExpedient, Long> {

	@Query("select e " +
			"from	ExecucioMassivaExpedient e " +
			"where 	e.expedient.id = :expedientId ")
	public List<ExecucioMassivaExpedient> getExecucioMassivaByExpedient(
			@Param("expedientId") Long expedientId
	);
	
	@Query("select count(e) " +
			"from	ExecucioMassivaExpedient e " +
			"where 	e.expedient.id = :execucioMassivaId " +
			" and e.dataFi is null")
	public Long findProgresExecucioMassivaActiveById(
			@Param("execucioMassivaId") Long execucioMassivaId);

	@Query("select e.execucioMassiva.id, " +
			"sum(case when e.estat = 0 then 1 else 0 end) as finalitzat, " + // ExecucioMassivaExpedient.ExecucioMassivaEstat.ESTAT_FINALITZAT
			"sum(case when e.estat = 1 then 1 else 0 end) as error, " +	// ExecucioMassivaExpedient.ExecucioMassivaEstat.ESTAT_ERROR
			"sum(case when e.estat = 2 then 1 else 0 end) as pendent, " + // ExecucioMassivaExpedient.ExecucioMassivaEstat.ESTAT_PENDENT
			"count(*) as total " +
			"from ExecucioMassivaExpedient e " +
			"where e.execucioMassiva in (:execucionsMassives) " +
			"group by e.execucioMassiva.id, " +
			"e.execucioMassiva.dataInici " +
			"order by e.execucioMassiva.dataInici DESC")
	public List<Object[]> findResultatsExecucionsMassives(@Param("execucionsMassives") List<ExecucioMassiva> execucionsMassives);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaExpedient e " +
			"where	e.execucioMassiva.id =	" +
			"			(select min(id) " +
			"			 from 	ExecucioMassiva " +
			"			 where 	dataInici <= :ara " +
			"					and dataFi is null) " +
			"	and	e.dataFi is null ")
	public Long findExecucioMassivaExpedientId(@Param("ara") Date ara);
	
	@Query("select min(e.id) " +
			"from	ExecucioMassivaExpedient e " +
			"where	e.execucioMassiva.id = :nextMassiu " +
			"   and	e.dataFi is null ")
	public Long findNextExecucioMassivaExpedient(@Param("nextMassiu") Long nextMassiu);
}
