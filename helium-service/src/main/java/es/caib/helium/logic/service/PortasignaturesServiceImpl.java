package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.helper.ExceptionHelper;
import es.caib.helium.logic.helper.*;
import es.caib.helium.logic.helpers.DocumentHelper;
import es.caib.helium.logic.intf.dto.engine.WProcessInstance;
import es.caib.helium.logic.intf.dto.engine.WToken;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.common.ThreadLocalInfo;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.AlertaRepository;
import es.caib.helium.persistence.repository.DocumentStoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.service.PortasignaturesService;
import es.caib.helium.persistence.repository.ExpedientRepository;
import es.caib.helium.persistence.repository.PortasignaturesRepository;

@Slf4j
@Service
public class PortasignaturesServiceImpl implements PortasignaturesService {

	@Autowired private PortasignaturesRepository portasignaturesRepository;
	@Autowired private DocumentStoreRepository documentStoreRepository;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private PaginacioHelper paginacioHelper;
	@Autowired private ExpedientRepository expedientRepository;
	@Autowired private UsuariActualHelper usuariActualHelper;
	//@Autowired private DocumentHelperV3 documentHelperV3;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private ExpedientDocumentHelper expedientDocumentHelper;
	@Autowired private ExceptionHelper exceptionHelper;
	@Autowired private ExpedientTipusService expedientTipusService;
	@Autowired private PluginHelper pluginHelper;
	@Autowired private WorkflowEngineApi workflowEngineApi;
	@Autowired private ExpedientLoggerHelper expedientLogHelper;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private AlertaRepository alertaRepository;

	private List<Integer> idsDocumentsProcessant = new ArrayList<Integer>();

	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {

		Long entornActualId = null;
		List<Long> tipusPermesos = null;
		//Si ets admin, no es filtra per entorn seleccionat
		if (!usuariActualHelper.isAdministrador()) {

			/**
			 * Si ets admin, no es filtra per entorn seleccionat ni per tipus permesos
			 * Pero si has seleccionat un expedient tipus al filtre, si que es té en compte
			 */
			//Si no ets admin, es filtra per l'entorn del filtre, que es de la sessió.
			entornActualId = filtreDto.getEntornId();
			//També es filtra per els tipus de expedient amb permis admin
			tipusPermesos = new ArrayList<Long>();
			if (entornActualId!=null) {
				List<ExpedientTipusDto> tipusPermisAdmin = expedientTipusService.findAmbEntornPermisConsultar(entornActualId);
				if (tipusPermisAdmin!=null) {
					for (ExpedientTipusDto etDto: tipusPermisAdmin) {
						tipusPermesos.add(etDto.getId());
					}
				}
			}
		}

		//Intentam revertir la conversió del estat que es produirá a ConversioTipusHelper lin 532
		Portasignatures.Transicio transicio = null;
		if (filtreDto.getEstat()!=null) {
			switch (filtreDto.getEstat()) {
			case BLOQUEJAT: break;	//Es queda com esta: Filtra per estat=BLOQUEJAT
			case PENDENT: break;	//Es queda com esta: Filtra per estat=PENDENT
			case SIGNAT:
				//Filtra per estat=PROCESSAT, TRANSCIO=SIGNAT
				filtreDto.setEstat(PortafirmesEstatEnum.PROCESSAT);
				transicio = Portasignatures.Transicio.SIGNAT;
				break;
			case REBUTJAT:
				//Filtra per estat=PROCESSAT, TRANSCIO=REBUTJAT
				filtreDto.setEstat(PortafirmesEstatEnum.PROCESSAT);
				transicio = Portasignatures.Transicio.REBUTJAT;
				break;
			case PROCESSAT: break;	//Es queda com esta: Filtra per estat=PROCESSAT
			case CANCELAT: break;	//Es queda com esta: Filtra per estat=CANCELAT
			case ERROR: break;		//Es queda com esta: Filtra per estat=ERROR
			case ESBORRAT: break;	//Es queda com esta: Filtra per estat=ESBORRAT
			default:
				break;
			}
		}

		Date dataInicial = null;
		if (filtreDto.getDataPeticioIni() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataPeticioIni());
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			dataInicial = c.getTime();
		}

		Date dataFinal = null;
		if (filtreDto.getDataPeticioFi() != null) {
			// Corregeix la data final per arribar a les 00:00:00h del dia següent.
			Calendar c = new GregorianCalendar();
			c.setTime(filtreDto.getDataPeticioFi());
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			dataFinal = c.getTime();
		}

		paginacioParams.canviaCamp("tipusExpedientNom", "expedient.tipus.nom");
		paginacioParams.canviaCamp("expedientIdentificador", "expedient.numero");

		// No es pot afegir un array buit a una query HQL
		// https://github.com/GovernIB/helium/issues/2000
		boolean sensePermisos = (tipusPermesos==null || tipusPermesos.isEmpty());
		if(sensePermisos) {
			tipusPermesos = new ArrayList<Long>();
			tipusPermesos.add(0L);
		}

