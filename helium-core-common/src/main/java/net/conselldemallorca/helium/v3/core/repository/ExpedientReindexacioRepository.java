/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientReindexacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a les reindexacions dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientReindexacioRepository extends JpaRepository<ExpedientReindexacio, Long> {

	/** Serveix per consultar la següent data de reindexació per un expedient a partir d'una
	 * data de reindexació.
	 * 
	 * @param expedientId
	 * @param darreraData
	 * @return
	 */
	@Query( "select min(r.dataReindexacio) " +
			"from ExpedientReindexacio r " +
			"where r.expedientId = :expedientId " +
			"	and r.dataReindexacio > :darreraData "
			)
	public Date findSeguentReindexacioData(
			@Param("expedientId") Long expedientId, 
			@Param("darreraData") Date darreraData);

}
