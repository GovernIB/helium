/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.extern.domini.ParellaCodiValor;
import net.conselldemallorca.helium.core.model.dao.AreaDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.CarrecDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.core.model.dao.EstatDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.FestiuDao;
import net.conselldemallorca.helium.core.model.dao.MailDao;
import net.conselldemallorca.helium.core.model.dao.PluginGestioDocumentalDao;
import net.conselldemallorca.helium.core.model.dao.PluginPersonaDao;
import net.conselldemallorca.helium.core.model.dao.PluginRegistreDao;
import net.conselldemallorca.helium.core.model.dao.PluginTramitacioDao;
import net.conselldemallorca.helium.core.model.dao.ReassignacioDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiDao;
import net.conselldemallorca.helium.core.model.dao.TerminiIniciatDao;
import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
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
import net.conselldemallorca.helium.core.model.service.AlertaService;
import net.conselldemallorca.helium.core.model.service.DocumentHelper;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.TerminiService;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesAssumpte;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesInteressat;
import net.conselldemallorca.helium.integracio.plugins.registre.DadesOficina;
import net.conselldemallorca.helium.integracio.plugins.registre.DocumentRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.integracio.plugins.tramitacio.ObtenirDadesTramitRequest;
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
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnnexDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreAnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.RegistreNotificacioDto;
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
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService.FiltreAnulat;
import net.conselldemallorca.helium.v3.core.api.service.Jbpm3HeliumService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.DtoConverter;
import net.conselldemallorca.helium.v3.core.helper.VariableHelper;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service que implementa la funcionalitat necessària per
 * a integrar Helium i jBPM.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("heliumServiceV3")
public class Jbpm3HeliumServiceImpl implements Jbpm3HeliumService {
	@Resource
	private EntornDao entornDao;
	@Resource
	private ExpedientTipusDao expedientTipusDao;
	@Resource
	private ExpedientDao expedientDao;
	@Resource
	private DefinicioProcesDao definicioProcesDao;
	@Resource
	private PluginPersonaDao pluginPersonaDao;
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
	private TerminiDao terminiDao;
	@Resource
	private TerminiIniciatDao terminiIniciatDao;
	@Resource
	private DominiDao dominiDao;
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
	private AlertaService alertaService;
	@Resource
	private ExpedientService expedientService;
	@Resource
	private DocumentService documentService;
	@Resource
	private TerminiService terminiService;
	@Resource
	private ExecucioMassivaService execucioMassivaService;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DocumentHelper documentHelper;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	@Transactional(readOnly = true)
	@Override
	public String getUsuariCodiActual() {
		imprimirFuncio("getUsuariCodiActual");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName();
	}

