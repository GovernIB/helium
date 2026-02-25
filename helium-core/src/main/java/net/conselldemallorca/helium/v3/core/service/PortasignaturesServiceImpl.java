package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.star.plugin.PluginException;

import net.conselldemallorca.helium.core.common.ThreadLocalInfo;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.ProcesCallbackHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.v3.core.repository.DocumentStoreRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

@Service
public class PortasignaturesServiceImpl implements PortasignaturesService {

	@Resource private PortasignaturesRepository portasignaturesRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private ExpedientRepository expedientRepository;
	@Resource private UsuariActualHelper usuariActualHelper;
	@Resource private DocumentHelperV3 documentHelperV3;
	@Resource private ExpedientTipusService expedientTipusService;
	@Resource private ProcesCallbackHelper procesCallbackHelper;
	@Resource private DocumentStoreRepository documentStoreRepository;
	
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
		boolean sensePermisos = (tipusPermesos==null || tipusPermesos.size()==0);
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
			Expedient expedient = expedientRepository.findOne(pf.getExpedientId());
			ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
					pf.getProcessInstanceId(),
					pf.getDocumentStoreId(),
					expedient.isArxiuActiu());
			String nom = pf.getDocumentNom();
			if (document != null) {
				nom = document.getArxiuNom();
				pf.setSignaturaUrlVerificacio(document.getSignaturaUrlVerificacio());
			}
			pf.setDocumentNom(nom);
			pf.setDocumentUUID(document.getArxiuUuid());
		}
		 
		 return pagina;
	}

	@Override
	@Transactional(readOnly=true)
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		Portasignatures ps = portasignaturesRepository.findById(portafirmesId);
		PortasignaturesDto resultat = conversioTipusHelper.convertir(ps, PortasignaturesDto.class);
		Expedient expedient = expedientRepository.findOne(resultat.getExpedientId());
		
		ExpedientDocumentDto document = documentHelperV3.findDocumentPerDocumentStoreId(
				resultat.getProcessInstanceId(),
				resultat.getDocumentStoreId(),
				expedient.isArxiuActiu());
		String nom = resultat.getDocumentNom();
		if (document != null) {
			nom = document.getDocumentNom();
		}
		resultat.setDocumentNom(nom);
		resultat.setDocumentUUID(document.getArxiuUuid());
		
		return resultat;
	}
	
	@Override
	@Transactional
	public boolean processarDocumentCallbackPortasignatures(
			Integer id,
			boolean rebujat,
			String motiuRebuig) {
		try {
			Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(id);
			if (portasignatures != null) {
				if (PortafirmesEstatEnum.PENDENT.equals(portasignatures.getEstat())) {
					portasignatures.setDataSignatRebutjat(new Date());
					if (!rebujat) {
						portasignatures.setEstat(PortafirmesEstatEnum.SIGNAT);
						portasignatures.setTransition(Transicio.SIGNAT);
					} else {
						portasignatures.setEstat(PortafirmesEstatEnum.REBUTJAT);
						portasignatures.setTransition(Transicio.REBUTJAT);
						portasignatures.setMotiuRebuig(motiuRebuig);
					}
					portasignaturesRepository.save(portasignatures);
					
					if (!procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId())) {
						procesCallbackHelper.afegirDocument(portasignatures.getDocumentId());
						try {
							processarDocumentPendentPortasignatures(id, portasignatures);
						} finally {
							if (procesCallbackHelper.isDocumentEnProces(portasignatures.getDocumentId()))
								procesCallbackHelper.eliminarDocument(portasignatures.getDocumentId());
						}
					}
					
					return true;
				} else if (PortafirmesEstatEnum.ESBORRAT.equals(portasignatures.getEstat())) {
					return true;
				} else if (PortafirmesEstatEnum.PROCESSAT.equals(portasignatures.getEstat())) {
					return true;
				} else {
					logger.error("El document rebut al callback (id=" + id + ") no està pendent del callback, el seu estat és " + portasignatures.getEstat());
				}
			} else {
				logger.error("El document rebut al callback (id=" + id + ") no s'ha trobat entre els documents enviats al portasignatures");
			}
		} catch (Exception ex) {
			logger.error("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat: " + ex.getMessage());
			logger.debug("El document rebut al callback (id=" + id + ") ha produit una excepció al ser processat", ex);
		}
		return false;
	}
	
	@Override
	@Transactional
	public boolean processarDocumentPendentPortasignatures(Integer id) {
		Portasignatures portasignatures = portasignaturesRepository.findByDocumentId(id);
		return processarDocumentPendentPortasignatures(id, portasignatures);
	}
	
	private boolean processarDocumentPendentPortasignatures(
			Integer id,
			Portasignatures portasignatures) {
		boolean resposta = false;
		if (portasignatures != null) {
			if (portasignatures.getDataProcessamentPrimer() == null)
				portasignatures.setDataProcessamentPrimer(new Date());
			portasignatures.setDataProcessamentDarrer(new Date());
			DocumentStore documentStore = documentStoreRepository.findOne(portasignatures.getDocumentStoreId());
			Long tokenId = portasignatures.getTokenId();
			//JbpmToken token;
			String processInstanceId;
			if (tokenId != null) {
				//token = jbpmDao.getTokenById(tokenId.toString());
				//processInstanceId = token.getProcessInstanceId();
			} else {
				//token = null;
				processInstanceId = documentStore.getProcessInstanceId();
			}
			
			if (documentStore != null) {
				if (PortafirmesEstatEnum.SIGNAT.equals(portasignatures.getEstat()) ||
					(PortafirmesEstatEnum.ERROR.equals(portasignatures.getEstat()) && Transicio.SIGNAT.equals(portasignatures.getTransition()))) {
					// Processa els documents signats
					try {
						ThreadLocalInfo.clearProcessInstanceFinalitzatIds();
//						expedientLogHelper.afegirLogExpedientPerProces(
//								processInstanceId,
//								ExpedientLogAccioTipus.PROCES_DOCUMENT_PORTAFIRMES,
//								new Boolean(true).toString());
						if (portasignatures.getDataSignalIntent() == null)
							portasignatures.setDataSignalIntent(new Date());
						portasignatures.setDataSignalOk(new Date());
						portasignatures.setEstat(PortafirmesEstatEnum.PROCESSAT);
						
						// Guarda el document
						if (portasignatures.getDataCustodiaIntent() == null) {
							portasignatures.setDataCustodiaIntent(new Date());
						}
//						afegirDocumentCustodia(
//								portasignatures.getDocumentId(),
//								documentStore);
						portasignatures.setDataCustodiaOk(new Date());
						Expedient expedient = portasignatures.getExpedient();
//						if (token != null
//								&& ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
//							// Avança el flux
//							jbpmDao.signalToken(
//									tokenId.longValue(),
//									portasignatures.getTransicioOK());
//							
//							//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
//							// ha anat bé, es possible que s'avanci cap al node "fi"
//							expedientHelper.verificarFinalitzacioExpedient(
//									expedient);
//							
//							// Reindexa els possibles canvis
//							getServiceUtils().expedientIndexLuceneUpdate(
//									token.getProcessInstanceId());
//						}
						resposta = true;
					} catch (Exception ex) {
//						errorProcesPsigna(
//								portasignatures,
//								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document firmat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					portasignaturesRepository.save(portasignatures);
				} else if (PortafirmesEstatEnum.REBUTJAT.equals(portasignatures.getEstat()) ||
						(PortafirmesEstatEnum.ERROR.equals(portasignatures.getEstat()) && Transicio.REBUTJAT.equals(portasignatures.getTransition()))) {
					// Processa els documents rebujats
					try {
//						expedientLogHelper.afegirLogExpedientPerProces(
//								processInstanceId,
//								ExpedientLogAccioTipus.PROCES_DOCUMENT_PORTAFIRMES,
//								new Boolean(false).toString());
//						if (token != null) {
//							
//							JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(
//									token.getProcessInstanceId());
//							Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
//							
//							if (ExpedientTipusTipusEnumDto.FLOW.equals(expedient.getTipus().getTipus())) {
//							
//								jbpmDao.signalToken(
//										tokenId.longValue(),
//										portasignatures.getTransicioKO());
//	
//								//Actualitzem l'estat de l'expedient, ja que si tot el procés de firma i de custòdia
//								// ha anat malament també és possible que s'avanci cap al node "fi"
//								expedientHelper.verificarFinalitzacioExpedient(
//										expedient);
//								
//								// Reindexa els possibles canvis
//								getServiceUtils().expedientIndexLuceneUpdate(
//										token.getProcessInstanceId());
//							}
//						}
						portasignatures.setEstat(PortafirmesEstatEnum.PROCESSAT);
						portasignatures.setErrorCallbackProcessant(null);
						resposta = true;
					} catch (Exception ex) {
//						errorProcesPsigna(
//								portasignatures,
//								exceptionHelper.getMissageFinalCadenaExcepcions(ex));
						logger.error("Error al processar el document rebutjat pel callback (id=" + portasignatures.getDocumentId() + ")", ex);
					}
					portasignaturesRepository.save(portasignatures);
				} else {
					String error = "El document de portasignatures (id=" + portasignatures.getDocumentId() + ") no està pendent de processar, està en estat " + portasignatures.getEstat().toString();
//					errorProcesPsigna(
//							portasignatures,
//							error);
					logger.error(error);
				}
			} else {
				String error = "El document rebut al callback (id=" + portasignatures.getDocumentId() + ") fa referència a un documentStore inexistent (id=" + portasignatures.getDocumentStoreId() + ")";
//				errorProcesPsigna(
//						portasignatures,
//						error);
				logger.error(error);
			}
			List<Portasignatures> ambErrors = new ArrayList<Portasignatures>(); //pluginPortasignaturesDao.findAmbErrorsPerExpedientId(portasignatures.getExpedient().getId());
			if (ambErrors.size() > 0)
				portasignatures.getExpedient().setErrorsIntegracions(true);
			else
				portasignatures.getExpedient().setErrorsIntegracions(false);
		} else {
			logger.error("El document de portasignatures (id=" + id + ") no s'ha trobat");
		}
		return resposta;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PortasignaturesServiceImpl.class);
}