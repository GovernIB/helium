package net.conselldemallorca.helium.v3.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientHelium;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un expedient que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientHeliumRepository extends JpaRepository<ExpedientHelium, Long> {
	@Query(	"select e " +
			"from ExpedientHelium e " +
			"where " +
			"    e.entorn = :entorn " +
			"and e.tipus in (:tipusPermesos) " +
			"and (:esNullExpedientTipus = true or e.tipus = :expedientTipus) " +
			"and (:esNullTitol = true or lower(e.titol) like lower('%'||:titol||'%')) " +
			"and (:esNullNumero = true or lower(e.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullDataInici1 = true or e.dataInici >= :dataInici1) " +
			"and (:esNullDataInici2 = true or e.dataInici <= :dataInici2) " +
			"and (:nomesIniciats = false or e.dataFi is null) " +
			"and (:nomesFinalitzats = false or e.dataFi is not null) " +
			"and (:esNullEstat = true or e.estat = :estat) " +
			"and (:esNullGeoPosX = true or e.geoPosX = :geoPosX) " +
			"and (:esNullGeoPosY = true or e.geoPosY = :geoPosY) " +
			"and (:esNullGeoReferencia = true or e.geoReferencia = :geoReferencia) " +
			"and (:nomesAmbTasquesActives = false " +
			"        or e.processInstanceId in (:rootProcessInstanceIdsAmbTasquesActives1) " +
			"        or e.processInstanceId in (:rootProcessInstanceIdsAmbTasquesActives2) " +
			"        or e.processInstanceId in (:rootProcessInstanceIdsAmbTasquesActives3) " +
			"        or e.processInstanceId in (:rootProcessInstanceIdsAmbTasquesActives4) " +
			"        or e.processInstanceId in (:rootProcessInstanceIdsAmbTasquesActives5)) " +
			"and (:mostrarAnulats = true or e.anulat = false) " +
			"and (:nomesAlertes = false or e.errorDesc is not null)")
	Page<ExpedientHelium> findByFiltreGeneralPaginat(
			@Param("entorn") Entorn entorn,
			@Param("tipusPermesos") Collection<ExpedientTipus> tipusPermesos,
			@Param("esNullExpedientTipus") boolean esNullExpedientTipus,
			@Param("expedientTipus") ExpedientTipus expedientTipus,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullDataInici1") boolean esNullDataInici1,
			@Param("dataInici1") Date dataInici1,
			@Param("esNullDataInici2") boolean esNullDataInici2,
			@Param("dataInici2") Date dataInici2,
			@Param("nomesIniciats") boolean nomesIniciats,
			@Param("nomesFinalitzats") boolean nomesFinalitzats,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") Estat estat,
			@Param("esNullGeoPosX") boolean esNullGeoPosX,
			@Param("geoPosX") Double geoPosX,
			@Param("esNullGeoPosY") boolean esNullGeoPosY,
			@Param("geoPosY") Double geoPosY,
			@Param("esNullGeoReferencia") boolean esNullGeoReferencia,
			@Param("geoReferencia") String geoReferencia,
			@Param("nomesAmbTasquesActives") boolean nomesAmbTasquesActives,
			@Param("rootProcessInstanceIdsAmbTasquesActives1") Collection<String> rootProcessInstanceIdsAmbTasquesActives1,
			@Param("rootProcessInstanceIdsAmbTasquesActives2") Collection<String> rootProcessInstanceIdsAmbTasquesActives2,
			@Param("rootProcessInstanceIdsAmbTasquesActives3") Collection<String> rootProcessInstanceIdsAmbTasquesActives3,
			@Param("rootProcessInstanceIdsAmbTasquesActives4") Collection<String> rootProcessInstanceIdsAmbTasquesActives4,
			@Param("rootProcessInstanceIdsAmbTasquesActives5") Collection<String> rootProcessInstanceIdsAmbTasquesActives5,
			@Param("mostrarAnulats") boolean mostrarAnulats,
			@Param("nomesAlertes") boolean nomesAlertes,			
			Pageable pageable);

	@Query(	"select cast(e.processInstanceId as long) " +
			"from ExpedientHelium e " +
			"where " +
			"    e.entorn = :entorn " +
			"and e.tipus in (:tipusPermesos) " +
			"and (:esNullExpedientTipus = true or e.tipus = :expedientTipus) " +
			"and (:esNullTitol = true or lower(e.titol) like lower('%'||:titol||'%')) ")
	public List<Long> findListExpedients(
			@Param("entorn") Entorn entorn, 
			@Param("tipusPermesos") Collection<ExpedientTipus> tipusPermesos,
			@Param("esNullExpedientTipus") boolean esNullExpedientTipus,
			@Param("expedientTipus") ExpedientTipus expedientTipus,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol,
			Pageable pageable);

	public Expedient findByProcessInstanceId(String processInstanceId);
}
