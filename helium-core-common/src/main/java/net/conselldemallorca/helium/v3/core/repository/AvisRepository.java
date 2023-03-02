/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Avis;

/**
 * Repositori per gestionar una entitat de base de dades del tipus avís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisRepository extends JpaRepository<Avis, Long> {
	
	@Query(	"from " +
			"    Avis a " +
			"where " +
			"    a.actiu = true " +
			"and a.dataInici <= :currentDate " +
			"and (horaInici is null or TO_CHAR(sysdate, 'HH24:MI') > horaInici) "+
			"and a.dataFinal >= :currentDate " +
			"and (horaFi is null or TO_CHAR(sysdate, 'HH24:MI') < horaFi) "
			)
	List<Avis> findActive(@Param("currentDate") Date currentDate);	
}
