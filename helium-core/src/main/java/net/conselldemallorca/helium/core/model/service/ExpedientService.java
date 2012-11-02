/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.AccioDao;
import net.conselldemallorca.helium.core.model.dao.AreaJbpmIdDao;
import net.conselldemallorca.helium.core.model.dao.AreaMembreDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.dao.EstatDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientLogDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.PluginCustodiaDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginGisDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.PluginPortasignaturesDao;
import net.conselldemallorca.helium.core.model.dao.PluginSignaturaDao;
import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.core.model.dao.RegistreDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.core.model.dto.DadesDocumentDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientConsultaDissenyDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientLogDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.PortasignaturesDto;
import net.conselldemallorca.helium.core.model.dto.PortasignaturesPendentDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TokenDto;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.exception.ExpedientRepetitException;
import net.conselldemallorca.helium.core.model.exception.IllegalArgumentsException;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.AreaMembre;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore.DocumentFont;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogEstat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.LogInfo;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.TipusEstat;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures.Transicio;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.security.acl.AclServiceDao;
import net.conselldemallorca.helium.core.security.permission.ExtendedPermission;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.gis.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarEventRequest;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.PublicarExpedientRequest;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmNodePosition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmToken;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.Authentication;
import org.springframework.security.acls.Permission;
import org.springframework.security.annotation.Secured;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per a gestionar els expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientService {

	private ExpedientDao expedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private DefinicioProcesDao definicioProcesDao;
	private EntornDao entornDao;
	private DocumentStoreDao documentStoreDao;
	private EstatDao estatDao;
	private LuceneDao luceneDao;
	private ConsultaDao consultaDao;
	private CampDao campDao;
	private ConsultaCampDao consultaCampDao;
	private PluginCustodiaDao pluginCustodiaDao;
	private RegistreDao registreDao;
	private AccioDao accioDao;
	private TerminiIniciatDao terminiIniciatDao;
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	private PluginPortasignaturesDao pluginPortasignaturesDao;
	private PluginTramitacioDao pluginTramitacioDao;
	private PluginSignaturaDao pluginSignaturaDao;
	private PluginPersonaDao pluginPersonaDao;
	private PluginGisDao pluginGisDao;
	private ExpedientLogDao expedientLogDao;
	private AreaMembreDao areaMembreDao;
	private AreaJbpmIdDao areaJbpmIdDao;

	private JbpmDao jbpmDao;
	private AclServiceDao aclServiceDao;
	private DtoConverter dtoConverter;
	private MessageSource messageSource;

	private DocumentHelper documentHelper;
	private ExpedientLogHelper expedientLogHelper;

	private ServiceUtils serviceUtils;



	public ExpedientDto getById(Long id) {
		Expedient expedient = expedientDao.getById(id, false);
		if (expedient != null)
			return dtoConverter.toExpedientDto(expedient, false);
		return null;
	}

	public TascaDto getStartTask(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			Map<String, Object> valors) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		if (definicioProcesId == null && definicioProces == null) {
			logger.error("No s'ha trobat la definició de procés (entorn=" + entornId + ", jbpmKey=" + expedientTipus.getJbpmProcessDefinitionKey() + ")");
		}
		String startTaskName = jbpmDao.getStartTaskName(definicioProces.getJbpmId());
		if (startTaskName != null) {
			return dtoConverter.toTascaInicialDto(startTaskName, definicioProces.getJbpmId(), valors);
		}
		return null;
	}

	public synchronized ExpedientDto iniciar(
			Long entornId,
			String usuari,
			Long expedientTipusId,
			Long definicioProcesId,
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
			IniciadorTipus iniciadorTipus,
			String iniciadorCodi,
			String responsableCodi,
			Map<String, DadesDocumentDto> documents,
			List<DadesDocumentDto> adjunts) {
		if (usuari != null)
			comprovarUsuari(usuari);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = (usuari != null) ? usuari : auth.getName();
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		Entorn entorn = entornDao.getById(entornId, false);
		String iniciadorCodiCalculat = (iniciadorTipus.equals(IniciadorTipus.INTERN)) ? usuariBo : iniciadorCodi;
		Expedient expedient = new Expedient(
				iniciadorTipus,
				iniciadorCodiCalculat,
				expedientTipus,
				entorn,
				UUID.randomUUID().toString());
		String responsableCodiCalculat = (responsableCodi != null) ? responsableCodi : expedientTipus.getResponsableDefecteCodi();
		if (responsableCodiCalculat == null)
			responsableCodiCalculat = iniciadorCodiCalculat;
		expedient.setResponsableCodi(responsableCodiCalculat);
		expedient.setRegistreNumero(registreNumero);
		expedient.setRegistreData(registreData);
		expedient.setUnitatAdministrativa(unitatAdministrativa);
		expedient.setIdioma(idioma);
		expedient.setAutenticat(autenticat);
		expedient.setTramitadorNif(tramitadorNif);
		expedient.setTramitadorNom(tramitadorNom);
		expedient.setInteressatNif(interessatNif);
		expedient.setInteressatNom(interessatNom);
		expedient.setRepresentantNif(representantNif);
		expedient.setRepresentantNom(representantNom);
		expedient.setAvisosHabilitats(avisosHabilitats);
		expedient.setAvisosEmail(avisosEmail);
		expedient.setAvisosMobil(avisosMobil);
		expedient.setNotificacioTelematicaHabilitada(notificacioTelematicaHabilitada);
		expedient.setNumeroDefault(
				expedientTipusDao.getNumeroExpedientDefaultActual(expedientTipusId));
		if (expedientTipus.getTeNumero()) {
			if (numero != null && numero.length() > 0) {
				expedient.setNumero(numero);
			} else {
				expedient.setNumero(
						getNumeroExpedientActual(entornId, expedientTipusId));
			}
		}
		// Verifica si l'expedient té el número repetit
		if (expedientDao.findAmbEntornTipusINumero(
				entornId,
				expedientTipusId,
				expedient.getNumero()) != null) {
			throw new ExpedientRepetitException(
					getServiceUtils().getMessage(
							"error.expedientService.jaExisteix",
							new Object[]{expedient.getNumero()}) );
		}
		// Actualitza l'any actual de l'expedient
		int any = Calendar.getInstance().get(Calendar.YEAR);
		if (expedientTipus.getAnyActual() == 0) {
			expedientTipus.setAnyActual(any);
		} else if (expedientTipus.getAnyActual() != any) {
			if (expedientTipus.isReiniciarCadaAny())
				expedientTipus.setSequencia(1);
			expedientTipus.setSequenciaDefault(1);
			expedientTipus.setAnyActual(any);
		}
		// Actualitza la seqüència del número d'expedient
		if (expedientTipus.getExpressioNumero() != null && !"".equals(expedientTipus.getExpressioNumero())) {
			if (expedient.getNumero().equals(getNumeroExpedientActual(entornId, expedientTipusId)))
				expedientTipus.setSequencia(expedientTipus.getSequencia() + 1);
		}
		// Actualitza la seqüència del número d'expedient per defecte
		if (expedient.getNumeroDefault().equals(expedientTipusDao.getNumeroExpedientDefaultActual(expedientTipusId)))
			expedientTipus.setSequenciaDefault(expedientTipus.getSequenciaDefault() + 1);
		// Configura el títol de l'expedient
		if (expedientTipus.getTeTitol()) {
			if (titol != null && titol.length() > 0)
				expedient.setTitol(titol);
			else
				expedient.setTitol("[Sense títol]");
		}
		// Inicia l'instància de procés jBPM
		ExpedientIniciantDto.setExpedient(expedient);
		DefinicioProces definicioProces = null;
		if (definicioProcesId != null) {
			definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		} else {
			definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					expedientTipus.getJbpmProcessDefinitionKey());
		}
		JbpmProcessInstance processInstance = jbpmDao.startProcessInstanceById(
				usuariBo,
				definicioProces.getJbpmId(),
				variables);
		expedient.setProcessInstanceId(processInstance.getId());
		// Emmagatzema el nou expedient
		expedientDao.saveOrUpdate(expedient);
		// Afegim els documents
		if (documents != null){
			for (Map.Entry<String, DadesDocumentDto> doc: documents.entrySet()) {
				if (doc.getValue() != null) {
					documentHelper.actualitzarDocument(
							null,
							expedient.getProcessInstanceId(),
							doc.getValue().getCodi(), 
							null,
							doc.getValue().getData(), 
							doc.getValue().getArxiuNom(), 
							doc.getValue().getArxiuContingut(),
							false);
				}
			}
		}
		// Afegim els adjunts
		if (adjunts != null) {
			for (DadesDocumentDto adjunt: adjunts) {
				String documentCodi = new Long(new Date().getTime()).toString();
				documentHelper.actualitzarDocument(
						null,
						expedient.getProcessInstanceId(),
						documentCodi,
						adjunt.getTitol(),
						adjunt.getData(), 
						adjunt.getArxiuNom(), 
						adjunt.getArxiuContingut(),
						true);
			}
		}
		// Verificar la ultima vegada que l'expedient va modificar el seu estat
		ExpedientLog log = expedientLogHelper.afegirLogExpedientPerProces(
				processInstance.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_INICIAR,
				null);
		log.setEstat(ExpedientLogEstat.IGNORAR);
		// Actualitza les variables del procés
		jbpmDao.signalProcessInstance(expedient.getProcessInstanceId(), transitionName);
		// Indexam l'expedient
		getServiceUtils().expedientIndexLuceneCreate(expedient.getProcessInstanceId());
		// Registra l'inici de l'expedient
		registreDao.crearRegistreIniciarExpedient(
				expedient.getId(),
				usuariBo);
		// Retorna la informació de l'expedient que s'ha iniciat
		ExpedientDto dto = dtoConverter.toExpedientDto(expedient, true);
		return dto;
	}

	public String getNumeroExpedientActual(
			Long entornId,
			Long expedientTipusId) {
		long increment = 0;
		String numero = null;
		Expedient expedient = null;
		do {
			numero = expedientTipusDao.getNumeroExpedientActual(
					expedientTipusId,
					increment);
			expedient = expedientDao.findAmbEntornTipusINumero(
					entornId,
					expedientTipusId,
					numero);
			increment++;
		} while (expedient != null);
		return numero;
	}

	public void editar(
			Long entornId,
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
			String grupCodi) {
		editar(entornId,
				id,
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
				false);
	}
	public void editar(
			Long entornId,
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
			boolean executatEnHandler) {
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		
		if (!executatEnHandler) {
			ExpedientLog elog = expedientLogHelper.afegirLogExpedientPerExpedient(
				id,
				ExpedientLogAccioTipus.EXPEDIENT_MODIFICAR,
				null);
			elog.setEstat(ExpedientLogEstat.IGNORAR);
		}

		String informacioVella = getInformacioExpedient(expedient);
		
		// Numero
		if (expedient.getTipus().getTeNumero()) {
			if (!StringUtils.equals(expedient.getNumero(), numero)) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.NUMERO + "#@#" + expedient.getNumero());
				expedient.setNumero(numero);
			}
		}
		// Titol
		if (expedient.getTipus().getTeTitol()) {
			if (!StringUtils.equals(expedient.getTitol(), titol)) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.TITOL + "#@#" + expedient.getTitol());
				expedient.setTitol(titol);
			}
		}
		// Responsable
		if (!StringUtils.equals(expedient.getResponsableCodi(), responsableCodi)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.RESPONSABLE + "#@#" + expedient.getResponsableCodi());
			expedient.setResponsableCodi(responsableCodi);
		}
		// Data d'inici
		if (expedient.getDataInici() != dataInici) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String inici = sdf.format(dataInici);
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.INICI + "#@#" + inici);
			expedient.setDataInici(dataInici);
		}
		// Comentari
		if (!StringUtils.equals(expedient.getComentari(), comentari)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.COMENTARI + "#@#" + expedient.getComentari());
			expedient.setComentari(comentari);
		}
		// Estat
		if (estatId != null) {
			if (expedient.getEstat() == null || expedient.getEstat().getId() != estatId) {
				expedientLogHelper.afegirProcessLogInfoExpedient(
						expedient.getProcessInstanceId(), 
						LogInfo.ESTAT + "#@#" + "---");
				expedient.setEstat(estatDao.getById(estatId, false));
			}
		} else if (expedient.getEstat() != null) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.ESTAT + "#@#" + expedient.getEstat().getId());
			expedient.setEstat(null);
		}
		// Geoposició
		if (expedient.getGeoPosX() != geoPosX) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOX + "#@#" + expedient.getGeoPosX());
			expedient.setGeoPosX(geoPosX);
		}
		if (expedient.getGeoPosY() != geoPosY) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOPOSICIOY + "#@#" + expedient.getGeoPosY());
			expedient.setGeoPosY(geoPosY);
		}
		// Georeferencia
		if (!StringUtils.equals(expedient.getGeoReferencia(), geoReferencia)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GEOREFERENCIA + "#@#" + expedient.getGeoReferencia());
			expedient.setGeoReferencia(geoReferencia);
		}
		// Grup
		if (!StringUtils.equals(expedient.getGrupCodi(), grupCodi)) {
			expedientLogHelper.afegirProcessLogInfoExpedient(
					expedient.getProcessInstanceId(), 
					LogInfo.GRUP + "#@#" + expedient.getGrupCodi());
			expedient.setGrupCodi(grupCodi);
		}
		
		luceneDao.updateExpedientCapsalera(
				expedient,
				getServiceUtils().isExpedientFinalitzat(expedient));
		String informacioNova = getInformacioExpedient(expedient);
		registreDao.crearRegistreModificarExpedient(
				expedient.getId(),
				getUsuariPerRegistre(),
				informacioVella,
				informacioNova);
	}
	public void delete(Long entornId, Long id) {
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		if (expedient != null) {
			List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
			for (JbpmProcessInstance pi: processInstancesTree)
				for (TerminiIniciat ti: terminiIniciatDao.findAmbProcessInstanceId(pi.getId()))
					terminiIniciatDao.delete(ti);
			jbpmDao.deleteProcessInstance(expedient.getProcessInstanceId());
			for (DocumentStore documentStore: documentStoreDao.findAmbProcessInstanceId(expedient.getProcessInstanceId())) {
				if (documentStore.isSignat()) {
					try {
						pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
					} catch (Exception ignored) {}
				}
				if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
					pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
				documentStoreDao.delete(documentStore.getId());
			}
			for (Portasignatures psigna: expedient.getPortasignatures()) {
				psigna.setEstat(TipusEstat.ESBORRAT);
			}
			expedientDao.delete(expedient);
			luceneDao.deleteExpedient(expedient);
			registreDao.crearRegistreEsborrarExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		} else {
			throw new NotFoundException(getServiceUtils().getMessage("error.expedientService.noExisteix"));
		}
	}
	public void anular(Long entornId, Long id, String motiu) { 
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		if (expedient != null) {
			List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
			String[] ids = new String[processInstancesTree.size()];
			int i = 0;
			for (JbpmProcessInstance pi: processInstancesTree)
				ids[i++] = pi.getId();
			jbpmDao.suspendProcessInstances(ids);
			expedient.setAnulat(true);
			expedient.setComentariAnulat(motiu);
			luceneDao.deleteExpedient(expedient);
			registreDao.crearRegistreAnularExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());
		} else {
			throw new NotFoundException(getServiceUtils().getMessage("error.expedientService.noExisteix"));
		}
	}
	
	public void desanular(Long entornId, Long id) {
		Expedient expedient = expedientDao.findAmbEntornIId(entornId, id);
		if (expedient != null) {
			List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(expedient.getProcessInstanceId());
			String[] ids = new String[processInstancesTree.size()];
			int i = 0;
			for (JbpmProcessInstance pi: processInstancesTree)
				ids[i++] = pi.getId();
			jbpmDao.resumeProcessInstances(ids);
			expedient.setAnulat(false);
			//luceneDao.deleteExpedient(expedient);
			/*registreDao.crearRegistreReprendreExpedient(
					expedient.getId(),
					SecurityContextHolder.getContext().getAuthentication().getName());*/
		} else {
			throw new NotFoundException(getServiceUtils().getMessage("error.expedientService.noExisteix"));
		}
	}
	
	
	
	public List<ExpedientDto> findAmbEntorn(Long entornId) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Expedient expedient: expedientDao.findAmbEntorn(entornId))
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		return resposta;
	}

	public int countAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats) {
		return expedientDao.countAmbEntornConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				getExpedientTipusIdPermesos(entornId),
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				getAreesOGrupsPerUsuari());
	}
	public List<ExpedientDto> findAmbEntornConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Expedient expedient: expedientDao.findAmbEntornConsultaGeneral(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				getExpedientTipusIdPermesos(entornId),
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				getAreesOGrupsPerUsuari()))
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		return resposta;
	}
	public List<ExpedientDto> findAmbEntornConsultaGeneralPaginat(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia,
			boolean mostrarAnulats,
			int firstRow,
			int maxResults,
			String sort,
			boolean asc) {
		List<ExpedientTipus> tipus = expedientTipusDao.findAmbEntorn(entornId);
		getServiceUtils().filterAllowed(
						tipus,
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ});
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (Expedient expedient: expedientDao.findAmbEntornConsultaGeneralPagedAndOrdered(
				entornId,
				titol,
				numero,
				dataInici1,
				dataInici2,
				expedientTipusId,
				getExpedientTipusIdPermesos(entornId),
				estatId,
				iniciat,
				finalitzat,
				geoPosX,
				geoPosY,
				geoReferencia,
				mostrarAnulats,
				getAreesOGrupsPerUsuari(),
				firstRow,
				maxResults,
				sort,
				asc))
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		return resposta;
	}
	public List<PortasignaturesPendentDto> findAmbEntornPendentPsigna(
			Long entornId) {
		List<Portasignatures> pendents = pluginPortasignaturesDao.findPendents();
		List<PortasignaturesPendentDto> resposta = new ArrayList<PortasignaturesPendentDto>();
		for (Portasignatures pendent: pendents) {
			DocumentStore docStore = documentStoreDao.getById(pendent.getDocumentStoreId(), false);
			if (docStore != null) {
				Expedient expedient = expedientDao.findAmbProcessInstanceId(docStore.getProcessInstanceId());
				if (expedient != null && expedient.getEntorn().getId().equals(entornId)) {
					if (getServiceUtils().filterAllowed(
							expedient.getTipus(),
							ExpedientTipus.class,
							new Permission[] {
								ExtendedPermission.ADMINISTRATION,
								ExtendedPermission.READ}) != null) {
						PortasignaturesPendentDto dto = new PortasignaturesPendentDto();
						dto.setId(pendent.getId());
						dto.setPortasignaturesId(pendent.getDocumentId());
						dto.setEstat(pendent.getEstat().toString());
						dto.setDataEnviat(pendent.getDataEnviat());
						dto.setDataCallbackPrimer(pendent.getDataCallbackPrimer());
						dto.setDataCallbackDarrer(pendent.getDataCallbackDarrer());
						dto.setErrorCallback(pendent.getErrorCallbackProcessant());
						dto.setExpedient(dtoConverter.toExpedientDto(expedient, false));
						DocumentDto document = documentHelper.getDocumentSenseContingut(pendent.getDocumentStoreId());
						dto.setDocument(document);
						resposta.add(dto);
					}
				}
			}
		}
		return resposta;
	}
	public List<ExpedientDto> findAmbEntornLikeIdentificador(
			Long entornId,
			String text) {
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		List<Expedient> expedients = expedientDao.findAmbEntornLikeIdentificador(entornId, text);
		for (Expedient expedient: expedients) {
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		}
		return resposta;
	}

	public List<ExpedientDto> findAmbDefinicioProcesId(Long definicioProcesId) {
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		List<ExpedientDto> resposta = new ArrayList<ExpedientDto>();
		for (JbpmProcessInstance pi: jbpmDao.findProcessInstancesWithProcessDefinitionId(definicioProces.getJbpmId())) {
			Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
			resposta.add(dtoConverter.toExpedientDto(expedient, false));
		}
		return resposta;
	}
	public int countAmbExpedientTipusId(Long expedientTipusId) {
		return expedientDao.countAmbExpedientTipusId(expedientTipusId);
	}
	public ExpedientDto findExpedientAmbEntornTipusITitol(
			Long entornId,
			Long expedientTipusId,
			String titol) {
		Expedient expedient = expedientDao.findAmbEntornTipusITitol(entornId, expedientTipusId, titol);
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}
	public ExpedientDto findExpedientAmbEntornTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		Expedient expedient = expedientDao.findAmbEntornTipusINumero(entornId, expedientTipusId, numero);
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}
	public ExpedientDto findExpedientAmbProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		if (pi == null)
			return null;
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		if (expedient == null)
			return null;
		return dtoConverter.toExpedientDto(expedient, false);
	}

	public int countAmbEntornConsultaDisseny(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors) {
		Consulta consulta = consultaDao.getById(consultaId, false);
		List<Camp> campsFiltre = getServiceUtils().findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		List<Long> idExpedients = luceneDao.findNomesIds(
				consulta.getExpedientTipus().getCodi(),
				campsFiltre,
				valors);
		return idExpedients.size();
	}
	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors,
			String sort,
			boolean asc) {
		return findAmbEntornConsultaDisseny(
				entornId,
				consultaId,
				valors,
				sort,
				asc,
				0,
				-1);
	}
	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
			Long entornId,
			Long consultaId,
			Map<String, Object> valors,
			String sort,
			boolean asc,
			int firstRow,
			int maxResults) {
		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
		Consulta consulta = consultaDao.getById(consultaId, false);
		List<Camp> campsFiltre = getServiceUtils().findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.FILTRE);
		List<Camp> campsInforme = getServiceUtils().findCampsPerCampsConsulta(
				consulta,
				TipusConsultaCamp.INFORME);
		afegirValorsPredefinits(consulta, valors, campsFiltre);
		List<Map<String, DadaIndexadaDto>> dadesExpedients = luceneDao.findAmbDadesExpedient(
				consulta.getExpedientTipus().getCodi(),
				campsFiltre,
				valors,
				campsInforme,
				sort,
				asc,
				firstRow,
				maxResults);
		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneDao.CLAU_EXPEDIENT_ID);
			Long expedientId = new Long(dadaExpedientId.getValorIndex());
			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
			Expedient expedient = expedientDao.getById(expedientId, false);
			if (expedient != null) {
				fila.setExpedient(
						dtoConverter.toExpedientDto(
								expedient,
								false));
				fila.setDadesExpedient(dadesExpedient);
				resposta.add(fila);
			}
			dadesExpedient.remove(LuceneDao.CLAU_EXPEDIENT_ID);
		}
		return resposta;
	}

	public List<TascaDto> findTasquesPerInstanciaProces(
			String processInstanceId,
			boolean ambVariables) {
		List<TascaDto> resposta = new ArrayList<TascaDto>();
		List<JbpmTask> tasks = jbpmDao.findTaskInstancesForProcessInstance(processInstanceId);
		for (JbpmTask task: tasks)
			resposta.add(dtoConverter.toTascaDto(task, null, ambVariables, true, true, true, true));
		Collections.sort(resposta);
		return resposta;
	}

	public InstanciaProcesDto getInstanciaProcesById(
			String processInstanceId,
			boolean ambImatgeProces,
			boolean ambVariables,
			boolean ambDocuments) {
		return dtoConverter.toInstanciaProcesDto(processInstanceId, ambImatgeProces, ambVariables, ambDocuments);
	}
	public List<InstanciaProcesDto> getArbreInstanciesProces(
			String processInstanceId) {
		List<InstanciaProcesDto> resposta = new ArrayList<InstanciaProcesDto>();
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		List<JbpmProcessInstance> piTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		for (JbpmProcessInstance jpi: piTree) {
			resposta.add(dtoConverter.toInstanciaProcesDto(jpi.getId(), false, false, false));
		}
		return resposta;
	}

	public List<Map<String, DadaIndexadaDto>> luceneGetDades(String processInstanceId) {
		return getServiceUtils().expedientIndexLucenGetDades(processInstanceId);
	}
	public void luceneUpdateIndexExpedient(String processInstanceId) {
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
	}
	public void luceneReindexarExpedient(String processInstanceId) {
		getServiceUtils().expedientIndexLuceneRecrear(processInstanceId);
	}

	@Secured({"ROLE_ADMIN"})
	public void luceneReindexarEntorn(Long entornId) {
		Entorn entorn = entornDao.getById(entornId, false);
		logger.info("Reindexant els expedients de l'entorn " + entorn.getNom());
		for (Expedient expedient: expedientDao.findAmbEntorn(entornId)) {
			try {
				if (!expedient.isAnulat()) {
					logger.info("Reindexant expedient " + expedient.getIdentificador() + "...");
					luceneReindexarExpedient(expedient.getProcessInstanceId());
				}
			} catch (Exception ex) {
				logger.error("Error al reindexar l'expedient " + expedient.getIdentificador(), ex);
			}
		}
	}

	public Entorn getEntornAmbProcessInstanceId(String processInstanceId) {
		Expedient expedient = expedientDao.findAmbProcessInstanceId(processInstanceId);
		if (expedient != null)
			return expedient.getEntorn();
		return null;
	}

	public Object getVariable(String processInstanceId, String varName) {
		return getServiceUtils().getVariableJbpmProcesValor(processInstanceId, varName);
	}
	public void createVariable(String processInstanceId, String varName, Object value) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varName);
		jbpmDao.setProcessInstanceVariable(processInstanceId, varName, value);
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
		registreDao.crearRegistreCrearVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName,
				value);
	}
	public void updateVariable(
			String processInstanceId,
			String varName,
			Object value) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varName);
		Object valorVell = getServiceUtils().getVariableJbpmProcesValor(processInstanceId, varName);
		Object valorOptimitzat = optimitzarValorPerConsultesDomini(
				processInstanceId,
				varName,
				value);
		jbpmDao.setProcessInstanceVariable(processInstanceId, varName, valorOptimitzat);
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
		registreDao.crearRegistreModificarVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName,
				valorVell,
				value);
	}
	public void deleteVariable(String processInstanceId, String varName) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR,
				varName);
		jbpmDao.deleteProcessInstanceVariable(processInstanceId, varName);
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
		registreDao.crearRegistreEsborrarVariableInstanciaProces(
				getExpedientPerProcessInstanceId(processInstanceId).getId(),
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				varName);
	}

	public void guardarRegistre(
			String processInstanceId,
			String campCodi,
			Object[] valors) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				campCodi);
		guardarRegistre(
				processInstanceId,
				campCodi,
				valors,
				-1);
	}
	public void guardarRegistre(
			String processInstanceId,
			String campCodi,
			Object[] valors,
			int index) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				campCodi);
		Object valor = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
		if (valor == null) {
			jbpmDao.setProcessInstanceVariable(
					processInstanceId,
					campCodi,
					new Object[]{valors});
		} else {
			Object[] valorMultiple = (Object[])valor;
			if (index != -1) {
				valorMultiple[index] = valors;
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valor);
			} else {
				Object[] valorNou = new Object[valorMultiple.length + 1];
				for (int i = 0; i < valorMultiple.length; i++)
					valorNou[i] = valorMultiple[i];
				valorNou[valorMultiple.length] = valors;
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valorNou);
			}
		}
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
	}
	public void esborrarRegistre(
			String processInstanceId,
			String campCodi,
			int index) {
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				campCodi);
		Object valor = jbpmDao.getProcessInstanceVariable(processInstanceId, campCodi);
		if (valor != null) {
			Object[] valorMultiple = (Object[])valor;
			if (valorMultiple.length > 0) {
				Object[] valorNou = new Object[valorMultiple.length - 1];
				for (int i = 0; i < valorNou.length; i++)
					valorNou[i] = (i < index) ? valorMultiple[i] : valorMultiple[i + 1];
				jbpmDao.setProcessInstanceVariable(
						processInstanceId,
						campCodi,
						valorNou);
			}
		}
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
	}

	/*public Long guardarDocument(
			String processInstanceId,
			Long documentId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		Document document = documentDao.getById(documentId, false);
		return createUpdateDocument(
				processInstanceId,
				document.getCodi(),
				document.getNom(),
				DocumentHelper.PREFIX_VAR_DOCUMENT + document.getCodi(),
				data,
				arxiuNom,
				arxiuContingut,
				false,
				false,
				null,
				null,
				null,
				null,
				false);
	}
	public Long guardarDocumentAmbDadesRegistre(
			String processInstanceId,
			Long documentId,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean registrat,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		Document document = documentDao.getById(documentId, false);
		return createUpdateDocument(
				processInstanceId,
				document.getCodi(),
				document.getNom(),
				DocumentHelper.PREFIX_VAR_DOCUMENT + document.getCodi(),
				data,
				arxiuNom,
				arxiuContingut,
				false,
				registrat,
				registreNumero,
				registreData,
				registreOficinaCodi,
				registreOficinaNom,
				registreEntrada);
		
	}*/

	/*public DocumentDto generarDocumentPlantilla(
			Long documentId,
			String processInstanceId,
			Date dataDocument) {
		return generarDocumentPlantilla(
				documentId,
				processInstanceId,
				dataDocument,
				false);
	}
	public DocumentDto generarDocumentPlantilla(
			Long documentId,
			String processInstanceId,
			Date dataDocument,
			boolean guardarAuto) {
		Document document = documentDao.getById(documentId, false);
		DocumentDto resposta = new DocumentDto();
		resposta.setDataCreacio(new Date());
		resposta.setDataDocument(new Date());
		resposta.setArxiuNom(document.getNom() + ".odt");
		if (document.isPlantilla()) {
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			ExpedientDto expedient = dtoConverter.toExpedientDto(
					expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId()),
					false);
			InstanciaProcesDto instanciaProces = dtoConverter.toInstanciaProcesDto(
					processInstanceId,
					true);
			Map<String, Object> model = new HashMap<String, Object>();
			model.putAll(instanciaProces.getVarsComText());
			try {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				byte[] resultat = plantillaDocumentDao.generarDocumentAmbPlantilla(
						expedient.getEntorn().getId(),
						document,
						auth.getName(),
						expedient,
						processInstanceId,
						null,
						dataDocument,
						model);
				resposta.setArxiuContingut(resultat);
				if (isActiuConversioVista()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getOpenOfficeUtils().convertir(
							resposta.getArxiuNom(),
							resultat,
							getExtensioVista(),
							baos);
					resposta.setArxiuNom(
							nomArxiuAmbExtensio(
									resposta.getArxiuNom(),
									getExtensioVista()));
					resposta.setArxiuContingut(baos.toByteArray());
				} else {
					resposta.setArxiuContingut(resultat);
				}
				if (guardarAuto) {
					guardarDocument(
							processInstanceId,
							documentId,
							dataDocument,
							resposta.getArxiuNom(),
							resposta.getArxiuContingut());
				}
			} catch (Exception ex) {
				throw new TemplateException(
						getServiceUtils().getMessage("error.expedientService.generarDocument"), ex);
			}
		} else {
			resposta.setArxiuContingut(document.getArxiuContingut());
		}
		return resposta;
	}*/

	/*public Long guardarAdjunt(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		String adId = (adjuntId == null) ? new Long(new Date().getTime()).toString() : adjuntId;
		return createUpdateDocument(
				processInstanceId,
				adId,
				adjuntTitol,
				DocumentHelper.PREFIX_ADJUNT + adId,
				data,
				arxiuNom,
				arxiuContingut,
				true,
				false,
				null,
				null,
				null,
				null,
				false);
	}*/
	/*public void deleteDocument(String processInstanceId, Long documentStoreId) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		if (documentStore != null) {
			String jbpmVariable = documentStore.getJbpmVariable();
			if (documentStore.isSignat())
				try {
					pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
				} catch (Exception ignored) {}
			if (documentStore.getFont().equals(DocumentFont.ALFRESCO))
				pluginGestioDocumentalDao.deleteDocument(documentStore.getReferenciaFont());
			documentStoreDao.delete(documentStoreId);
			jbpmDao.deleteProcessInstanceVariable(processInstanceId, jbpmVariable);
			JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
			Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
			registreDao.crearRegistreEsborrarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					getVarNameFromDocumentStore(documentStore));
		}
	}*/

	public void deleteSignatura(
			String processInstanceId,
			Long documentStoreId) {
		DocumentStore documentStore = documentStoreDao.getById(documentStoreId, false);
		if (documentStore != null) {
			if (documentStore.isSignat()) {
				try {
					pluginCustodiaDao.esborrarSignatures(documentStore.getReferenciaCustodia());
				} catch (Exception ignored) {}
				String jbpmVariable = documentStore.getJbpmVariable();
				documentStore.setReferenciaCustodia(null);
				documentStore.setSignat(false);
				JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
				Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
				registreDao.crearRegistreEsborrarSignatura(
						expedient.getId(),
						processInstanceId,
						SecurityContextHolder.getContext().getAuthentication().getName(),
						getVarNameFromDocumentStore(documentStore));
				List<JbpmTask> tasks = jbpmDao.findTaskInstancesForProcessInstance(processInstanceId);
				for (JbpmTask task: tasks) {
					jbpmDao.deleteTaskInstanceVariable(
							task.getId(),
							jbpmVariable);
				}
			}
		}
	}

	public List<TokenDto> getActiveTokens(String processInstanceId, boolean withNodePosition) {
		List<TokenDto> resposta = new ArrayList<TokenDto>();
		Map<String, JbpmToken> activeTokens = jbpmDao.getActiveTokens(processInstanceId);
		Map<String, JbpmNodePosition> positions = null;
		if (withNodePosition) {
			positions = getNodePositions(processInstanceId);
			int[] dimensions = getImageDimensions(processInstanceId);
			int minX = 0;
			int maxX = dimensions[0];
			int minY = 0;
			int maxY = dimensions[1];
			for (JbpmNodePosition position: positions.values()) {
				if (position.getPosX() < minX)
					minX = position.getPosX();
				if (position.getPosX() + position.getWidth() > maxX)
					maxX = position.getPosX() + position.getWidth();
				if (position.getPosY() < minY)
					minY = position.getPosY();
				if (position.getPosY() + position.getHeight() > maxY)
					maxY = position.getPosY() + position.getHeight();
			}
			if (minX < 0 || minY < 0) {
				for (JbpmNodePosition position: positions.values()) {
					position.setPosX(position.getPosX() - minX);
					position.setPosY(position.getPosY() - minY);
				}
			}
		}
		for (String tokenName: activeTokens.keySet()) {
			JbpmToken token = activeTokens.get(tokenName);
			TokenDto dto = toTokenDto(token);
			if (withNodePosition) {
				JbpmNodePosition position = positions.get(token.getNodeName());
				if (position != null) {
					dto.setNodePosX(position.getPosX());
					dto.setNodePosY(position.getPosY());
					dto.setNodeWidth(position.getWidth());
					dto.setNodeHeight(position.getHeight());
				}
			}
			resposta.add(dto);
		}
		return resposta;
	}

	public List<TokenDto> getAllTokens(String processInstanceId) {
		List<TokenDto> resposta = new ArrayList<TokenDto>();
		Map<String, JbpmToken> activeTokens = jbpmDao.getActiveTokens(processInstanceId);
		for (String tokenName: activeTokens.keySet()) {
			JbpmToken token = activeTokens.get(tokenName);
			TokenDto dto = toTokenDto(token);
			resposta.add(dto);
		}
		return resposta;
	}

	public void reassignarTasca(
			Long entornId,
			String taskId,
			String expression) {
		String previousActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
//		JbpmTask task = jbpmDao.getTaskById(taskId);
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerTasca(
				taskId,
				ExpedientLogAccioTipus.TASCA_REASSIGNAR,
				null);
		jbpmDao.reassignTaskInstance(taskId, expression);
		String currentActors = expedientLogHelper.getActorsPerReassignacioTasca(taskId);
		expedientLog.setAccioParams(previousActors + "::" + currentActors);
		registreDao.crearRegistreRedirigirTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				expression);
	}
	public void suspendreTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		expedientLogHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_SUSPENDRE,
				null);
		jbpmDao.suspendTaskInstance(taskId);
		registreDao.crearRegistreSuspendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void reprendreTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		expedientLogHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CONTINUAR,
				null);
		jbpmDao.resumeTaskInstance(taskId);
		registreDao.crearRegistreReprendreTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}
	public void cancelarTasca(
			Long entornId,
			String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		expedientLogHelper.afegirLogExpedientPerProces(
				task.getProcessInstanceId(),
				ExpedientLogAccioTipus.TASCA_CANCELAR,
				null);
		jbpmDao.cancelTaskInstance(taskId);
		registreDao.crearRegistreCancelarTasca(
				getExpedientPerTaskInstanceId(taskId).getId(),
				taskId,
				SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public List<FilaResultat> getResultatConsultaDomini(
			String processInstanceId,
			String campCodi,
			String textInicial) throws DominiException {
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
		return dtoConverter.getResultatConsultaDomini(
				definicioProces,
				null,
				processInstanceId,
				campCodi,
				textInicial,
				null);
	}

	public List<Registre> getRegistrePerExpedient(Long expedientId) {
		return registreDao.findAmbExpedientId(expedientId);	
	}

	public void aturar(
			String processInstanceId,
			String motiu,
			String usuari) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ATURAR,
				motiu);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmDao.suspendProcessInstances(ids);
		expedient.setInfoAturat(motiu);
		registreDao.crearRegistreAturarExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName(),
				motiu);
	}
	public void reprendre(
			String processInstanceId,
			String usuari) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedient.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_REPRENDRE,
				null);
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		List<JbpmProcessInstance> processInstancesTree = jbpmDao.getProcessInstanceTree(rootProcessInstance.getId());
		String[] ids = new String[processInstancesTree.size()];
		int i = 0;
		for (JbpmProcessInstance pi: processInstancesTree)
			ids[i++] = pi.getId();
		jbpmDao.resumeProcessInstances(ids);
		expedient.setInfoAturat(null);
		registreDao.crearRegistreReprendreExpedient(
				expedient.getId(),
				(usuari != null) ? usuari : SecurityContextHolder.getContext().getAuthentication().getName());
	}

	public List<String> findArrivingNodeNames(String tokenId) {
		return jbpmDao.findArrivingNodeNames(tokenId);
	}
	public void tokenRetrocedir(
			String tokenId,
			String nodeName,
			boolean cancelTasks) {
		JbpmToken token = jbpmDao.getTokenById(tokenId);
		String nodeNameVell = token.getNodeName();
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(token.getProcessInstanceId());
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		jbpmDao.tokenRedirect(new Long(tokenId).longValue(), nodeName, cancelTasks, true, false);
		registreDao.crearRegistreRedirigirToken(
				expedient.getId(),
				token.getProcessInstanceId(),
				SecurityContextHolder.getContext().getAuthentication().getName(),
				token.getFullName(),
				nodeNameVell,
				nodeName);
	}

	public Object evaluateScript(
			String processInstanceId,
			String script,
			String outputVar) {
		Set<String> outputVars = new HashSet<String>();
		if (outputVar != null)
			outputVars.add(outputVar);
		Map<String, Object> output =  jbpmDao.evaluateScript(processInstanceId, script, outputVars);
		actualitzarDataFiExpedient(processInstanceId);
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
		return output.get(outputVar);
	}

	public List<RespostaValidacioSignatura> verificarSignatura(Long id) {
		DocumentStore documentStore = documentStoreDao.getById(id, false);
		DocumentDto document = documentHelper.getDocumentOriginal(id, true);
		if (pluginCustodiaDao.potObtenirInfoSignatures()) {
			return pluginCustodiaDao.dadesValidacioSignatura(
					documentStore.getReferenciaCustodia());
		} else if (isSignaturaFileAttached()) {
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
					null,
					signatures.get(0),
					true);
			resposta.add(res);
			return resposta;
		} else {
			List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
			List<byte[]> signatures = pluginCustodiaDao.obtenirSignatures(
					documentStore.getReferenciaCustodia());
			for (byte[] signatura: signatures) {
				RespostaValidacioSignatura res = pluginSignaturaDao.verificarSignatura(
						document.getArxiuContingut(),
						signatura,
						true);
				resposta.add(res);
			}
			return resposta;
		}
	}

	public void changeProcessInstanceVersion(
			String processInstanceId,
			int newVersion) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, newVersion);
	}
	public void changeProcessInstanceVersion(String processInstanceId) {
		jbpmDao.changeProcessInstanceVersion(processInstanceId, -1);
	}

	public boolean isAccioPublica(
			String processInstanceId,
			String accioCodi) {
		JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
		Accio accio = accioDao.findAmbDefinicioProcesICodi(definicioProces.getId(), accioCodi);
		return accio.isPublica();
	}
	public void executarAccio(
			String processInstanceId,
			String accioCodi) {
		JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
		Accio accio = accioDao.findAmbDefinicioProcesICodi(definicioProces.getId(), accioCodi);
		expedientLogHelper.afegirLogExpedientPerProces(
				processInstance.getId(),
				ExpedientLogAccioTipus.EXPEDIENT_ACCIO,
				accio.getJbpmAction());
		jbpmDao.executeActionInstanciaProces(
				processInstanceId,
				accio.getJbpmAction());
		actualitzarDataFiExpedient(processInstanceId);
		getServiceUtils().expedientIndexLuceneUpdate(processInstanceId);
	}

	public DefinicioProces getDefinicioProcesPerProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance processInstance = jbpmDao.getProcessInstance(processInstanceId);
		return definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
	}

	public void createRelacioExpedient(
			Long expedientIdOrigen,
			Long expedientIdDesti) {
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedientIdOrigen,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_AFEGIR,
				expedientIdDesti.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientDao.getById(expedientIdOrigen, false);
		Expedient desti = expedientDao.getById(expedientIdDesti, false);
		boolean existeix = false;
		for (Expedient relacionat: origen.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientIdDesti.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			origen.addRelacioOrigen(desti);
		existeix = false;
		for (Expedient relacionat: desti.getRelacionsOrigen()) {
			if (relacionat.getId().longValue() == expedientIdOrigen.longValue()) {
				existeix = true;
				break;
			}
		}
		if (!existeix)
			desti.addRelacioOrigen(origen);
	}
	public void deleteRelacioExpedient(
			Long expedientIdOrigen,
			Long expedientIdDesti) {
		ExpedientLog expedientLog = expedientLogHelper.afegirLogExpedientPerExpedient(
				expedientIdOrigen,
				ExpedientLogAccioTipus.EXPEDIENT_RELACIO_ESBORRAR,
				expedientIdDesti.toString());
		expedientLog.setEstat(ExpedientLogEstat.IGNORAR);
		Expedient origen = expedientDao.getById(expedientIdOrigen, false);
		Expedient desti = expedientDao.getById(expedientIdDesti, false);
		origen.removeRelacioOrigen(desti);
		desti.removeRelacioOrigen(origen);
	}

	public void publicarExpedient(
			Expedient expedient,
			PublicarExpedientRequest request) {
		String identificador = expedient.getNumeroIdentificador();
		String clau = new Long(System.currentTimeMillis()).toString();
		request.setExpedientIdentificador(identificador);
		request.setExpedientClau(clau);
		pluginTramitacioDao.publicarExpedient(request);
		Expedient ex = expedientDao.getById(expedient.getId(), false);
		ex.setTramitExpedientIdentificador(identificador);
		ex.setTramitExpedientClau(clau);
	}
	public void publicarEvent(
			PublicarEventRequest request) {
		pluginTramitacioDao.publicarEvent(request);
	}

	public List<ExpedientLogDto> getLogsOrdenatsPerData(Long expedientId) {
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		List<ExpedientLog> logs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(expedientId);
		for (ExpedientLog log: logs) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		return resposta;
	}
	public List<ExpedientLogDto> getLogsPerTascaOrdenatsPerData(Long expedientId) {
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		List<ExpedientLog> logs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(expedientId);
		List<String> taskIds = new ArrayList<String>();
		String parentProcessInstanceId = null;
		Map<String, String> processos = new HashMap<String, String>();
		for (ExpedientLog log: logs) {
			if (	//log.getAccioTipus() == ExpedientLogAccioTipus.TASCA_REASSIGNAR ||
					!log.isTargetTasca() ||
					!taskIds.contains(log.getTargetId())) {
				taskIds.add(log.getTargetId());
				// Obtenim el token de cada registre
				JbpmToken token = null;
				if (log.getJbpmLogId() != null) {
					token = expedientLogHelper.getTokenByJbpmLogId(log.getJbpmLogId());
				}
				String tokenName = null;
				String processInstanceId = null;
				if (token != null && token.getToken() != null) {
					tokenName = token.getToken().getFullName();
					processInstanceId = token.getProcessInstanceId();
					
					// Entram per primera vegada
					if (parentProcessInstanceId == null) {
						parentProcessInstanceId = processInstanceId;
						processos.put(processInstanceId, "");
					} else {
						// Canviam de procés
						if (!parentProcessInstanceId.equals(token.getProcessInstanceId())){
							// Entram en un nou subproces
							if (!processos.containsKey(processInstanceId)) {
								processos.put(processInstanceId, token.getToken().getProcessInstance().getSuperProcessToken().getFullName());
							}
						}
						tokenName = processos.get(processInstanceId) + tokenName;
					}
				}
				
				ExpedientLogDto dto = new ExpedientLogDto();
				dto.setId(log.getId());
				dto.setData(log.getData());
				dto.setUsuari(log.getUsuari());
				dto.setEstat(log.getEstat().name());
				dto.setAccioTipus(log.getAccioTipus().name());
				dto.setAccioParams(log.getAccioParams());
				dto.setTargetId(log.getTargetId());
				dto.setTokenName(tokenName);
				dto.setTargetTasca(log.isTargetTasca());
				dto.setTargetProces(log.isTargetProces());
				dto.setTargetExpedient(log.isTargetExpedient());
				resposta.add(dto);
			}
		}
		return resposta;
	}
	public void retrocedirFinsLog(Long expedientLogId, boolean retrocedirPerTasques) {
		ExpedientLog log = expedientLogDao.getById(expedientLogId, false);
		ExpedientLog logRetroces = expedientLogHelper.afegirLogExpedientPerExpedient(
				log.getExpedient().getId(),
				retrocedirPerTasques ? ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR_TASQUES : ExpedientLogAccioTipus.EXPEDIENT_RETROCEDIR,
				expedientLogId.toString());
		expedientLogHelper.retrocedirFinsLog(expedientLogId, retrocedirPerTasques, logRetroces.getId());
		logRetroces.setEstat(ExpedientLogEstat.IGNORAR);
		getServiceUtils().expedientIndexLuceneUpdate(
				log.getExpedient().getProcessInstanceId());
	}
	public Map<String, TascaDto> getTasquesPerLogExpedient(Long expedientId) {
		List<ExpedientLog> logs = expedientLogDao.findAmbExpedientIdOrdenatsPerData(expedientId);
		Map<String, TascaDto> tasquesPerLogs = new HashMap<String, TascaDto>();
		for (ExpedientLog log: logs) {
			if (log.isTargetTasca()) {
				JbpmTask task = jbpmDao.getTaskById(log.getTargetId());
				if (task != null)
					tasquesPerLogs.put(
							log.getTargetId(),
							dtoConverter.toTascaDto(
									task,
									null,
									false,
									false,
									true,
									true,
									true));
			}
		}
		return tasquesPerLogs;
	}
	public List<ExpedientLogDto> findLogsRetroceditsOrdenatsPerData(Long expedientLogId) {
		List<ExpedientLog> logs = expedientLogHelper.findLogsRetrocedits(expedientLogId);
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		for (ExpedientLog log: logs) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		return resposta;
	}
	public List<ExpedientLogDto> findLogsTascaOrdenatsPerData(Long targetId) {
		List<ExpedientLog> logs = expedientLogDao.findLogsTascaByIdOrdenatsPerData(targetId);
		List<ExpedientLogDto> resposta = new ArrayList<ExpedientLogDto>();
		for (ExpedientLog log: logs) {
			ExpedientLogDto dto = new ExpedientLogDto();
			dto.setId(log.getId());
			dto.setData(log.getData());
			dto.setUsuari(log.getUsuari());
			dto.setEstat(log.getEstat().name());
			dto.setAccioTipus(log.getAccioTipus().name());
			dto.setAccioParams(log.getAccioParams());
			dto.setTargetId(log.getTargetId());
			dto.setTargetTasca(log.isTargetTasca());
			dto.setTargetProces(log.isTargetProces());
			dto.setTargetExpedient(log.isTargetExpedient());
			resposta.add(dto);
		}
		return resposta;
	}

	public List<PortasignaturesDto> findDocumentsPendentsPortasignatures(String processInstanceId) {
		List<PortasignaturesDto> resposta = new ArrayList<PortasignaturesDto>();
		List<Portasignatures> pendents = pluginPortasignaturesDao.findPendentsPerProcessInstanceId(processInstanceId);
		for (Portasignatures pendent: pendents) {
			PortasignaturesDto dto = new PortasignaturesDto();
			dto.setId(pendent.getId());
			dto.setDocumentId(pendent.getDocumentId());
			dto.setTokenId(pendent.getTokenId());
			dto.setDataEnviat(pendent.getDataEnviat());
			if (TipusEstat.ERROR.equals(pendent.getEstat())) {
				if (Transicio.SIGNAT.equals(pendent.getTransition()))
					dto.setEstat(TipusEstat.SIGNAT.toString());
				else
					dto.setEstat(TipusEstat.REBUTJAT.toString());
				dto.setError(true);
			} else {
				dto.setEstat(pendent.getEstat().toString());
				dto.setError(false);
			}
			if (pendent.getTransition() != null)
				dto.setTransicio(pendent.getTransition().toString());
			dto.setDocumentStoreId(pendent.getDocumentStoreId());
			dto.setMotiuRebuig(pendent.getMotiuRebuig());
			dto.setTransicioOK(pendent.getTransicioOK());
			dto.setTransicioKO(pendent.getTransicioKO());
			dto.setDataProcesPrimer(pendent.getDataCallbackPrimer());
			dto.setDataProcesDarrer(pendent.getDataCallbackDarrer());
			dto.setErrorProcessant(pendent.getErrorCallbackProcessant());
			dto.setProcessInstanceId(pendent.getProcessInstanceId());
			resposta.add(dto);
		}
		return resposta;
	}


	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setCampDao(
			CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setConsultaCampDao(
			ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setConsultaDao(ConsultaDao consultaDao) {
		this.consultaDao = consultaDao;
	}
	@Autowired
	public void setPluginCustodiaDao(PluginCustodiaDao pluginCustodiaDao) {
		this.pluginCustodiaDao = pluginCustodiaDao;
	}
	@Autowired
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setRegistreDao(RegistreDao registreDao) {
		this.registreDao = registreDao;
	}
	@Autowired
	public void setAccioDao(AccioDao accioDao) {
		this.accioDao = accioDao;
	}
	@Autowired
	public void setTerminiIniciatDao(TerminiIniciatDao terminiIniciatDao) {
		this.terminiIniciatDao = terminiIniciatDao;
	}
	@Autowired
	public void setPluginGestioDocumentalDao(
			PluginGestioDocumentalDao pluginGestioDocumentalDao) {
		this.pluginGestioDocumentalDao = pluginGestioDocumentalDao;
	}
	@Autowired
	public void setPluginTramitacioDao(PluginTramitacioDao pluginTramitacioDao) {
		this.pluginTramitacioDao = pluginTramitacioDao;
	}
	@Autowired
	public void setPluginSignaturaDao(
			PluginSignaturaDao pluginSignaturaDao) {
		this.pluginSignaturaDao = pluginSignaturaDao;
	}
	@Autowired
	public void setPluginPortasignaturesDao(PluginPortasignaturesDao pluginPortasignaturesDao) {
		this.pluginPortasignaturesDao = pluginPortasignaturesDao;
	}
	@Autowired
	public void setPluginPersonaDao(PluginPersonaDao pluginPersonaDao) {
		this.pluginPersonaDao = pluginPersonaDao;
	}
	@Autowired
	public void setPluginGisDao(PluginGisDao pluginGisDao) {
		this.pluginGisDao = pluginGisDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setExpedientLogHelper(ExpedientLogHelper expedientLogHelper) {
		this.expedientLogHelper = expedientLogHelper;
	}
	@Autowired
	public void setDocumentHelper(DocumentHelper documentHelper) {
		this.documentHelper = documentHelper;
	}
	@Autowired
	public void setExpedientLogDao(ExpedientLogDao expedientLogDao) {
		this.expedientLogDao = expedientLogDao;
	}
	@Autowired
	public void setAreaMembreDao(AreaMembreDao areaMembreDao) {
		this.areaMembreDao = areaMembreDao;
	}
	@Autowired
	public void setAreaJbpmIdDao(AreaJbpmIdDao areaJbpmIdDao) {
		this.areaJbpmIdDao = areaJbpmIdDao;
	}



	@SuppressWarnings("rawtypes")
	private Map<String, JbpmNodePosition> getNodePositions(String processInstanceId) {
		Map<String, JbpmNodePosition> resposta = new HashMap<String, JbpmNodePosition>();
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		byte[] gpdBytes = jbpmDao.getResourceBytes(jpd.getId(), "gpd.xml");
		if (gpdBytes != null) {
			try {
				Element root = org.dom4j.DocumentHelper.parseText(new String(gpdBytes)).getRootElement();
				Iterator it = root.elementIterator("node");
				while (it.hasNext()) {
					Element node = (Element)it.next();
					String nodeName = node.attributeValue("name");
					JbpmNodePosition nodePosition = new JbpmNodePosition();
					nodePosition.setPosX(new Integer(node.attributeValue("x")).intValue());
					nodePosition.setPosY(new Integer(node.attributeValue("y")).intValue());
					nodePosition.setWidth(new Integer(node.attributeValue("width")).intValue());
					nodePosition.setHeight(new Integer(node.attributeValue("height")).intValue());
					resposta.put(nodeName, nodePosition);
				}
			} catch (Exception ex) {
				logger.error("No s'ha pogut desxifrar l'arxiu gpd.xml", ex);
			}
		}
		return resposta;
	}
	private int[] getImageDimensions(String processInstanceId) {
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		byte[] gpdBytes = jbpmDao.getResourceBytes(jpd.getId(), "gpd.xml");
		if (gpdBytes != null) {
			try {
				Element root = org.dom4j.DocumentHelper.parseText(new String(gpdBytes)).getRootElement();
				return new int[] {
						Integer.parseInt(root.attributeValue("width")),
						Integer.parseInt(root.attributeValue("height"))};
			} catch (Exception ex) {
				logger.error("No s'ha pogut desxifrar l'arxiu gpd.xml", ex);
			}
		}
		return null;
	}

	private String getInformacioExpedient(Expedient expedient) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (expedient.getTitol() != null)
			sb.append("titol: \"" + expedient.getTitol() + "\", ");
		if (expedient.getNumero() != null)
			sb.append("numero: \"" + expedient.getNumero() + "\", ");
		if (expedient.getEstat() != null)
			sb.append("estat: \"" + expedient.getEstat().getNom() + "\", ");
		sb.append("dataInici: \"" + expedient.getDataInici() + "\", ");
		if (expedient.getDataFi() != null)
			sb.append("dataFi: \"" + expedient.getDataFi() + "\", ");
		if (expedient.getComentari() != null && expedient.getComentari().length() > 0)
			sb.append("comentari: \"" + expedient.getComentari() + "\", ");
		if (expedient.getResponsableCodi() != null)
			sb.append("responsableCodi: \"" + expedient.getResponsableCodi() + "\", ");
		sb.append("iniciadorCodi: \"" + expedient.getIniciadorCodi() + "\"");
		sb.append("]");
		return sb.toString();
	}

	private TokenDto toTokenDto(JbpmToken token) {
		TokenDto dto = new TokenDto();
		dto.setId(token.getId());
		dto.setName(token.getName());
		dto.setFullName(token.getFullName());
		dto.setParentName(token.getParentTokenName());
		dto.setParentFullName(token.getParentTokenFullName());
		dto.setNodeName(token.getNodeName());
		dto.setStart(token.getStart());
		dto.setEnd(token.getEnd());
		dto.setAbleToReactivateParent(token.isAbleToReactivateParent());
		dto.setTerminationImplicit(token.isTerminationImplicit());
		dto.setSuspended(token.isSuspended());
		dto.setNodeEnter(token.getNodeEnter());
		dto.setRoot(token.isRoot());
		return dto;
	}

	private Expedient getExpedientPerProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}
	private Expedient getExpedientPerTaskInstanceId(String taskId) {
		JbpmTask task = jbpmDao.getTaskById(taskId);
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(
				task.getProcessInstanceId());
		return expedientDao.findAmbProcessInstanceId(pi.getId());
	}

	

	/*private Long createUpdateDocument(
				String processInstanceId,
				String documentCodi,
				String documentNom,
				String jbpmVariable,
				Date data,
				String arxiuNom,
				byte[] arxiuContingut,
				boolean adjunt,
				boolean registrat,
				String registreNumero,
				Date registreData,
				String registreOficinaCodi,
				String registreOficinaNom,
				boolean registreEntrada) {
		JbpmProcessInstance rootProcessInstance = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(rootProcessInstance.getId());
		String nomArxiuAntic = null;
		// Obté la referencia al documentStore del jBPM
		Long docStoreId = (Long)jbpmDao.getProcessInstanceVariable(
				processInstanceId,
				jbpmVariable);
		boolean creat = (docStoreId == null);
		// Crea el document al store
		if (docStoreId == null) {
			docStoreId = documentStoreDao.create(
					processInstanceId,
					jbpmVariable,
					data,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? DocumentFont.ALFRESCO : DocumentFont.INTERNA,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					adjunt,
					documentNom);
		} else {
			DocumentStore docStore = documentStoreDao.getById(docStoreId, false);
			nomArxiuAntic = docStore.getArxiuNom();
			documentStoreDao.update(
					docStoreId,
					data,
					arxiuNom,
					(pluginGestioDocumentalDao.isGestioDocumentalActiu()) ? null : arxiuContingut,
					documentNom);
			if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu())
				pluginGestioDocumentalDao.deleteDocument(docStore.getReferenciaFont());
		}
		// Crea el document a dins la gestió documental
		if (arxiuContingut != null && pluginGestioDocumentalDao.isGestioDocumentalActiu()) {
			String referenciaFont = pluginGestioDocumentalDao.createDocument(
					expedient,
					docStoreId.toString(),
					documentNom,
					data,
					arxiuNom,
					arxiuContingut);
			documentStoreDao.updateReferenciaFont(
					docStoreId,
					referenciaFont);
		}
		// Guarda la referència al nou document a dins el jBPM
		jbpmDao.setProcessInstanceVariable(processInstanceId, jbpmVariable, docStoreId);
		// Registra l'acció
		if (creat) {
			registreDao.crearRegistreCrearDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					documentCodi,
					arxiuNom);
		} else {
			registreDao.crearRegistreModificarDocumentInstanciaProces(
					expedient.getId(),
					processInstanceId,
					SecurityContextHolder.getContext().getAuthentication().getName(),
					documentCodi,
					nomArxiuAntic,
					arxiuNom);
		}
		return docStoreId;
	}*/

	

	private boolean isSignaturaFileAttached() {
		String fileAttached = GlobalProperties.getInstance().getProperty("app.signatura.plugin.file.attached");
		return "true".equalsIgnoreCase(fileAttached);
	}

	private String getVarNameFromDocumentStore(DocumentStore documentStore) {
		String jbpmVariable = documentStore.getJbpmVariable();
		if (documentStore.isAdjunt())
			return jbpmVariable.substring(DocumentHelper.PREFIX_ADJUNT.length());
		else
			return jbpmVariable.substring(DocumentHelper.PREFIX_VAR_DOCUMENT.length());
	}

	private void comprovarUsuari(String usuari) {
		PersonaDto persona = pluginPersonaDao.findAmbCodiPlugin(usuari);
		if (persona == null)
			throw new IllegalArgumentsException(
					getServiceUtils().getMessage("error.expedientService.trobarPersona",
							new Object[]{usuari}));
	}
	
	public String getXmlExpedients(List<DadesExpedient> expedients) {
		return pluginGisDao.getXMLExpedientsPlugin(expedients);
	}
	
	public URL getUrlVisor() {
		return pluginGisDao.getUrlVisorPlugin();
	}

	private Long[] getExpedientTipusIdPermesos(Long entornId) {
		List<ExpedientTipus> tipus = expedientTipusDao.findAmbEntorn(entornId);
		getServiceUtils().filterAllowed(
						tipus,
						ExpedientTipus.class,
						new Permission[] {
							ExtendedPermission.ADMINISTRATION,
							ExtendedPermission.READ});
		Long[] resposta = new Long[tipus.size()];
		for (int i = 0; i < resposta.length; i++)
			resposta[i] = tipus.get(i).getId();
		return resposta;
	}

	private void afegirValorsPredefinits(
			Consulta consulta,
			Map<String, Object> valors,
			List<Camp> camps) {
		if (consulta.getValorsPredefinits() != null && consulta.getValorsPredefinits().length() > 0) {
			String[] parelles = consulta.getValorsPredefinits().split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parella = (parelles[i].contains(":")) ? parelles[i].split(":") : parelles[i].split("=");
				if (parella.length == 2) {
					String campCodi = parella[0];
					String valor = parella[1];
					for (Camp camp: camps) {
						if (camp.getCodi().equals(campCodi)) {
							valors.put(
									camp.getDefinicioProces().getJbpmKey() + "." + campCodi,
									Camp.getComObject(
											camp.getTipus(),
											valor));
							break;
						}
					}
				}
			}
		}
	}

	private String getUsuariPerRegistre() {
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null)
			return SecurityContextHolder.getContext().getAuthentication().getName();
		else
			return "Procés automàtic";
	}

	private String[] getAreesOGrupsPerUsuari() {
		String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
		if (esIdentitySourceHelium()) {
			List<AreaMembre> membres = areaMembreDao.findAmbUsuariCodi(usuariCodi);
			List<String> codisArea = new ArrayList<String>();
			for (AreaMembre membre: membres) {
				codisArea.add(membre.getArea().getCodi());
			}
			String[] resposta = codisArea.toArray(new String[membres.size()]);
			return resposta;
		} else {
			List<String> codisArea = areaJbpmIdDao.findAmbUsuariCodi(usuariCodi);
			String[] resposta = codisArea.toArray(new String[codisArea.size()]);
			return resposta;
		}
	}
	private boolean esIdentitySourceHelium() {
		String identitySource = GlobalProperties.getInstance().getProperty("app.jbpm.identity.source");
		return (identitySource.equalsIgnoreCase("helium"));
	}

	private void actualitzarDataFiExpedient(String processInstanceId) {
		JbpmProcessInstance pi = jbpmDao.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientDao.findAmbProcessInstanceId(pi.getId());
		if (pi.getEnd() != null)
			expedient.setDataFi(pi.getEnd());
	}

	private Object optimitzarValorPerConsultesDomini(
			String processInstanceId,
			String varName,
			Object varValue) {
		JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(jpd.getId());
		Camp camp = campDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				varName);
		if (camp.isDominiCacheText()) {
			if (varValue != null) {
				if (	camp.getTipus().equals(TipusCamp.SELECCIO) ||
						camp.getTipus().equals(TipusCamp.SUGGEST)) {
					String text = dtoConverter.getCampText(
							null,
							processInstanceId,
							camp,
							varValue);
					return new DominiCodiDescripcio(
							(String)varValue,
							text);
				}
			}
		}
		return varValue;
	}

	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					expedientDao,
					definicioProcesDao,
					campDao,
					consultaCampDao,
					luceneDao,
					dtoConverter,
					jbpmDao,
					aclServiceDao,
					messageSource);
		}
		return serviceUtils;
	}

	private static final Log logger = LogFactory.getLog(ExpedientService.class);

}
