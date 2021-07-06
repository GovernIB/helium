/**
 * 
 */
package es.caib.helium.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.helium.persist.entity.Reassignacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una reassignació que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReassignacioRepository extends JpaRepository<Reassignacio, Long> {

	//TODO: probablement no s'utlitzi, esborrar per a la 4.0
	@Query(	"from " +
			"    Reassignacio re " +
			"where " +
			"    re.dataFi >= :dataFi " +
			"and re.dataCancelacio is null")
	public List<Reassignacio> findLlistaActius(
			@Param("dataFi") Date dataFi);

	@Query(	"from " +
			"    Reassignacio re " +
			"where " +
			"    re.usuariOrigen = :usuariOrigen " +
			"and re.dataInici <= :dataInici " +
			"and re.dataFi >= :dataFi " +
			"and re.dataCancelacio is null " + 
			"and re.tipusExpedientId is null ")
	public Reassignacio findByUsuari(
			@Param("usuariOrigen") String usuariOrigen, 
			@Param("dataFi") Date dataFi, 
			@Param("dataInici") Date dataInici);

	@Query(	"from " +
			"    Reassignacio re " +
			"where " +
			"    re.usuariOrigen = :usuariOrigen " +
			"and re.tipusExpedientId = :expedientTipusId " +
			"and re.dataInici <= :dataInici " +
			"and re.dataFi >= :dataFi " +
			"and re.dataCancelacio is null ")
	public Reassignacio findByUsuariAndTipusExpedientId(
			@Param("usuariOrigen") String usuariOrigen, 
			@Param("expedientTipusId") Long expedientTipusId, 
			@Param("dataFi") Date dataFi, 
			@Param("dataInici") Date dataInici);
	
	@Query(	"from Reassignacio r " +
			"where " +
			"   r.tipusExpedientId = :tipusExpedientId " +
			"	and (:esNullFiltre = true or lower(r.usuariOrigen) like lower('%'||:filtre||'%') or lower(r.usuariDesti) like lower('%'||:filtre||'%')) ")
	Page<Reassignacio> findByFiltrePaginat(
			@Param("tipusExpedientId") Long expedientTipusId,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);	
}