		PaginaDto<PortasignaturesDto> pagina = paginacioHelper.toPaginaDto(
				portasignaturesRepository.findByFiltrePaginat(
						entornActualId == null,
						entornActualId,
						sensePermisos,
						tipusPermesos,
						filtreDto.getTipusId() == null,
						filtreDto.getTipusId(),
						filtreDto.getExpedientId() == null,
						filtreDto.getExpedientId(),
						filtreDto.getNumeroExpedient() == null,
						filtreDto.getNumeroExpedient(),
						filtreDto.getDocumentNom() == null,
						filtreDto.getDocumentNom(),
						filtreDto.getEstat() == null,
						filtreDto.getEstat(),
						transicio == null,
						transicio,
						dataInicial == null,
						dataInicial,
						dataFinal == null,
						dataFinal,
						filtreDto.getDocumentId() == null,
						filtreDto.getDocumentId(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
						PortasignaturesDto.class);

		for (PortasignaturesDto pf : pagina.getContingut() ) {
			if (filtreDto.getEstat()!=null) {
				pf.setEstat(filtreDto.getEstat().toString());
			}
			DocumentStore document = documentStoreRepository.findById(pf.getDocumentStoreId()).orElse(null);
			String nom = pf.getDocumentNom();
			if (document != null) {
				nom = document.getArxiuNom();
				pf.setDocumentUUID(document.getArxiuUuid());
			}
			pf.setDocumentNom(nom);
		}

		 return pagina;
	}

	@Override
	@Transactional(readOnly=true)
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		Portasignatures ps = portasignaturesRepository.findById(portafirmesId).orElse(null);
		PortasignaturesDto resultat = conversioTipusHelper.convertir(ps, PortasignaturesDto.class);
		DocumentStore document = documentStoreRepository.findById(resultat.getDocumentStoreId()).orElse(null);
		String nom = resultat.getDocumentNom();
		if (document != null) {
			nom = document.getArxiuNom();
			resultat.setDocumentUUID(document.getArxiuUuid());
		}
		resultat.setDocumentNom(nom);

		return resultat;
	}

