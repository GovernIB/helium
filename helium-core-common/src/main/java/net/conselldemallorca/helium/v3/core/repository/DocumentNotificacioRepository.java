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

import net.conselldemallorca.helium.core.model.hibernate.DocumentNotificacio;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioEstatEnumDto;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una notificació electrònica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentNotificacioRepository extends JpaRepository<DocumentNotificacio, Long> {

	DocumentNotificacio findById(Long id);
	
	DocumentNotificacio findByEnviamentIdentificadorAndEnviamentReferencia(
			String identificador,
			String referenciaEnviament);
	
	List<DocumentNotificacio> findByExpedientOrderByEnviatDataDesc(Expedient expedient);
	
	List<DocumentNotificacio> findByExpedientAndDocumentId(Expedient expedient, Long documentStoreId); 
	
	/** Consulta la llista d'identificadors de documents notificats a l'expedient.
	 * 
	 * @param expedient Expedient on es busquen les notificacions.
	 * @return Llista de documents notificats.
	 */
	@Query ("select distinct dn.document.id " +
			"from DocumentNotificacio dn " +
			"where dn.expedient = :expedient" )
	public List<Long> getDocumentsNotificatsIdsPerExpedient(
			@Param("expedient") Expedient expedient);

	/** Mètode per consultar les notificacions amb filtre paginat des del llistat de notificacions enviades a NOTIB.
	 * @param esNullTipus
	 * @param tipus
	 * @param esNullConcepte
	 * @param concepte
	 * @param esNullEstat
	 * @param estat
	 * @param esNullDataInicial
	 * @param dataInicial
	 * @param esNullDataFinal
	 * @param dataFinal
	 * @param esNullExpedientId
	 * @param expedientId 
	 * @param esNullUnitatOrganitzativaCodi
	 * @param unitatOrganitzativaCodi
	 * @param esNullExpedientTipusId
	 * @param expedientTipusId
	 * @param esNullExpedientTipusIdsPermesosProcedimetComu
	 * @param expedientTipusIdsPermesosProcedimetComu
	 * @param esNullFiltre
	 * @param filtre
	 * @param pageable
	 * @return
	 */
	@Query(	"from DocumentNotificacio n " +
			"where " +
			" 	 (:esNullTipus = true or n.tipus = :tipus) " + 
			"and (:esNullConcepte = true or lower(n.concepte) like lower('%'||:concepte||'%')) " +
			"and (:esNullEstat = true or n.estat = :estat) " + 
			"and (:esNullDataInicial = true or n.enviamentDatatData >= :dataInicial) " +
			"and (:esNullDataFinal = true or n.enviamentDatatData <= :dataFinal) " +
			"and (:esNullInteressat = true "
				+ " or lower(n.titularNom) like lower('%'||:interessat||'%')   "
				+ " or lower(n.titularLlinatge1) like lower('%'||:interessat||'%')  "
				+ " or lower(n.titularLlinatge2) like lower('%'||:interessat||'%') "
				+ " or lower(n.titularNif) like lower('%'||:interessat||'%') ) " +
			"and (:esNullExpedientId = true or n.expedient.id = :expedientId) " +
			"and (:esNullExpedienNumero = true or lower(n.expedient.numero) like lower('%'||:expedientNumero||'%') or lower(n.expedient.titol) like lower('%'||:expedientNumero||'%')) " +

			"and (:esNullNomDocument = true or lower(n.document.arxiuNom) like lower('%'||:nomDocument||'%')) " +
			"and (:esNullUnitatOrganitzativaCodi = true or lower(n.emisorDir3Codi) like lower('%'||:unitatOrganitzativaCodi||'%')) " +
			"and (:esNullExpedientTipusId = true or n.expedient.tipus.id = :expedientTipusId) " +
			"and ((:esNullExpedientTipusIds = true or n.expedient.tipus.id in (:expedientTipusIds)) " + 
			"		or "
			+ " 		((:esNullExpedientTipusIdsPermesosProcedimetComu = true or n.expedient.tipus.id in (:expedientTipusIdsPermesosProcedimetComu)) "
//			+ "				and (:esNullUnitatsOrganitvesCodis = true or n.organCodi in (:unitatsOrganitvesCodis))"
			+ "			) "
			+ "  ) " + 
			" and (:esNullFiltre = true or (n.errorDescripcio) like lower('%'||:filtre||'%') ) ")

	Page<DocumentNotificacio> findAmbFiltrePaginat(
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipus") EnviamentTipusEnumDto  tipus,
			@Param("esNullConcepte") boolean esNullConcepte,
			@Param("concepte") String concepte,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") NotificacioEstatEnumDto estat,
			@Param("esNullDataInicial") boolean esNullDataInicial,
			@Param("dataInicial") Date dataInicial,
			@Param("esNullDataFinal") boolean esNullDataFinal,
			@Param("dataFinal") Date dataFinal,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullExpedientId") boolean esNullExpedientId,
			@Param("expedientId") Long expedientId,
			@Param("esNullExpedienNumero") boolean esNullExpedienNumero,
			@Param("expedientNumero") String expedientNumero, 
			@Param("esNullNomDocument") boolean esNullNomDocument,
			@Param("nomDocument") String nomDocument,	
			@Param("esNullUnitatOrganitzativaCodi") boolean esNullUnitatOrganitzativaCodi,
			@Param("unitatOrganitzativaCodi") String unitatOrganitzativaCodi,
			@Param("esNullExpedientTipusId") boolean esNullExpedientTipusId,
			@Param("expedientTipusId") Long expedientTipusId,
			@Param("esNullExpedientTipusIds") boolean esNullExpedientTipusIds,
			@Param("expedientTipusIds") List<Long> expedientTipusIds,
			@Param("esNullExpedientTipusIdsPermesosProcedimetComu") boolean esNullExpedientTipusIdsPermesosProcedimetComu,
			@Param("expedientTipusIdsPermesosProcedimetComu") List<Long> expedientTipusIdsPermesosProcedimetComu,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre, 
			Pageable pageable);
}
