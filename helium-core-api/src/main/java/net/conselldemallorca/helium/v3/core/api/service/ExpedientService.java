/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import net.conselldemallorca.helium.v3.core.api.dto.AccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentConvertirException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;


/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientService {

	/**
	 * Crea/inicia un nou expedient.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param usuari
	 *            L'usuari de l'expedient.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param definicioProcesId
	 *            La definició de procés de l'expedient.
	 * @param any
	 *            L'any de l'expedient.
	 * @param numero
	 *            El número de l'expedient.
	 * @param titol
	 *            El títol de l'expedient.
	 * @param registreNumero
	 * @param registreData
	 * @param unitatAdministrativa
	 * @param idioma
	 * @param autenticat
	 * @param tramitadorNif
	 * @param tramitadorNom
	 * @param interessatNif
	 * @param interessatNom
	 * @param representantNif
	 * @param representantNom
	 * @param avisosHabilitats
	 * @param avisosEmail
	 * @param avisosMobil
	 * @param notificacioTelematicaHabilitada
	 * @param variables
	 * @param transitionName
	 * @param iniciadorTipus
	 * @param iniciadorCodi
	 * @param responsableCodi
	 * @param documents
	 * @param adjunts
	 * @return El nou expedient creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto create(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
			Integer any,
			String numero,
			String titol,
			String registreNumero,
			Date registreData,
			Long unitatAdministrativa,
			String idioma,
			boolean autenticat,
			String tramitadorNif,
			String tramitadorNom,
			String interessatNif,
			String interessatNom,
			String representantNif,
			String representantNom,
			boolean avisosHabilitats,
			String avisosEmail,
			String avisosMobil,
			boolean notificacioTelematicaHabilitada,
			Map<String, Object> variables,
			String transitionName,
			IniciadorTipusDto iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts);

	/**
	 * Modifica la informació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol modificar.
	 * @param numero
	 *            Número d'expedient.
	 * @param titol
	 *            Títol de l'expedient.
	 * @param responsableCodi
	 *            Codi de l'usuari responsable de l'expedient.
	 * @param dataInici
	 *            Data d'inici de l'expedient.
	 * @param comentari
	 *            Comentari de l'expedient.
	 * @param estatId
	 *            Estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència de l'expedient.
	 * @param geoPosY
	 *            Posició Y de la georeferència de l'expedient.
	 * @param geoReferencia
	 *            Codi de la georeferència de l'expedient.
	 * @param grupCodi
	 *            Codi del grup al qual pertany l'expedient.
	 * @param execucioDinsHandler
	 *            Indica si la invocació prové d'un handler.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void update(
			Long id,
			String numero,
			String titol,
			String responsableCodi,
			Date dataInici,
			String comentari,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			String grupCodi,
			boolean execucioDinsHandler);

	/**
	 * Esborra un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delete(Long id);

	/**
	 * Retorna un expedient donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDto findAmbId(Long id);

	/**
	 * Retorna varios expedients donat el seu id.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return L'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> findAmbIds(Set<Long> ids);

	/**
	 * Consulta d'expedients per entorn paginada.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @param paginacioParams
	 *            Paràmetres de paginació.
	 * @return La pàgina del llistat d'expedients.
	 * @throws Exception 
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public PaginaDto<ExpedientDto> findAmbFiltrePaginat(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesMeves,
			boolean nomesAlertes,
			MostrarAnulatsDto mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			PaginacioParamsDto paginacioParams) throws Exception;

	/**
	 * Consulta només ids d'expedient per entorn.
	 * 
	 * @param entornId
	 *            Atribut id de l'entorn l'expedient que es vol consultar.
	 * @param expedientTipusId
	 *            Atribut id del tipus d'expedient.
	 * @param titol
	 *            Fragment del títol de l'expedient.
	 * @param numero
	 *            Fragment del número de l'expedient.
	 * @param dataInici1
	 *            Data d'inici inicial.
	 * @param dataInici2
	 *            Data d'inici final.
	 * @param dataFi1
	 *            Data de fi inicial.
	 * @param dataFi2
	 *            Data de fi final.
	 * @param estatTipus
	 *            Tipus d'estat a consultar (inicial, final o altres).
	 * @param estatId
	 *            Atribut id de l'estat de l'expedient.
	 * @param geoPosX
	 *            Posició X de la georeferència.
	 * @param geoPosY
	 *            Posició Y de la georeferència.
	 * @param geoReferencia
	 *            Codi de la georeferència.
	 * @return La pàgina del llistat d'expedients.
	 * @throws Exception 
	 * @throws NotFoundException
	 *             Si no s'ha trobat algun dels elements especificats
	 *             mitjançant el seu id (entorn, tipus, estat).
	 * @throws NotAllowedException
	 *             Si no es tenen permisos per a accedir als elements
	 *             especificats mitjançant el seu id (entorn, tipus, estat).
	 */
	public List<Long> findIdsAmbFiltre(
			Long entornId,
			Long expedientTipusId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Date dataFi1,
			Date dataFi2,
			EstatTipusDto estatTipus,
			Long estatId,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean nomesMeves,
			boolean nomesAlertes,
			MostrarAnulatsDto mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup);

	/**
	 * Retorna l'arxiu amb la imatge de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La imatge.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista de persones que han fet alguna tasca de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de persones.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<PersonaDto> findParticipants(Long id);

	/**
	 * Retorna la llista d'accions visibles de l'expedient especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<AccioDto> findAccionsVisibles(Long id);

	/**
	 * Retorna la llista de tasques pendents de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista de tasques pendents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientTascaDto> findTasquesPendents(Long id, boolean permisosVerOtrosUsuarios, boolean nomesMeves, boolean nomesTasquesPersonals, boolean nomesTasquesGrup);

	/**
	 * Retorna la llista de dades d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDadaDto> findDadesPerInstanciaProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista d'agrupacions de dades d'una instància
	 * de procés de l'expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<CampAgrupacioDto> findAgrupacionsDadesPerInstanciaProces(
			Long id,
			String processInstanceId);

	/**
	 * Retorna la llista de documents d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDocumentDto> findDocumentsPerInstanciaProces(
			Long expedientId,
			String processInstanceId);

	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentStoreId
	 *            Atribut id de la taula document_store del document que
	 *            es vol consultar.
	 * @param documentCodi
	 *            Codi del document que es vol consultar.
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDocumentDto findDocumentPerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String documentCodi);
	
	/**
	 * Retorna un document d'una instància de procés de
	 * l'expedient, per documentStoreId
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentStoreId
	 *            Atribut id de la taula document_store del document que
	 *            es vol consultar.
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDocumentDto findDocumentPerDocumentStoreId(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId);

	/**
	 * Esborra un document d'una instància de procés de
	 * l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param processInstanceId
	 *            Atribut processInstanceId que es vol consultar. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param documentStoreId
	 *            Atribut id de la taula document_store del document que
	 *            es vol esborrar.
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void esborrarDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId);

	/**
	 * Retorna l'arxiu del document.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @param documentStoreId
	 *            Atribut id del document que es vol descarregar.
	 * @param documentNom 
	 * @return L'arxiu del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public ArxiuDto getArxiuPerDocument(
			Long id,
			Long documentStoreId);

	/**
	 * Atura la tramitació d'un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol aturar.
	 * @param motiu
	 *            El motiu per aturar la tramitació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void aturar(
			Long id,
			String motiu);

	/**
	 * Anula un expedient.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol anular.
	 * @param motiu
	 *            El motiu per anular l'expedient.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void cancel(
			Long id,
			String motiu);

	/**
	 * Crea una relació entre dos expedients.
	 * 
	 * @param expedientOrigenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param expedientDestiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NotFoundException
	 *             Si no es troba algun dels expedients amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void createRelacioExpedient(
			Long origenId,
			Long destiId);

	/**
	 * Esborra una relació entre dos expedients.
	 * 
	 * @param origenId
	 *            Atribut id de l'expedient origen de la relació.
	 * @param destiId
	 *            Atribut id de l'expedient destí de la relació.
	 * @throws NotFoundException
	 *             Si no es troba algun dels expedients amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public void deleteRelacioExpedient(
			Long origenId,
			Long destiId);

	/**
	 * Retorna la llista d'expedients relacionats amb l'expedient
	 * especificat.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol consultar.
	 * @return La llista d'expedients.
	 * @throws NotFoundException
	 *             Si no s'ha trobat cap expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDto> findRelacionats(Long id);

	/**
	 * Canvia la versió de la definició de procés.
	 * 
	 * @param id
	 *            Atribut id de l'expedient que es vol actualitzar.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés que es vol actualitzar.
	 * @param versio
	 *            Número de versió de la nova definició de procés.
	 * @return 
	 * @throws NotFoundException
	 *             Si no es troba l'expedient amb l'id especificat.
	 * @throws NotAllowedException
	 *             Si no es tenen els permisos adequats.
	 */
	public String canviVersioDefinicioProces(
			Long id,
			int versio);

	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornid, String text);

	public List<InstanciaProcesDto> getArbreInstanciesProces(
			Long processInstanceId);
	
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId);

	public SortedSet<Entry<InstanciaProcesDto, List<ExpedientLogDto>>> getLogsOrdenatsPerData(ExpedientDto expedient, boolean detall);
	
	public Map<String, ExpedientTascaDto> getTasquesPerLogExpedient(Long expedientId);
	
	public void retrocedirFinsLog(Long logId, boolean retrocedirPerTasques);
	
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId);	

	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long logId);

	public void cancelarTasca(Long expedientId, Long taskId);

	public void suspendreTasca(Long expedientId, Long taskId);

	public void reprendreTasca(Long expedientId, Long taskId);

	public void reassignarTasca(String taskId, String expression);

	public List<TascaDadaDto> findConsultaFiltre(Long consultaId);

	public List<TascaDadaDto> findConsultaInforme(Long consultaId);
	
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			Set<Long> ids);
	
	public List<Long> findIdsPerConsultaInforme(Long consultaId, Map<String, Object> valors, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup);

	public void evaluateScript(Long expedientId, String script);

	public enum FiltreAnulat {
		ACTIUS		("expedient.consulta.anulats.actius"),
		ANUL_LATS	("expedient.consulta.anulats.anulats"),
		TOTS		("expedient.consulta.anulats.tots");
		private final String codi;
		private final String id;
		FiltreAnulat(String codi) {
			this.codi = codi;
			this.id = this.name();
		}
		public String getCodi(){
			return this.codi;
		}
		public String getId() {
			return id;
		}
	}

	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(Long consultaId, Map<String, Object> valorsPerService, 
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup, PaginacioParamsDto paginacioParams) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException;

	public String getNumeroExpedientActual(Long entornId, Long expedientTipusId, Integer any);

	public ExpedientTascaDto getStartTask(Long entornId, Long expedientTipusId, Long definicioProcesId, Map<String, Object> valors);

	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId);

	public List<DocumentDto> findListDocumentsPerDefinicioProces(Long definicioProcesId, String processInstanceId, String expedientTipusNom);

	public List<CampDto> getCampsInstanciaProcesById(String processInstanceId);

	public DocumentDto findDocumentsPerId(Long id);

	public ArxiuDto arxiuDocumentPerMostrar(String token);

	public void crearModificarDocument(Long expedientId, String processInstanceId, Long documentStoreId, String nom, String nomArxiu, Long docId, byte[] arxiu, Date data) throws Exception;

	public ArxiuDto generarDocumentAmbPlantillaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) throws NotFoundException, DocumentGenerarException, DocumentConvertirException;

	/**
	 * Genera un document amb plantilla.
	 * 
	 * @param tascaId
	 *            La tasca a dins la qual es genera el document
	 * @param documentCodi
	 *            El codi del document a generar
	 * @return El document generat o null si el document s'adjunta automàticament a la tasca
	 * @throws NotFoundException
	 *            Si el document a generar no s'ha trobat
	 * @throws DocumentGenerarException
	 *            Si s'ha produit algun error al generar el document
	 * @throws DocumentConvertirException
	 *            Si s'ha produit algun error al convertir el document
	 */
	public ArxiuDto generarDocumentAmbPlantillaTasca(
			String tascaId,
			String documentCodi) throws NotFoundException, DocumentGenerarException, DocumentConvertirException;

	public boolean isExtensioDocumentPermesa(String extensio);

	public void buidarLogExpedient(String processInstanceId);

	public void createVariable(Long expedientId, String processInstanceId,	String varName, Object value);

	public void updateVariable(Long expedientId, String processInstanceId, String varName, Object varValor);

	public void deleteVariable(Long expedientId, String processInstanceId, String varName);

	//public ExpedientDocumentDto findDocumentPerInstanciaProcesDocumentStoreId(Long expedientId, Long documentStoreId, String docCodi);

	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId);

	public void deleteSignatura(Long expedientId, Long documentStoreId) throws Exception;

	public ExpedientLogDto findLogById(Long logId);

	public List<ExpedientTascaDto> findTasquesPerInstanciaProces(Long expedientId, String processInstanceId, boolean mostrarDeOtrosUsuarios);

	public boolean isDiferentsTipusExpedients(Set<Long> ids);

	public void activa(Long id);
	
	public void reprendre(Long id) throws Exception;
	
	public void desfinalitzar(Long id) throws Exception;

	public boolean luceneReindexarExpedient(Long expedientId);

	public ArxiuDto arxiuDocumentPerSignar(String token);

	public List<AccioDto> findAccionsVisiblesAmbProcessInstanceId(String processInstanceId);

	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol);
	
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero);

	public ExpedientDadaDto getDadaPerInstanciaProces(String processInstanceId, String variableCodi, boolean incloureVariablesBuides);

	public TascaDadaDto getTascaDadaDtoFromExpedientDadaDto(ExpedientDadaDto dadaPerInstanciaProces);

	public List<ExpedientDadaDto> findDadesPerInstanciaProces(String procesId);
}
