/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.client.expedient.expedient.ExpedientClientService;
import es.caib.helium.logic.helper.*;
import es.caib.helium.logic.intf.WProcessInstance;
import es.caib.helium.logic.intf.WTaskInstance;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi.ExpedientRetroaccioTipus;
import es.caib.helium.logic.intf.dto.*;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto;
import es.caib.helium.logic.intf.dto.ExpedientErrorDto.ErrorTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDto;
import es.caib.helium.logic.intf.exception.*;
import es.caib.helium.logic.intf.service.AnotacioService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.*;
import es.caib.helium.persist.entity.ConsultaCamp.TipusConsultaCamp;
import es.caib.helium.persist.entity.DocumentStore.DocumentFont;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.entity.Termini;
import es.caib.helium.persist.entity.Portasignatures.TipusEstat;
import es.caib.helium.persist.repository.*;
import es.caib.helium.persist.util.ThreadLocalInfo;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementació dels mètodes del servei ExpedientService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("expedientServiceV3")
public class ExpedientServiceImpl implements ExpedientService {

	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private DocumentStoreRepository documentStoreRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private PortasignaturesRepository portasignaturesRepository;
	@Resource
	private NotificacioRepository notificacioRepository;
	@Resource
	private DocumentNotificacioRepository documentNotificacioRepository;
	@Resource
	private AnotacioRepository anotacioRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private ExpedientRegistreHelper expedientRegistreHelper;
	@Resource
	private MessageServiceHelper messageServiceHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ConsultaHelper consultaHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private DocumentHelper documentHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
//	@Resource
//	private LuceneHelper luceneHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
//	@Resource
//	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private NotificacioHelper notificacioHelper;
	@Resource
	private HerenciaHelper herenciaHelper;
	@Resource
	private DistribucioHelper distribucioHelper;
	@Resource
	private AnotacioService anotacioService;

