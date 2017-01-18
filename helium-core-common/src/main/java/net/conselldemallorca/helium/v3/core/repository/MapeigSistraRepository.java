/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;
import net.conselldemallorca.helium.core.model.hibernate.TramitSistra;

/**
 * Mètodes per administrar la informació relativa als mapejos de variables, documents i adjunts
 * d'un tipus d'expedient amb els tràmits de Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MapeigSistraRepository extends JpaRepository<MapeigSistra, Long> {

	/** Retorna la llista de codis helium utilitzats per a un tipus d'expedient i un tipus determinat
	 * 
	 * @param expedientTipus
	 * @param tipus
	 * @return
	 */
	List<String> findCodiHeliumByExpedientTipusAndTipus(ExpedientTipus expedientTipus, TipusMapeig tipus);	
	
	/** Compta agrupat per tipus quantes variables, documents o ajunts hi ha. Retorna
	 * una llista <[TipusMapeig, count]>
	 * 
	 * @param expedientTipus
	 * @return
	 */
	@Query(	"select m.tipus, count(*) " +
			"from MapeigSistra m " +
			"where m.expedientTipus = :expedientTipus " +
			"group by m.tipus ")
	List<Object[]> countTipus( @Param("expedientTipus") ExpedientTipus expedientTipus);
	
	@Query(	"select m.tipus, count(*) " +
			"from MapeigSistra m " +
			"where m.expedientTipus = :expedientTipus " +
			" and m.tramitSistra = :tramitSistra " +
			"group by m.tipus ")
	List<Object[]> countTipusAndTramit(
			@Param("expedientTipus") ExpedientTipus expedientTipus,
			@Param("tramitSistra") TramitSistra tramitSistra);

	@Query(	"from MapeigSistra m " +
			"where " +
			"   m.expedientTipus.id = :expedientTipusId " +
			"   and m.tramitSistra.id = :tramitSistraId " +
			"   and m.tipus = :tipus ")
	Page<MapeigSistra> findByFiltrePaginat(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("tramitSistraId") Long tramitSistraId,
			@Param("tipus") TipusMapeig tipus,
			Pageable pageable);

	MapeigSistra findByExpedientTipusAndCodiHelium(ExpedientTipus expedientTipus, String codiHelium);
	
	MapeigSistra findByExpedientTipusAndCodiHeliumAndTramitSistra(ExpedientTipus expedientTipus, String codiHelium, TramitSistra tramitSistra);

	MapeigSistra findByExpedientTipusAndCodiSistra(ExpedientTipus expedientTipus, String codiSistra);
	
	MapeigSistra findByExpedientTipusAndCodiSistraAndTramitSistra(ExpedientTipus expedientTipus, String codiSistra, TramitSistra tramitSistra);
	
	List<MapeigSistra> findByExpedientTipusAndTramitSistra(ExpedientTipus expedientTipus, TramitSistra tramitSistra);

	@Query(	"from MapeigSistra m " +
			"where " +
			"   m.expedientTipus.id = :expedientTipusId")
	List<MapeigSistra> findAmbExpedientTipus(
			@Param("expedientTipusId") Long expedientTipusId);
}
