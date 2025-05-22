/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un valor d'una enumeració que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EnumeracioValorsRepository extends JpaRepository<EnumeracioValors, Long> {

	List<EnumeracioValors> findByEnumeracioOrderByOrdreAsc(
			Enumeracio enumeracio);

	@Query("select e from EnumeracioValors e "
			+ "where e.enumeracio.id = :enumeracioId "
			+ "order by e.ordre, e.id")
	List<EnumeracioValors> findByEnumeracioOrdenat(@Param("enumeracioId") Long enumeracioId);

	@Query(	"from EnumeracioValors e " +
			"where " +
			"   e.enumeracio.id = :enumeracioId " +
			"	and (:esNullFiltre = true or lower(e.codi) like lower('%'||:filtre||'%') or lower(e.nom) like lower('%'||:filtre||'%')) ")
	public Page<EnumeracioValors> findByFiltrePaginat(
			@Param("enumeracioId") Long enumeracioId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);
	
	public EnumeracioValors findByEnumeracioAndCodi(Enumeracio enumeracio, String codi);
	
	public List<EnumeracioValors> findByEnumeracioIdOrderByOrdreAsc(Long enumeracioId);
	
	/** Consulta el següent valor per a ordre dels valors de la enumeració. */
	@Query(	"select coalesce( max( e.ordre), -1) + 1 " +
			"from EnumeracioValors e " +
			"where " +
			"    e.enumeracio.id = :enumeracioId " )
	Integer getNextOrdre(@Param("enumeracioId") Long enumeracioId);

	/** Mètode per consultar el número de valors que té una enumeració. */
	@Query(	"select v.enumeracio.id, " +
			"		count(v) " +
			"from EnumeracioValors v " +
			"where " +
			"    v.enumeracio.id in (:enumeracionsId) " +
			"group by v.enumeracio.id ")
	List<Object[]> countValors(@Param("enumeracionsId") Set<Long> enumeracionsId);
	
	// Aquesta query no te en compte Herencia
	@Query(value = "SELECT COUNT(vi.ID_) " + 
					" FROM HEL_CAMP c " + 
					" JOIN JBPM_VARIABLEINSTANCE vi ON c.CODI = vi.NAME_ " + 
					" JOIN JBPM_PROCESSINSTANCE pi ON pi.ID_ = vi.PROCESSINSTANCE_ " + 
					" LEFT JOIN HEL_EXPEDIENT e ON e.ID = pi.EXPEDIENT_ID_ AND e.TIPUS_ID = c.EXPEDIENT_TIPUS_ID " + 
					" LEFT JOIN HEL_DEFINICIO_PROCES dp ON (pi.EXPEDIENT_ID_ IS NULL AND c.DEFINICIO_PROCES_ID = dp.ID AND pi.PROCESSDEFINITION_ = dp.JBPM_ID) " + 
					" WHERE  " + 
					" c.ENUMERACIO_ID = :enumeracioId " + 
					" AND ( " + 
					"	(pi.EXPEDIENT_ID_ IS NOT NULL AND c.EXPEDIENT_TIPUS_ID = e.TIPUS_ID) " + 
					"	OR " + 
					"	(pi.EXPEDIENT_ID_ IS NULL AND c.DEFINICIO_PROCES_ID = dp.ID) " + 
					" ) " + 
					" AND TO_CHAR(DBMS_LOB.SUBSTR(vi.STRINGVALUE_, 64, 1)) = :valorCodi", nativeQuery = true)
	public List<BigDecimal> countValueUsage(@Param("enumeracioId") Long enumeracioId, @Param("valorCodi") String valorCodi);
}
