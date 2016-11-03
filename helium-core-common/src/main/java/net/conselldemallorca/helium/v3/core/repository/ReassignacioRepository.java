/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Reassignacio;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una reassignació que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReassignacioRepository extends JpaRepository<Reassignacio, Long> {

	@Query(	"from " +
			"    Reassignacio re " +
			"where " +
			"    re.usuariOrigen = :usuariOrigen " +
			"and re.dataInici <= :ara " +
			"and re.dataFi >= :ara")
	public Reassignacio findActivesByUsuariOrigenAndData(
			@Param("usuariOrigen") String usuariOrigen,
			@Param("ara") Date ara);

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
			"    re.tipusExpedientId = :tipusExpedientId " +
			"and re.dataFi >= :dataFi " +
			"and re.dataCancelacio is null")
	public List<Reassignacio> findLlistaActius(
			@Param("tipusExpedientId") Long tipusExpedientId, 
			@Param("dataFi") Date dataFi);

	@Query("select re from Reassignacio re where re.id = :id AND re.dataFi >= :dataFi AND re.dataCancelacio is null")
	public List<Reassignacio> findLlistaActiusModificacio(
			@Param("id") Long id, 
			@Param("dataFi") Date dataFi);

	@Query(	"from " +
			"    Reassignacio re " +
			"where " +
			"    re.usuariOrigen = :usuariOrigen " +
			"and re.dataInici <= :dataInici " +
			"and re.dataFi >= :dataFi " +
			"and re.dataCancelacio is null")
	public Reassignacio findByUsuari(
			@Param("usuariOrigen") String usuariOrigen, 
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

	@Query(	"from Reassignacio r " +
			"where " +
			"   r.tipusExpedientId = :expedientTipusId")
	public List<Reassignacio> findAmbExpedientTipus(@Param("expedientTipusId") Long expedientTipusId);	
	
}