	@Override
	@Transactional
	public boolean processarDocumentCallbackPortasignatures(Integer id, boolean rebujat, String motiuRebuig) {
		try {
			Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(id);
			if (portasignatures != null) {
				if (PortafirmesEstatEnum.PENDENT.equals(portasignatures.getEstat())) {
					portasignatures.setDataSignatRebutjat(new Date());
					if (!rebujat) {
						portasignatures.setEstat(PortafirmesEstatEnum.SIGNAT);
						portasignatures.setTransition(Portasignatures.Transicio.SIGNAT);
					} else {
						portasignatures.setEstat(PortafirmesEstatEnum.REBUTJAT);
						portasignatures.setTransition(Portasignatures.Transicio.REBUTJAT);
						portasignatures.setMotiuRebuig(motiuRebuig);
					}
					portasignaturesRepository.save(portasignatures);

					if (!idsDocumentsProcessant.contains(portasignatures.getDocumentId())) {
						idsDocumentsProcessant.add(portasignatures.getDocumentId());
						try {
							processarDocumentPendentPortasignatures(id, portasignatures);
						} finally {
							idsDocumentsProcessant.remove(portasignatures.getDocumentId());
						}
					}

					return true;
				} else if (PortafirmesEstatEnum.ESBORRAT.equals(portasignatures.getEstat())) {
					return true;
				} else if (PortafirmesEstatEnum.PROCESSAT.equals(portasignatures.getEstat())) {
					return true;
				} else {
					log.error("El document rebut al callback (id=" + id + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
				}
			} else {
				log.error("El document rebut al callback (id=" + id + ") no s'ha trobat entre els documents enviats al portasignatures");
			}
		} catch (Exception ex) {
			log.error("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat: " + ex.getMessage());
			log.debug("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat", ex);
		}
		return false;
	}

	@Override
	public boolean processarDocumentPendentPortasignatures(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean processarDocumentPendentPortasignatures(
		Integer id,
		Portasignatures portasignatures) {
		boolean resposta = false;
		if (portasignatures != null) {
			if (portasignatures.getDataProcessamentPrimer() == null)
				portasignatures.setDataProcessamentPrimer(new Date());
			portasignatures.setDataProcessamentDarrer(new Date());
			DocumentStore documentStore = documentStoreRepository.getReferenceById(portasignatures.getDocumentStoreId());
			Long tokenId = portasignatures.getTokenId();
			WToken token;
			String processInstanceId;
			if (tokenId != null) {
				token = workflowEngineApi.getTokenById(tokenId.toString());
				processInstanceId = token.getProcessInstanceId();
			} else {
				token = null;
				processInstanceId = documentStore.getProcessInstanceId();
			}

			if (documentStore != null) {
				if (PortafirmesEstatEnum.SIGNAT.equals(portasignatures.getEstat()) ||
					(PortafirmesEstatEnum.ERROR.equals(portasignatures.getEstat()) && Portasignatures.Transicio.SIGNAT.equals(portasignatures.getTransition()))) {
					// Processa els documents signats
					try {
						ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
						expedientLogHelper.afegirLogExpedientPerProces(
							processInstanceId,
							ExpedientLog.ExpedientLogAccioTipus.PROCES_DOCUMENT_PORTAFIRMES,
							Boolean.toString(true));
						if (portasignatures.getDataSignalIntent() == null)
							portasignatures.setDataSignalIntent(new Date());
						portasignatures.setDataSignalOk(new Date());
						portasignatures.setEstat(PortafirmesEstatEnum.PROCESSAT);

						// Guarda el document
						if (portasignatures.getDataCustodiaIntent() == null) {
							portasignatures.setDataCustodiaIntent(new Date());
						}
						portasignatures.setDataCustodiaOk(new Date());
						Expedient expedient = portasignatures.getExpedient();
						if (token != null
							&& ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
							// Avança el flux
							workflowEngineApi.signalToken(
								tokenId,
								portasignatures.getTransicioOK());

							//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
							// ha anat bé, és possible que s'avanci cap al node "fi"
							expedientHelper.verificarFinalitzacioExpedient(
								expedient);
						}
						resposta = true;
					} catch (Exception pex) {
						errorProcesPsigna(
							portasignatures,
							exceptionHelper.getMissageFinalCadenaExcepcions(pex));
						log.error("Error al processar el document firmat pel callback (id=" + portasignatures.getDocumentId() + "): " + exceptionHelper.getMissageFinalCadenaExcepcions(pex), pex);
					}
					portasignaturesRepository.save(portasignatures);
				} else if (PortafirmesEstatEnum.REBUTJAT.equals(portasignatures.getEstat()) ||
					(PortafirmesEstatEnum.ERROR.equals(portasignatures.getEstat()) && Portasignatures.Transicio.REBUTJAT.equals(portasignatures.getTransition()))) {
					// Processa els documents rebutjats
					try {
						expedientLogHelper.afegirLogExpedientPerProces(
							processInstanceId,
							ExpedientLog.ExpedientLogAccioTipus.PROCES_DOCUMENT_PORTAFIRMES,
							Boolean.toString(false));
						if (token != null) {

							WProcessInstance rootProcessInstance = workflowEngineApi.getRootProcessInstance(
								token.getProcessInstanceId());
							Expedient expedient = expedientRepository.findByProcessInstanceId(rootProcessInstance.getId());

							if (ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
								workflowEngineApi.signalToken(
									tokenId,
									portasignatures.getTransicioKO());

								//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
								// ha anat malament també és possible que s'avanci cap al node "fi"
								expedientHelper.verificarFinalitzacioExpedient(
									expedient);
							}
						}
						portasignatures.setEstat(PortafirmesEstatEnum.PROCESSAT);
						portasignatures.setErrorCallbackProcessant(null);
						resposta = true;
					} catch (Exception ex) {
						errorProcesPsigna(
							portasignatures,
							exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						log.error("Error al processar el document rebutjat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					portasignaturesRepository.save(portasignatures);
				} else {
					String error = "El document de portasignatures (id=" + portasignatures.getDocumentId() + ") no està pendent de processar, està en estat " + portasignatures.getEstat().toString();
					errorProcesPsigna(
						portasignatures,
						error);
					log.error(error);
				}
			} else {
				String error = "El document rebut al callback (id=" + portasignatures.getDocumentId() + ") fa referència a un documentStore inexistent (id=" + portasignatures.getDocumentStoreId() + ")";
				errorProcesPsigna(
					portasignatures,
					error);
				log.error(error);
			}
			List<Portasignatures> ambErrors = portasignaturesRepository.findByExpedientAndEstat(portasignatures.getExpedient(), PortafirmesEstatEnum.ERROR);
			portasignatures.getExpedient().setErrorsIntegracions(!ambErrors.isEmpty());
		} else {
			log.error("El document de portasignatures (id=" + id + ") no s'ha trobat");
		}
		return resposta;
	}

	private void errorProcesPsigna(
		Portasignatures portasignatures,
		String errorCallback) {
		portasignatures.setErrorCallbackProcessant(errorCallback);
		portasignatures.setEstat(PortafirmesEstatEnum.ERROR);
		String expedientResponsable = portasignatures.getExpedient().getResponsableCodi();
		if (expedientResponsable != null) {
			Alerta alerta = new Alerta(
				new Date(),
				expedientResponsable,
				"",
				portasignatures.getExpedient().getEntorn());
			alerta.setExpedient(portasignatures.getExpedient());
			DocumentDto document = documentHelper.getDocumentSenseContingut(portasignatures.getDocumentStoreId());
			String causa = null;
			if (document != null)
				causa = "Error al processar resposta del portasignatures per al document \"" + document.getDocumentNom() + "\": " + errorCallback;
			else
				causa = "Error al processar resposta del portasignatures amb id " + portasignatures.getDocumentId();
			if (causa.length() > 255)
				alerta.setCausa(causa.substring(0, 248) + "[...]");
			else
				alerta.setCausa(causa);
			alertaRepository.save(alerta);
		}
	}
}