	@Transactional(readOnly = true)
	@Override
	public EntornDto getEntornActual() {
		imprimirFuncio("getEntornActual");
		Long entornId = EntornActual.getEntornId();
		logger.debug("Obtenint entorn actual (idEntornActual=" + entornId + ")");
		if (entornId == null)
			return null;
		return conversioTipusHelper.convertir(
				entornDao.getById(entornId, false),
				EntornDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto getExpedientIniciant() {
		imprimirFuncio("getExpedientIniciant");
		logger.debug("Obtenint expedient en fase d'inici");
		return conversioTipusHelper.convertir(
				ExpedientIniciantDto.getExpedient(),
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto getExpedientAmbEntornITipusINumero(
			Long entornId,
			String expedientTipusCodi,
			String numero) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		imprimirFuncio("getExpedientAmbEntornITipusINumero");
		logger.debug("Obtenint expedient donat entorn, tipus i número (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"numero=" + numero + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusDao.findAmbEntornICodi(
				entornId,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertir(
				expedientDao.findAmbEntornTipusINumero(
						entornId,
						expedientTipus.getId(),
						numero),
				ExpedientDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ExpedientDto getExpedientArrelAmbProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		imprimirFuncio("getExpedientArrelAmbProcessInstanceId");
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getExpedientDonatProcessInstanceId(processInstanceId),
				ExpedientDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public EntornDto getEntornAmbProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		imprimirFuncio("getEntornAmbProcessInstanceId");
		logger.debug("Obtenint expedient donada una instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getEntornDonatProcessInstanceId(processInstanceId),
				EntornDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int version) {
		imprimirFuncio("getDefinicioProcesAmbJbpmKeyIVersio");
		logger.debug("Obtenint la definició de procés donat el codi jBPM i la versió (jbpmKey=" + jbpmKey + ", version=" + version +")");
		return conversioTipusHelper.convertir(
				definicioProcesDao.findAmbJbpmKeyIVersio(
						jbpmKey,
						version),
				DefinicioProcesDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws EntornNotFoundException {
		imprimirFuncio("getDarreraVersioAmbEntornIJbpmKey");
		logger.debug("Obtenint la darrera versió de la definició de procés donat l'entorn i el codi jBPM (entornId=" + entornId + ", jbpmKey=" + jbpmKey + ")");
		return conversioTipusHelper.convertir(
				definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
						entornId,
						jbpmKey),
				DefinicioProcesDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		imprimirFuncio("getDefinicioProcesPerProcessInstanceId");
		logger.debug("Obtenint la definició de procés donada la instància de procés (processInstanceId=" + processInstanceId + ")");
		return conversioTipusHelper.convertir(
				getDefinicioProcesDonatProcessInstanceId(processInstanceId),
				DefinicioProcesDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PersonaDto getPersonaAmbCodi(String codi) {
		imprimirFuncio("getPersonaAmbCodi");
		logger.debug("Obtenint persona (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				pluginPersonaDao.findAmbCodiPlugin(codi),
				PersonaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi) throws EntornNotFoundException {
		imprimirFuncio("getAreaAmbEntornICodi");
		logger.debug("Obtenint area donat l'entorn i el codi (" +
				"entornId=" + entornId + ", " +
				"codi=" + codi + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		return conversioTipusHelper.convertir(
				areaDao.findAmbEntornICodi(entornId, codi),
				AreaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) throws EntornNotFoundException, AreaNotFoundException {
		imprimirFuncio("getCarrecAmbEntornIAreaICodi");
		logger.debug("Obtenint carrec donat l'entorn, l'àrea i el codi (" +
				"entornId=" + entornId + ", " +
				"areaCodi=" + areaCodi + ", " +
				"carrecCodi=" + carrecCodi + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		Area area = areaDao.findAmbEntornICodi(entornId, areaCodi);
		if (area == null)
			throw new AreaNotFoundException();
		return conversioTipusHelper.convertir(
				carrecDao.findAmbEntornAreaICodi(
						entornId,
						areaCodi,
						carrecCodi),
				CarrecDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<FestiuDto> findFestiusAll() {
		imprimirFuncio("findFestiusAll");
		logger.debug("Obtenint la llista de tots els festius");
		return conversioTipusHelper.convertirList(
				festiuDao.findAll(),
				FestiuDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ReassignacioDto findReassignacioActivaPerUsuariOrigen(
			String usuariCodi) {
		imprimirFuncio("findReassignacioActivaPerUsuariOrigen");
		logger.debug("Obtenint reassignació activa per a l'usuari (usuariCodi=" + usuariCodi + ")");
		return conversioTipusHelper.convertir(
				reassignacioDao.findByUsuari(usuariCodi),
				ReassignacioDto.class);
	}

	@Transactional
	@Override
	public void alertaCrear(
			Long entornId,
			Long expedientId,
			Date data,
			String usuariCodi,
			String text) throws EntornNotFoundException, ExpedientNotFoundException {
		imprimirFuncio("alertaCrear");
		logger.debug("Creant alerta (" +
				"entornId=" + entornId + ", " +
				"expedientId=" + expedientId + ", " +
				"data=" + data + ", " +
				"usuariCodi=" + usuariCodi + ", " +
				"text=" + text + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		Expedient expedient = expedientDao.getById(expedientId, false);
		if (expedient == null)
			throw new ExpedientNotFoundException();
		Alerta alerta = new Alerta();
		alerta.setEntorn(entorn);
		alerta.setExpedient(expedient);
		alerta.setDataCreacio(data);
		alerta.setDestinatari(usuariCodi);
		alerta.setText(text);
		alertaService.create(alerta);
	}

	@Transactional
	@Override
	public void alertaEsborrarAmbTaskInstanceId(long taskInstanceId) {
		imprimirFuncio("alertaEsborrarAmbTaskInstanceId");
		logger.debug("Esborrant alertes amb taskInstance (taskInstanceId=" + taskInstanceId + ")");
		alertaService.esborrarAmbTasca(taskInstanceId);
	}

	@Transactional
	@Override
	public void expedientModificarEstat(
			String processInstanceId,
			String estatCodi) throws ProcessInstanceNotFoundException, ExpedientNotFoundException, EstatNotFoundException {
		imprimirFuncio("expedientModificarEstat");
		logger.debug("Modificant estat de l'expedient (processInstanceId=" + processInstanceId + ", estatCodi=" + estatCodi + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Estat estat = estatDao.findAmbExpedientTipusICodi(
				expedient.getTipus().getId(),
				estatCodi);
		if (estat == null)
			throw new EstatNotFoundException();
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				estat.getId(),
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientModificarComentari(
			String processInstanceId,
			String comentari) throws ExpedientNotFoundException {
		imprimirFuncio("expedientModificarComentari");
		logger.debug("Modificant comentari de l'expedient (processInstanceId=" + processInstanceId + ", comentari=" + comentari + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				comentari,
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientModificarGeoref(
			String processInstanceId,
			Double posx,
			Double posy,
			String referencia) throws ExpedientNotFoundException {
		imprimirFuncio("expedientModificarGeoref");
		logger.debug("Modificant georeferència de l'expedient (" +
				"processInstanceId=" + processInstanceId + ", " +
				"posx=" + posx + ", " +
				"posy=" + posy + ", " +
				"referencia=" + referencia + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				posx,
				posy,
				referencia,
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientModificarGrup(
			String processInstanceId,
			String grupCodi) throws ExpedientNotFoundException {
		imprimirFuncio("expedientModificarGrup");
		logger.debug("Modificant grup de l'expedient (processInstanceId=" + processInstanceId + ", grupCodi=" + grupCodi + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				grupCodi);
	}

	@Transactional
	@Override
	public void expedientModificarNumero(
			String processInstanceId,
			String numero) throws ExpedientNotFoundException {
		imprimirFuncio("expedientModificarNumero");
		logger.debug("Modificant número de l'expedient (processInstanceId=" + processInstanceId + ", numero=" + numero + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				numero,
				expedient.getTitol(),
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientModificarResponsable(
			String processInstanceId,
			String responsableCodi) throws ExpedientNotFoundException, PersonaNotFoundException {
		imprimirFuncio("expedientModificarResponsable");
		logger.debug("Modificant responsable de l'expedient (processInstanceId=" + processInstanceId + ", responsableCodi=" + responsableCodi + ")");
		if (pluginPersonaDao.findAmbCodiPlugin(responsableCodi) == null)
			throw new PersonaNotFoundException();
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				expedient.getTitol(),
				responsableCodi,
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientModificarTitol(
			String processInstanceId,
			String titol) throws ExpedientNotFoundException {
		imprimirFuncio("expedientModificarTitol");
		logger.debug("Modificant títol de l'expedient (processInstanceId=" + processInstanceId + ", titol=" + titol + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.editar(
				expedient.getEntorn().getId(),
				expedient.getId(),
				expedient.getNumero(),
				titol,
				expedient.getResponsableCodi(),
				expedient.getDataInici(),
				expedient.getComentari(),
				(expedient.getEstat() != null) ? expedient.getEstat().getId() : null,
				expedient.getGeoPosX(),
				expedient.getGeoPosY(),
				expedient.getGeoReferencia(),
				expedient.getGrupCodi());
	}

	@Transactional
	@Override
	public void expedientAturar(
			String processInstanceId,
			String motiu) throws ExpedientNotFoundException {
		imprimirFuncio("expedientAturar");
		logger.debug("Aturant expedient (processInstanceId=" + processInstanceId + ", motiu=" + motiu + ")");
		getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.aturar(
				processInstanceId,
				motiu,
				null);
	}

	@Transactional
	@Override
	public void expedientReprendre(
			String processInstanceId) throws ExpedientNotFoundException {
		imprimirFuncio("expedientReprendre");
		logger.debug("Reprenent expedient (processInstanceId=" + processInstanceId + ")");
		getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.reprendre(processInstanceId, null);
	}

	@Transactional
	@Override
	public void expedientReindexar(
			String processInstanceId) throws ExpedientNotFoundException {
		imprimirFuncio("expedientReindexar");
		logger.debug("Indexant expedient (processInstanceId=" + processInstanceId + ")");
		getExpedientDonatProcessInstanceId(processInstanceId);
		expedientService.luceneReindexarExpedient(processInstanceId);
	}

	@Transactional
	@Override
	public ArxiuDto documentGenerarAmbPlantilla(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi,
			Date dataDocument,
			boolean forsarAdjuntarAuto) throws DefinicioProcesNotFoundException, DocumentNotFoundException, DocumentGenerarException {
		imprimirFuncio("documentGenerarAmbPlantilla");
		logger.debug("Generant document amb plantilla (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"dataDocument=" + dataDocument + ", " +
				"forsarAdjuntarAuto=" + forsarAdjuntarAuto + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Document document = documentDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				documentCodi);
		if (document == null)
			throw new DocumentNotFoundException();
		try {
			net.conselldemallorca.helium.core.model.dto.DocumentDto generat = documentService.generarDocumentPlantilla(
					definicioProces.getEntorn().getId(),
					document.getId(),
					taskInstanceId,
					processInstanceId,
					dataDocument,
					forsarAdjuntarAuto);
			ArxiuDto arxiu = new ArxiuDto();
			arxiu.setNom(generat.getArxiuNom());
			arxiu.setContingut(generat.getArxiuContingut());
			return arxiu;
		} catch (Exception ex) {
			throw new DocumentGenerarException(ex);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public TerminiDto getTerminiAmbDefinicioProcesICodi(
			String processInstanceId,
			String terminiCodi) throws ProcessInstanceNotFoundException {
		imprimirFuncio("getTerminiAmbDefinicioProcesICodi");
		logger.debug("Obtenint termini donada la instància de procés i el codi (processInstanceId=" + processInstanceId + ", terminiCodi=" + terminiCodi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		return conversioTipusHelper.convertir(
				terminiDao.findAmbDefinicioProcesICodi(
						definicioProces.getId(),
						terminiCodi),
						TerminiDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public TerminiIniciatDto getTerminiIniciatAmbProcessInstanceITerminiCodi(
			String processInstanceId,
			String terminiCodi)
			throws TerminiNotFoundException {
		imprimirFuncio("getTerminiIniciatAmbProcessInstanceITerminiCodi");
		logger.debug("Obtenint termini iniciat donada la instància de procés i el codi (" +
				"processInstanceId=" + processInstanceId + ", " +
				"terminiCodi=" + terminiCodi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		return conversioTipusHelper.convertir(
				terminiIniciatDao.findAmbTerminiIdIProcessInstanceId(
						termini.getId(),
						processInstanceId),
				TerminiIniciatDto.class);
	}

	@Transactional
	@Override
	public void configurarTerminiIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException {
		imprimirFuncio("configurarTerminiIniciatAmbDadesJbpm");
		logger.debug("Configurant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"timerId=" + timerId + ")");
		TerminiIniciat termini = terminiIniciatDao.getById(terminiIniciatId, false);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiService.configurarTerminiIniciatAmbDadesJbpm(
				terminiIniciatId,
				taskInstanceId,
				timerId);
	}

	@Transactional
	@Override
	public Date terminiCalcularDataInici(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		imprimirFuncio("terminiCalcularDataInici");
		logger.debug("Calculant data d'inici de termini a partir d'una data de fi (" +
				"fi=" + fi + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiService.getDataIniciTermini(fi, anys, mesos, dies, laborable);
	}

	@Transactional
	@Override
	public Date terminiCalcularDataFi(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable) {
		imprimirFuncio("terminiCalcularDataFi");
		logger.debug("Calculant data de fi de termini a partir d'una data d'inici (" +
				"inici=" + inici + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"laborable=" + laborable + ")");
		return terminiService.getDataFiTermini(inici, anys, mesos, dies, laborable);
	}

	@Transactional
	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws TerminiNotFoundException {
		imprimirFuncio("terminiIniciar1");
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"anys=" + anys + ", " +
				"mesos=" + mesos + ", " +
				"dies=" + dies + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		terminiService.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				anys,
				mesos,
				dies,
				esDataFi);
		
	}

	@Transactional
	@Override
	public void terminiIniciar(
			String terminiCodi,
			String processInstanceId,
			Date data,
			boolean esDataFi) throws TerminiNotFoundException {
		imprimirFuncio("terminiIniciar2");
		logger.debug("Iniciant termini (" +
				"terminiCodi=" + terminiCodi + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"data=" + data + ", " +
				"esDataFi=" + esDataFi + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(processInstanceId);
		Termini termini = terminiDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				terminiCodi);
		if (termini == null)
			throw new TerminiNotFoundException();
		terminiService.iniciar(
				termini.getId(),
				processInstanceId,
				data,
				esDataFi);
	}

	@Transactional
	@Override
	public void terminiCancelar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		imprimirFuncio("terminiCancelar");
		logger.debug("Cancelant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiIniciatDao.getById(terminiIniciatId, false);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiService.cancelar(terminiIniciatId, data);
	}

	@Transactional
	@Override
	public void terminiPausar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		imprimirFuncio("terminiPausar");
		logger.debug("Pausant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiIniciatDao.getById(terminiIniciatId, false);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiService.pausar(terminiIniciatId, data);
	}

	@Transactional
	@Override
	public void terminiContinuar(
			Long terminiIniciatId,
			Date data) throws TerminiIniciatNotFoundException {
		imprimirFuncio("terminiContinuar");
		logger.debug("Continuant termini iniciat (" +
				"terminiIniciatId=" + terminiIniciatId + ", " +
				"data=" + data + ")");
		TerminiIniciat termini = terminiIniciatDao.getById(terminiIniciatId, false);
		if (termini == null)
			throw new TerminiIniciatNotFoundException();
		terminiService.continuar(terminiIniciatId, data);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DominiRespostaFilaDto> dominiConsultar(
			String processInstanceId,
			String dominiCodi,
			String dominiId,
			Map<String, Object> parametres) throws ExpedientNotFoundException, DominiNotFoundException, DominiConsultaException {
		imprimirFuncio("dominiConsultar");
		logger.debug("Executant una consulta de domini (" +
				"processInstanceId=" + processInstanceId + ", " +
				"dominiCodi=" + dominiCodi + ", " +
				"dominiId=" + dominiId + ", " +
				"parametres=" + parametres + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Domini domini = dominiDao.findAmbEntornICodi(
				expedient.getEntorn().getId(),
				dominiCodi);
		if (domini == null)
			throw new DominiNotFoundException();
		try {
			List<FilaResultat> files = dominiDao.consultar(
					expedient.getEntorn().getId(),
					domini.getId(),
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

	@Transactional(readOnly = true)
	@Override
	public List<EnumeracioValorDto> enumeracioConsultar(
			String processInstanceId,
			String enumeracioCodi) throws ExpedientNotFoundException, EnumeracioNotFoundException {
		imprimirFuncio("enumeracioConsultar");
		logger.debug("Consultant els valors d'una enumeració (" +
				"processInstanceId=" + processInstanceId + ", " +
				"enumeracioCodi=" + enumeracioCodi + ")");
		Expedient expedient = getExpedientDonatProcessInstanceId(processInstanceId);
		Enumeracio enumeracio = enumeracioDao.findAmbEntornAmbOSenseTipusExpICodi(
				expedient.getEntorn().getId(),
				expedient.getTipus().getId(),
				enumeracioCodi);
		if (enumeracio == null)
			throw new EnumeracioNotFoundException();
		return conversioTipusHelper.convertirList(
				enumeracio.getEnumeracioValors(),
				EnumeracioValorDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CampTascaDto> findCampsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		imprimirFuncio("findCampsPerTaskInstance");
		logger.debug("Consultant els camps del formulari de la tasca (taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new TaskInstanceNotFoundException();
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Tasca tasca = tascaDao.findAmbActivityNameIDefinicioProces(
				task.getName(),
				definicioProces.getId());
		if (tasca == null)
			throw new TascaNotFoundException();
		return conversioTipusHelper.convertirList(
				campTascaDao.findAmbTascaOrdenats(tasca.getId()),
				CampTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException, DefinicioProcesNotFoundException, TascaNotFoundException {
		imprimirFuncio("findDocumentsPerTaskInstance");
		logger.debug("Consultant els documents de la tasca (taskInstanceId=" + taskInstanceId + ")");
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		if (task == null)
			throw new TaskInstanceNotFoundException();
		DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmId(
				task.getProcessDefinitionId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Tasca tasca = tascaDao.findAmbActivityNameIDefinicioProces(
				task.getName(),
				definicioProces.getId());
		if (tasca == null)
			throw new TascaNotFoundException();
		return conversioTipusHelper.convertirList(
				documentTascaDao.findAmbTascaOrdenats(tasca.getId()),
				DocumentTascaDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public String getCodiVariablePerDocumentCodi(String documentCodi) {
		imprimirFuncio("getCodiVariablePerDocumentCodi");
		logger.debug("Obtenint el codi de variable jBPM pel document (documentCodi=" + documentCodi + ")");
		return documentHelper.getVarPerDocumentCodi(documentCodi, false);
	}

	@Transactional(readOnly = true)
	@Override
	public DocumentDto getDocumentInfo(Long documentStoreId) {
		imprimirFuncio("getDocumentInfo");
		logger.debug("Obtenint informació del document (documentStoreId=" + documentStoreId + ")");
		return conversioTipusHelper.convertir(
				documentService.documentInfo(documentStoreId),
				DocumentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDto getArxiuPerMostrar(Long documentStoreId) {
		imprimirFuncio("getArxiuPerMostrar");
		logger.debug("Obtenint arxiu del document (documentStoreId=" + documentStoreId + ")");
		return conversioTipusHelper.convertir(
				documentService.arxiuDocumentPerMostrar(documentStoreId),
				ArxiuDto.class);
	}

	@Transactional
	@Override
	public Long documentExpedientGuardar(
			String processInstanceId,
			String documentCodi,
			Date data,
			String arxiuNom,
			byte[] arxiuContingut) {
		imprimirFuncio("documentExpedientGuardar");
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

	@Transactional
	@Override
	public Long documentExpedientAdjuntar(
			String processInstanceId,
			String adjuntId,
			String adjuntTitol,
			Date adjuntData,
			String arxiuNom,
			byte[] arxiuContingut) {
		imprimirFuncio("documentExpedientAdjuntar");
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

	@Transactional
	@Override
	public void documentExpedientEsborrar(
			String taskInstanceId,
			String processInstanceId,
			String documentCodi) {
		imprimirFuncio("documentExpedientEsborrar");
		logger.debug("Esborrant un document de dins l'expedient (" +
				"taskInstanceId=" + taskInstanceId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"documentCodi=" + documentCodi + ")");
		documentHelper.esborrarDocument(
				taskInstanceId,
				processInstanceId,
				documentCodi);
	}

	@Transactional
	@Override
	public void documentExpedientGuardarDadesRegistre(
			Long documentStoreId,
			String registreNumero,
			Date registreData,
			String registreOficinaCodi,
			String registreOficinaNom,
			boolean registreEntrada) {
		imprimirFuncio("documentExpedientGuardarDadesRegistre");
		logger.debug("Esborrant un document de dins l'expedient (" +
				"documentStoreId=" + documentStoreId + ", " +
				"registreNumero=" + registreNumero + ", " +
				"registreData=" + registreData + ", " +
				"registreOficinaCodi=" + registreOficinaCodi + ", " +
				"registreOficinaNom=" + registreOficinaNom + ", " +
				"registreEntrada=" + registreEntrada + ")");
		documentService.guardarDadesRegistre(
				documentStoreId,
				registreNumero,
				registreData,
				registreOficinaCodi,
				registreOficinaNom,
				registreEntrada);
	}

	@Transactional
	@Override
	public void emailSend(
			String fromAddress,
			List<String> recipients,
			List<String> ccRecipients,
			List<String> bccRecipients,
			String subject,
			String text,
			List<ArxiuDto> attachments) throws PluginException {
		imprimirFuncio("emailSend");
		logger.debug("Enviant correu (" +
				"fromAddress=" + fromAddress + ", " +
				"recipients=" + recipients + ", " +
				"ccRecipients=" + ccRecipients + ", " +
				"bccRecipients=" + bccRecipients + ", " +
				"subject=" + subject + ", " +
				"text=" + text + ")");
		try {
			mailDao.send(
					fromAddress,
					recipients,
					ccRecipients,
					bccRecipients,
					subject,
					text,
					conversioTipusHelper.convertirList(
							attachments,
							net.conselldemallorca.helium.core.model.dto.ArxiuDto.class));
		} catch (Exception ex) {
			throw new PluginException(ex);
		}
	}

	@Transactional
	@Override
	public boolean isRegistreActiu() {
		imprimirFuncio("isRegistreActiu");
		logger.debug("Comprovant si el registre està actiu");
		return pluginRegistreDao.isRegistreActiu();
	}

	@Transactional
	@Override
	public RegistreIdDto registreAnotacioEntrada(
			RegistreAnotacioDto anotacio) throws PluginException {
		imprimirFuncio("registreAnotacioEntrada");
		RegistreEntrada registreEntrada = new RegistreEntrada();
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
		}
		RespostaAnotacioRegistre respostaPlugin = pluginRegistreDao.registrarEntrada(
				registreEntrada);
		if (respostaPlugin.isOk()) {
			RegistreIdDto resposta = new RegistreIdDto();
			resposta.setNumero(respostaPlugin.getNumero());
			resposta.setData(respostaPlugin.getData());
			return resposta;
		} else {
			throw new PluginException("[" + respostaPlugin.getErrorCodi() + "]: " + respostaPlugin.getErrorDescripcio());
		}
	}

	@Transactional
	@Override
	public RegistreIdDto registreAnotacioSortida(RegistreAnotacioDto anotacio)
			throws PluginException {
		imprimirFuncio("registreAnotacioSortida");
		RegistreSortida registreSortida = new RegistreSortida();
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
		}
		RespostaAnotacioRegistre respostaPlugin = pluginRegistreDao.registrarSortida(
				registreSortida);
		if (respostaPlugin.isOk()) {
			RegistreIdDto resposta = new RegistreIdDto();
			resposta.setNumero(respostaPlugin.getNumero());
			resposta.setData(respostaPlugin.getData());
			return resposta;
		} else {
			throw new PluginException("[" + respostaPlugin.getErrorCodi() + "]: " + respostaPlugin.getErrorDescripcio());
		}
	}

	@Transactional
	@Override
	public RegistreIdDto registreNotificacio(
			RegistreNotificacioDto notificacio) throws PluginException {
		imprimirFuncio("registreNotificacio");
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public Date registreNotificacioComprovarRecepcio(
			String registreNumero) throws PluginException {
		imprimirFuncio("registreNotificacioComprovarRecepcio");
		RespostaJustificantRecepcio resposta = pluginRegistreDao.obtenirJustificantRecepcio(registreNumero);
		if (!resposta.isError()) {
			return resposta.getData();
		} else {
			throw new PluginException("[" + resposta.getErrorCodi() + "]: " + resposta.getErrorDescripcio());
		}
	}

	@Transactional
	@Override
	public String getRegistreOficinaNom(String oficinaCodi)
			throws PluginException {
		imprimirFuncio("getRegistreOficinaNom");
		return pluginRegistreDao.obtenirNomOficina(oficinaCodi);
	}

	@Transactional
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
			String transicioKO) {
		imprimirFuncio("portasignaturesEnviar");
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void zonaperExpedientCrear(String processInstanceId,
			ZonaperExpedientDto dadesExpedient) throws PluginException {
		imprimirFuncio("zonaperExpedientCrear");
		// TODO Auto-generated method stub
		
	}

	@Transactional
	@Override
	public void zonaperEventCrear(String processInstanceId,
			ZonaperEventDto dadesEvent) throws PluginException {
		imprimirFuncio("");
		// TODO Auto-generated method stub
		
	}

	@Transactional(readOnly = true)
	@Override
	public EstatDto findEstatAmbEntornIExpedientTipusICodi(
			Long entornId,
			String expedientTipusCodi,
			String estatCodi) throws EntornNotFoundException, ExpedientTipusNotFoundException {
		imprimirFuncio("findEstatAmbEntornIExpedientTipusICodi");
		logger.debug("Obtenint l'estat donat l'entorn, el tipus d'expedient i el codi (" +
				"entornId=" + entornId + ", " +
				"expedientTipusCodi=" + expedientTipusCodi + ", " +
				"estatCodi=" + estatCodi + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusDao.findAmbEntornICodi(
				entornId,
				expedientTipusCodi);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertir(
				estatDao.findAmbExpedientTipusICodi(
						expedientTipus.getId(),
						estatCodi),
				EstatDto.class);
	}

	@Transactional
	@Override
	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String documentCodi) throws DefinicioProcesNotFoundException {
		imprimirFuncio("getDocumentDisseny");
		logger.debug("Obtenint el document de disseny donada la definició de procés i el codi (" +
				"definicioProcesId=" + definicioProcesId + ", " +
				"documentCodi=" + documentCodi + ")");
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		return conversioTipusHelper.convertir(
				documentDao.findAmbDefinicioProcesICodi(
						definicioProcesId,
						documentCodi),
				DocumentDissenyDto.class);
	}

	@Transactional
	@Override
	public void expedientRelacionar(
			Long expedientIdOrigen,
			Long expedientIdDesti) throws ExpedientNotFoundException {
		imprimirFuncio("expedientRelacionar");
		logger.debug("Relacionant els expedients (" +
				"expedientIdOrigen=" + expedientIdOrigen + ", " +
				"expedientIdDesti=" + expedientIdDesti + ")");
		Expedient origen = expedientDao.getById(expedientIdOrigen, false);
		if (origen == null)
			throw new ExpedientNotFoundException();
		Expedient desti = expedientDao.getById(expedientIdDesti, false);
		if (desti == null)
			throw new ExpedientNotFoundException();
		expedientService.createRelacioExpedient(expedientIdOrigen, expedientIdDesti);
	}

	@Transactional
	@Override
	public void tokenRedirigir(
			long tokenId,
			String nodeName,
			boolean cancelarTasques) {
		imprimirFuncio("tokenRedirigir");
		logger.debug("Redirigint el token (" +
				"tokenId=" + tokenId + ", " +
				"nodeName=" + nodeName + ", " +
				"cancelarTasques=" + cancelarTasques + ")");
		expedientService.tokenRetrocedir(
				new Long(tokenId).toString(),
				nodeName,
				cancelarTasques);
	}

	@Transactional
	@Override
	public ArxiuDto getArxiuGestorDocumental(String id) {
		imprimirFuncio("getArxiuGestorDocumental");
		logger.debug("Obtenint arxiu de la gestió documental (id=" + id + ")");
		ArxiuDto arxiu = new ArxiuDto();
		arxiu.setContingut(
				pluginGestioDocumentalDao.retrieveDocument(id));
		return arxiu;
	}

	@Transactional
	@Override
	public TramitDto getTramit(String numero, String clau) {
		imprimirFuncio("getTramit");
		logger.debug("Obtenint dades del tràmit (numero=" + numero + ", clau=" + clau + ")");
		ObtenirDadesTramitRequest request = new ObtenirDadesTramitRequest();
		request.setNumero(numero);
		request.setClau(clau);
		return conversioTipusHelper.convertir(
				pluginTramitacioDao.obtenirDadesTramit(request),
				TramitDto.class);
	}

	@Transactional
	@Override
	public ExpedientDadaDto getDadaPerProcessInstance(
			String processInstanceId,
			String varCodi) throws DefinicioProcesNotFoundException, CampNotFoundException {
		imprimirFuncio("getDadaPerProcessInstance");
		logger.debug("Obtenint la dada de l'instància de procés (processInstanceId=" + processInstanceId + ")");
		DefinicioProces definicioProces = getDefinicioProcesDonatProcessInstanceId(
				processInstanceId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Camp camp = campDao.findAmbDefinicioProcesICodi(
				definicioProces.getId(),
				varCodi);
		if (camp == null)
			throw new CampNotFoundException();
		ExpedientDadaDto resposta = new ExpedientDadaDto();
		Object valor = jbpmHelper.getProcessInstanceVariable(
				processInstanceId,
				varCodi);
		resposta.setText(
				variableHelper.getTextVariableSimple(
						camp, 
						valor, 
						null, 
						null, 
						processInstanceId));
		return resposta;
	}

	@Transactional
	@Override
	public List<ExpedientDto> findExpedientsConsultaGeneral(
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
			FiltreAnulat mostrarAnulats) throws EntornNotFoundException, ExpedientTipusNotFoundException, EstatNotFoundException {
		imprimirFuncio("findExpedientsConsultaGeneral");
		logger.debug("Consultant expedients (" +
				"entornId=" + entornId + ", " +
				"titol=" + titol + ", " +
				"numero=" + numero + ", " +
				"dataInici1=" + dataInici1 + ", " +
				"dataInici2=" + dataInici2 + ", " +
				"expedientTipusId=" + expedientTipusId + ", " +
				"estatId=" + estatId + ", " +
				"iniciat=" + iniciat + ", " +
				"finalitzat=" + finalitzat + ", " +
				"geoPosX=" + geoPosX + ", " +
				"geoPosY=" + geoPosY + ", " +
				"geoReferencia=" + geoReferencia + ", " +
				"mostrarAnulats=" + mostrarAnulats + ")");
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new EntornNotFoundException();
		if (expedientTipusId != null) {
			ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
			if (expedientTipus == null)
				throw new ExpedientTipusNotFoundException();
			if (estatId != null) {
				Estat estat = estatDao.getById(estatId, false);
				if (estat == null)
					throw new EstatNotFoundException();
			}
		}
		return conversioTipusHelper.convertirList(
				expedientService.findAmbEntornConsultaGeneral(
						entornId,
						titol,
						numero,
						dataInici1,
						dataInici2,
						expedientTipusId,
						estatId,
						iniciat,
						finalitzat,
						geoPosX,
						geoPosY,
						geoReferencia,
						mostrarAnulats),
				ExpedientDto.class);
	}

	@Transactional
	@Override
	public void initializeDefinicionsProces() {
		imprimirFuncio("initializeDefinicionsProces");
		List<ExpedientTipus> llistat = expedientTipusDao.findAll();
		for (ExpedientTipus expedientTipus: llistat) {
			Hibernate.initialize(expedientTipus.getDefinicionsProces());
		}
	}
	
	@Transactional
	@Override
	public boolean mesuraIsActiu() {
		return MesuresTemporalsHelper.isActiu();
	}
	@Transactional
	@Override
	public void mesuraIniciar(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraIniciar(clau, familia, tipusExpedient, tasca, detall);
	}
	@Transactional
	@Override
	public void mesuraCalcular(String clau, String familia, String tipusExpedient, String tasca, String detall) {
		mesuresTemporalsHelper.mesuraCalcular(clau, familia, tipusExpedient, tasca, detall);
	}

	@Transactional
	@Override
	public void updateExpedientError(String processInstanceId, String errorDesc, String errorFull) {
		imprimirFuncio("updateExpedientError");
		expedientService.updateExpedientError(processInstanceId, errorDesc, errorFull);
	}

	@Transactional
	@Override
	public String getHeliumProperty(String propertyName) {
		imprimirFuncio("getHeliumProperty");
		return GlobalProperties.getInstance().getProperty(propertyName);
	}

	@Transactional
	@Override
	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto dto = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
		return conversioTipusHelper.convertir(
				dto,
				OperacioMassivaDto.class);
	}

	@Transactional
	@Override
	public void executarExecucioMassiva(OperacioMassivaDto operacioMassiva) throws Exception {
		execucioMassivaService.executarExecucioMassiva(
				conversioTipusHelper.convertir(
						operacioMassiva,
						net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto.class));
	}

	@Transactional
	@Override
	public void generaInformeError(OperacioMassivaDto operacioMassiva, Exception e) {
		imprimirFuncio("generaInformeError");
		execucioMassivaService.generaInformeError(operacioMassiva, e);
	}

	@Transactional
	@Override
	public void actualitzaUltimaOperacio(OperacioMassivaDto operacioMassiva) {
		imprimirFuncio("actualitzaUltimaOperacio");
		execucioMassivaService.actualitzaUltimaOperacio(operacioMassiva);
	}

	private Expedient getExpedientDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
		if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId))
			return expedientIniciant;
		JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new ProcessInstanceNotFoundException();
		return expedientDao.findAmbProcessInstanceId(processInstance.getId());
	}
	private DefinicioProces getDefinicioProcesDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new ProcessInstanceNotFoundException();
		return definicioProcesDao.findAmbJbpmId(processInstance.getProcessDefinitionId());
	}
	private Entorn getEntornDonatProcessInstanceId(
			String processInstanceId) throws ProcessInstanceNotFoundException {
		JbpmProcessInstance processInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		if (processInstance == null)
			throw new ProcessInstanceNotFoundException();
		Expedient exp = expedientDao.findAmbProcessInstanceId(processInstance.getId());
		return exp.getEntorn();
	}

	private void imprimirFuncio(String nom) {
		//System.out.println(">>> JBPM3_SERVICE METODE: " + nom);
	}

	private static final Logger logger = LoggerFactory.getLogger(Jbpm3HeliumServiceImpl.class);

}
