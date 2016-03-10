/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;

import net.conselldemallorca.helium.core.common.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.helper.TascaSegonPlaHelper.InfoSegonPla;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesExpedient;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantDetallRecepcio;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.TramitacioPluginException;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaColumnaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DominiRespostaFilaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReassignacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReferenciaRDSJustificanteDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnnexDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantDetallRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperEventDto;
import net.conselldemallorca.helium.v3.core.api.dto.ZonaperExpedientDto;
import net.conselldemallorca.helium.v3.core.api.exception.AreaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentGenerarException;
import net.conselldemallorca.helium.v3.core.api.exception.DocumentNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiConsultaException;
import net.conselldemallorca.helium.v3.core.api.exception.DominiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EnumeracioNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EstatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PersonaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.PluginException;
import net.conselldemallorca.helium.v3.core.api.exception.ProcessInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.CampTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentTascaRepository;
import net.conselldemallorca.helium.v3.core.repository.DominiRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EnumeracioRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.FestiuRepository;
import net.conselldemallorca.helium.v3.core.repository.ReassignacioRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiIniciatRepository;

/**
 * Service que implementa la funcionalitat necessària per
 * a integrar Helium i jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class Jbpm3HeliumHelper implements Jbpm3HeliumService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private DominiRepository dominiRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private CarrecRepository carrecRepository;
	@Resource
	private FestiuRepository festiuRepository;
	@Resource
	private ReassignacioRepository reassignacioRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private TerminiIniciatRepository terminiIniciatRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private EnumeracioRepository enumeracioRepository;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private CampTascaRepository campTascaRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private DocumentTascaRepository documentTascaRepository;

	/*@Resource
	private ExpedientTipusDao expedientTipusDao;
	@Resource
	private ExpedientDao expedientDao;
	@Resource
	private DefinicioProcesDao definicioProcesDao;
	@Resource
	private AreaDao areaDao;
	@Resource
	private CarrecDao carrecDao;
	@Resource
	private FestiuDao festiuDao;
	@Resource
	private ReassignacioDao reassignacioDao;
	@Resource
	private EstatDao estatDao;
	@Resource
	private DocumentDao documentDao;
	@Resource
	private CampDao campDao;
	@Resource
	private TerminiIniciatDao terminiIniciatDao;
	@Resource
	private EnumeracioDao enumeracioDao;
	@Resource
	private TascaDao tascaDao;
	@Resource
	private CampTascaDao campTascaDao;
	@Resource
	private DocumentTascaDao documentTascaDao;
	@Resource
	private MailDao mailDao;
	@Resource
	private PluginRegistreDao pluginRegistreDao;
	@Resource
	private PluginGestioDocumentalDao pluginGestioDocumentalDao;
	@Resource
	private PluginTramitacioDao pluginTramitacioDao;
	@Resource
	private PluginPersonaDao pluginPersonaDao;
	@Resource
	private PluginService pluginService;
	@Resource
	private AlertaDao alertaDao;*/
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private DominiHelper dominiHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private VariableHelper variableHelper;	
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private TerminiHelper terminiHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private MailHelper mailHelper;
	@Resource
	private NotificacioElectronicaHelper notificacioElectronicaHelper;

	/*@Resource
	private ExpedientService expedientService;
	@Resource
	private DocumentService documentService;
	@Resource
	private ExecucioMassivaService execucioMassivaService;*/

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Resource
	private ServiceUtils serviceUtils;

	@Resource
	private MetricRegistry metricRegistry;
	
	@Resource
	private TascaSegonPlaHelper tascaSegonPlaHelper;



	@Override
	public String getUsuariCodiActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	@Override
	public EntornDto getEntornActual() {
		Long entornId = EntornActual.getEntornId();
		logger.debug("Obtenint entorn actual (idEntornActual=" + entornId + ")");
		if (entornId == null)
			return null;
		return conversioTipusHelper.convertir(
				entornRepository.findOne(entornId),
				EntornDto.class);
	}

	@Override
	public ExpedientDto getExpedientIniciant() {
		logger.debug("Obtenint expedient en fase d'inici");
		return conversioTipusHelper.convertir(
				ExpedientIniciantDto.getExpedient(),
				ExpedientDto.class);
	}

	@Override
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		logger.debug("Obtenint expedient donat entorn, tipus i número (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"numero=" + numero + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertir(
				expedientRepository.findByEntornAndTipusAndNumero(
						entorn,
						expedientTipus,
						numero),
				ExpedientDto.class);
	}

	@Override
	public void luceneDeleteExpedient(String processInstanceId) {
		logger.debug("Esborra expedient donada una instància de procés (" +
				"processInstanceId=" + processInstanceId + ")");
		serviceUtils.expedientIndexLuceneDelete(processInstanceId);
	}
	
	@Override
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getExpedientDonatProcessInstanceId(processInstanceId),
				ExpedientDto.class);
	}
	
	@Override
	public EntornDto getEntornAmbProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getEntornDonatProcessInstanceId(processInstanceId),
				EntornDto.class);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version) {
		logger.debug("Obtenint la definició de procés donat el codi jBPM i la versió (jbpmKey=" + jbpmKey + ", version=" + version +")");
		return conversioTipusHelper.convertir(
				definicioProcesRepository.findByJbpmKeyAndVersio(
						jbpmKey,
						version),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws EntornNotFoundException {
		logger.debug("Obtenint la darrera versió de la definició de procés donat l'entorn i el codi jBPM (entornId=" + entornId + ", jbpmKey=" + jbpmKey + ")");
		return conversioTipusHelper.convertir(
				definicioProcesRepository.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						jbpmKey),
				DefinicioProcesDto.class);
	}

	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		logger.debug("Obtenint la definició de procés donada la instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getDefinicioProcesDonatProcessInstanceId(processInstanceId),
				DefinicioProcesDto.class);
	}

	@Override
	public PersonaDto getPersonaAmbCodi(String codi) {
		logger.debug("Obtenint persona (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				pluginHelper.personaFindAmbCodi(codi),
				PersonaDto.class);
	}

	@Override
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi) throws EntornNotFoundException {
		logger.debug("Obtenint area donat l'entorn i el codi (" +
				"entornId=" + entornId + ", " +
				"codi=" + codi + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		return conversioTipusHelper.convertir(
				areaRepository.findByEntornAndCodi(
						entorn,
						codi),
				AreaDto.class);
	}

	@Override
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) throws EntornNotFoundException, AreaNotFoundException {
		logger.debug("Obtenint carrec donat l'entorn, l'àrea i el codi (" +
				"entornId=" + entornId + ", " +
				"areaCodi=" + areaCodi + ", " +
				"carrecCodi=" + carrecCodi + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		Area area = areaRepository.findByEntornAndCodi(
				entorn,
				areaCodi);
		if (area == null)
			throw new AreaNotFoundException();
		return conversioTipusHelper.convertir(
				carrecRepository.findByEntornAndAreaAndCodi(
						entorn,
						area,
						carrecCodi),
				CarrecDto.class);
	}

	@Override
	public List<FestiuDto> findFestiusAll() {
		logger.debug("Obtenint la llista de tots els festius");
		return conversioTipusHelper.convertirList(
				festiuRepository.findAll(),
				FestiuDto.class);
	}

	@Override
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String usuariCodi) {
		logger.debug("Obtenint reassignació activa per a l'usuari ("
				+ "usuariCodi=" + usuariCodi + ")");
		Date ara = new Date();
		return conversioTipusHelper.convertir(
				reassignacioRepository.findByUsuari(
						usuariCodi,
						ara,
						ara),
				ReassignacioDto.class);
	}

	@Override
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException {
		logger.debug("Creant alerta (" +
				"entornId=" + entornId + ", " +
				"expedientId=" + expedientId + ", " +
				"data=" + data + ", " +
				"usuariCodi=" + usuariCodi + ", " +
				"text=" + text + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		Alerta alerta = new Alerta();
		alerta.setEntorn(entorn);
		alerta.setExpedient(expedient);
		alerta.setDataCreacio(data);
		alerta.setDestinatari(usuariCodi);
		alerta.setText(text);
		alertaRepository.save(alerta);
	}

	@Override
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		logger.debug("Esborrant alertes amb taskInstance (" +
				"taskInstanceId=" + taskInstanceId + ")");
		Date ara = new Date();
		List<TerminiIniciat> terminis = terminiIniciatRepository.findByTaskInstanceId(
				new Long(taskInstanceId).toString());
		for (TerminiIniciat termini: terminis) {
			for (Alerta alerta: termini.getAlertes()) {
				alerta.setDataEliminacio(ara);
			}
		}
	}

	@Override
	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi) throws ProcessInstanceNotFoundException, ExpedientNotFoundException, EstatNotFoundException {
		logger.debug("Modificant estat de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"estatCodi=" + estatCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Estat estat = estatRepository.findByExpedientTipusAndCodi(
				expedient.getTipus(),
				estatCodi);
		if (estat == null)
			throw new EstatNotFoundException();
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				estat.getId(),
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarComentari(
			String processInstanceId,
			String comentari) throws ExpedientNotFoundException {
		logger.debug("Modificant comentari de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"comentari=" + comentari + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				comentari,
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia) throws ExpedientNotFoundException {
		logger.debug("Modificant georeferència de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"posx=" + posx + ", " +
				"posy=" + posy + ", " +
				"referencia=" + referencia + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				posx,
				posy,
				referencia,
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi) throws ExpedientNotFoundException {
		logger.debug("Modificant grup de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"grupCodi=" + grupCodi + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				grupCodi,
				false);
	}

	@Override
	public void expedientModificarNumero(
			String processInstanceId,
			String numero) throws ExpedientNotFoundException {
		logger.debug("Modificant número de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"numero=" + numero + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				numero,
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi) throws ExpedientNotFoundException, PersonaNotFoundException {
		logger.debug("Modificant responsable de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"responsableCodi=" + responsableCodi + ")");
		if (pluginHelper.personaFindAmbCodi(responsableCodi) == null)
			throw new PersonaNotFoundException();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				expedient.getTitol(),
				responsableCodi,
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientModificarTitol(
			String processInstanceId,
			String titol) throws ExpedientNotFoundException {
		logger.debug("Modificant títol de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"titol=" + titol + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.update(
				expedient,
				expedient.getNumero(),
				titol,
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi(),
				false);
	}

	@Override
	public void expedientAturar(
			String processInstanceId,
			String motiu) throws ExpedientNotFoundException {
		logger.debug("Aturant expedient (processInstanceId=" + processInstanceId + ", motiu=" + motiu + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.aturar(
				expedient,
				motiu,
				null);
	}

	@Override
	public void expedientReprendre(
			String processInstanceId) throws ExpedientNotFoundException {
		logger.debug("Reprenent expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		expedientHelper.reprendre(
				expedient,
				null);
	}

	@Override
	public void expedientReindexar(
			String processInstanceId) throws ExpedientNotFoundException {
		logger.debug("Reindexant expedient (processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		serviceUtils.expedientIndexLuceneRecrear(expedient);
	}

	@Override
	public void expedientBuidaLogs(
			String processInstanceId) {
		logger.debug("Buidant logs expedient (processInstanceId=" + processInstanceId + ")");
		ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(processInstanceId);
		jbpmHelper.deleteProcessInstanceTreeLogs(piexp.getProcessInstanceId());
	}

	@Override
	public ArxiuDto documentGenerarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException {
		logger.debug("Generant document amb plantilla (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"dataDocument=" + dataDocument + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		Document document = documentRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				documentCodi);
		if (document == null)
			throw new DocumentNotFoundException();
		ArxiuDto generat = documentHelper.generarDocumentAmbPlantillaIConvertir(
				expedient,
				document,
				taskInstanceId,
				processInstanceId,
				dataDocument);
		documentHelper.actualitzarDocument(
				taskInstanceId,
				processInstanceId,
				document.getCodi(),
				null,
				dataDocument,
				generat.getNom(),
				generat.getContingut(),
				false);
		return generat;
	}

	@Override
	public void createDadesTasca(Long taskId) {
		tascaHelper.createDadesTasca(taskId);
	}

	@Override
	public TerminiDto getTerminiAmbProcessInstanceICodi(
			String processInstanceId,
			String terminiCodi) throws ProcessInstanceNotFoundException {
		logger.debug("Obtenint termini donada la instància de procés i el codi (" +
				"processInstanceId=" + processInstanceId + "," +
				"terminiCodi=" + terminiCodi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		return conversioTipusHelper.convertir(
				termini,
				TerminiDto.class);
	}

	@Override
	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
			String processInstanceId,
			String terminiCodi)
			throws TerminiNotFoundException {
		logger.debug("Obtenint termini iniciat donada la instància de procés i el codi (" +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiCodi=" + terminiCodi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		TerminiIniciat terminiIniciat = terminiHelper.findIniciatAmbDefinicioProcesICodi(
				definicioProces,
				processInstanceId,
				terminiCodi);
		if (terminiIniciat == null)
			throw new TerminiIniciatNotFoundException();
		return conversioTipusHelper.convertir(
				terminiIniciat,
				TerminiIniciatDto.class);
	}

	@Override
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException {
		logger.debug("Configurant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"timerId=" + timerId + ")");
		TerminiIniciat terminiIniciat = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		terminiIniciat.setTaskInstanceId(taskInstanceId);
		if (timerId != null)
			terminiIniciat.afegirTimerId(timerId.longValue());
	}

	@Override
	public Date terminiCalcularDataInici(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		logger.debug("Calculant data d'inici de termini a partir d'una data de fi (" +
				"fi=" + fi + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiHelper.getDataIniciTermini(fi, anys, mesos, dies, laborable);
	}

	@Override
	public Date terminiCalcularDataFi(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		logger.debug("Calculant data de fi de termini a partir d'una data d'inici (" +
				"inici=" + inici + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiHelper.getDataFiTermini(inici, anys, mesos, dies, laborable);
	}

	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws TerminiNotFoundException {
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		terminiHelper.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				anys,
				mesos,
				dies,
				esDataFi);
		
	}

	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			boolean esDataFi) throws TerminiNotFoundException {
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiHelper.findAmbDefinicioProcesICodi(
				definicioProces,
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		terminiHelper.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				esDataFi);
	}

	@Override
	public void terminiCancelar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		logger.debug("Cancelant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiHelper.cancelar(terminiIniciatId, data);
	}

	@Override
	public void terminiPausar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		logger.debug("Pausant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiHelper.pausar(terminiIniciatId, data);
	}

	@Override
	public void terminiContinuar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		logger.debug("Continuant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiHelper.findTerminiIniciatById(terminiIniciatId);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiHelper.continuar(terminiIniciatId, data);
	}

	@Override
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws ExpedientNotFoundException, DominiNotFoundException, DominiConsultaException {
		logger.debug("Executant una consulta de domini (" +
				"processInstanceId=" + processInstanceId + ", " +
				"dominiCodi=" + dominiCodi + ", " +
				"dominiId=" + dominiId + ", " +
				"parametres=" + parametres + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Domini domini = dominiRepository.findByEntornAndCodi(
				expedient.getEntorn(),
				dominiCodi);
		if (domini == null)
			throw new DominiNotFoundException();
		try {
			List<FilaResultat> files = dominiHelper.consultar(
					domini,
					dominiId,
					parametres);
			List<DominiRespostaFilaDto> resposta = new ArrayList<DominiRespostaFilaDto>();
			for (FilaResultat fila: files) {
				DominiRespostaFilaDto filaDto = new DominiRespostaFilaDto();
				for (ParellaCodiValor columna: fila.getColumnes()) {
					DominiRespostaColumnaDto columnaDto = new DominiRespostaColumnaDto();
					columnaDto.setCodi(columna.getCodi());
					columnaDto.setValor(columna.getValor());
					filaDto.getColumnes().add(columnaDto);
				}
				resposta.add(filaDto);
			}
			return resposta;
		} catch (Exception ex) {
			throw new DominiConsultaException(ex);
		}
	}

	@Override
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws ExpedientNotFoundException, EnumeracioNotFoundException {
		logger.debug("Consultant els valors d'una enumeració (" +
				"processInstanceId=" + processInstanceId + ", " +
				"enumeracioCodi=" + enumeracioCodi + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioRepository.findByEntornAndCodiAndExpedientTipusNull(
				expedient.getEntorn(),
				enumeracioCodi);
		if (enumeracio == null) {
			enumeracio = enumeracioRepository.findByEntornAndExpedientTipusAndCodi(
					expedient.getEntorn(),
					expedient.getTipus(),
					enumeracioCodi);
		}
		if (enumeracio == null)
			throw new EnumeracioNotFoundException();
		return conversioTipusHelper.convertirList(
				enumeracio.getEnumeracioValors(),
				EnumeracioValorDto.class);
	}

	@Override
	public List<CampTascaDto> findCampsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		logger.debug("Consultant els camps del formulari de la tasca (" +
				"taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new TaskInstanceNotFoundException();
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		if (tasca == null)
			throw new TascaNotFoundException();
		return conversioTipusHelper.convertirList(
				campTascaRepository.findAmbTascaOrdenats(tasca.getId()),
				CampTascaDto.class);
	}

	@Override
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		logger.debug("Consultant els documents de la tasca (taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new TaskInstanceNotFoundException();
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Tasca tasca = tascaRepository.findByJbpmNameAndDefinicioProces(
				task.getTaskName(),
				definicioProces);
		if (tasca == null)
			throw new TascaNotFoundException();
		return conversioTipusHelper.convertirList(
				documentTascaRepository.findAmbTascaOrdenats(tasca.getId()),
				DocumentTascaDto.class);
	}

	@Override
	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		logger.debug("Obtenint el codi de variable jBPM pel document (" +
				"documentCodi=" + documentCodi + ")");
		return documentHelper.getVarPerDocumentCodi(documentCodi, false);
	}

	@Override
	public DocumentDto getDocumentInfo(Long documentStoreId) {
		logger.debug("Obtenint informació del document (" +
				"documentStoreId=" + documentStoreId + ")");
		return conversioTipusHelper.convertir(
				documentHelper.getDocumentOriginal(
						documentStoreId,
						false),
				DocumentDto.class);
	}

	@Override
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		logger.debug("Obtenint arxiu del document (" +
				"documentStoreId=" + documentStoreId + ")");
		DocumentDto document = documentHelper.getDocumentOriginal(
				documentStoreId,
				false);
		if (document == null)
			return null;
		return documentHelper.getArxiuPerDocumentStoreId(
				documentStoreId,
				false,
				true);
	}

	@Override
	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		logger.debug("Guardant un document a dins l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"data=" + data + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ")");
		return documentHelper.actualitzarDocument(
				null,
				processInstanceId,
				documentCodi,
				null,
				data,
				arxiuNom,
				arxiuContingut,
				false);
	}

	@Override
	public void reprendreExpedient(String processInstanceId) throws Exception{
		logger.debug("reprendreExpedient (" +
				"processInstanceId=" + processInstanceId + ")");
		jbpmHelper.reprendreExpedient(processInstanceId);
	}
	
	@Override
	public boolean tokenActivar(long tokenId, boolean activar) {
		logger.debug("tokenActivar (" +
				"tokenId=" + tokenId + ", " +
				"activar=" + activar + ")");
		try {
			return jbpmHelper.tokenActivar(tokenId, activar);
		} catch (Exception ex) {
			return false;
		} 
	}

	@Override
	public Long documentExpedientAdjuntar(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut) {
		logger.debug("Guardant un document adjunt a dins l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"adjuntId=" + adjuntId + ", " +
				"adjuntTitol=" + adjuntTitol + ", " +
				"adjuntData=" + adjuntData + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ")");
		return documentHelper.actualitzarDocument(
				null,
				processInstanceId,
				null,
				adjuntTitol,
				adjuntData,
				arxiuNom,
				arxiuContingut,
				true);
	}

	@Override
	public void documentExpedientEsborrar(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		logger.debug("Esborrant un document de dins l'expedient (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		documentHelper.esborrarDocument(
				taskInstanceId,
				processInstanceId,
				documentCodi);
	}

	@Override
	public void documentExpedientGuardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		logger.debug("Esborrant un document de dins l'expedient (" +
				"documentStoreId=" + documentStoreId + ", " +
				"registreNumero=" + registreNumero + ", " +
				"registreData=" + registreData + ", " +
				"registreOficinaCodi=" + registreOficinaCodi + ", " +
				"registreOficinaNom=" + registreOficinaNom + ", " +
				"registreEntrada=" + registreEntrada + ")");
		documentHelper.guardarDadesRegistre(
				documentStoreId,
				registreNumero,
				registreData,
				registreOficinaCodi,
				registreOficinaNom,
				registreEntrada);
	}

	@Override
	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws PluginException {
		logger.debug("Enviant correu (" +
				"fromAddress=" + fromAddress + ", " +
				"recipients=" + recipients + ", " +
				"ccRecipients=" + ccRecipients + ", " +
				"bccRecipients=" + bccRecipients + ", " +
				"subject=" + subject + ", " +
				"text=" + text + ")");
		try {
			mailHelper.send(
					fromAddress,
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					conversioTipusHelper.convertirList(
							attachments,
							ArxiuDto.class));
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	@Override
	public boolean isRegistreActiu() {
		logger.debug("Comprovant si el plugin de registre està actiu");
		return pluginHelper.registreIsPluginActiu();
	}

	@Override
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) {
		/*RegistreEntrada registreEntrada = new RegistreEntrada();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreEntrada.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setAutenticat(true);
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreEntrada.setDadesInteressat(dadesInteressat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		String idiomaExtracte = anotacio.getAssumpteIdiomaCodi();
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		dadesAssumpte.setTipus(
				anotacio.getAssumpteTipus());
		dadesAssumpte.setRegistreNumero(
				anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(
				anotacio.getAssumpteRegistreAny());
		registreEntrada.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setIdiomaCodi((annex.getIdiomaCodi() != null) ? annex.getIdiomaCodi() : "ca");
				document.setData(annex.getData());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreEntrada.setDocuments(documents);
		}*/
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioEntrada(
				anotacio);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	@Override
	public RegistreIdDto registreAnotacioSortida(
			RegistreAnotacioDto anotacio) {
		/*RegistreSortida registreSortida = new RegistreSortida();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(anotacio.getOrganCodi());
		dadesOficina.setOficinaCodi(anotacio.getOficinaCodi());
		registreSortida.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setAutenticat(true);
		dadesInteressat.setEntitatCodi(anotacio.getEntitatCodi());
		dadesInteressat.setNomAmbCognoms(anotacio.getInteressatNomAmbCognoms());
		dadesInteressat.setMunicipiCodi(anotacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(anotacio.getInteressatMunicipiNom());
		registreSortida.setDadesInteressat(dadesInteressat);
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		String idiomaExtracte = anotacio.getAssumpteIdiomaCodi();
		dadesAssumpte.setAssumpte(anotacio.getAssumpteExtracte());
		dadesAssumpte.setIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		dadesAssumpte.setTipus(
				anotacio.getAssumpteTipus());
		dadesAssumpte.setRegistreNumero(
				anotacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(
				anotacio.getAssumpteRegistreAny());
		registreSortida.setDadesAssumpte(dadesAssumpte);
		if (anotacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: anotacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setIdiomaCodi((annex.getIdiomaCodi() != null) ? annex.getIdiomaCodi() : "ca");
				document.setData(annex.getData());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreSortida.setDocuments(documents);
		}*/
		RegistreIdDto respostaPlugin = pluginHelper.registreAnotacioSortida(
				anotacio);
		RegistreIdDto resposta = new RegistreIdDto();
		resposta.setNumero(respostaPlugin.getNumero());
		resposta.setData(respostaPlugin.getData());
		return resposta;
	}

	@Override
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero) throws PluginException {
		return pluginHelper.registreDataJustificantRecepcio(registreNumero);
	}

	@Override
	public String registreObtenirOficinaNom(String oficinaCodi)
			throws PluginException {
		return pluginHelper.registreOficinaNom(oficinaCodi);
	}

	@Override
	public RegistreIdDto notificacioCrear(
			RegistreNotificacioDto notificacio) throws PluginException {
		RegistreNotificacio registreNotificacio = new RegistreNotificacio();
		DadesOficina dadesOficina = new DadesOficina();
		dadesOficina.setOrganCodi(notificacio.getOrganCodi());
		dadesOficina.setOficinaCodi(notificacio.getOficinaCodi());
		registreNotificacio.setDadesOficina(dadesOficina);
		DadesInteressat dadesInteressat = new DadesInteressat();
		dadesInteressat.setAutenticat(true);
		dadesInteressat.setEntitatCodi(notificacio.getEntitatCodi());
		dadesInteressat.setNomAmbCognoms(notificacio.getInteressatNomAmbCognoms());
		dadesInteressat.setMunicipiCodi(notificacio.getInteressatMunicipiCodi());
		dadesInteressat.setMunicipiNom(notificacio.getInteressatMunicipiNom());
		dadesInteressat.setNif(notificacio.getInteressatNif());
		registreNotificacio.setDadesInteressat(dadesInteressat);
		DadesExpedient dadesExpedient = new DadesExpedient();
		dadesExpedient.setIdentificador(notificacio.getExpedientIdentificador());
		dadesExpedient.setClau(notificacio.getExpedientClau());
		dadesExpedient.setUnitatAdministrativa(notificacio.getExpedientUnitatAdministrativa());
		registreNotificacio.setDadesExpedient(dadesExpedient);		
		DadesAssumpte dadesAssumpte = new DadesAssumpte();
		String idiomaExtracte = notificacio.getAssumpteIdiomaCodi();
		dadesAssumpte.setAssumpte(notificacio.getAssumpteExtracte());
		dadesAssumpte.setIdiomaCodi(
				(idiomaExtracte != null) ? idiomaExtracte : "ca");
		dadesAssumpte.setTipus(
				notificacio.getAssumpteTipus());
		dadesAssumpte.setRegistreNumero(
				notificacio.getAssumpteRegistreNumero());
		dadesAssumpte.setRegistreAny(
				notificacio.getAssumpteRegistreAny());
		DadesNotificacio dadesNotificacio = new DadesNotificacio();
		dadesNotificacio.setJustificantRecepcio(notificacio.isNotificacioJustificantRecepcio());
		dadesNotificacio.setAvisTitol(notificacio.getNotificacioAvisTitol());
		dadesNotificacio.setAvisText(notificacio.getNotificacioAvisText());
		dadesNotificacio.setAvisTextSms(notificacio.getNotificacioAvisTextSms());
		dadesNotificacio.setOficiTitol(notificacio.getNotificacioOficiTitol());
		dadesNotificacio.setOficiText(notificacio.getNotificacioOficiText());
		dadesNotificacio.setIdiomaCodi(notificacio.getAssumpteIdiomaCodi());
		dadesNotificacio.setTipus(notificacio.getAssumpteTipus());
		dadesNotificacio.setAssumpte(notificacio.getAssumpteExtracte());
		dadesNotificacio.setUnitatAdministrativa(notificacio.getUnitatAdministrativa());
		dadesNotificacio.setRegistreNumero(notificacio.getAssumpteRegistreNumero());
		dadesNotificacio.setRegistreAny(notificacio.getAssumpteRegistreAny());
		registreNotificacio.setDadesNotificacio(dadesNotificacio);
		if (notificacio.getAnnexos() != null) {
			List<DocumentRegistre> documents = new ArrayList<DocumentRegistre>();
			for (RegistreAnnexDto annex: notificacio.getAnnexos()) {
				DocumentRegistre document = new DocumentRegistre();
				document.setNom(annex.getNom());
				document.setIdiomaCodi((annex.getIdiomaCodi() != null) ? annex.getIdiomaCodi() : "ca");
				document.setData(annex.getData());
				document.setArxiuNom(annex.getArxiuNom());
				document.setArxiuContingut(annex.getArxiuContingut());
				documents.add(document);
			}
			registreNotificacio.setDocuments(documents);
		}
		RespostaAnotacioRegistre respostaPlugin = pluginHelper.tramitacioRegistrarNotificacio(
				registreNotificacio);
		if (respostaPlugin.isOk()) {
			RegistreIdDto resposta = new RegistreIdDto();
			resposta.setNumero(respostaPlugin.getNumero());
			resposta.setData(respostaPlugin.getData());
			ReferenciaRDSJustificanteDto referenciaRDSJustificante = new ReferenciaRDSJustificanteDto();
			referenciaRDSJustificante.setClave(respostaPlugin.getReferenciaRDSJustificante().getClave());
			referenciaRDSJustificante.setCodigo(respostaPlugin.getReferenciaRDSJustificante().getCodigo());
			resposta.setReferenciaRDSJustificante(referenciaRDSJustificante);			
			return resposta;
		} else {
			throw new PluginException("[" + respostaPlugin.getErrorCodi() + "]: " + respostaPlugin.getErrorDescripcio());
		}
	}

	@Override
	public RespostaJustificantRecepcioDto notificacioElectronicaJustificant(
			String registreNumero) throws PluginException, TramitacioPluginException {
		RespostaJustificantRecepcio resposta = pluginHelper.tramitacioObtenirJustificant(
				registreNumero);
		if (!resposta.isError()) {
			return conversioTipusHelper.convertir(resposta, RespostaJustificantRecepcioDto.class);
		} else {
			throw new PluginException("[" + resposta.getErrorCodi() + "]: " + resposta.getErrorDescripcio());
		}
	}

	@Override
	public RespostaJustificantDetallRecepcioDto notificacioElectronicaJustificantDetall(
			String registreNumero) throws PluginException, TramitacioPluginException {
		RespostaJustificantDetallRecepcio resposta =  pluginHelper.tramitacioObtenirJustificantDetall(
				registreNumero);
		if (!resposta.isError()) {
			return conversioTipusHelper.convertir(resposta, RespostaJustificantDetallRecepcioDto.class);
		} else {
			throw new PluginException("[" + resposta.getErrorCodi() + "]: " + resposta.getErrorDescripcio());
		}
	}

	@Override
	public void notificacioGuardar(
			Long expedientId,
			String numero,
			Date data,
			String RDSClave,
			Long RDSCodigo) {
		logger.debug("Guardant una notificació de l'expedient (" +
				"expedientId=" + expedientId + ", " +
				"numero=" + numero + ", " +
				"data=" + data + ", " +
				"RDSClave=" + RDSClave + ", " +
				"RDSCodigo=" + RDSCodigo + ")");
		notificacioElectronicaHelper.create(
				expedientId,
				numero,
				data,
				RDSClave,
				RDSCodigo);
	}

	@Override
	public boolean notificacioEsborrar(
			String numero,
			String clave,
			Long codigo) {
		logger.debug("Esborrar una notificació de l'expedient (" +
				"numero=" + numero + ", " +
				"RDSClave=" + clave + ", " +
				"RDSCodigo=" + codigo + ")");
		return notificacioElectronicaHelper.delete(
				numero,
				clave,
				codigo);
	}

	@Override
	public void portasignaturesEnviar(
			Long documentId,
			List<Long> annexosId,
			PersonaDto persona,
			List<PersonaDto> personesPas1,
			int minSignatarisPas1,
			List<PersonaDto> personesPas2,
			int minSignatarisPas2,
			List<PersonaDto> personesPas3,
			int minSignatarisPas3,
			Long expedientId,
			String importancia,
			Date dataLimit,
			Long tokenId,
			Long processInstanceId,
			String transicioOK,
			String transicioKO) throws PluginException {
		DocumentDto document = documentHelper.getDocumentVista(
				documentId,
				true,
				true);
		List<DocumentDto> annexos = null;
		if (annexosId != null) {
			annexos = new ArrayList<DocumentDto>();
			for (Long docId: annexosId) {
				DocumentDto docDto = documentHelper.getDocumentVista(
						docId,
						false,
						false);
				if (docDto != null){
					annexos.add(docDto);
				}
			}
		}
		pluginHelper.portasignaturesEnviar(
				document,
				annexos,
				persona,
				personesPas1,
				minSignatarisPas1,
				personesPas2,
				minSignatarisPas2,
				personesPas3,
				minSignatarisPas3,
				expedientRepository.findOne(expedientId),
				importancia,
				dataLimit,
				tokenId,
				processInstanceId,
				transicioOK,
				transicioKO);
	}

	@Override
	public void portasignaturesEliminar(
			List<Integer> documentIds) throws PluginException {
		pluginHelper.portasignaturesCancelar(documentIds);
	}

	@Override
	public void zonaperExpedientCrear(
			ExpedientDto expedient,
			ZonaperExpedientDto dadesExpedient) throws PluginException {
		try {
			String identificador = expedient.getNumeroIdentificador();
			String clau = new Long(System.currentTimeMillis()).toString();
			dadesExpedient.setExpedientIdentificador(identificador);
			dadesExpedient.setExpedientClau(clau);
			pluginHelper.tramitacioZonaperExpedientCrear(expedient, dadesExpedient);
			Expedient ex = expedientRepository.findOne(expedient.getId());
			ex.setTramitExpedientIdentificador(identificador);
			ex.setTramitExpedientClau(clau);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

	@Override
	public void zonaperEventCrear(
			String processInstanceId,
			ZonaperEventDto dadesEvent) throws PluginException {
		try {
			Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
			pluginHelper.tramitacioZonaperEventCrear(expedient, dadesEvent);
		} catch (Exception e) {
			throw new PluginException(e);
		}
	}

	@Override
	public TramitDto getTramit(String numero, String clau) {
		logger.debug("Obtenint dades del tràmit (numero=" + numero + ", clau=" + clau + ")");
		return conversioTipusHelper.convertir(
				pluginHelper.tramitacioObtenirDadesTramit(numero, clau),
				TramitDto.class);
	}

	@Override
	public ArxiuDto getArxiuGestorDocumental(String id) {
		logger.debug("Obtenint arxiu de la gestió documental (id=" + id + ")");
		ArxiuDto arxiu = new ArxiuDto();
		arxiu.setContingut(
				pluginHelper.gestioDocumentalObtenirDocument(id));
		return arxiu;
	}

	@Override
	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
			Long entornId,
			String expedientTipusCodi,
			String estatCodi) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		logger.debug("Obtenint l'estat donat l'entorn, el tipus d'expedient i el codi (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"estatCodi=" + estatCodi + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertir(
				estatRepository.findByExpedientTipusAndCodi(
						expedientTipus,
						estatCodi),
				EstatDto.class);
	}

	@Override
	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String documentCodi) throws DefinicioProcesNotFoundException {
		logger.debug("Obtenint el document de disseny donada la definició de procés i el codi (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"documentCodi=" + documentCodi + ")");
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		return conversioTipusHelper.convertir(
				documentRepository.findByDefinicioProcesAndCodi(
						definicioProces,
						documentCodi),
				DocumentDissenyDto.class);
	}

	@Override
	public void expedientRelacionar(
			Long expedientIdOrigen,
			Long expedientIdDesti) throws ExpedientNotFoundException {
		logger.debug("Relacionant els expedients (" +
				"expedientIdOrigen=" + expedientIdOrigen + ", " +
				"expedientIdDesti=" + expedientIdDesti + ")");
		Expedient origen = expedientRepository.findOne(expedientIdOrigen);
		if (origen == null)
			throw new ExpedientNotFoundException();
		Expedient desti = expedientRepository.findOne(expedientIdDesti);
		if (desti == null)
			throw new ExpedientNotFoundException();
		expedientHelper.relacioCrear(
				origen,
				desti);
	}

	@Override
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		logger.debug("Redirigint el token (" +
				"tokenId=" + tokenId + ", " +
				"nodeName=" + nodeName + ", " +
				"cancelarTasques=" + cancelarTasques + ")");
		expedientHelper.tokenRetrocedir(
				new Long(tokenId).toString(),
				nodeName,
				cancelarTasques);
	}

	@Override
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi) throws DefinicioProcesNotFoundException, CampNotFoundException {
		logger.debug("Obtenint la dada de l'instància de procés (processInstanceId=" + processInstanceId + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
				processInstanceId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Camp camp = campRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				varCodi);
		if (camp == null)
			throw new CampNotFoundException();
		ExpedientDadaDto resposta = new ExpedientDadaDto();
		Object valor = jbpmHelper.getProcessInstanceVariable(
				processInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextPerCamp(
						camp, 
						valor, 
						null, 
						null, 
						processInstanceId));
		return resposta;
	}

	@Override
	public ExpedientDto findExpedientAmbMateixTipusINumero(
			Long entornId,
			Long expedientTipusId,
			String numero) {
		logger.debug("findExpedientsAmbMateixTipusINumero (" +
				"entornId=" + entornId + ", " +
				"numero=" + numero + ", " +
				"expedientTipusId=" + expedientTipusId + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		Expedient expedient = expedientRepository.findByEntornAndTipusAndNumero(
				entorn,
				expedientTipus,
				numero);
		if (expedient == null)
			return null;
		return conversioTipusHelper.convertir(expedient, ExpedientDto.class);
	}

	@Override
	public List<ExpedientDto> findExpedientsConsultaGeneral(
			Long entornId,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			Long expedientTipusId,
			Long estatId,
			boolean nomesIniciats,
			boolean nomesFinalitzats) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		logger.debug("Consultant expedients (" +
				"entornId=" + entornId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"estatId=" + estatId + ", " +
				"nomesFinalitzats=" + nomesFinalitzats + ", " +
				"nomesFinalitzats=" + nomesFinalitzats + ")");
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = null;
		Estat estat = null;
		if (expedientTipusId != null) {
			expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
			if (expedientTipus == null)
				throw new ExpedientTipusNotFoundException();
			if (estatId != null) {
				estat = estatRepository.findOne(estatId);
				if (estat == null)
					throw new EstatNotFoundException();
			}
		}
		return conversioTipusHelper.convertirList(
				expedientHelper.findByFiltreGeneral(
						entorn,
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedientTipus,
						estat,
						nomesIniciats,
						nomesFinalitzats),
				ExpedientDto.class);
	}

	@Override
	@Transactional
	public void initializeDefinicionsProces() {
		List<ExpedientTipus> llistat = expedientTipusRepository.findAll();
		for (ExpedientTipus expedientTipus: llistat) {
			Hibernate.initialize(expedientTipus.getDefinicionsProces());
		}
	}

	@Override
	public boolean mesuraIsActiu() {
		return MesuresTemporalsHelper.isActiu();
	}
	@Override
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
	}
	@Override
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
	}

	@Override
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateExpedientError(
			Long jobId,
			Long expedientId,
			String errorDesc,
			String errorFull) {
		logger.error("JOB (" + jobId + "): Actualitzant error de l'expedient");
//		if (jobId != null)
//			jbpmHelper.retryJob(jobId);
		Expedient expedient = expedientRepository.findOne(expedientId);
		expedient.setErrorDesc(errorDesc);
		expedient.setErrorFull(errorFull);
		expedientRepository.save(expedient);
//		expedientHelper.updateError(
//				expedient,
//				errorDesc,
//				errorFull);
	}

	@Override
	public String getHeliumProperty(String propertyName) {
		return GlobalProperties.getInstance().getProperty(propertyName);
	}

	@Override
	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		/*net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto dto = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
		return conversioTipusHelper.convertir(
				dto,
				OperacioMassivaDto.class);*/
		return null;
	}

	@Override
	public void executarExecucioMassiva(OperacioMassivaDto operacioMassiva) throws Exception {
		/*execucioMassivaService.executarExecucioMassiva(
				conversioTipusHelper.convertir(
						operacioMassiva,
						net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto.class));*/
	}

	@Override
	public void generaInformeError(OperacioMassivaDto operacioMassiva, Exception e) {
		//execucioMassivaService.generaInformeError(operacioMassiva, e);
	}

	@Override
	public void actualitzaUltimaOperacio(OperacioMassivaDto operacioMassiva) {
		//execucioMassivaService.actualitzaUltimaOperacio(operacioMassiva);
	}

	@Override
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
	
	@Override
	public List<String> getRolsByCodi(String codi) {
		List<String> rols = new ArrayList<String>();
		try {
			if (pluginHelper.personaIsPluginActiu())
				rols = pluginHelper.personaFindRolsAmbCodi(codi);
		} catch (Exception ex) {
			// En cas que hi hagi una excepció,retornarem la llista buida 
		}
		return rols;
	}
	
	@Override
	public void setErrorTascaSegonPla(Long taskId, Exception ex) {
		if (tascaSegonPlaHelper.isTasquesSegonPlaLoaded()) {
		Map<Long, InfoSegonPla> map = tascaSegonPlaHelper.getTasquesSegonPla();
			if (map.containsKey(taskId)) {
				map.get(taskId).setError((ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
			}
		}
	}



	private Expedient getExpedientDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		return expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
	}
	private DefinicioProces getDefinicioProcesDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(processInstanceId);
		return definicioProcesRepository.findByJbpmId(
				processInstance.getProcessDefinitionId());
	}

	private Entorn getEntornDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new ProcessInstanceNotFoundException();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		return expedient.getEntorn();
	}

	private static final Logger logger = LoggerFactory.getLogger(Jbpm3HeliumHelper.class);

}
