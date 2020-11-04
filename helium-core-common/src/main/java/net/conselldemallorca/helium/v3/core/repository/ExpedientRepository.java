/**
 * 
 */
package net.conselldemallorca.helium.v3.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un expedient que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientRepository extends JpaRepository<Expedient, Long> {

	List<Expedient> findByIdIn(Collection<Long> id);

	Expedient findByEntornIdAndTipusIdAndNumero(
			Long entornId,
			Long tipusId,
			String numero);
	
	
	@Query(	"select e.id " +
			 "from Expedient e " +
			 "where " +
			 "   e.reindexarData is not null " +
			 "   order by e.reindexarData asc ")
	List<Long> findAmbDataReindexacio();
	
	
	@Query(	"select e " +
			"from Expedient e " +
			"where " +
			"   e.entorn.id = :entornId " +
			"   and e.tipus.id = :tipusId " +
			"	and (:esNullTitol = true or lower(e.titol) like lower('%'||:titol||'%')) ")
	List<Expedient> findByEntornIdAndTipusIdAndTitol(
			@Param("entornId") Long entornId,
			@Param("tipusId") Long tipusId,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol);
	
	/** Mètode per buscar els expedients d'un tipus d'expedient el número o el títol dels quals continguin el text de cerca. Serveix
	 * pel suggest d'expedients.
	 * 
	 * @return
	 */
	@Query(	"select e " +
			"from Expedient e " +
			"where " +
			"   e.tipus.id = :expedientTipusId " +
			"	and ((lower(e.titol) like lower('%'||:text||'%')) " + 
			"		 or (e.numero like '%'||:text||'%')) ")
	List<Expedient> findByTipusAndNumeroOrTitol(
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("text") String text);

	Expedient findByEntornAndTipusAndNumero(
			Entorn entorn,
			ExpedientTipus tipus,
			String numero);

	Expedient findByEntornAndTipusAndNumeroDefault(
			Entorn entorn,
			ExpedientTipus tipus,
			String numeroDefault);

	Expedient findByEntornIdAndId(
			Long entornId,
			Long id);
	
	@Query(	"from Expedient e " +
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
	List<Expedient> findByFiltreGeneral(
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
			@Param("nomesAlertes") boolean nomesAlertes);

	@Query(	"from Expedient e " +
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
	Page<Expedient> findByFiltreGeneralPaginat(
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

	@Query(	"from Expedient e " +
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
			"and (((:mostrarAnulats = true or e.anulat = false) and :mostrarNomesAnulats = false) or (:mostrarNomesAnulats = true and e.anulat = true)) ")
	List<Long> findIdsByFiltreGeneral(
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
			@Param("mostrarNomesAnulats") boolean mostrarNomesAnulats);

	@Query(	"select e.processInstanceId " +
			"from Expedient e " +
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
			"and (((:mostrarAnulats = true or e.anulat = false) and :mostrarNomesAnulats = false) or (:mostrarNomesAnulats = true and e.anulat = true)) " +
			"and (:nomesAlertes = false or e.errorDesc is not null)")
	List<String> findProcessInstanceIdsByFiltreGeneral(
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
			@Param("mostrarAnulats") boolean mostrarAnulats,
			@Param("mostrarNomesAnulats") boolean mostrarNomesAnulats,
			@Param("nomesAlertes") boolean nomesAlertes);

	@Query("select e.id, e.processInstanceId from Expedient e where " +
			"       ( e.id in (:ids1) " +
			"        or e.id in (:ids2) " +
			"        or e.id in (:ids3) " +
			"        or e.id in (:ids4) " +
			"        or e.id in (:ids5) )" +
			"and (:mostrarAnulats = true or e.anulat = false) " +
			"and (:nomesAlertes = false or e.errorDesc is not null)")
	List<Object[]> findAmbIdsByFiltreConsultesTipus(
			@Param("ids1") Collection<Long> ids1,
			@Param("ids2") Collection<Long> ids2,
			@Param("ids3") Collection<Long> ids3,
			@Param("ids4") Collection<Long> ids4,
			@Param("ids5") Collection<Long> ids5,
			@Param("mostrarAnulats") boolean mostrarAnulats,
			@Param("nomesAlertes") boolean nomesAlertes);

	@Query(	"select " +
			"    e " +
			"from " +
			"    Expedient e " +
			"where " +
			"    entorn.id = :entornId " +
			"and (lower(titol) like lower('%'||:text||'%') or lower(numero) like lower('%'||:text||'%')) " +
			"order by " +
			"    numero, " +
			"    titol")
	List<Expedient> findAmbEntornLikeIdentificador(
			@Param("entornId") Long entornId,
			@Param("text") String text);

	/** Consulta els tipus d'expedient per id. El conjunt ids no pot tenir
	 * més de 1000 registres per evitar l'error de BBDD de la clàusula in(...).
	 * @param ids
	 * @return
	 */
	@Query("select e from Expedient e where " +
			"        e.id in (:ids) ")
	List<Expedient> findAmbIds(
			@Param("ids") Collection<Long> ids);

	
	/** Consulta els diferents tipus per id. El conjunt ids no pot tenir
	 * més de 1000 registres per evitar l'error de BBDD de la clàusula in(...).
	 * @param ids
	 * @return
	 */
	@Query("select distinct e.tipus.id from Expedient e where " +
			"        e.id in (:ids) ")
	List<Long> getIdsDiferentsTipusExpedients(
			@Param("ids") Collection<Long> ids);
	
	@Query(	"select e.id " +
			"from Expedient e " +
			"where e.processInstanceId = :processInstanceId")
	Long findIdByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

	Expedient findByProcessInstanceId(String processInstanceId);

	@Query(	"select e.tipus.id " +
			"from Expedient e " +
			"where e.processInstanceId = :processInstanceId")
	Long findExpedientTipusIdByProcessInstanceId(String processInstanceId);

	/** Mètode per buscar un expedient per a un tipus d'expedient i un número d'expedient per veure si coincideix amb cap anotació de registre.
	 * 
	 * @param tipus
	 * @param numero
	 * @return
	 */
	Expedient findByTipusAndNumero(ExpedientTipus tipus, String numero);

	/** Consulta els expedients inicats a partir del codi de l'iniciador. Serveix
	 * per comprovar si s'ha iniciat algun expedient per Sistra amb el mateix número d'entrada
	 * i clau d'accés.
	 * @param iniciadorCodi En el cas de sistra és la combinació entre el número d'entrada i la clau
	 * d'accés.
	 * @return
	 */
	List<Expedient> findByIniciadorCodi(String iniciadorCodi);


	/** Compta els expedients amb error de reindexació per a un tipus d'expedient concret.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select count(e) " +
			"from Expedient e " +
			"where e.tipus.id = :expedientTipusId " +
			" 		and e.anulat = false " +
			"		and e.reindexarData is not null")
	public Long countPendentReindexacio(@Param("expedientTipusId") Long expedientTipusId);
	
	/** Compta el númer d'expedients amb error de reindexació per a un tipus d'expedient concret.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select count(e) " +
			"from Expedient e " +
			"where e.tipus.id = :expedientTipusId " +
			" 		and e.anulat = false " +
			"		and e.reindexarError = true ")
	public Long countErrorsReindexacio(@Param("expedientTipusId") Long expedientTipusId);

	/** Consulta els identificadors dels expedients amb error de reindexació per a un tipus d'expedient concret.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select e.id " +
			"from Expedient e " +
			"where e.tipus.id = :expedientTipusId " +
			" 		and e.anulat = false " +
			"		and e.reindexarError = true ")
	public List<Long> findIdsErrorsReindexacio(@Param("expedientTipusId") Long expedientTipusId);
	
	
	/** Consulta els identificadors dels expedients pendents de reindexació per a un tipus d'expedient concret.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select e.id " +
			"from Expedient e " +
			"where e.tipus.id = :expedientTipusId " +
			" 		and e.anulat = false " +
			"		and e.reindexarData is not null")
	public List<Long> findIdsPendentsReindexacio(@Param("expedientTipusId") Long expedientTipusId);
	
	/** Consulta els identificadors dels expedients amb error de reindexació o pendents per a un tipus d'expedient concret.
	 * 
	 * @param expedientTipusId
	 * @return
	 */
	@Query("select e.id " +
			"from Expedient e " +
			"where e.tipus.id = :expedientTipusId " +
			" 		and e.anulat = false " +
			"		and (e.reindexarError = true or e.reindexarData is not null) ")
	public List<Long> findIdsReindexacio(@Param("expedientTipusId") long expedientTipusId);

	/** Mètode per modificar només l'error de reindexació i posar la data de reindexació a null per evitar modificar la resta de l'expedient. */
	@Modifying
	@Query("update Expedient e set e.reindexarError = :error, e.reindexarData = :data where e.id = :id")
	public int setReindexarErrorData(@Param("id") Long id, @Param("error") boolean error, @Param("data") Date data);


}
