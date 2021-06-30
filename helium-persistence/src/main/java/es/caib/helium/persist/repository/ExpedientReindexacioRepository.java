/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.ExpedientReindexacio;

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
	
	


	/** Mètode per consultar els diferent identificadors de tipus d'expedient amb reindexacions pendents o errors de reindexació.
	 * 
	 * @return Retorna una llista d'objectes amb els resultats: 
	 * List<Object[] {expedientTipusId, errors, pendents>
	 */
	@Query("select e.tipus.id, " +
			"		sum(case when e.reindexarError = true then 1 else 0 end), " +
			"		sum(case when e.reindexarData is not null then 1 else 0 end) " +
			"from Expedient e " +
			"where (:esNulEntornId = true or e.tipus.entorn.id = :entornId) " +
			"		and e.anulat = false " + 
			"		and (e.reindexarError = true or e.reindexarData is not null) " +
			"group by e.tipus.id ")
	public List<Object[]> getDades(
			@Param("esNulEntornId") boolean esNulEntornId,
			@Param("entornId") Long entornId);
	
	/** Compta el número d'expedients diferents que hi ha en la cua de reindexació
	 * asíncrona. Pot ser que un expedient estigui més d'una vegada pels diferents canvis
	 * pendents de reindexar però compta com una vegada.
	 * 
	 * @return
	 */
	@Query("select count(distinct expedientId) " +
		   "from ExpedientReindexacio ")
	public Long countExpedientsPendents();

	
}