	@Resource
	private ExpedientClientService expedientClientService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
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
			List<DadesDocumentDto> adjunts,
			Long anotacioId,
			boolean anotacioInteressatsAssociar) throws Exception {
		logger.debug("Creant nou expedient (" +
				"entornId=" + entornId + ", " +
				"usuari=" + usuari + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ", " +
				"any=" + any + ", " +
				"titol=" + titol + ", " +
				"anotacioId=" + anotacioId + ", " +
				"anotacioInteressatsAssociar=" + anotacioInteressatsAssociar + ")");
		
		Expedient expedient = null;
		Anotacio anotacio = null;
		try {
			if (anotacioId != null) {
				anotacio = anotacioRepository.getById(anotacioId);
				ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
				if (expedientTipus.isDistribucioSistra()) {
					// Extreu documents i variables segons el mapeig sistra
					if (variables == null)
						variables = new HashMap<String, Object>();
					variables.putAll(distribucioHelper.getDadesInicials(expedientTipus, anotacio));
					if (documents == null) 
						documents = new HashMap<String, DadesDocumentDto>();
					documents.putAll(distribucioHelper.getDocumentsInicials(expedientTipus, anotacio));
					if (adjunts == null)
						adjunts = new ArrayList<DadesDocumentDto>();
					adjunts.addAll(distribucioHelper.getDocumentsAdjunts(expedientTipus, anotacio));
				}
			}
			
			// Es crida la creació a través del helper per evitar errors de concurrència de creació de dos expedients
			// a la vegada que ja s'ha donat el cas.
			expedient = expedientHelper.iniciar(
					entornId, 
					usuari, 
					expedientTipusId, 
					definicioProcesId, 
					any, 
					numero, 
					titol, 
					registreNumero, 
					registreData, 
					unitatAdministrativa, 
					idioma, 
					autenticat, 
					tramitadorNif, 
					tramitadorNom, 
					interessatNif, 
					interessatNom, 
					representantNif, 
					representantNom, 
					avisosHabilitats, 
					avisosEmail, 
					avisosMobil, 
					notificacioTelematicaHabilitada, 
					variables, 
					transitionName, 
					iniciadorTipus, 
					iniciadorCodi, 
					responsableCodi, 
					documents, 
					adjunts);

			if (anotacioId != null) {
				// Incorporporar l'anotació a l'expedient
				anotacioService.incorporarExpedient(
						anotacio.getId(), 
						expedientTipusId, 
						expedient.getId(),
						anotacioInteressatsAssociar,
						true);
			}

			// Retorna la informació de l'expedient que s'ha iniciat
			ExpedientDto dto = conversioTipusServiceHelper.convertir(
					expedient,
					ExpedientDto.class);
			return dto;
		} catch (ExecucioHandlerException ex) {
			throw new TramitacioHandlerException(
					(expedient != null) ? expedient.getEntorn().getId() : null, 
					(expedient != null) ? expedient.getEntorn().getCodi() : null,
					(expedient != null) ? expedient.getEntorn().getNom() : null, 
					(expedient != null) ? expedient.getId() : null, 
					(expedient != null) ? expedient.getTitol() : null,
					(expedient != null) ? expedient.getNumero() : null,
					(expedient != null) ? expedient.getTipus().getId() : null, 
					(expedient != null) ? expedient.getTipus().getCodi() : null,
					(expedient != null) ? expedient.getTipus().getNom() : null,
					ex.getProcessInstanceId(),
					ex.getTaskInstanceId(),
					ex.getTokenId(),
					ex.getClassName(),
					ex.getMethodName(),
					ex.getFileName(),
					ex.getLineNumber(),
					"", 
					ex.getCause());
		// TODO: Controlar la excepció
//		} catch (ValidationException ex) {
		} catch (Exception ex) {
			throw new TramitacioValidacioException("Error de validació en Handler", ex);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
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
			boolean execucioDinsHandler) {
		logger.debug(
				"Modificar informació de l'expedient (" +
				"id=" + id + ", " +
				"numero=" + numero + ", " +
				"titol=" + titol + ", " +
				"responsableCodi=" + responsableCodi + ", " +
				"dataInici=" + dataInici + ", " +
				"comentari=" + comentari + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				true,
				false,
				false);
		if (estatId != null && estatId == -1) {
			estatId = expedient.getEstat() != null ? expedient.getEstat().getId() : null;
			List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
			String[] ids = new String[processInstancesTree.size()];
			int i = 0;
			for (WProcessInstance pi: processInstancesTree)
				ids[i++] = pi.getId();
			Date dataFinalitzacio = new Date();
			workflowEngineApi.finalitzarExpedient(ids, dataFinalitzacio);
			expedient.setDataFi(dataFinalitzacio);
			workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
					expedient.getId(),
					ExpedientRetroaccioTipus.EXPEDIENT_FINALITZAR,
					null);
			estatId = null;
		}
		expedientHelper.update(
				expedient,
				numero,
				titol,
				responsableCodi,
				dataInici,
				comentari,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				grupCodi,
				execucioDinsHandler);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Map<String, DadaIndexadaDto>> luceneGetDades(long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		return indexHelper.expedientIndexLuceneGetDades(expedient.getProcessInstanceId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean luceneReindexarExpedient(Long expedientId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		
		return indexHelper.expedientIndexLuceneRecrear(expedient);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		if (documentStore == null)
			throw new NoTrobatException(DocumentStore.class, documentStoreId);
		return documentHelper.getRespostasValidacioSignatura(documentStore);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long id) {
		logger.debug("Esborrant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				false,
				false,
				true,
				false);
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
		if (expedient.isArxiuActiu()) {
			// Si l'expedient està emmagatzemat a dins l'arxiu comprovam que
			// l'expedient no contengui documents firmats abans d'esborrar-lo.
			List<String> processInstanceIds = new ArrayList<String>();
			for (WProcessInstance processInstance: processInstancesTree) {
				processInstanceIds.add(processInstance.getId());
			}
			List<DocumentStore> documentsSignats = documentStoreRepository.findByProcessInstanceIdInAndSignatTrue(
					processInstanceIds);
			if (!documentsSignats.isEmpty()) {
				throw new ValidacioException("Aquest expedient no es pot esborrar perquè conté documents firmats");
			}
		}
		for (Notificacio notificacio: notificacioRepository.findByExpedientOrderByDataEnviamentDesc(expedient)) {
			notificacioRepository.delete(notificacio);
		}
		
		anotacioService.esborrarAnotacionsExpedient(expedient.getId());

		// Ordena per id de menor a major per evitar errors de dependències
		Collections.sort(
				processInstancesTree,
				new Comparator<WProcessInstance>() {
					public int compare(WProcessInstance o1, WProcessInstance o2) {
						Long l1 = new Long(o1.getId());
						Long l2 = new Long(o2.getId());
						return l2.compareTo(l1);
					}
				});
		for (WProcessInstance pi: processInstancesTree){
			for (TerminiIniciat ti: terminiIniciatRepository.findByProcessInstanceId(pi.getId()))
				terminiIniciatRepository.delete(ti);
			workflowEngineApi.deleteProcessInstance(pi.getId());
			for (DocumentStore documentStore: documentStoreRepository.findByProcessInstanceId(pi.getId())) {
				if (documentStore.isSignat()) {
					try {
						pluginHelper.custodiaEsborrarSignatures(documentStore.getReferenciaCustodia(), expedient);
					} catch (Exception ignored) {}
				}
				List<DocumentNotificacio> enviaments = documentNotificacioRepository.findByExpedientAndDocumentId(expedient, documentStore.getId());
				if (enviaments != null && enviaments.size() > 0)
					documentNotificacioRepository.deleteAll(enviaments);

				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginHelper.gestioDocumentalDeleteDocument(
							documentStore.getReferenciaFont(), expedient);
				documentStoreRepository.deleteById(documentStore.getId());
			}
		}
		for (Portasignatures psigna: expedient.getPortasignatures()) {
			psigna.setEstat(TipusEstat.ESBORRAT);
		}
		for (ExecucioMassivaExpedient eme: execucioMassivaExpedientRepository.getExecucioMassivaByExpedient(id)) {
			execucioMassivaExpedientRepository.delete(eme);
		}
		expedientRepository.delete(expedient);
		// TODO: MS de dades i MS expedients --> Borrar expedient
		expedientClientService.deleteExpedientV1(expedient.getId());
//		luceneHelper.deleteExpedient(expedient);
		if (expedient.getArxiuUuid() != null && pluginHelper.arxiuExisteixExpedient(expedient.getArxiuUuid())) {			
			pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
		}
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ESBORRAR);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long findIdAmbProcessInstanceId(String processInstanceId) {
		return expedientRepository.findIdByProcessInstanceId(processInstanceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbIdAmbPermis(Long id) {
		logger.debug("Consultant l'expedient amb permis lectura (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		ExpedientDto expedientDto = conversioTipusServiceHelper.convertir(
				expedient,
				ExpedientDto.class);
		//  A vegades es produeix un null pointer accedint al tipus d'expedient del DTO, issue #1094
		if (expedientDto.getTipus() == null) {
			// Es marca com un error per identificar quan falla i es rectifica la propietat del dto per continuar treballant
			logger.error("La propietat expedientDto.tipus és null (expedient.getTipus() = " + expedient.getTipus() + ", es fixarà el tipus del dto manualment");
			expedientDto.setTipus(conversioTipusServiceHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		}
		expedientHelper.omplirPermisosExpedient(expedientDto);
		expedientHelper.trobarAlertesExpedient(expedient, expedientDto);
		return expedientDto;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDto findAmbId(Long id) {
		logger.debug("Consultant l'expedient sense comprovar permisos (id=" + id + ")");
		Expedient expedient = expedientRepository.getById(id);
		return conversioTipusServiceHelper.convertir(
				expedient,
				ExpedientDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findAmbIds(Set<Long> ids) {
		List<ExpedientDto> listExpedient = new ArrayList<ExpedientDto>();
		logger.debug("Consultant l'expedient (ids=" + ids + ")");
		Iterator<Long> iterator = ids.iterator();
		Set<Long> idsConsulta = new HashSet<Long>();
		int consultats = 0;
		int n, i;
		while (consultats < ids.size()) {
			idsConsulta.clear();
			// Fa la consulta cada 1000 perquè és el màxim d'ids per clàusula in() a la BBDD
			n = Math.min(1000, ids.size()-consultats);
			for (i=0; i< n; i++) {
				idsConsulta.add(iterator.next());
			}
			consultats += n;
			for (Expedient expedient : expedientRepository.findAmbIds(idsConsulta)) {
				listExpedient.add(conversioTipusServiceHelper.convertir(
						expedient,
						ExpedientDto.class));
			}
		}
		return listExpedient;
	}

	// TODO: Passar a MS expedients
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
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
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients paginada (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
					expedientTipus.getId(), 
					estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class,estatId);
			}
		}
		// Calcula la data fi pel filtre
		dataInici2 = this.ajustaFinalDia(dataInici2);
		// Calcula la data finalització fi pel filtre
		dataFi2 = this.ajustaFinalDia(dataFi2);
		
		// Obté la llista de tipus d'expedient permesos
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		// Executa la consulta amb paginació
		ResultatConsultaPaginada<Long> expedientsIds = workflowEngineApi.expedientFindByFiltre(
				entornId,
				auth.getName(),
				tipusPermesosIds,
				titol,
				numero,
				expedientTipusId,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
				paginacioParams,
				false);
		// Retorna la pàgina amb la resposta
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>(); 
		if (expedientsIds.getCount() > 0) {
			expedients = conversioTipusServiceHelper.convertirList(
				expedientRepository.findByIdIn(expedientsIds.getLlista()),
				ExpedientDto.class);
		}
		// Després de la consulta els expedients es retornen en ordre invers
		Collections.sort(expedients, new ExpedientDtoIdsComparator(expedientsIds.getLlista()));

		if (expedients.size() > 0) {
			expedientHelper.omplirPermisosExpedients(expedients);
			expedientHelper.trobarAlertesExpedients(expedients);
		}
		return paginacioHelper.toPaginaDto(
				expedients,
				expedientsIds.getCount(),
				paginacioParams);
	}

	/** Ajusta el dia per a que estigui tot inclòs. Ajusta l'hora i els minuts fins al final del dia. */
	private Date ajustaFinalDia(Date data) {
		Date ret = null;
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			ret = cal.getTime();
		}	
		return ret;
	}

	/** Classe per poder comparar la posició dels expedients segons la llista d'identificadors ordenada
	 * que es retorna de la consulta d'expedients paginada i ordenada. Segons la posició de l'identificador
	 * a la llista retornada els expedients van en una o altra posició. 
	 */
	public class ExpedientDtoIdsComparator implements Comparator<ExpedientDto> {

		private List<Long> ids;
		
		public ExpedientDtoIdsComparator(List<Long> ids){
			this.ids = ids;
		}
		
		/** L'ordre dels expedients depén de la posició del seu ID després de la consulta. */
		@Override
		public int compare(ExpedientDto e1, ExpedientDto e2) {
			Integer i1 = ids.indexOf(e1.getId());
			Integer i2 = ids.indexOf(e2.getId());
			return i1.compareTo(i2);
		}

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
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
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general d'expedients només ids (" +
				"entornId=" + entornId + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"dataFi1=" + dataFi1 + ", " +
				"dataFi2=" + dataFi2 + ", " +
				"estatTipus=" + estatTipus + ", " +
				"estatId=" + estatId + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);
		}
		// Comprova l'accés a l'estat
		Estat estat = null;
		if (estatId != null) {
			estat = estatRepository.findByExpedientTipusAndIdAmbHerencia(
					expedientTipus.getId(), 
					estatId);
			if (estat == null) {
				logger.debug("No s'ha trobat l'estat (expedientTipusId=" + expedientTipusId + ", estatId=" + estatId + ")");
				throw new NoTrobatException(Estat.class, estatId);
			}
		}
		// Calcula la data fi pel filtre
		dataInici2 = this.ajustaFinalDia(dataInici2);
		// Calcula la data finalitzacio fi pel filtre
		dataFi2 = this.ajustaFinalDia(dataFi2);

		// Obté la llista de tipus d'expedient permesos
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		// Executa la consulta amb paginació
		ResultatConsultaPaginada<Long> expedientsIds = workflowEngineApi.expedientFindByFiltre(
				entornId,
				auth.getName(),
				tipusPermesosIds,
				titol,
				numero,
				expedientTipusId,
				dataInici1,
				dataInici2,
				dataFi1,
				dataFi2,
				estatId,
				geoPosX,
				geoPosY,
				geoReferencia,
				EstatTipusDto.INICIAT.equals(estatTipus),
				EstatTipusDto.FINALITZAT.equals(estatTipus),
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				true, // nomesTasquesMeves, // TODO Si no te permis SUPERVISION nomesTasquesMeves = false
				new PaginacioParamsDto(),
				false);
		return expedientsIds.getLlista();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findPerSuggest(Long expedientTipusId, String text) {
		logger.debug("Consulta suggest d'expedients (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"text=" + text + ")");
		List<Expedient> expedients = null;
		if (expedientTipusId != null) {
			// Comprova l'accés al tipus d'expedient
			expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					expedientTipusId);
			expedients = expedientRepository.findByTipusAndNumeroOrTitol(expedientTipusId, text); 
		}
		return conversioTipusServiceHelper.convertirList(
				expedients,
				ExpedientDto.class);
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean isDiferentsTipusExpedients(Set<Long> ids) {
		
		boolean diferents = false;
		Set<Long> idsTipusExpedients = new HashSet<Long>();
		Set<Long> idsConsulta = new HashSet<Long>();
		Iterator<Long> iterator = ids.iterator();
		int consultats = 0;
		int n, i; 
		while (!diferents && consultats < ids.size()) {
			idsConsulta.clear();
			// Fa la consulta cada 1000 perquè és el màxim d'ids per clàusula in() a la BBDD
			n = Math.min(1000, ids.size()-consultats);
			for (i=0; i< n; i++) {
				idsConsulta.add(iterator.next());
			}
			consultats += n;
			idsTipusExpedients.addAll(expedientRepository.getIdsDiferentsTipusExpedients(idsConsulta));
			diferents = idsTipusExpedients.size() > 1;
		}
		return diferents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getImatgeDefinicioProces(
			Long id,
			String processInstanceId) {
		logger.debug("Consulta de la imatge de la definició de procés (" +
				"id=" + id + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		DefinicioProces definicioProces;
		if (processInstanceId != null) {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					processInstanceId);
		} else {
			definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
					expedient.getProcessInstanceId());
		}
		String resourceName = "processimage.jpg";
		ArxiuDto imatge = new ArxiuDto();
		imatge.setNom(resourceName);
		imatge.setContingut(
				workflowEngineApi.getResourceBytes(
						definicioProces.getJbpmId(),
						resourceName));
		return imatge;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PersonaDto> findParticipants(Long id) {
		logger.debug("Consulta de participants per a l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		
		List<ExpedientTascaDto> tasques = tascaHelper.findTasquesPerExpedientPerInstanciaProces(
				expedient,
				expedient.getProcessInstanceId(),
				true, // completades
				true, // no completades
				true);
		
		Set<String> codisPersona = new HashSet<String>();
		List<PersonaDto> resposta = new ArrayList<PersonaDto>();
		for (ExpedientTascaDto tasca: tasques) {
			if (tasca.getAssignee() != null && !codisPersona.contains(tasca.getAssignee())) {
				resposta.add(tasca.getResponsable());
				codisPersona.add(tasca.getAssignee());
			}
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	// No pot ser readOnly per mor de la cache de les tasques
	@Transactional
	public List<ExpedientTascaDto> findTasquesPendents(
			Long expedientId,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		logger.debug("Consulta de tasques pendents de l'expedient (" +
				"id=" + expedientId + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean tasquesAltresUsuaris = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.SUPERVISION,
						ExtendedPermission.TASK_SUPERV,
						ExtendedPermission.ADMINISTRATION},
					auth);
		List<ExpedientTascaDto> resposta = new ArrayList<ExpedientTascaDto>();
		for (WProcessInstance jpi: workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId())) {
			resposta.addAll(
					tascaHelper.findTasquesPerExpedientPerInstanciaProces(
							jpi.getId(),
							expedient,
							tasquesAltresUsuaris,
							nomesTasquesPersonals,
							nomesTasquesGrup));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void deleteSignatura(
			Long expedientId,
			Long documentStoreId) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				false,
				true,
				false,
				false);
		DocumentStore documentStore = documentStoreRepository.getById(documentStoreId);
		if (documentStore != null && documentStore.isSignat()) {
				pluginHelper.custodiaEsborrarSignatures(
						documentStore.getReferenciaCustodia(),
						expedient);
			String jbpmVariable = documentStore.getJbpmVariable();
			documentStore.setReferenciaCustodia(null);
			documentStore.setSignat(false);
			expedientRegistreHelper.crearRegistreEsborrarSignatura(
					expedient.getId(),
					expedient.getProcessInstanceId(),
					SecurityContextHolder.getContext().getAuthentication().getName(),
					getVarNameFromDocumentStore(documentStore));
			List<WTaskInstance> tasks = workflowEngineApi.findTaskInstancesByProcessInstanceId(expedient.getProcessInstanceId());
			for (WTaskInstance task: tasks) {
				workflowEngineApi.deleteTaskInstanceVariable(
						task.getId(),
						jbpmVariable);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void aturar(
			Long id,
			String motiu) {
		logger.debug("Aturant la tramitació de l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.aturar(
				expedient,
				motiu,
				null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void reprendre(Long id) {
		logger.debug("Reprenent la tramitació de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.STOP,
						ExtendedPermission.ADMINISTRATION});
		expedientHelper.reprendre(expedient, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void anular(
			Long id,
			String motiu) {
		logger.debug("Anulant l'expedient (" +
				"id=" + id + ", " +
				"motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar(
//				"Anular",
//				"expedient",
//				expedient.getTipus().getNom());
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		workflowEngineApi.suspendProcessInstances(ids);
		expedient.setAnulat(true);
		expedient.setComentariAnulat(motiu);
		// TODO: MS Dades + MS Expedient --> anular expedient
//		luceneHelper.deleteExpedient(expedient);
		crearRegistreExpedient(
				expedient.getId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.ANULAR);
//		mesuresTemporalsHelper.mesuraCalcular(
//				"Anular",
//				"expedient",
//				expedient.getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void desanular(Long id) {
		logger.debug("Activant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.CANCEL,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_REPRENDRE,
				null,
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR
		);
		logger.debug("Reprenent les instàncies de procés associades a l'expedient (id=" + id + ")");
		List<WProcessInstance> processInstancesTree = workflowEngineApi.getProcessInstanceTree(
				expedient.getProcessInstanceId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (WProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		workflowEngineApi.resumeProcessInstances(ids);
		expedient.setAnulat(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void desfinalitzar(Long id) {
		logger.debug("Desfinalitzant l'expedient (id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.UNDO_END,
						ExtendedPermission.ADMINISTRATION});
		// Desfinalitza
		expedientHelper.desfinalitzar(
				expedient, 
				null);		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void finalitzar(Long id) {		
		logger.debug("Finalitzar l'expedient (id=" + id + ")");
		expedientHelper.finalitzar(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void migrarArxiu(Long id) {
		logger.debug("Migrar l'expedient (id=" + id + ") a l'arxiu");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				expedient.getId(),
				ExpedientRetroaccioTipus.EXPEDIENT_MIGRAR_ARXIU,
				null);

		try {
			expedientHelper.migrarArxiu(expedient);
		} catch (Exception ex) {
			String errorDescripcio = "Error migrant l'expedient " + expedient.getTitol() + " a l'arxiu: " + ex.getMessage();
			if (expedient.getArxiuUuid() != null && !expedient.getArxiuUuid().isEmpty()) {
				logger.info("Es procedeix a esborrar l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.");
				try{
					pluginHelper.arxiuExpedientEsborrar(expedient.getArxiuUuid());
				} catch(Exception aex) {
					logger.error("Error esborrant l'expedient '" + expedient.getTitol() + "' amb uid '" + expedient.getArxiuUuid() + "' de l'arxiu per error en la migració.", aex);
				}
			}
			throw new TramitacioException(
					expedient.getEntorn().getId(), 
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					errorDescripcio, 
					ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void relacioCreate(
			Long origenId,
			Long destiId) {
		logger.debug("Creant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				origenId,
				ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				destiId.toString(),
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR
		);
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				destiId,
				ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				origenId.toString(),
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR
		);
		expedientHelper.relacioCrear(origen, desti);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void relacioDelete(
			Long origenId,
			Long destiId) {
		logger.debug("Esborrant relació d'expedients (" +
				"origenId=" + origenId + ", " +
				"destiId=" + destiId + ")");
		Expedient origen = expedientHelper.getExpedientComprovantPermisos(
				origenId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		Expedient desti = expedientHelper.getExpedientComprovantPermisos(
				destiId,
				new Permission[] {
						ExtendedPermission.RELATE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				origenId,
				ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				destiId.toString(),
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR
		);
		workflowRetroaccioApi.afegirInformacioRetroaccioPerExpedient(
				destiId,
				ExpedientRetroaccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				origenId.toString(),
				WorkflowRetroaccioApi.ExpedientRetroaccioEstat.IGNORAR
		);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> relacioFindAmbExpedient(Long id) {
		logger.debug("Consulta d'expedients relacionats amb l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<ExpedientDto> list = new ArrayList<ExpedientDto>();
		for (Expedient relacionat: expedient.getRelacionsOrigen()) {			
			list.add(findAmbIdAmbPermis(relacionat.getId()));
		}
		return list;
	}

	/**
	 * Processament d'un script en un expedient.
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesScriptExec(
			Long expedientId,
			String processInstanceId,
			String script) throws PermisDenegatException, NoTrobatException{
		logger.debug("Executa script sobre l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"script=" + script + ")");
		
		// Obtenim expedient comprovant que esté permisos per executar scripts i administrar
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.SCRIPT_EXE,
						ExtendedPermission.ADMINISTRATION});

		expedientHelper.comprovarInstanciaProces(expedient, processInstanceId);

		// TODO: Mètriques
//		if (MesuresTemporalsHelper.isActiu()) {
//			mesuresTemporalsHelper.mesuraIniciar("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
//		}
		
		// executa l'script
		workflowEngineApi.evaluateScript(processInstanceId, script, new HashSet<String>());
		
		
		expedientHelper.verificarFinalitzacioExpedient(expedient);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				ExpedientRetroaccioTipus.PROCES_SCRIPT_EXECUTAR,
				script
		);
//		if (MesuresTemporalsHelper.isActiu())
//			mesuresTemporalsHelper.mesuraCalcular("Executar SCRIPT", "expedient", expedient.getTipus().getNom());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesDefinicioProcesActualitzar(
			String processInstanceId,
			int versio) {
		logger.debug("Canviant versió de la definició de procés (" +
				"processInstanceId=" + processInstanceId + ", " +
				"versio=" + versio + ")");
		ExpedientDto piexp = workflowEngineApi.expedientFindByProcessInstanceId(processInstanceId);
		expedientHelper.getExpedientComprovantPermisos(
				piexp.getId(),
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		if (defprocAntiga == null)
			throw new NoTrobatException(DefinicioProces.class, processInstanceId);
		workflowEngineApi.changeProcessInstanceVersion(processInstanceId, versio);
		// Apunta els terminis iniciats cap als terminis
		// de la nova definició de procés
		DefinicioProces defprocNova = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
		updateTerminis(processInstanceId, defprocAntiga, defprocNova);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void procesDefinicioProcesCanviVersio(
			Long expedientId,
			Long definicioProcesId,
			Long[] subProcesIds,
			List<DefinicioProcesExpedientDto> subDefinicioProces) {
		logger.debug("Canviant versió de les definicións de procés de l'expedient (" +
				"expedientId" + expedientId + ", " +
				"definicioProcesId" + definicioProcesId + ", " +
				"subProcesIds=" + subProcesIds + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DEFPROC_UPDATE,
						ExtendedPermission.ADMINISTRATION});
		if (!expedient.isAmbRetroaccio()) {
			workflowRetroaccioApi.eliminaInformacioRetroaccio(expedient.getProcessInstanceId());
		}
		if (definicioProcesId != null) {
			DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(expedient.getProcessInstanceId());
			DefinicioProces defprocNova = definicioProcesRepository.getById(definicioProcesId);
			if (!defprocAntiga.equals(defprocNova)) {
				workflowEngineApi.changeProcessInstanceVersion(expedient.getProcessInstanceId(), defprocNova.getVersio());
				updateTerminis(expedient.getProcessInstanceId(), defprocAntiga, defprocNova);
			}
		}
		// Subprocessos
		if (subProcesIds != null && subProcesIds.length > 0) {
			// Arriben amb el mateix ordre??
			List<WProcessInstance> instanciesProces = workflowEngineApi.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (WProcessInstance instanciaProces: instanciesProces) {
				DefinicioProces defprocAntiga = expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
				int versio = findVersioDefProcesActualitzar(subDefinicioProces, subProcesIds, instanciaProces.getProcessDefinitionName());
				if (versio != -1 && versio != defprocAntiga.getVersio()) {
					workflowEngineApi.changeProcessInstanceVersion(instanciaProces.getId(), versio);
					DefinicioProces defprocNova =  expedientHelper.findDefinicioProcesByProcessInstanceId(instanciaProces.getId());
					updateTerminis(instanciaProces.getId(), defprocAntiga, defprocNova);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccioDto> accioFindVisiblesAmbProcessInstanceId(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant les accions visibles de la instància de procés(" +
				"expedientId" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		List<Accio> accions = null;
		if (expedient.getTipus().isAmbInfoPropia()) {
			boolean ambHerencia = HerenciaHelper.ambHerencia(expedient.getTipus());
			accions = accioRepository.findAmbExpedientTipusAndOcultaFalse(
					expedient.getTipus().getId(),
					ambHerencia);
		} else {
			DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(processInstanceId);
			accions = accioRepository.findAmbDefinicioProcesAndOcultaFalse(definicioProces);
		}
		Iterator<Accio> it = accions.iterator();
		while (it.hasNext()) {
			Accio accio = it.next();
			if (!permetreExecutarAccioExpedient(
					accio,
					expedient))
				it.remove();
		}
		return conversioTipusServiceHelper.convertirList(
				accions,
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public AccioDto accioFindAmbId(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		logger.debug("Consultant l'acció amb l'id (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioId=" + accioId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		expedientHelper.comprovarInstanciaProces(
				expedient,
				processInstanceId);
		return conversioTipusServiceHelper.convertir(
				accioRepository.getById(accioId),
				AccioDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void accioExecutar(
			Long expedientId,
			String processInstanceId,
			Long accioId) {
		logger.debug("Executant l'acció a dins una instància de procés (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioId=" + accioId + ")");
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		Accio accio = accioRepository.getById(accioId);
		if (accio == null)
			throw new NoTrobatException(Accio.class, accioId);
		if (permetreExecutarAccioExpedient(accio, expedient)) {
			// TODO: Mètriques
//			mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
			workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
					processInstanceId,
					ExpedientRetroaccioTipus.EXPEDIENT_ACCIO,
					accio.getJbpmAction()
			);
			try {
				// Executa l'acció
				workflowEngineApi.executeActionInstanciaProces(
						processInstanceId,
						accio.getJbpmAction(),
						herenciaHelper.getProcessDefinitionIdHeretadaAmbExpedient(expedient));
			} catch (Exception ex) {
				if (ex instanceof ExecucioHandlerException) {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "': " + ex.toString(),
							ex.getCause());
				} else {
					logger.error(
							"Error al executa l'acció '" + accio.getCodi() + "'",
							ex);
				}
				throw new TramitacioException(
						expedient.getEntorn().getId(), 
						expedient.getEntorn().getCodi(), 
						expedient.getEntorn().getNom(), 
						expedient.getId(), 
						expedient.getTitol(), 
						expedient.getNumero(), 
						expedient.getTipus().getId(), 
						expedient.getTipus().getCodi(), 
						expedient.getTipus().getNom(), 
						"Error al executa l'acció '" + accio.getCodi() + "'", 
						ex);
			}
			expedientHelper.verificarFinalitzacioExpedient(expedient);
			indexHelper.expedientIndexLuceneUpdate(processInstanceId);
//			mesuresTemporalsHelper.mesuraCalcular("Executar ACCIO" + accio.getNom(), "expedient", expedient.getTipus().getNom());
		} else {
			throw new PermisDenegatException(
					expedientId, 
					Expedient.class, 
					new Permission[]{ExtendedPermission.WRITE},
					null);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void executarCampAccio(
			Long expedientId, 
			String processInstanceId, 
			String accioCamp) {
		
		logger.debug("Executant camp acció dins una instància de procés (" +
				"expedientId" + expedientId + ", " +
				"processInstanceId" + processInstanceId + ", " +
				"accioCamp=" + accioCamp + ")");

		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);

		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar("Executar ACCIO" + accioCamp, "expedient", expedient.getTipus().getNom());
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				ExpedientRetroaccioTipus.EXPEDIENT_ACCIO,
				accioCamp
		);
		try {
			// Executa l'acció
			workflowEngineApi.executeActionInstanciaProces(
					processInstanceId,
					accioCamp,
					herenciaHelper.getProcessDefinitionIdHeretadaAmbExpedient(expedient));
			
		} catch (Exception ex) {
			if (ex instanceof ExecucioHandlerException) {
				logger.error(
						"Error al executa l'acció '" + accioCamp + "': " + ex.toString(),
						ex.getCause());
			} else {
				logger.error(
						"Error al executa l'acció '" + accioCamp + "'",
						ex);
			}
			throw new TramitacioException(
					expedient.getEntorn().getId(), 
					expedient.getEntorn().getCodi(), 
					expedient.getEntorn().getNom(), 
					expedient.getId(), 
					expedient.getTitol(), 
					expedient.getNumero(), 
					expedient.getTipus().getId(), 
					expedient.getTipus().getCodi(), 
					expedient.getTipus().getNom(), 
					"Error al executa l'acció '" + accioCamp + "'", 
					ex);
		}
		expedientHelper.verificarFinalitzacioExpedient(expedient);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
//		mesuresTemporalsHelper.mesuraCalcular("Executar CAMP ACCIO" + accioCamp, "expedient", expedient.getTipus().getNom());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AlertaDto> findAlertes(Long id) {
		logger.debug("Consulta alertes de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Alerta> alertes = alertaRepository.findByExpedientAndDataEliminacioNull(expedient);
		// Convertir a AlertaDto
		return conversioTipusServiceHelper.convertirList(alertes, AlertaDto.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Object[] findErrorsExpedient(Long id) {
		logger.debug("Consulta errors de l'expedient (" +
				"id=" + id + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				id,
				true,
				false,
				false,
				false);
		List<Portasignatures> portasignatures = portasignaturesRepository.findByExpedientAndEstat(expedient, TipusEstat.ERROR);
		List<ExpedientErrorDto> errors_int = new ArrayList<ExpedientErrorDto>();
		
		if(!portasignatures.isEmpty()){
			for(Portasignatures ps: portasignatures) {
				errors_int.add(new ExpedientErrorDto(ErrorTipusDto.INTEGRACIONS, ps.getErrorCallbackProcessant()));
			}
		}
		
		List<ExpedientErrorDto> errors_bas = new ArrayList<ExpedientErrorDto>();
		if (expedient.getErrorDesc() != null) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, expedient.getErrorDesc(), expedient.getErrorFull()));
		}
		
		if (expedient.isReindexarError()) {
			errors_bas.add(new ExpedientErrorDto(ErrorTipusDto.BASIC, 
					messageServiceHelper.getMessage("expedient.consulta.reindexacio.error"),
					messageServiceHelper.getMessage("expedient.consulta.reindexacio.error.full")));
		}
		
		return new Object[]{errors_bas,errors_int};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDto> findSuggestAmbEntornLikeIdentificador(Long entornId, String text) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientRepository.findAmbEntornLikeIdentificador(entornId, text);
		for (Expedient expedient: expedients) {
			resposta.add(conversioTipusServiceHelper.convertir(expedient,ExpedientDto.class));
		}
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<InstanciaProcesDto> getArbreInstanciesProces(
				Long processInstanceId) {
		return expedientHelper.getArbreInstanciesProces(String.valueOf(processInstanceId));
	}


	// TODO: Passar a MS Dades
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> findConsultaInformePaginat(
			final Long consultaId, 
			Map<String, Object> valorsPerService,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			 final PaginacioParamsDto paginacioParams) {
		// TODO: Mètriques
//		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta");
//		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
		
		final List<ExpedientConsultaDissenyDto> expedientsConsultaDisseny = findConsultaDissenyPaginat(
			consultaId,
			valorsPerService,
			paginacioParams,
			nomesMeves, 
			nomesAlertes, 
			mostrarAnulats,
			nomesTasquesPersonals,
			nomesTasquesGrup,
			null
		);
		
//		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "0");
//		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
		
		final int numExpedients= findIdsPerConsultaInforme(
				consultaId,
				valorsPerService, 
				nomesMeves, 
				nomesAlertes, 
				mostrarAnulats,
				nomesTasquesPersonals,
				nomesTasquesGrup
			).size();
		
//		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "1");
//		mesuresTemporalsHelper.mesuraIniciar("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
		
		Page<ExpedientConsultaDissenyDto> paginaResultats = new Page<ExpedientConsultaDissenyDto>() {
			
			@Override
			public Iterator<ExpedientConsultaDissenyDto> iterator() {
				return getContent().iterator();
			}
			
			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean isFirst() {
				return paginacioParams.getPaginaNum() == 0;
			}

			@Override
			public boolean hasPrevious() {
				return paginacioParams.getPaginaNum() > 0;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public boolean hasNext() {
				return false;
			}
			
			@Override
			public boolean hasContent() {
				return !expedientsConsultaDisseny.isEmpty();
			}
			
			@Override
			public int getTotalPages() {
				return 0;
			}
			
			@Override
			public long getTotalElements() {
				return numExpedients;
			}

			@Override
			public <U> Page<U> map(Function<? super ExpedientConsultaDissenyDto, ? extends U> function) {
				return null;
			}

			@Override
			public Sort getSort() {
				List<Order> orders = new ArrayList<Order>();
				for (OrdreDto or : paginacioParams.getOrdres()) {
					orders.add(new Order(or.getDireccio().equals(OrdreDireccioDto.ASCENDENT) ? Direction.ASC : Direction.DESC, or.getCamp()));
				}
				return Sort.by(orders);
			}
			
			@Override
			public int getSize() {
				return paginacioParams.getPaginaTamany();
			}
			
			@Override
			public int getNumberOfElements() {
				return 0;
			}
			
			@Override
			public int getNumber() {
				return 0;
			}
			
			@Override
			public List<ExpedientConsultaDissenyDto> getContent() {				
				return expedientsConsultaDisseny;
			}
		};

		PaginaDto<ExpedientConsultaDissenyDto> resposta = paginacioHelper.toPaginaDto(
				paginaResultats,
				ExpedientConsultaDissenyDto.class);
		
//		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta", null, null, "2");
//		mesuresTemporalsHelper.mesuraCalcular("CONSULTA INFORME EXPEDIENTS v3", "consulta");
		return resposta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public InstanciaProcesDto getInstanciaProcesById(String processInstanceId) {
		InstanciaProcesDto dto = new InstanciaProcesDto();
		dto.setId(processInstanceId);
		WProcessInstance pi = workflowEngineApi.getProcessInstance(processInstanceId);
		if (pi.getProcessInstance() == null)
			return null;
		dto.setInstanciaProcesPareId(pi.getParentProcessInstanceId());
		if (pi.getDescription() != null && pi.getDescription().length() > 0)
			dto.setTitol(pi.getDescription());
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId());
		dto.setDefinicioProces(conversioTipusServiceHelper.convertir(definicioProces, DefinicioProcesDto.class));
		List<ExpedientDocumentDto> documents = documentHelper.findDocumentsPerInstanciaProces(processInstanceId);
				//documentRepository.findByDefinicioProces(definicioProces);
		Map<String, DocumentDto> documentsDto = new HashMap<String, DocumentDto>();
		for(ExpedientDocumentDto doc : documents) {
			documentsDto.put(doc.getDocumentCodi(), conversioTipusServiceHelper.convertir(doc, DocumentDto.class));
		}
		dto.setVarsDocuments(documentsDto);
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<CampDto> getCampsInstanciaProcesById(
			Long expedientTipusId,
			String processInstanceId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		DefinicioProces definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
				processInstanceId);

		List<Camp> camps;
		if (expedientTipus.isAmbInfoPropia()) {
			camps = campRepository.findByExpedientTipusAmbHerencia(expedientTipusId);
		} else {
			camps = campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		} 
		return conversioTipusServiceHelper.convertirList(camps, CampDto.class);
			
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaFiltre(Long consultaId) {
		Consulta consulta = consultaRepository.getById(consultaId);
		
		List<TascaDadaDto> listTascaDada = consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		
		// Quitamos las variables predefinidas de los filtros con amplitud 0
		Iterator<TascaDadaDto> itListTascaDada = listTascaDada.iterator();
		TascaDadaDto tascaDada;
		while(itListTascaDada.hasNext()) {
			tascaDada = itListTascaDada.next();
			if (consulta.getMapValorsPredefinits().containsKey(tascaDada.getVarCodi())
					&& tascaDada.getAmpleCols() == 0) {
				itListTascaDada.remove();
			}
		}
		
		return listTascaDada;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInforme(Long consultaId) {
		Consulta consulta = consultaRepository.getById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<TascaDadaDto> findConsultaInformeParams(Long consultaId) {
		Consulta consulta = consultaRepository.getById(consultaId);
		return consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.PARAM);
	}


	// TODO: Passar a MS Dades
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public List<ExpedientConsultaDissenyDto> findConsultaDissenyPaginat(
			Long consultaId,
			Map<String, Object> valors,
			PaginacioParamsDto paginacioParams,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			Set<Long> ids) {
		Consulta consulta = consultaRepository.getById(consultaId);
		
		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		
		List<Camp> campsInforme = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME));
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		
		String sort = "expedient$identificador"; //Constants.EXPEDIENT_CAMP_ID;
		boolean asc = false;
		int firstRow = 0;
		int maxResults = -1;
		
		if (paginacioParams != null) {
			for (OrdreDto or : paginacioParams.getOrdres()) {
				asc = or.getDireccio().equals(OrdreDireccioDto.ASCENDENT);
				String clau = or.getCamp().replace(
						Constants.EXPEDIENT_PREFIX_JSP,
						Constants.EXPEDIENT_PREFIX);
				if (or.getCamp().contains("dadesExpedient")) {
					sort = clau.replace("/", ".").replace("dadesExpedient.", "").replace(".valorMostrar", "");
				} else {
					sort = clau.replace(
							".",
							Constants.EXPEDIENT_PREFIX_SEPARATOR);
				}
				break;
			}
			firstRow = paginacioParams.getPaginaNum()*paginacioParams.getPaginaTamany();
			maxResults = paginacioParams.getPaginaTamany();
		}
		List<Long> llistaExpedientIds = new ArrayList<Long>();
		if (ids == null || ids.isEmpty()) {
			llistaExpedientIds = null;
//			llistaExpedientIds = luceneHelper.findIdsAmbDadesExpedientPaginatV3(
//					consulta.getEntorn(),
//					consulta.getExpedientTipus(),
//					campsFiltre,
//					campsInforme,
//					valors,
//					sort,
//					asc,
//					0,
//					-1);
		} else {
			llistaExpedientIds.addAll(ids);
		}
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		List<Map<String, DadaIndexadaDto>> dadesExpedients = new ArrayList<Map<String,DadaIndexadaDto>>();
		if (!llistaExpedientIds.isEmpty())
			dadesExpedients = null;
//			dadesExpedients = luceneHelper.findAmbDadesExpedientPaginatV3(
//					consulta.getEntorn().getCodi(),
//					llistaExpedientIds,
//					campsInforme,
//					sort,
//					asc,
//					firstRow,
//					maxResults);
		
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = null;
//			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.getById(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						campsInforme,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
//			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}

		if (paginacioParams == null) {
			Iterator<Map<String, DadaIndexadaDto>> it = dadesExpedients.iterator();
			while (it.hasNext()) {
				Map<String, DadaIndexadaDto> dadesExpedient = it.next();
				DadaIndexadaDto dadaExpedientId = null;
//				DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
				if (dadaExpedientId != null && !llistaExpedientIds.contains(Long.parseLong(dadaExpedientId.getValorIndex()))) {
					it.remove();
				}
			}
		}
		return resposta;
	}


	// TODO: Passar a MS Dades
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsPerConsultaInforme(
			Long consultaId,
			Map<String, Object> valors,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Consulta consulta = consultaRepository.getById(consultaId);
		
		List<Camp> campsFiltre = consultaHelper.toListCamp(consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		
		afegirValorsPredefinits(consulta, valors, campsFiltre);

		List<Long> llistaExpedientIds = null;
//		List<Long> llistaExpedientIds = luceneHelper.findNomesIds(
//				consulta.getEntorn(),
//				consulta.getExpedientTipus(),
//				campsFiltre,
//				valors);
		boolean filtreTasques = nomesMeves || nomesTasquesPersonals || nomesTasquesGrup;
		if (filtreTasques) {
			filtrarExpedientsAmbTasques(
					llistaExpedientIds,
					nomesMeves,
					nomesAlertes,
					mostrarAnulats,
					nomesTasquesPersonals,
					nomesTasquesGrup);
		}
		return llistaExpedientIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public boolean existsExpedientAmbEntornTipusITitol(Long entornId, Long expedientTipusId, String titol) {
		List<Expedient> expedients = expedientHelper.findByEntornIdAndTipusAndTitol(entornId, expedientTipusId, titol);
		return !expedients.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId,
			Integer any) {
		logger.debug("Consulta del número d'expedient pel tipus d'expedient(" +
				"entornId" + entornId + ", " +
				"expedientTipusId" + expedientTipusId + ", " +
				"any=" + any + ")");
		return expedientHelper.getNumeroExpedientActual(
				entornId, 
				expedientTipusId, 
				any);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly=true)
	public ExpedientTascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusRepository.getById(expedientTipusId);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesRepository.getById(definicioProcesId);
		}

		if (definicioProces == null){
			definicioProces = definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = workflowEngineApi.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null) {
			return tascaHelper.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}

	// TODO: Passar a MS Dades
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientConsultaDissenyDto> consultaFindPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			Set<Long> expedientIdsSeleccio,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"expedientIdsSeleccio=" + expedientIdsSeleccio + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"paginacioParams=" + paginacioParams + ")");
//		
//		// Mètriques - Timers
//		Timer.Context contextConsultaLuceneTotal = null;
//		Timer.Context contextConsultaMongoTotal = null;
//		
//		final Timer timerConsultaLuceneTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.lucene"));
//		final Timer timerConsultaMongoTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "consulta.mongoDB"));
//		
//		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "consulta.count"));
//		countTotal.inc();
//		
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.getById(consultaId);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					consulta.getExpedientTipus().getId());
		// Obte la llista d'expedients permesos
		List<Long> expedientIdsPermesos;
		if (expedientIdsSeleccio != null && !expedientIdsSeleccio.isEmpty()) {
			expedientIdsPermesos = new ArrayList<Long>(expedientIdsSeleccio);
		} else {
			List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ResultatConsultaPaginada<Long> expedientsIds = workflowEngineApi.expedientFindByFiltre(
					entorn.getId(),
					auth.getName(),
					tipusPermesosIds,
					null,
					null,
					expedientTipus.getId(),
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					false,
					MostrarAnulatsDto.SI.equals(mostrarAnulats),
					MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
					nomesAlertes,
					nomesErrors,
					nomesTasquesPersonals,
					nomesTasquesGrup,
					nomesMeves,
					new PaginacioParamsDto(),
					false);
			expedientIdsPermesos = expedientsIds.getLlista();
		}
		// Obte la llista d'expedients de lucene passant els expedients permesos
		// com a paràmetres
		List<Camp> filtreCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
		List<Camp> informeCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
						consulta,
						TipusConsultaCamp.INFORME));

		Object[] respostaLucene = null;
		
//		boolean ctxLuceneStoped = false;
//		try {
//			contextConsultaLuceneTotal = timerConsultaLuceneTotal.time();

		respostaLucene = null;
//			respostaLucene = luceneHelper.findPaginatAmbDadesV3(
//					entorn,
//					expedientTipus,
//					expedientIdsPermesos,
//					filtreCamps,
//					filtreValors,
//					informeCamps,
//					paginacioParams);
			
//			contextConsultaLuceneTotal.stop();
//			ctxLuceneStoped = true;
//			contextConsultaMongoTotal = timerConsultaMongoTotal.time();
//			
//			mongoDBHelper.findPaginatAmbDadesV3(
//					expedientIdsPermesos, 
//					filtreCamps, 
//					filtreValors, 
//					informeCamps, 
//					paginacioParams);
//		} finally {
//			if (!ctxLuceneStoped) {
//				contextConsultaLuceneTotal.stop();
//			}
//			contextConsultaMongoTotal.stop();
//		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, DadaIndexadaDto>> dadesExpedients = (List<Map<String, DadaIndexadaDto>>)respostaLucene[0];
		Long count = (Long)respostaLucene[1];
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = null;
//			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientRepository.getById(Long.parseLong(dadaExpedientId.getValorIndex()));
			if (expedient != null) {
				ExpedientDto expedientDto = expedientHelper.toExpedientDto(expedient);
				expedientHelper.omplirPermisosExpedient(expedientDto);
				fila.setExpedient(expedientDto);
				consultaHelper.revisarDadesExpedientAmbValorsEnumeracionsODominis(
						dadesExpedient,
						informeCamps,
						expedient);
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
//			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
		}
		return paginacioHelper.toPaginaDto(
				resposta,
				count.intValue(),
				paginacioParams);
	}

	// TODO: Passar a MS Dades
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<Long> consultaFindNomesIdsPaginat(
			Long consultaId,
			Map<String, Object> filtreValors,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean nomesErrors,
			MostrarAnulatsDto mostrarAnulats,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta general d'expedients paginada (" +
				"consultaId=" + consultaId + ", " +
				"filtreValors=" + filtreValors + ", " +
				"nomesTasquesPersonals=" + nomesTasquesPersonals + ", " +
				"nomesTasquesGrup=" + nomesTasquesGrup + ", " +
				"nomesMeves=" + nomesMeves + ", " +
				"nomesAlertes=" + nomesAlertes + ", " +
				"nomesErrors=" + nomesErrors + ", " +
				"mostrarAnulats=" + mostrarAnulats + 
				"paginacioParams=" + paginacioParams + ")");
		// Comprova l'accés a la consulta
		Consulta consulta = consultaRepository.getById(consultaId);
		if (consulta == null) {
			throw new NoTrobatException(Consulta.class,consultaId);
		}
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				consulta.getEntorn().getId(),
				true);
		// Comprova l'accés al tipus d'expedient
		ExpedientTipus expedientTipus = expedientTipusHelper.getExpedientTipusComprovantPermisLectura(
					consulta.getExpedientTipus().getId());
		// Obte la llista d'expedients permesos segons els filtres
		List<Long> tipusPermesosIds = expedientTipusHelper.findIdsAmbPermisRead(entorn);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ResultatConsultaPaginada<Long> expedientsIds = workflowEngineApi.expedientFindByFiltre(
				entorn.getId(),
				auth.getName(),
				tipusPermesosIds,
				null,
				null,
				expedientTipus.getId(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				false,
				false,
				MostrarAnulatsDto.SI.equals(mostrarAnulats),
				MostrarAnulatsDto.NOMES_ANULATS.equals(mostrarAnulats),
				nomesAlertes,
				nomesErrors,
				nomesTasquesPersonals,
				nomesTasquesGrup,
				nomesMeves,
				new PaginacioParamsDto(),
				false);
		// Obte la llista d'ids de lucene passant els expedients permesos
		// com a paràmetres
		List<Camp> filtreCamps = consultaHelper.toListCamp(
				consultaHelper.findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE));
		afegirValorsPredefinits(consulta, filtreValors, filtreCamps);
		Object[] respostaLucene = null;
//		Object[] respostaLucene = luceneHelper.findPaginatNomesIdsV3(
//				entorn,
//				expedientTipus,
//				expedientsIds.getLlista(),
//				filtreCamps,
//				filtreValors,
//				paginacioParams);
		@SuppressWarnings("unchecked")
		List<Long> ids = (List<Long>)respostaLucene[0];
		Long count = (Long)respostaLucene[1];
		return paginacioHelper.toPaginaDto(
				ids,
				count.intValue(),
				paginacioParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean existsExpedientAmbEntornTipusINumero(Long entornId, Long expedientTipusId, String numero) {
		return expedientRepository.findByEntornIdAndTipusIdAndNumero(
				entornId,
				expedientTipusId,
				numero) != null;
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(Long expedientId) {
		logger.debug("Obtenint informació de l'arxiu per l'expedient ("
				+ "expedientId=" + expedientId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		ArxiuDetallDto arxiuDetall = null;
		if (expedient.isArxiuActiu() 
				&& expedient.getArxiuUuid() != null) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			List<ContingutArxiu> continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						arxiuDetall.setEniEstat(NtiExpedienteEstadoEnumDto.INDEX_REMISSIO);
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
			if (continguts != null) {
				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
				for (ContingutArxiu cont: continguts) {
					ArxiuContingutDto detallFill = new ArxiuContingutDto();
					detallFill.setIdentificador(
							cont.getIdentificador());
					detallFill.setNom(
							cont.getNom());
					if (cont.getTipus() != null) {
						switch (cont.getTipus()) {
						case EXPEDIENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
							break;
						case DOCUMENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
							break;
						case CARPETA:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
							break;
						}
					}
					detallFills.add(detallFill);
				}
				arxiuDetall.setFills(detallFills);
			}
		}
		return arxiuDetall;
	}


	@Transactional(readOnly = true)
	@Override
	public List<ExpedientDto> findAmbIniciadorCodi(String iniciadorCodi) {
		logger.debug("Consultant expedients per iniciadorCodi (iniciadorCodi=" + iniciadorCodi + ")");
		List<Expedient> expedients = expedientRepository.findByIniciadorCodi(iniciadorCodi);
		return conversioTipusServiceHelper.convertirList(expedients, ExpedientDto.class);
	}

		
	private Registre crearRegistreExpedient(
			Long expedientId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.EXPEDIENT,
				String.valueOf(expedientId));
		return registreRepository.save(registre);
	}

	private boolean permetreExecutarAccioExpedient(
			Accio accio,
			Expedient expedient) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean permesa = false;
	
		if (accio.getRols() == null || accio.getRols().isEmpty()) {
			permesa = true;
		} else {
			for (String rol: accio.getRols().split(",")) {
				if (isUserInRole(auth, rol)) {
					permesa = true;
					break;
				}
			}
		}
		
		if (permesa && !accio.isPublica()) { 
			permesa = permisosHelper.isGrantedAny(
					expedient.getTipus().getId(),
					ExpedientTipus.class,
					new Permission[] {
							ExtendedPermission.WRITE,
							ExtendedPermission.ADMINISTRATION},
					auth);
		}
		return permesa;
	}
	
//	private boolean permetreExecutarAccioExpedient(
//			Accio accio,
//			Expedient expedient) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		boolean permesa = true;
//		if (!accio.isPublica()) {
//			permesa = permisosHelper.isGrantedAny(
//					expedient.getTipus().getId(),
//					ExpedientTipus.class,
//					new Permission[] {
//							ExtendedPermission.WRITE,
//							ExtendedPermission.ADMINISTRATION},
//					auth);
//		}
//		if (permesa && accio.getRols() != null && !accio.getRols().isEmpty()) {
//			permesa = false;
//			for (String rol: accio.getRols().split(",")) {
//				if (isUserInRole(auth, rol)) {
//					permesa = true;
//					break;
//				}
//			}
//		}
//		return permesa;
//	}

	/** Afegeix els valors predefinits si aquest no existeixen. */
	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> campsFiltre) {
		for (Camp camp: campsFiltre) {
			String campFiltreCodi = camp.getDefinicioProces()!= null ? camp.getDefinicioProces().getJbpmKey() + "." + camp.getCodi() : camp.getCodi();
			if (consulta.getMapValorsPredefinits().containsKey(camp.getCodi())
					&& !valors.containsKey(campFiltreCodi)) {
				valors.put(
						campFiltreCodi,
						Camp.getComObject(
								camp.getTipus(),
								consulta.getMapValorsPredefinits().get(camp.getCodi())));
			}
		}
	}

	private boolean isUserInRole(
			Authentication auth,
			String role) {
		for (GrantedAuthority ga: auth.getAuthorities()) {
			if (role.equals(ga.getAuthority()))
				return true;
		}
		return false;
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(
					Constants.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(
					Constants.PREFIX_DOCUMENT.length());
	}

	private int findVersioDefProcesActualitzar(List<DefinicioProcesExpedientDto> definicionsProces, Long[] definicionsProcesId, String key) {
		int versio = -1;
		int i = 0;
		while (i < definicionsProces.size() && !definicionsProces.get(i).getJbpmKey().equals(key)) 
			i++;
		if (i < definicionsProces.size() && definicionsProcesId[i] != null) {
			DefinicioProces definicioProces = definicioProcesRepository.getById(definicionsProcesId[i]);
			if (definicioProces != null) 
				versio = definicioProces.getVersio();
		}
		return versio;
	}

	// Apunta els terminis iniciats cap als terminis de la nova definició de procés
	private void updateTerminis(String procesInstanceId, DefinicioProces defprocAntiga, DefinicioProces defprocNova) {
		List<TerminiIniciat> terminisIniciats = terminiIniciatRepository.findByProcessInstanceId(procesInstanceId);
		for (TerminiIniciat terminiIniciat: terminisIniciats) {
			Termini termini = terminiIniciat.getTermini();
			if (termini.getDefinicioProces() != null && termini.getDefinicioProces().getId().equals(defprocAntiga.getId())) {
				for (Termini terminiNou: defprocNova.getTerminis()) {
					if (terminiNou.getCodi().equals(termini.getCodi())) {
						termini.removeIniciat(terminiIniciat);
						terminiNou.addIniciat(terminiIniciat);
						terminiIniciat.setTermini(terminiNou);
						break;
					}
				}
			}
		}
	}

	private void filtrarExpedientsAmbTasques(
			List<Long> llistaExpedientIds,
			boolean nomesMeves,
			boolean nomesAlertes,
			boolean mostrarAnulats,
			boolean nomesTasquesPersonals,
			boolean nomesTasquesGrup) {
		Set<Long> ids1 = null;
		Set<Long> ids2 = null;
		Set<Long> ids3 = null;
		Set<Long> ids4 = null;
		Set<Long> ids5 = null;
		int index = 0;
		for (Long id: llistaExpedientIds) {
			if (index == 0)
				ids1 = new HashSet<Long>();
			if (index == 1000)
				ids2 = new HashSet<Long>();
			if (index == 2000)
				ids3 = new HashSet<Long>();
			if (index == 3000)
				ids4 = new HashSet<Long>();
			if (index == 4000)
				ids5 = new HashSet<Long>();
			if (index < 1000)
				ids1.add(id);
			else if (index < 2000)
				ids2.add(id);
			else if (index < 3000)
				ids3.add(id);
			else if (index < 4000)
				ids4.add(id);
			else
				ids5.add(id);
			index++;
		}
		List<Object[]> idsInstanciesProces = expedientRepository.findAmbIdsByFiltreConsultesTipus(
				ids1,
				ids2,
				ids3,
				ids4,
				ids5,
				mostrarAnulats,
				nomesAlertes);
		
		List<String> idsPI = new ArrayList<String>();
		List<Long> idsExp = new ArrayList<Long>();
		for (Object[] id : idsInstanciesProces) {
			idsExp.add((Long) id[0]);
			idsPI.add((String) id[1]);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> ids = workflowEngineApi.findRootProcessInstances( //findRootProcessInstancesWithTasksCommand(
						auth.getName(),
						idsPI,
						nomesMeves,
						nomesTasquesPersonals,
						nomesTasquesGrup);		
		Iterator<Long> itExps = llistaExpedientIds.iterator();
		ArrayList<Long> removeList = new ArrayList<Long>();
		while (itExps.hasNext()) {
			Long elem = itExps.next();
			int pos = idsExp.indexOf(elem);
			if (pos == -1 || !ids.contains(idsPI.get(pos))) {
				removeList.add(elem);
			}
		}
		llistaExpedientIds.removeAll(removeList);
	}
	
	
	/*
	 * Notificacions d'expedient
	 */
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioDto> findNotificacionsPerExpedientId(Long expedientId) {
		List<NotificacioDto> notificacions =  conversioTipusServiceHelper.convertirList(
				notificacioHelper.findNotificacionsPerExpedientId(expedientId), 
				NotificacioDto.class);
		
		for (NotificacioDto notificacio: notificacions) {
			ExpedientDocumentDto document = documentHelper.findDocumentPerDocumentStoreId(
					notificacio.getDocument().getProcessInstanceId(), 
					notificacio.getDocument().getId());
			notificacio.getDocument().setDocumentNom(document.getDocumentNom());
		}
		
		return notificacions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DadesNotificacioDto> findNotificacionsNotibPerExpedientId(Long expedientId) throws NoTrobatException {
		List<DadesNotificacioDto> notificaionsDto = new ArrayList<DadesNotificacioDto>();
		
		List<DocumentNotificacio> notificacions = notificacioHelper.findNotificacionsNotibPerExpedientId(expedientId);
		for (DocumentNotificacio notificacio: notificacions) {
			DadesNotificacioDto notificaicoDto = notificacioHelper.toDadesNotificacioDto(notificacio);
			notificaionsDto.add(notificaicoDto);
		}
		return notificaionsDto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioDto findNotificacioPerId(Long notificacioId) {
		NotificacioDto notificacio =  conversioTipusServiceHelper.convertir(notificacioRepository.getById(notificacioId), NotificacioDto.class);
		
		if (notificacio.getDocument() != null) {
			ExpedientDocumentDto document = documentHelper.findDocumentPerDocumentStoreId(
					notificacio.getDocument().getProcessInstanceId(), 
					notificacio.getDocument().getId());
			notificacio.getDocument().setDocumentNom(document.getDocumentNom());
			notificacio.getDocument().setArxiuExtensio(document.getArxiuExtensio());
			notificacio.getDocument().setDataCreacio(document.getDataCreacio());
			notificacio.getDocument().setDataDocument(document.getDataDocument());
		}
		
		if (notificacio.getAnnexos() != null) {
			for(DocumentNotificacioDto annex: notificacio.getAnnexos()) {
				ExpedientDocumentDto document = documentHelper.findDocumentPerDocumentStoreId(
						annex.getProcessInstanceId(), 
						annex.getId());
				annex.setDocumentNom(document.getDocumentNom());
				annex.setArxiuExtensio(document.getArxiuExtensio());
				annex.setDataCreacio(document.getDataCreacio());
				annex.setDataDocument(document.getDataDocument());
			}
		}
		
		return notificacio;
	}
	
	@Override
	@Transactional
	public void notificacioReprocessar(Long notificacioId) {
		Notificacio notificacio = notificacioRepository.getById(notificacioId);
		notificacioHelper.obtenirJustificantNotificacio(notificacio);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findProcesInstanceIdsAmbEntornAndProcessDefinitionName(
			Long entornId, 
			String jbpmKey) {
		logger.debug("Consultant instancies de procés amb entorn i process definition name(" + 
			"entornId = " + entornId + 
			", jbpmKey = " + jbpmKey + ")");
		List<String> processInstancesIds = new ArrayList<String>();
		for (WProcessInstance processInstance :
				workflowEngineApi.findProcessInstancesWithProcessDefinitionNameAndEntorn(
					jbpmKey, 
					entornId))
			processInstancesIds.add(processInstance.getId());
		return processInstancesIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findAmbDefinicioProcesId(
			Long definicioProcesId) {
		logger.debug("Consultant instancies de procés amb process definition id(" + 
			"definicioProcesId = " + definicioProcesId + ")");
		DefinicioProces definicioProces = definicioProcesRepository.getById(definicioProcesId);
		List<String> processInstancesIds = new ArrayList<String>();
		for (WProcessInstance processInstance : workflowEngineApi.findProcessInstancesWithProcessDefinitionId(definicioProces.getJbpmId()))
			processInstancesIds.add(processInstance.getId());
		return processInstancesIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public byte[] getZipDocumentacio(Long expedientId) {
		
		Expedient expedient = expedientRepository.getById(expedientId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(baos);
		ZipEntry ze;
		ArxiuDto arxiu;
		try {
			// Consulta l'arbre de processos
			List<InstanciaProcesDto> arbreProcessos =
					expedientHelper.getArbreInstanciesProces(String.valueOf(expedient.getProcessInstanceId()));
			// Llistat de noms dins del zip per no repetir-los.
			Set<String> nomsArxius = new HashSet<String>();
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
				List<ExpedientDocumentDto> documentsInstancia = 
						documentHelper.findDocumentsPerInstanciaProces(
							instanciaProces.getId());
				// Per cada document de la instància
				for (ExpedientDocumentDto document : documentsInstancia) {
					// Consulta l'arxiu del document
					DocumentStore documentStore = documentStoreRepository.getById(document.getId());
					if (documentStore == null) {
						throw new NoTrobatException(
								DocumentStore.class,
								document.getId());
					}
					// Consulta el contingut.
					arxiu = documentHelper.getArxiuPerDocumentStoreId(
							document.getId(),
							false,
							false);
					// Crea l'entrada en el zip
					String recursNom = this.getZipRecursNom(
							expedient, 
							instanciaProces, 
							document, 
							arxiu,
							nomsArxius);
					ze = new ZipEntry(recursNom);
					out.putNextEntry(ze);
					out.write(arxiu.getContingut());
					out.closeEntry();
				}
			}			
			out.close();
		} catch (Exception e) {
			String errMsg = "Error construint el zip dels documents per l'expedient " + expedient.getIdentificador() + ": " + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
		return baos.toByteArray();
	}

	/** Estableix en nom de l'arxiu a partir del document i l'extensió de l'arxiu. Afegeix una carpeta
	 * si el procés no és el principal, corregeix els caràcters estranys i vigila que no es repeteixin.
	 * 
	 * @param expedient Per determinar si és el procés principal
	 * @param instanciaProces Per determinar si és el procés principal i crear una carpeta en cas contrari.
	 * @param document Per recuperar el nom per l'arxiu
	 * @param arxiu Per recuperar l'extensió
	 * @param nomsArxius Per controlar la llista de noms utilitzats.
	 * @return
	 */
	private String getZipRecursNom(Expedient expedient, InstanciaProcesDto instanciaProces,
			ExpedientDocumentDto document, ArxiuDto arxiu, Set<String> nomsArxius) {
		String recursNom;
		// Nom
		String nom;
		// Segons si és adjunt o document
		if (document.isAdjunt())
			nom = document.getAdjuntTitol();
		else
			nom = document.getDocumentNom();
		nom = nom.replaceAll("/", "_");
		// Carpeta
		String carpeta = null;
		if (!instanciaProces.getId().equals(expedient.getProcessInstanceId())) {
			// Carpeta per un altre procés
			carpeta = instanciaProces.getId() + " - " + instanciaProces.getTitol();
			carpeta = carpeta.replaceAll("/", "_");
		}
		// Extensió
		String extensio = arxiu.getExtensio();

		// Vigila que no es repeteixi
		int comptador = 0;
		do {
			recursNom = (carpeta != null ? carpeta + "/" : "") +
						nom + 
						(comptador > 0 ? " (" + comptador + ")" : "") +
						"." + extensio;
			comptador++;
		} while (nomsArxius.contains(recursNom));

		// Guarda en nom com a utiltizat
		nomsArxius.add(recursNom);
		return recursNom;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void arreglarMetadadesNti(Long expedientId) {
		Expedient expedient = expedientRepository.getById(expedientId);
		if ( (expedient.isArxiuActiu() && expedient.isNtiActiu())
				&& (expedient.getNtiOrgano() == null 
					|| expedient.getNtiClasificacion() == null 
					|| expedient.getNtiSerieDocumental() == null)) {
			// Consulta la informació de l'expedient i actualitza l'expedient
			expedient.setNtiVersion(ExpedientHelper.VERSIO_NTI);
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = pluginHelper.arxiuExpedientInfo(expedient.getArxiuUuid());
			expedient.setNtiIdentificador(
					expedientArxiu.getMetadades().getIdentificador());
			expedient.setNtiVersion(expedientArxiu.getMetadades().getVersioNti());
			expedient.setNtiClasificacion(expedientArxiu.getMetadades().getClassificacio());
			expedient.setNtiSerieDocumental(expedientArxiu.getMetadades().getSerieDocumental());
			if (expedientArxiu.getMetadades().getOrgans() != null 
					&& expedientArxiu.getMetadades().getOrgans().size() > 0) {
				expedient.setNtiOrgano(expedientArxiu.getMetadades().getOrgans().get(0));				
			}
			// Per cada document actualitza la informació NTI del document
			// Consulta l'arbre de processos
			List<InstanciaProcesDto> arbreProcessos =
					expedientHelper.getArbreInstanciesProces(String.valueOf(expedient.getProcessInstanceId()));
			for (InstanciaProcesDto instanciaProces: arbreProcessos) {
				// Per cada instancia de proces consulta els documents.
				List<ExpedientDocumentDto> documentsInstancia = 
						documentHelper.findDocumentsPerInstanciaProces(
							instanciaProces.getId());
				// Per cada document de la instància
				for (ExpedientDocumentDto documentDto : documentsInstancia) {
					DocumentStore documentStore = documentStoreRepository.getById(documentDto.getId());
					Document document;
					if (!documentStore.isAdjunt() && documentStore.getCodiDocument() != null)
						document = documentHelper.findDocumentPerInstanciaProcesICodi(documentStore.getProcessInstanceId(), documentStore.getCodiDocument());
					else 
						document = null;
					documentHelper.actualizarMetadadesNti(
							expedient, 
							document, 
							documentStore, 
							null, 
							null, 
							null, 
							null);
					// Recupera l'identificador de l'arxiu
					if (documentStore.getArxiuUuid() != null) {
						es.caib.plugins.arxiu.api.Document documentArxiuInfo = pluginHelper.arxiuDocumentInfo(
								documentStore.getArxiuUuid(), 
								null, 
								false, 
								documentStore.isSignat());
						documentStore.setNtiIdentificador(documentArxiuInfo.getMetadades().getIdentificador());
					}
				}
			}				
		}
	}

	@Override
	public void clearExpedientIniciant() {
		ThreadLocalInfo.setExpedient(null);
	}


	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}
