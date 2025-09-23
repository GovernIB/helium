/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExplotacioTemps;

/**
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExplotacioTempsRepository extends JpaRepository<ExplotacioTemps, Long> {

	public ExplotacioTemps findFirstByData(Date data);

	@Query("SELECT e.data FROM ExplotacioTemps e WHERE e.data BETWEEN :startDate AND :endDate")
	public List<Date> findDatesBetween(
			@Param("startDate") Date startDate, 
			@Param("endDate") Date endDate);

	public List<ExplotacioTemps> findByDataIn(List<Date> missingDates);

	public List<ExplotacioTemps> findByDataBetweenOrderByDataDesc(Date fromDate, Date toDate);

}
