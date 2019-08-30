/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Dao pels objectes del tipus ExecucioMassiva.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassiva, Long> {
	
	List<ExecucioMassiva> findByUsuariAndEntornOrderByDataIniciDesc(String usuari, Long entorn, Pageable pageable);
	List<ExecucioMassiva> findByEntornOrderByDataIniciDesc(Long entorn, Pageable pageable);
	
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
}
