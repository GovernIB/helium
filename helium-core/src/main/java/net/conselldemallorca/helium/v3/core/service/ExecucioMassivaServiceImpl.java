package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstanceExpedient;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.DefinicioProcesHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MailHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helper.TerminiHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient.ExecucioMassivaEstat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.util.CsvHelper;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.ExecucioMassivaException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientRegistreService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTascaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.AccioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.PersonaRepository;

/**
 * Servei per a gestionar la tramitació massiva d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("execucioMassivaServiceV3")
@Transactional(noRollbackForClassName = "java.lang.Exception")
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private PersonaRepository personaRepository;
	@Resource
	private ConsultaRepository consultaRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private AccioRepository accioRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private TerminiHelper terminiHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private DocumentHelperV3 documentHelperV3;
	@Autowired
	private MetricRegistry metricRegistry;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private MailHelper mailHelper;
	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private DefinicioProcesHelper definicioProcesHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource(name = "documentHelperV3")
	private DocumentHelperV3 documentHelper;
	@Resource
	private UsuariActualHelper usuariActualHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;

	@Autowired
	private TascaService tascaService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	@Autowired
	private ExpedientTascaService expedientTascaService;
	@Autowired
	private ExpedientRegistreService expedientRegistreService;

	@Transactional
	@Override
	public void crearExecucioMassiva(ExecucioMassivaDto dto) {
		if ((dto.getExpedientIds() != null && !dto.getExpedientIds().isEmpty())
				|| (dto.getTascaIds() != null && dto.getTascaIds().length > 0)
				|| (dto.getProcInstIds() != null && !dto.getProcInstIds().isEmpty())
				|| (dto.getDefProcIds() != null && dto.getDefProcIds().length > 0)
				|| (ExecucioMassivaTipusDto.ALTA_MASSIVA.equals(dto.getTipus()) && dto.getContingutCsv() != null)) {
			String log = "Creació d'execució massiva (" + dto.getTipus() + ", dataInici=" + dto.getDataInici();
			if (dto.getExpedientTipusId() != null)
				log += ", expedientTipusId=" + dto.getExpedientTipusId();
			log += ", numExpedients=";
			if (dto.getExpedientIds() != null)
				log += dto.getExpedientIds().size();
			else if (dto.getProcInstIds() != null)
				log += dto.getProcInstIds().size();
			else if (dto.getContingutCsv() != null)
				log += dto.getContingutCsv().length - 1;
			else
				log += "0";
			log += ")";
			logger.debug(log);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			ExecucioMassiva execucioMassiva = new ExecucioMassiva(auth.getName(),
					ExecucioMassivaTipus.valueOf(dto.getTipus().toString()));
			if (dto.getDataInici() == null) {
				execucioMassiva.setDataInici(new Date());
			} else {
				execucioMassiva.setDataInici(dto.getDataInici());
			}
			execucioMassiva.setEnviarCorreu(dto.getEnviarCorreu());
			execucioMassiva.setParam1(dto.getParam1());
			execucioMassiva.setParam2(dto.getParam2());
			ExpedientTipus expedientTipus = null;
			if (dto.getExpedientTipusId() != null) {
				expedientTipus = expedientTipusRepository.findById(dto.getExpedientTipusId());
				execucioMassiva.setExpedientTipus(expedientTipus);
			}
			int ordre = 0;
			boolean expedients = false;
			if (dto.getExpedientIds() != null) {

				for (Long expedientId : dto.getExpedientIds()) {
					Expedient expedient = expedientRepository.findOne(expedientId);
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(execucioMassiva, expedient, ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
					if (expedientTipus == null && expedient != null)
						expedientTipus = expedient.getTipus();
				}
			} else if (dto.getTascaIds() != null) {
				for (String tascaId : dto.getTascaIds()) {
					JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(tascaId, false, false);
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(execucioMassiva, expedient, tascaId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			} else if (dto.getProcInstIds() != null) {
				for (String procinstId : dto.getProcInstIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(execucioMassiva, procinstId, ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
					if (expedientTipus == null) {
						Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(procinstId);
						if (expedient != null)
							expedientTipus = expedient.getTipus();
					}
				}
			} else if (dto.getDefProcIds() != null && dto.getDefProcIds().length > 0) {
				for (Long defProcId : dto.getDefProcIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(execucioMassiva, defProcId, ordre++);
					execucioMassiva.addExpedient(eme);
				}
			} else if (dto.getContingutCsv() != null && dto.getContingutCsv().length > 1) {
				// Alta massiva per CSV
				DefinicioProces definicioProces = definicioProcesHelper.findDarreraVersioDefinicioProces(expedientTipus, expedientTipus.getJbpmProcessDefinitionKey());
				for (int i =0; i<dto.getContingutCsv().length-1; i++) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(execucioMassiva, definicioProces.getId(), ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;					
				}
				
			}
			// Entorn
			Long entornId = expedientTipus != null && expedientTipus.getEntorn() != null
					? expedientTipus.getEntorn().getId()
					: EntornActual.getEntornId();
			execucioMassiva.setEntorn(entornId);

			if (expedients || (dto.getDefProcIds() != null && dto.getDefProcIds().length > 0)) {

				execucioMassiva.setRols(expedientTipusHelper.getRolsTipusExpedient(auth, expedientTipus));

//				logger.info(">> EXEC:MASS - Parametres: ");
//				logger.info(">>>>> usuari:  " + execucioMassiva.getUsuari());
//				logger.info(">>>>> rols:    " + execucioMassiva.getRols());
//				logger.info(">>>>> entorn:  " + execucioMassiva.getEntorn());
//				logger.info(">>>>> dat_ini: " + execucioMassiva.getDataInici());
//				logger.info(">>>>> dat_fi:  " + execucioMassiva.getDataFi());
//				if (execucioMassiva.getExpedientTipus() != null)
//					logger.info(">>>>> tip_exp: " + execucioMassiva.getExpedientTipus().getCodi());
//				logger.info(">>>>> tipus:   " + execucioMassiva.getTipus());
//				logger.info(">>>>> env_cor: " + (execucioMassiva.getEnviarCorreu() != null ? (execucioMassiva.getEnviarCorreu() ? "SI" : "NO") : "NO"));
//				logger.info(">>>>> param1:  " + execucioMassiva.getParam1());

				execucioMassivaRepository.save(execucioMassiva);
			} else
				throw new ValidacioException("S'ha intentat crear una execució massiva sense assignar expedients.");
		}
	}

	@Transactional(readOnly = true)
	@Override
	public ExecucioMassivaDto findAmbId(Long execucioMassivaId) {
		ExecucioMassivaDto ret = null;
		logger.debug(
				"Consultant l'execucio massiva per id (" +
				"execucioMassivaId=" + execucioMassivaId + ")");
		ExecucioMassiva execucioMassiva = execucioMassivaRepository.findOne(execucioMassivaId);
		if (execucioMassiva != null)
			ret = conversioTipusHelper.convertir(
					execucioMassiva,
					ExecucioMassivaDto.class);
		return ret;
	}


	@Transactional
	@Override
	public void rependreExecucioMassiva(Long id) {

		try {
			ExecucioMassiva execucioMassiva = execucioMassivaRepository.findOne(id);
			if (execucioMassiva == null) {
				throw new NoTrobatException(ExecucioMassiva.class, id);
			}

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!UsuariActualHelper.isAdministrador(auth)) {
				expedientTipusHelper
						.getExpedientTipusComprovantPermisLectura(execucioMassiva.getExpedientTipus().getId());
			}

			// Cancel·la les execucions massives d'expedients pendents
			List<ExecucioMassivaExpedient> execucionsMassivesExpedients = execucioMassivaExpedientRepository
					.findByExecucioMassivaId(id);
			int n = 0;
			for (ExecucioMassivaExpedient e : execucionsMassivesExpedients) {
				if (e.getDataFi() != null && ExecucioMassivaEstat.ESTAT_CANCELAT.equals(e.getEstat())) {
					e.setEstat(ExecucioMassivaEstat.ESTAT_PENDENT);
					e.setDataFi(null);
					n++;
				}
			}	
			execucioMassiva.setDataFi(null);
			logger.info("S'han représ " + n + " execucions d'expedients de " + execucionsMassivesExpedients.size()
					+ " execucions per l'execució massiva amb id " + id);

		} catch (Exception ex) {
			String errMsg = "No s'ha pogut rependre la execucio massiva amb id " + id + ": " + ex.getMessage();
			logger.error(errMsg, ex);
			throw new RuntimeException(errMsg, ex);
		}
	}
	
	@Transactional
	@Override
	public void rependreExecucioMassivaExpedient(Long id) {
		ExecucioMassivaExpedient eme = null;
		try {
			eme = execucioMassivaExpedientRepository.findOne(id);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_PENDENT);
			eme.setDataFi(null);
			execucioMassivaExpedientRepository.save(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + id + ". No s'ha pogut canviar el estat del procés", ex);
			throw new ValidacioException("OPERACIO:" + id + ". No s'ha pogut canviar el estat del procés", ex);
		}
	}

	@Transactional
	@Override
	public int cancelarExecucioMassiva(Long id) {
		int n = 0;
		try {
			// Comprova el permís de lectura sobre l'expedient o d'administració sobre
			// Helium
			ExecucioMassiva execucioMassiva = execucioMassivaRepository.findOne(id);
			if (execucioMassiva == null)
				throw new NoTrobatException(ExecucioMassiva.class, id);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!UsuariActualHelper.isAdministrador(auth)) {
				expedientTipusHelper
						.getExpedientTipusComprovantPermisLectura(execucioMassiva.getExpedientTipus().getId());
			}
			// Cancel·la les execucions massives d'expedients pendents
			List<ExecucioMassivaExpedient> execucionsMassivesExpedients = execucioMassivaExpedientRepository
					.findByExecucioMassivaId(id);
			Date dataFi = new Date();
			for (ExecucioMassivaExpedient e : execucionsMassivesExpedients) {
				if (e.getDataFi() == null && ExecucioMassivaEstat.ESTAT_PENDENT.equals(e.getEstat())) {
					e.setEstat(ExecucioMassivaEstat.ESTAT_CANCELAT);
					e.setDataFi(dataFi);
					n++;
				}
			}
			logger.info("S'han cancel·lat " + n + " execucions d'expedients de " + execucionsMassivesExpedients.size()
					+ " execucions per l'execució massiva amb id " + id);
		} catch (Exception ex) {
			String errMsg = "No s'han pogut cancel·lar les execucions massives d'expedients per l'execució massiva amb id "
					+ id + ": " + ex.getMessage();
			logger.error(errMsg, ex);
			throw new RuntimeException(errMsg, ex);
		}
		return n;
	}

	@Transactional
	@Override
	public void cancelarExecucioMassivaExpedient(Long id) {
		ExecucioMassivaExpedient eme = null;
		try {
			eme = execucioMassivaExpedientRepository.findOne(id);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_CANCELAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + id + ". No s'ha pogut canviar el estat del procés", ex);
			throw new ValidacioException("OPERACIO:" + id + ". No s'ha pogut canviar el estat del procés", ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public String getJsonExecucionsMassives(int results, String nivell) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		int page = results < 0 ? 0 : results;

		Pageable paginacio = new PageRequest(page, 10, Direction.DESC, "dataInici");
		List<ExecucioMassiva> execucions = null;

		// Revisa el nivell que pot veure
		if (nivell != null && "admin".equals(nivell)) {
			// nivell admin
			if (usuariActualHelper.isAdministrador()) {
				// hel_admin
				execucions = execucioMassivaRepository.findAll(paginacio).getContent();
			} else {
				// admin entorn
				execucions = execucioMassivaRepository.findByEntornOrderByDataIniciDesc(EntornActual.getEntornId(),
						paginacio);
			}
		} else {
			// nivell user
			execucions = execucioMassivaRepository.findByUsuariAndEntornOrderByDataIniciDesc(auth.getName(),
					EntornActual.getEntornId(), paginacio);
		}

		// Recuperem els resultats
		final int ID = 0;
		final int FINALITZAT = 1;
		final int ERROR = 2;
		final int PENDENT = 3;
		final int TOTAL = 4;

		JSONArray ljson = new JSONArray();

		if (!execucions.isEmpty()) {
			List<Object[]> comptadorsExecucions = execucioMassivaExpedientRepository
					.findResultatsExecucionsMassives(execucions);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			for (ExecucioMassiva execucio : execucions) {

				Object[] comptadorTrobat = null;
				for (Object[] comptadorsExecucio : comptadorsExecucions) {
					if (execucio.getId().equals(comptadorsExecucio[ID])) {
						comptadorTrobat = comptadorsExecucio;
						break;
					}
				}

				if (comptadorTrobat != null) {
					JSONArray ljson_exp = new JSONArray();
					String tasca = "";

					Long id = (Long) comptadorTrobat[ID];
					Long finalitzat = (Long) comptadorTrobat[FINALITZAT];
					Long error = (Long) comptadorTrobat[ERROR];
					Long processat = (error + finalitzat);
					Long pendent = (Long) comptadorTrobat[PENDENT];
					Long total = (Long) comptadorTrobat[TOTAL];

					Map<String, Serializable> mjson = new LinkedHashMap<String, Serializable>();
					mjson.put("id", id);
					mjson.put("tipus", execucio.getTipus() != null ? execucio.getTipus().toString() : "");
					mjson.put("expedientTipusId",
							execucio.getExpedientTipus() != null ? execucio.getExpedientTipus().getId() : "");
					mjson.put("expedientTipusCodi",
							execucio.getExpedientTipus() != null ? execucio.getExpedientTipus().getCodi() : "");
					mjson.put("expedientTipusNom",
							execucio.getExpedientTipus() != null ? execucio.getExpedientTipus().getNom() : "");
					mjson.put("entornId",
							execucio.getExpedientTipus() != null && execucio.getExpedientTipus().getEntorn() != null
									? execucio.getExpedientTipus().getEntorn().getId()
									: "");
					mjson.put("entornCodi",
							execucio.getExpedientTipus() != null && execucio.getExpedientTipus().getEntorn() != null
									? execucio.getExpedientTipus().getEntorn().getCodi()
									: "");
					mjson.put("entornNom",
							execucio.getExpedientTipus() != null && execucio.getExpedientTipus().getEntorn() != null
									? execucio.getExpedientTipus().getEntorn().getNom()
									: "");
					mjson.put("text", JSONValue.escape(getTextExecucioMassiva(execucio, tasca)));

					mjson.put("finalitzat", finalitzat);
					mjson.put("error", error);
					mjson.put("pendent", pendent);
					mjson.put("processat", processat);
					mjson.put("total", total);

					mjson.put("executades", getPercent((total - pendent), total));
					mjson.put("cancelada", processat < total && execucio.getDataFi() != null);

					mjson.put("data", sdf.format(execucio.getDataInici()));
					if (execucio.getDataFi() != null)
						mjson.put("dataFi", sdf.format(execucio.getDataFi()));
					mjson.put("tasca", tasca);
					mjson.put("expedients", ljson_exp);
					String nomSencer = "";
					try {
						nomSencer = pluginHelper.personaFindAmbCodi(execucio.getUsuari()).getNomSencer();
					} catch (Exception e) {
						logger.error(e);
					}
					mjson.put("usuari", nomSencer);
					ljson.add(mjson);
				}
			}
		}
		String ojson = JSONValue.toJSONString(ljson);
		return ojson;
	}

	@Override
	public Object deserialize(byte[] bytes) {
		Object obj = null;
		if (bytes != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			try {
				ObjectInputStream ois = new ObjectInputStream(bis);
				obj = ois.readObject();
			} catch (IOException e) {
				logger.error(e);
			} catch (ClassNotFoundException e) {
				logger.error(e);
			}
		}
		return obj;
	}

	@Override
	public byte[] serialize(Object obj) {
		byte[] bytes = null;
		if (obj != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(obj);
				oos.flush();
				oos.close();
				bos.close();
			} catch (IOException e) {
				logger.error(e);
			}
			bytes = bos.toByteArray();
		}
		return bytes;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public String getExecucioMassivaDetall(Long execucioMassivaId) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		ExecucioMassiva execucio = execucioMassivaRepository.findOne(execucioMassivaId);
		if (execucio == null)
			throw new NoTrobatException(ExecucioMassiva.class, execucioMassivaId);

		List<ExecucioMassivaExpedient> expedients = execucio.getExpedients();
		JSONArray ljson_exp = new JSONArray();
		String tasca = "";
		Long finalitzat = 0L;
		Long danger = 0L;
		Long pendent = 0L;
		if (!expedients.isEmpty()) {
			ExecucioMassivaExpedient em = expedients.get(0);
			if (em.getTascaId() != null) {
				JbpmTask task = jbpmHelper.getTaskById(em.getTascaId());
				if (task != null) {
					if (task.isCacheActiu())
						tasca = task.getFieldFromDescription("titol");
					else
						tasca = task.getTaskName();
				}
			}
			for (ExecucioMassivaExpedient expedient : expedients) {
				Expedient exp = expedient.getExpedient();
				String titol = "";
				if (exp != null) {
					if (exp.getNumero() != null)
						titol = "[" + exp.getNumero() + "]";
					if (exp.getTitol() != null)
						titol += (titol.length() > 0 ? " " : "") + exp.getTitol();
					if (titol.length() == 0)
						titol = exp.getNumeroDefault();
				} else if (execucio.getTipus() == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC) {
					titol = messageHelper.getMessage("expedient.massiva.actualitzar.dp") + " "
							+ expedient.getExecucioMassiva().getParam1();
				} else if (execucio.getTipus() == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC) {
					DefinicioProces dp = definicioProcesRepository.findOne(expedient.getDefinicioProcesId());
					String idPerMostrar = dp != null ? dp.getIdPerMostrar() : expedient.getAuxText();
					titol = messageHelper.getMessage("expedient.massiva.eliminar.dp") + " (" + idPerMostrar + ")";
				} else if (execucio.getTipus() == ExecucioMassivaTipus.PROPAGAR_PLANTILLES) {
					titol = expedient.getAuxText() != null ? expedient.getAuxText()
							: definicioProcesRepository.findOne(expedient.getDefinicioProcesId()).getJbpmKey();
				} else if (execucio.getTipus() == ExecucioMassivaTipus.PROPAGAR_CONSULTES) {
					titol = expedient.getAuxText() != null ? expedient.getAuxText()
							: consultaRepository.findOne(expedient.getDefinicioProcesId()).getCodi();
				}
				Map<String, Object> mjson_exp = new LinkedHashMap<String, Object>();
				mjson_exp.put("id", expedient.getId());
				mjson_exp.put("titol", titol);
				mjson_exp.put("estat", expedient.getEstat().name());

				if (expedient.getDataFi() != null) {
					mjson_exp.put("dataFi", sdf.format(expedient.getDataFi()));
				}

				String error = expedient.getError();
				if (error != null || expedient.getEstat() == ExecucioMassivaEstat.ESTAT_ERROR) {
					if (error != null)
						error = error.replace("'", "&#8217;").replace("\"", "&#8220;");
					danger++;
				} else if (expedient.getEstat() == ExecucioMassivaEstat.ESTAT_FINALITZAT) {
					finalitzat++;
				} else if (expedient.getDataFi() == null
						&& ExecucioMassivaEstat.ESTAT_PENDENT.equals(expedient.getEstat())) {
					pendent++;
				}
				mjson_exp.put("error", JSONValue.escape(error));
				ljson_exp.add(mjson_exp);
			}
		}
		Map<String, Serializable> mjson = new LinkedHashMap<String, Serializable>();
		mjson.put("id", execucio.getId());
		mjson.put("tipus", execucio.getTipus() != null ? execucio.getTipus().toString() : "");
		mjson.put("expedientTipusId", execucio.getExpedientTipus() != null ? execucio.getExpedientTipus().getId() : "");
		mjson.put("text", JSONValue.escape(getTextExecucioMassiva(execucio, tasca)));

		long total = (long) expedients.size();
		mjson.put("error", danger);
		mjson.put("pendent", pendent);
		mjson.put("finalitzat", finalitzat);
		mjson.put("processat", finalitzat + danger);
		mjson.put("total", total);
		mjson.put("data", sdf.format(execucio.getDataInici()));
		mjson.put("executades", getPercent((total - pendent), total));
		if (execucio.getDataFi() != null) {
			mjson.put("dataFi", sdf.format(execucio.getDataFi()));
		}
		mjson.put("tasca", tasca);
		mjson.put("expedients", ljson_exp);
		String nomSencer = "";

		nomSencer = pluginHelper.personaFindAmbCodi(execucio.getUsuari()).getNomSencer();

		mjson.put("usuari", nomSencer);
		String ojson = JSONValue.toJSONString(mjson);
		return ojson;
	}

	@Transactional(readOnly = true)
	private DefinicioProces getDefinicioProces(ExecucioMassiva exe) {
		DefinicioProces definicioProces = null;
		try {
			Object obj = (Object) deserialize(exe.getParam2());
			Long dfId = null;
			if (obj instanceof Long) {
				dfId = (Long) obj;
			} else {
				Object[] arobj = (Object[]) deserialize(exe.getParam2());
				if (arobj[0] instanceof Long) {
					dfId = (Long) arobj[0];
				}
			}
			if (dfId != null)
				definicioProces = definicioProcesRepository.findById(dfId);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + exe.getId() + ". No s'ha pogut obtenir la definicioProces del procés", ex);
		}
		return definicioProces;
	}

	private String getTextExecucioMassiva(ExecucioMassiva execucioMassiva, String tasca) {
		String label = null;
		ExecucioMassivaTipus tipus = execucioMassiva.getTipus();
		if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_TASCA)) {
			label = messageHelper.getMessage("expedient.massiva.tasca") + " " + tasca + ": ";
			String param = execucioMassiva.getParam1();
			Object param2 = deserialize(execucioMassiva.getParam2());
			if (param.equals("Guardar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.guardar");
			} else if (param.equals("Validar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.validar");
			} else if (param.equals("Completar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.completar");
			} else if (param.equals("Restaurar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.restaurar");
			} else if (param.equals("Accio")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.accio")
						+ (param2 == null ? "" : " &#8216;" + ((Object[]) param2)[1] + "&#8217;");
			} else if (param.equals("DocGuardar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.guardar")
						+ (param2 == null ? "" : " &#8216;'" + ((Object[]) param2)[1] + "&#8217;");
			} else if (param.equals("DocEsborrar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.borrar")
						+ (param2 == null ? "" : " &#8216;" + ((Object[]) param2)[1] + "&#8217;");
			} else if (param.equals("DocGenerar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.generar")
						+ (param2 == null ? "" : " &#8216;'" + ((Object[]) param2)[1] + "&#8217;");
			} else if (param.equals("RegEsborrar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.reg.borrar")
						+ (param2 == null ? "" : " &#8216;" + ((Object[]) param2)[1] + "&#8217;");
			} else if (param.equals("RegGuardar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.reg.guardar")
						+ (param2 == null ? "" : " &#8216;" + ((Object[]) param2)[1] + "&#8217;");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC)) {
			if (execucioMassiva.getExpedientTipus() == null && execucioMassiva.getParam1() != null) {
				String versio = "";
				try {
					versio += (Integer) deserialize(execucioMassiva.getParam2());
				} catch (Exception e) {
				}
				label = messageHelper.getMessage("expedient.massiva.actualitzar") + " (" + execucioMassiva.getParam1()
						+ " v." + versio + ")";
			} else {
				DefinicioProces definicioProces = getDefinicioProces(execucioMassiva);
				label = messageHelper.getMessage("expedient.massiva.actualitzar") + (definicioProces == null ? ""
						: " (" + definicioProces.getJbpmKey() + " v." + definicioProces.getVersio() + ")");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC)) {
			label = messageHelper.getMessage("expedient.massiva.eliminar.versio.dp") + " ("
					+ execucioMassiva.getExpedientTipus().getNom() + ")";
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_SCRIPT)) {
			String script = "";
			if (execucioMassiva.getParam2() != null) {
				try {
					Object param2 = deserialize(execucioMassiva.getParam2());
					if (param2 instanceof Object[]) {
						script = (String) ((Object[]) param2)[0];
					} else if (param2 instanceof String) {
						script = (String) param2;
					} else {
						script = param2.toString();
					}
					script = script.replace("'", "&#39;").replace("\"", "&#34;");
				} catch (Exception ex) {
					logger.error("OPERACIO:" + execucioMassiva.getId() + ". No s'ha pogut obtenir la operació", ex);
				}
			}
			// String script = ((String)
			// deserialize(execucioMassiva.getParam2())).replace("'", "&#39;").replace("\"",
			// "&#34;");
			label = messageHelper.getMessage("expedient.massiva.executarScriptMas") + " "
					+ (script.length() > 20 ? script.substring(0, 20) : script);
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_ACCIO)) {
			String accio = "";
			if (execucioMassiva.getParam2() != null) {
				try {
					Object param2 = deserialize(execucioMassiva.getParam2());
					if (param2 instanceof Object[]) {
						accio = (String) ((Object[]) param2)[0];
					} else {
						accio = (String) param2;
					}
				} catch (Exception ex) {
					logger.error("OPERACIO:" + execucioMassiva.getId() + ". No s'ha pogut obtenir la operació", ex);
				}
			}
			label = messageHelper.getMessage("expedient.massiva.accions") + " " + accio;
		} else if (tipus.equals(ExecucioMassivaTipus.ATURAR_EXPEDIENT)) {
			Object paramDos = deserialize(execucioMassiva.getParam2());
			String motiu = null;
			if (paramDos != null)
				motiu = paramDos.toString();
			label = messageHelper.getMessage("expedient.massiva.aturar")
					+ (motiu == null ? "" : ": " + (motiu.length() > 20 ? motiu.substring(0, 20) : motiu));
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_VARIABLE)) {
			label = messageHelper.getMessage("expedient.massiva.modificar_variables") + " "
					+ execucioMassiva.getParam1();
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_DOCUMENT)) {
			label = messageHelper.getMessage("expedient.massiva.documents");
		} else if (tipus.equals(ExecucioMassivaTipus.REINDEXAR)) {
			label = messageHelper.getMessage("expedient.eines.reindexar.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.BUIDARLOG)) {
			label = messageHelper.getMessage("expedient.eines.buidarlog.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.REPRENDRE_EXPEDIENT)) {
			label = messageHelper.getMessage("expedient.eines.reprendre_expedient");
		} else if (tipus.equals(ExecucioMassivaTipus.FINALITZAR_EXPEDIENT)) {
			label = messageHelper.getMessage("expedient.eines.finalitzar_expedient");
		} else if (tipus.equals(ExecucioMassivaTipus.MIGRAR_EXPEDIENT)) {
			label = messageHelper.getMessage("expedient.eines.migrar_expedient");
		} else if (tipus.equals(ExecucioMassivaTipus.REASSIGNAR)) {
			label = messageHelper.getMessage("expedient.eines.reassignar.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.PROPAGAR_PLANTILLES)) {
			label = messageHelper.getMessage("expedient.eines.propagar.plantilles",
					new Object[] { execucioMassiva.getExpedientTipus().getCodi() });
		} else if (tipus.equals(ExecucioMassivaTipus.PROPAGAR_CONSULTES)) {
			label = messageHelper.getMessage("expedient.eines.propagar.consultes",
					new Object[] { execucioMassiva.getExpedientTipus().getCodi() });
		} else if (tipus.equals(ExecucioMassivaTipus.ALTA_MASSIVA)) {
			label = messageHelper.getMessage("expedient.massiva.altaMassiva",
					new Object[] { execucioMassiva.getExpedientTipus().getCodi() });
		} else {
			label = tipus.name();
		}
		return label;
	}

	@Override
	public Long getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		Date ara = new Date();
		Long execucioMassivaExpedientId = null;
		Boolean nextFound = false;
		if (ultimaExecucioMassiva != null) {
			Long nextMassiu = execucioMassivaRepository.getNextMassiu(ultimaExecucioMassiva, ara);
			if (nextMassiu != null) {
				nextFound = true;
				execucioMassivaExpedientId = execucioMassivaExpedientRepository
						.findNextExecucioMassivaExpedient(nextMassiu);
			}
		}

		if (execucioMassivaExpedientId == null && !nextFound)
			execucioMassivaExpedientId = execucioMassivaExpedientRepository.findExecucioMassivaExpedientId(ara);

		if (execucioMassivaExpedientId == null) {
			// Comprobamos si es una ejecución masiva sin expedientes asociados. En ese caso
			// actualizamos la fecha de fin
			Long mas = execucioMassivaRepository.getMinExecucioMassiva(ara);
			if (mas != null) {
				ExecucioMassiva massiva = execucioMassivaRepository.findOne(mas);
				if (massiva != null) {
					massiva.setDataFi(new Date());
					execucioMassivaRepository.saveAndFlush(massiva);
				}
			}
		}
		return execucioMassivaExpedientId;
	}

	@Override
	@Transactional
	public void executarExecucioMassiva(Long ome_id) {
		ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
		if (ome == null)
			throw new NoTrobatException(ExecucioMassivaExpedient.class, ome_id);

		ExecucioMassiva exm = ome.getExecucioMassiva();
		ExecucioMassivaTipus tipus = exm.getTipus();
		Entorn entorn = entornHelper.getEntornComprovantPermisos(exm.getEntorn(), false);

		Expedient expedient = null;
		if (ome.getExpedient() != null) {
			expedient = ome.getExpedient();
		} else if (tipus != ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC
				&& tipus != ExecucioMassivaTipus.PROPAGAR_PLANTILLES
				&& tipus != ExecucioMassivaTipus.PROPAGAR_CONSULTES
				&& tipus != ExecucioMassivaTipus.ALTA_MASSIVA) {
			expedient = expedientHelper.findExpedientByProcessInstanceId(ome.getProcessInstanceId());
		}

		ExpedientTipus expedientTipus;
		if (expedient == null && (tipus == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC
				|| tipus == ExecucioMassivaTipus.PROPAGAR_PLANTILLES
				|| tipus == ExecucioMassivaTipus.PROPAGAR_CONSULTES
				|| tipus == ExecucioMassivaTipus.ALTA_MASSIVA))
			expedientTipus = exm.getExpedientTipus();
		else
			expedientTipus = expedient.getTipus();

		logger.debug("Executant la acció massiva (" + "expedientTipusId="
				+ (expedientTipus != null ? expedientTipus.getId() : "") + ", " + "dataInici=" + ome.getDataInici()
				+ ", " + "expedient=" + ome.getId() + ", " + "acció=" + exm.getTipus());

		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(ExecucioMassivaService.class, "executar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry
				.counter(MetricRegistry.name(ExecucioMassivaService.class, "executar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry
				.timer(MetricRegistry.name(ExecucioMassivaService.class, "executar", entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry
				.counter(MetricRegistry.name(ExecucioMassivaService.class, "executar.count", entorn.getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(MetricRegistry.name(ExecucioMassivaService.class, "completar",
				entorn.getCodi(), (expedientTipus != null ? expedientTipus.getCodi() : "")));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(MetricRegistry.name(ExecucioMassivaService.class,
				"completar.count", entorn.getCodi(), (expedientTipus != null ? expedientTipus.getCodi() : "")));
		countTipexp.inc();
		try {
			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();

//			final String user = exm.getUsuari();
//	        Principal principal = new Principal() {
//				public String getName() {
//					return user;
//				}
//			};

			Authentication authentication = new UsernamePasswordAuthenticationToken(
					ome.getExecucioMassiva().getAuthenticationPrincipal(), "N/A", // ome.getExecucioMassiva().getAuthenticationCredentials(),
					ome.getExecucioMassiva().getAuthenticationRoles());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String expedient_s = null;
			if (MesuresTemporalsHelper.isActiu())
				expedient_s = (expedientTipus != null ? expedientTipus.getNom() : "");

			if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA) {
				gestioTasca(ome);
			} else if (tipus == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC) {
				mesuresTemporalsHelper.mesuraIniciar("Actualitzar", "massiva", expedient_s);
				actualitzarVersio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC) {
				mesuresTemporalsHelper.mesuraIniciar("Eliniar", "massiva", expedient_s);
				eliminarVersio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT) {
				mesuresTemporalsHelper.mesuraIniciar("Executar script", "massiva", expedient_s);
				executarScript(ome);
				mesuresTemporalsHelper.mesuraCalcular("Executar script", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO) {
				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva", expedient_s);
				executarAccio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.ATURAR_EXPEDIENT) {
				mesuresTemporalsHelper.mesuraIniciar("Aturar expedient", "massiva", expedient_s);
				aturarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Aturar expedient", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_VARIABLE) {
				mesuresTemporalsHelper.mesuraIniciar("Modificar variable", "massiva", expedient_s);
				modificarVariable(ome);
				mesuresTemporalsHelper.mesuraCalcular("Modificar variable", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_DOCUMENT) {
				mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", expedient_s);
				modificarDocument(ome);
				mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REINDEXAR) {
				mesuresTemporalsHelper.mesuraIniciar("Reindexar", "massiva", expedient_s);
				reindexarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Reindexar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.BUIDARLOG) {
				mesuresTemporalsHelper.mesuraIniciar("Buidar log", "massiva", expedient_s);
				buidarLogExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Buidar log", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE_EXPEDIENT) {
				mesuresTemporalsHelper.mesuraIniciar("desfer fi process instance", "massiva", expedient_s);
				reprendreExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("desfer fi process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.FINALITZAR_EXPEDIENT) {
				mesuresTemporalsHelper.mesuraIniciar("fi process instance", "massiva", expedient_s);
				finalitzarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("fi process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.MIGRAR_EXPEDIENT) {
				mesuresTemporalsHelper.mesuraIniciar("fi process instance", "massiva", expedient_s);
				migrarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("fi process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE) {
				mesuresTemporalsHelper.mesuraIniciar("reprendre tramitació process instance", "massiva", expedient_s);
				reprendreTramitacio(ome);
				mesuresTemporalsHelper.mesuraCalcular("reprendre tramitació process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REASSIGNAR) {
				mesuresTemporalsHelper.mesuraIniciar("Reassignar", "massiva", expedient_s);
				// reassignarExpedient(ome);
				reassignarTasca(ome);
				mesuresTemporalsHelper.mesuraCalcular("Reassignar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.PROPAGAR_PLANTILLES) {
				mesuresTemporalsHelper.mesuraIniciar("Propagar plantilles", "massiva", expedient_s);
				propagarPlantilles(ome);
				mesuresTemporalsHelper.mesuraCalcular("Propagar plantilles", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.PROPAGAR_CONSULTES) {
				mesuresTemporalsHelper.mesuraIniciar("Propagar consultes", "massiva", expedient_s);
				propagarConsultes(ome);
				mesuresTemporalsHelper.mesuraCalcular("Propagar consultes", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.ALTA_MASSIVA) {
				mesuresTemporalsHelper.mesuraIniciar("Alta massiva CSV", "massiva", expedient_s);
				altaMassivaCsv(ome);
				mesuresTemporalsHelper.mesuraCalcular("Alta massiva CSV", "massiva", expedient_s);				
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
		} catch (Exception ex) {
			logger.error("Error al executar la acció massiva (expedientTipusId="
					+ (expedientTipus != null ? expedientTipus.getId() : "") + ", dataInici=" + ome.getDataInici()
					+ ", expedient=" + (expedient == null ? null : expedient.getId()) + ", acció=" + ome, ex);

			Throwable excepcioRetorn = ex;
			if (tipus != ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC && ExceptionUtils.getRootCause(ex) != null) {
				excepcioRetorn = ExceptionUtils.getRootCause(ex);
			}

			TascaProgramadaServiceImpl.saveError(ome_id, excepcioRetorn, exm.getTipus());
			throw new ExecucioMassivaException(entorn.getId(), entorn.getCodi(), entorn.getNom(),
					expedient == null ? null : expedient.getId(), expedient == null ? null : expedient.getTitol(),
					expedient == null ? null : expedient.getNumero(),
					expedientTipus == null ? null : expedientTipus.getId(),
					expedientTipus == null ? null : expedientTipus.getCodi(),
					expedientTipus == null ? null : expedientTipus.getNom(), ome.getExecucioMassiva().getId(),
					ome.getId(), "Error al executar la acció massiva", ex);
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}

	@Override
	public void actualitzaUltimaOperacio(Long ome_id) {
		ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
		if (ome == null)
			throw new NoTrobatException(ExecucioMassivaExpedient.class, ome_id);

		if (ome.getExecucioMassiva().getExpedients().size() == ome.getOrdre() + 1) {
			try {
				ExecucioMassiva em = ome.getExecucioMassiva();
				em.setDataFi(new Date());
				execucioMassivaRepository.save(em);
				if (em.getTipus() == ExecucioMassivaTipus.BUIDARLOG && em.getParam1() != null
						&& !em.getParam1().isEmpty()) {

					// Establim les dades d'autenticació i d'entorn per a poder crear i executar una
					// execució massiva programada
					Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
					Authentication authentication = new UsernamePasswordAuthenticationToken(
							ome.getExecucioMassiva().getAuthenticationPrincipal(), "N/A",
							ome.getExecucioMassiva().getAuthenticationRoles());

					SecurityContextHolder.getContext().setAuthentication(authentication);

					Long ea = EntornActual.getEntornId();
					EntornActual.setEntornId(em.getEntorn());

					// programem l'execució massiva
					programarEliminarDp(Long.parseLong(em.getParam1()), em.getExpedientTipus().getId());

					// torem a deixar els valors d'entor i autenticació tal i com estaven abans
					EntornActual.setEntornId(ea);
					SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU:" + ome.getExecucioMassiva().getId()
						+ ". No s'ha pogut finalitzar l'expedient massiu", ex);
				throw new ExecucioMassivaException(ome.getExpedient().getEntorn().getId(),
						ome.getExpedient().getEntorn().getCodi(), ome.getExpedient().getEntorn().getNom(),
						ome.getExpedient().getId(), ome.getExpedient().getTitol(), ome.getExpedient().getNumero(),
						ome.getExpedient().getTipus().getId(), ome.getExpedient().getTipus().getCodi(),
						ome.getExpedient().getTipus().getNom(), ome.getExecucioMassiva().getId(), ome.getId(),
						"EXPEDIENTMASSIU: No s'ha pogut finalitzar l'expedient massiu", ex);
			}
			try {
				if (ome.getExecucioMassiva().getEnviarCorreu()) {

					// Correu
					List<String> emailAddresses = new ArrayList<String>();

					if (pluginHelper.personaIsPluginActiu()) {
						PersonaDto persona = pluginHelper.personaFindAmbCodi(ome.getExecucioMassiva().getUsuari());
						emailAddresses.add(persona.getEmail());
					} else {
						Persona persona = personaRepository.findByCodi(ome.getExecucioMassiva().getUsuari());
						emailAddresses.add(persona.getEmail());
					}

					mailHelper.send(GlobalProperties.getInstance().getProperty("app.correu.remitent"), emailAddresses,
							null, null, "Execució massiva: " + ome.getExecucioMassiva().getTipus(),
							"L'execució massiva ha finalitzat.");
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
				throw new ExecucioMassivaException(ome.getExpedient().getEntorn().getId(),
						ome.getExpedient().getEntorn().getCodi(), ome.getExpedient().getEntorn().getNom(),
						ome.getExpedient().getId(), ome.getExpedient().getTitol(), ome.getExpedient().getNumero(),
						ome.getExpedient().getTipus().getId(), ome.getExpedient().getTipus().getCodi(),
						ome.getExpedient().getTipus().getNom(), ome.getExecucioMassiva().getId(), ome.getId(),
						"EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generaInformeError(Long ome_id, String error) {
		ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
		if (ome == null)
			throw new NoTrobatException(ExecucioMassivaExpedient.class, ome_id);

		Date ara = new Date();
		ome.setDataInici(new Date());
		ome.setDataFi(ara);
		ome.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);

		ome.setError(error);
		execucioMassivaExpedientRepository.save(ome);
	}
	
	@Override
	@Transactional
	public ExecucioMassivaListDto getDarreraAltaMassiva(Long expedientTipusId) {

		ExecucioMassivaListDto ret = null;
		logger.debug(
				"Consultant la darrera alta massiva per tipus d'expedient (" +
				"expedientTipusId=" + expedientTipusId + ")");
		Long execucioMassivaId = execucioMassivaRepository.getMinExecucioMassivaByExpedientTipusId(
				expedientTipusId, 
				ExecucioMassiva.ExecucioMassivaTipus.ALTA_MASSIVA);
		if (execucioMassivaId != null) {
			ExecucioMassiva execucioMassiva = execucioMassivaRepository.findOne(execucioMassivaId);
			ret = conversioTipusHelper.convertir(execucioMassiva, ExecucioMassivaListDto.class);
			
			List<Object[]> resultats = execucioMassivaExpedientRepository.findResultatsExecucionsMassives(Arrays.asList(execucioMassiva));
			if (resultats.size() > 0) {
				Long finalitzat = (Long) resultats.get(0)[1];
				Long error = (Long) resultats.get(0)[2];
				Long processat = (error + finalitzat);
				Long pendent = (Long) resultats.get(0)[3];
				Long total = (Long) resultats.get(0)[4];
				
				ret.setFinalitzat(finalitzat);
				ret.setError(error);
				ret.setProcessat(processat);
				ret.setPendent(pendent);
				ret.setTotal(total);
			}
			
		}
		return ret;
	}	
	
	
	@Override
	@Transactional
	public String[][] getResultatAltaMassiva(Long execucioMassivaId) {

		String[][] resultat = null;
		logger.debug(
				"Consultant el resultat de l'alta massiva (" +
				"execucioMassivaId=" + execucioMassivaId + ")");
		
		List<String[]> files = new ArrayList<String[]>();
		// 1a fila amb els títols per les columnes
		String[] fila = new String[6];
		fila[0] = "Id";
		fila[1] = "Identificador";
		fila[2] = "Número";
		fila[3] = "Títol";
		fila[4] = "ProcessId";
		fila[5] = "Error";
		files.add(fila);
		for (ExecucioMassivaExpedient eme : execucioMassivaExpedientRepository.findByExecucioMassivaId(execucioMassivaId)) {
			fila = new String[6];
			if (eme.getExpedient() != null) {
				fila[0] = eme.getExpedient().getId().toString();
				fila[1] = eme.getExpedient().getIdentificador();
				fila[2] = eme.getExpedient().getNumero();
				fila[3] = eme.getExpedient().getTitol();
				fila[4] = eme.getExpedient().getProcessInstanceId();
			}
			fila[5] = eme.getError();
			files.add(fila);
		}
	    resultat = new String[files.size()][];
	    for (int i = 0; i < files.size(); i++) {
	    	resultat[i] = files.get(i);
	    }
		return resultat;
	}


	private void programarEliminarDp(Long dpId, Long expedientTipusId) throws Exception {
		Date dInici = new Date();
		ExecucioMassivaDto emdto = new ExecucioMassivaDto();

		emdto.setDataInici(dInici);
		emdto.setEnviarCorreu(false);
		emdto.setExpedientTipusId(expedientTipusId);
		emdto.setTipus(ExecucioMassivaTipusDto.ELIMINAR_VERSIO_DEFPROC);
		emdto.setDefProcIds(new Long[] { dpId });

		this.crearExecucioMassiva(emdto);
	}

	@SuppressWarnings("unchecked")
	private void gestioTasca(ExecucioMassivaExpedient ome) {
		String tascaId = ome.getTascaId();
		String accio = ome.getExecucioMassiva().getParam1();
		String tasca = null;
		String expedient = null;
		ExpedientTascaDto task = tascaService.findAmbIdPerTramitacio(tascaId);
		if (MesuresTemporalsHelper.isActiu()) {
			if (task != null)
				tasca = task.getTascaNom();
			expedient = ome.getExpedient().getTipus().getNom();
		}
		try {
			ome.setDataInici(new Date());
			if ("Guardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				Map<String, Object> valors = (Map<String, Object>) param2[1];
				tascaService.guardar(tascaId, valors);
				mesuresTemporalsHelper.mesuraCalcular("Guardar", "massiva_tasca", expedient, tasca);
			} else if ("Validar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Validar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				Map<String, Object> valors = (Map<String, Object>) param2[1];
				tascaService.validar(tascaId, valors);
				mesuresTemporalsHelper.mesuraCalcular("Validar", "massiva_tasca", expedient, tasca);
			} else if ("Completar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Completar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				Long entornId = (Long) param2[0];
				String transicio = (String) param2[1];
				Long ea = EntornActual.getEntornId();
				EntornActual.setEntornId(entornId);
				tascaService.completarMassiu(tascaId, transicio);
				EntornActual.setEntornId(ea);
				mesuresTemporalsHelper.mesuraCalcular("Completar", "massiva_tasca", expedient, tasca);
			} else if ("Restaurar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Restaurar", "massiva_tasca", expedient, tasca);
				tascaService.restaurar(tascaId);
				mesuresTemporalsHelper.mesuraCalcular("Restaurar", "massiva_tasca", expedient, tasca);
			} else if ("Accio".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				String accio_exec = (String) param2[1];
				tascaService.executarAccio(tascaId, accio_exec);
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva_tasca", expedient, tasca);
			} else if ("DocGuardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				Long entornId = (Long) this.getParam(param2, 0);
				String codi = (String) this.getParam(param2, 1);
				Date data = (Date) this.getParam(param2, 2);
				byte[] contingut = (byte[]) this.getParam(param2, 3);
				String nomArxiu = (String) this.getParam(param2, 4);
				String arxiuContentType = (String) this.getParam(param2, 5);
				Boolean ambFirma = (Boolean) this.getParam(param2, 6);
				Boolean firmaSeparada = (Boolean) this.getParam(param2, 7);
				byte[] firmaContingut = (byte[]) this.getParam(param2, 8);

				if (tascaService.isTascaValidada(tascaId)) {
					tascaService.guardarDocumentTasca(entornId, tascaId, codi, data, nomArxiu, contingut,
							arxiuContentType, ambFirma != null ? ambFirma.booleanValue() : false,
							firmaSeparada != null ? firmaSeparada : false, firmaContingut,
							ome.getExecucioMassiva().getUsuari());
				} else {
					throw new ValidacioException("OPERACIO:" + ome.getId()
							+ ". No s'ha pogut guardar el document a la tasca. Perquè la tasca no està validada");
				}
				mesuresTemporalsHelper.mesuraCalcular("Guardar document", "massiva_tasca", expedient, tasca);
			} else if ("DocEsborrar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Esborrar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				String codi = (String) param2[1];
				if (tascaService.isTascaValidada(tascaId)) {
					tascaService.esborrarDocument(tascaId, codi, ome.getExecucioMassiva().getUsuari());
				} else {
					throw new ValidacioException("OPERACIO:" + ome.getId()
							+ ". No s'ha pogut esborrar el document a la tasca perquè la tasca no està validada");
				}
				mesuresTemporalsHelper.mesuraCalcular("Esborrar document", "massiva_tasca", expedient, tasca);
			} else if ("DocGenerar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Generar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				String documentCodi = (String) param2[1];
				expedientDocumentService.generarAmbPlantillaPerTasca(tascaId, documentCodi);
				mesuresTemporalsHelper.mesuraCalcular("Generar document", "massiva_tasca", expedient, tasca);
			}

			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut executar '" + accio + "' de la tasca.", ex);
			throw new ExecucioMassivaException(ome.getExpedient().getEntorn().getId(),
					ome.getExpedient().getEntorn().getCodi(), ome.getExpedient().getEntorn().getNom(),
					ome.getExpedient().getId(), ome.getExpedient().getTitol(), ome.getExpedient().getNumero(),
					ome.getExpedient().getTipus().getId(), ome.getExpedient().getTipus().getCodi(),
					ome.getExpedient().getTipus().getNom(), ome.getExecucioMassiva().getId(), ome.getId(),
					"OPERACIO:" + ome.getId() + ". No s'ha pogut executar '" + accio + "' de la tasca.", ex);
		}
	}

	/**
	 * Mètode per obtenir un paràmetre amb l'índex vigilant el número de paràmetres.
	 * Si no hi és retorna null.
	 * 
	 * @param parametres
	 * @param index
	 * @return Retorna el paràmetre o null si l'índex és major que la llista de
	 *         paràmetres.
	 */
	private Object getParam(Object[] parametres, int index) {
		return index < parametres.length ? parametres[index] : null;
	}

	private void actualitzarVersio(ExecucioMassivaExpedient ome) throws Exception {
		try {
			ome.setDataInici(new Date());
			if (ome.getExpedient() != null) {

				Expedient exp = ome.getExpedient();
				Object[] param2 = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
				// Proces principal
				Long definicioProcesId = (Long) param2[0];
				Long expedientProcesInstanceId = Long.parseLong(exp.getProcessInstanceId());
				InstanciaProcesDto instanciaProces = expedientService
						.getInstanciaProcesById(exp.getProcessInstanceId());
				DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
				int versioActual = instanciaProces.getDefinicioProces().getVersio();
				int versioNova = definicioProces.getVersio();

				if (versioActual != versioNova)
					expedientService.procesDefinicioProcesActualitzar(exp.getProcessInstanceId(),
							definicioProces.getVersio());

				// Subprocessos
				Long[] subProcesIds = (Long[]) param2[1];
				if (subProcesIds != null) {
					String[] keys = (String[]) param2[2];
					List<InstanciaProcesDto> arbreProcessos = expedientService
							.getArbreInstanciesProces(expedientProcesInstanceId);
					for (InstanciaProcesDto ip : arbreProcessos) {
						int versio = findVersioDefProcesActualitzar(keys, subProcesIds,
								ip.getDefinicioProces().getJbpmKey());
						if (versio != -1 && versio != ip.getDefinicioProces().getVersio())
							expedientService.procesDefinicioProcesActualitzar(ip.getId(), versio);
					}
				}
			} else {
				Integer versio = (Integer) deserialize(ome.getExecucioMassiva().getParam2());
				Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(ome.getProcessInstanceId());
				ome.setExpedient(expedient);
				expedientService.procesDefinicioProcesActualitzar(ome.getProcessInstanceId(), versio);
			}

			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut canviar la versió del procés", ex);
			throw ex;
		}
	}

	private void eliminarVersio(ExecucioMassivaExpedient ome) throws Exception {
		boolean esborrarDf = false;
		Long entornId = ome.getExecucioMassiva().getEntorn();
		Entorn entorn = entornHelper.getEntornComprovantPermisos(entornId, false);
		DefinicioProces definicioProces = definicioProcesRepository.findById(ome.getDefinicioProcesId());
		List<Consulta> consultes = consultaRepository.findByEntorn(entorn);
		boolean esborrar = true;
		if (consultes.isEmpty()) {
			esborrarDf = true;
		} else {
			for (Consulta consulta : consultes) {
				Set<ConsultaCamp> llistat = consulta.getCamps();
				for (ConsultaCamp c : llistat) {
					if ((definicioProces.getVersio() == c.getDefprocVersio())
							&& (definicioProces.getJbpmKey().equals(c.getDefprocJbpmKey()))) {
						esborrar = false;
					}
				}
				if (!esborrar) {
					throw new Exception(messageHelper.getMessage("error.exist.cons.df", new Object[] {
							consulta.getNom(), definicioProces.getIdPerMostrar(), definicioProces.getVersio() }));
				} else {
					esborrarDf = true;
				}
			}
		}
		if (esborrarDf) {
			try {
				undeploy(entorn, ome.getExecucioMassiva().getExpedientTipus().getId(), definicioProces);
				ome.setAuxText(definicioProces.getIdPerMostrar());
				ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
				ome.setDataFi(new Date());
				execucioMassivaExpedientRepository.saveAndFlush(ome);
			} catch (Exception ex) {
				logger.error("No s'han pogut esborrar les definicions de procés", ex);
				if (ex instanceof DataIntegrityViolationException
						|| ex.getCause() instanceof DataIntegrityViolationException
						|| (ex.getCause() != null && ex.getCause().getCause() != null
								&& ex.getCause().getCause() instanceof DataIntegrityViolationException)
						|| "ConstraintViolationException".equalsIgnoreCase(ex.getClass().getSimpleName())
						|| (ex.getCause() != null && "ConstraintViolationException"
								.equalsIgnoreCase(ex.getCause().getClass().getSimpleName()))
						|| (ex.getCause() != null && ex.getCause().getCause() != null && "ConstraintViolationException"
								.equalsIgnoreCase(ex.getCause().getCause().getClass().getSimpleName()))) {

					String msg = (ex instanceof DataIntegrityViolationException
							|| "ConstraintViolationException".equalsIgnoreCase(ex.getClass().getSimpleName()))
									? getErrorMsg(ex)
									: (ex.getCause() instanceof DataIntegrityViolationException
											|| "ConstraintViolationException"
													.equalsIgnoreCase(ex.getCause().getClass().getSimpleName()))
															? getErrorMsg(ex.getCause())
															: getErrorMsg(ex.getCause().getCause());

					Long processInstanceId = Long.parseLong(definicioProces.getJbpmId());

					if (msg.contains("HELIUM.FK_TASKINST_TASK"))
						msg = messageHelper.getMessage("error.defpro.eliminar.constraint.taskinstance");
					if (msg.contains("HELIUM.FK_JOB_ACTION"))
						msg = messageHelper.getMessage("error.defpro.eliminar.constraint.job");
					if (msg.contains("HELIUM.FK_LOG_")) {
						if (GraphSession.errorsDelete.containsKey(processInstanceId))
							msg = messageHelper.getMessage("error.defpro.eliminar.constraint.log");
						else
							msg = messageHelper.getMessage("error.defpro.eliminar.constraint.log_no_exp");
					}
					if (msg.contains("HELIUM.FK_SWL_ASSDEL") || msg.contains("HELIUM.FK_SWIMLANEINST_SL"))
						msg = messageHelper.getMessage("error.defpro.eliminar.constraint.swl");
					if (msg.contains("HELIUM.FK_TRANS_PROCDEF"))
						msg = messageHelper.getMessage("error.defpro.eliminar.constraint.procdef");

					if (GraphSession.errorsDelete.containsKey(processInstanceId)) {

						msg += "####exp_afectats###" + definicioProces.getId().toString() + "###";
						for (ProcessInstanceExpedient expedient : GraphSession.errorsDelete.get(processInstanceId)) {
							msg += "&&&"
									+ (expedient.getIdentificador().equals("[null] null") ? expedient.getNumeroDefault()
											: expedient.getIdentificador())
									+ "@" + expedient.getId();
						}

						GraphSession.errorsDelete.remove(processInstanceId);
					}

					throw new Exception(messageHelper.getMessage("error.defpro.eliminar.constraint",
							new Object[] { definicioProces.getIdPerMostrar(), "" }) + ": " + msg);

				} else {
					throw new Exception(messageHelper.getMessage("error.proces.peticio") + ": "
							+ ExceptionUtils.getRootCauseMessage(ex), ExceptionUtils.getRootCause(ex));
				}
			}
		}
	}

	@CacheEvict(value = "consultaCache", allEntries = true)
	private void undeploy(Entorn entorn, Long expedientTipusId, DefinicioProces definicioProces) {
		if (expedientTipusId == null) {
			jbpmHelper.esborrarDesplegament(definicioProces.getJbpmId());
			for (Document doc : definicioProces.getDocuments())
				documentRepository.delete(doc.getId());
			for (Termini termini : definicioProces.getTerminis())
				terminiHelper.deleteTermini(termini.getId());
			definicioProcesRepository.delete(definicioProces);
		} else {
			if (comprovarExpedientTipus(expedientTipusId, definicioProces.getId())) {
				jbpmHelper.esborrarDesplegament(definicioProces.getJbpmId());
				for (Document doc : definicioProces.getDocuments()) {
					documentRepository.delete(doc);
				}
				definicioProces.setDocuments(null);
				for (Termini termini : definicioProces.getTerminis()) {
					terminiHelper.deleteTermini(termini.getId());
				}
				definicioProces.setTerminis(null);
				definicioProcesRepository.delete(definicioProces);
			} else {
				throw new IllegalArgumentException(messageHelper.getMessage("error.dissenyService.noTipusExp"));
			}
		}
	}

	private String getErrorMsg(Throwable ex) {
		StringWriter errors = new StringWriter();
		logger.error(ex);
		String errorFull = errors.toString();
		errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t",
				"&nbsp;&nbsp;&nbsp;&nbsp;");
		errorFull = StringEscapeUtils.escapeJavaScript(errorFull);
		return errorFull;
	}

	private boolean comprovarExpedientTipus(Long expedientTipusId, Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(expedientTipusId);
		if (expedientTipus != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
			return expedientTipus.equals(definicioProces.getExpedientTipus());
		}
		return false;
	}

	private int findVersioDefProcesActualitzar(String[] keys, Long[] defProces, String key) {
		int versio = -1;
		int i = 0;
		while (i < keys.length && !keys[i].equals(key))
			i++;
		if (i < keys.length && defProces[i] != null) {
			DefinicioProces definicioProces = definicioProcesRepository.findOne(defProces[i]);
			if (definicioProces != null)
				versio = definicioProces.getVersio();
		}
		return versio;
	}

	private void executarScript(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			Object param2 = deserialize(ome.getExecucioMassiva().getParam2());
			String script = "";
			if (param2 instanceof Object[]) {
				script = (String) ((Object[]) param2)[0];
			} else {
				script = (String) param2;
			}
			expedientService.procesScriptExec(exp.getId(), exp.getProcessInstanceId(), script);
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut executar l'script", ex);
			throw ex;
		}
	}

	private void executarAccio(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			Object param2 = deserialize(ome.getExecucioMassiva().getParam2());
			String accioCodi;
			if (param2 instanceof Object[]) {
				accioCodi = (String) ((Object[]) param2)[0];
			} else {
				accioCodi = (String) param2;
			}

			DefinicioProces definicioProces = expedientHelper
					.findDefinicioProcesByProcessInstanceId(exp.getProcessInstanceId());
			boolean infoPropia = definicioProces.getExpedientTipus() != null
					&& definicioProces.getExpedientTipus().isAmbInfoPropia();
			boolean ambHerencia = HerenciaHelper.ambHerencia(definicioProces.getExpedientTipus());
			Accio accio = null;
			if (infoPropia) {
				accio = accioRepository.findByExpedientTipusIdAndCodi(definicioProces.getExpedientTipus().getId(),
						accioCodi);
				if (accio == null && ambHerencia)
					accio = accioRepository.findByExpedientTipusIdAndCodi(
							definicioProces.getExpedientTipus().getExpedientTipusPare().getId(), accioCodi);
			} else {
				accio = accioRepository.findByCodiAndDefinicioProces(accioCodi, definicioProces);
			}
			if (accio == null)
				throw new NoTrobatException(Accio.class, accioCodi);

			expedientService.accioExecutar(exp.getId(), exp.getProcessInstanceId(), accio.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut executar l'accio", ex);
			throw ex;
		}
	}

	private void aturarExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			String motiu = (ome.getExecucioMassiva().getParam2() == null ? null
					: (String) deserialize(ome.getExecucioMassiva().getParam2()));
			if (!exp.isAturat()) {
				expedientService.aturar(exp.getId(), motiu);
				ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			} else {
				ome.setError(messageHelper.getMessage("error.expedient.ja.aturat"));
				ome.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
			}
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut aturar l'expedient", ex);
			throw ex;
		}
	}

	private void modificarVariable(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			String var = ome.getExecucioMassiva().getParam1();
			Object[] params = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
			String idPI = exp.getProcessInstanceId();
			if (idPI != null) {
				expedientDadaService.update(exp.getId(), idPI, var, params[2]);
			} else {
				tascaService.updateVariable(exp.getId(), (String) params[1], var, params[2]);
			}
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut modificat la variable", ex);
			throw ex;
		}
	}

	private void modificarDocument(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			String fileName = ome.getExecucioMassiva().getParam1();
			// Paràmetres
			Object[] params = (Object[]) deserialize(ome.getExecucioMassiva().getParam2());
			Long docId = null;
			Date data = null;
			String nom = null;
			byte[] contingut = null;
			String arxiuContentType = null;
			boolean ambFirma = false;
			boolean firmaSeparada = false;
			byte[] firmaContingut = null;
			NtiOrigenEnumDto ntiOrigen = null;
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion = null;
			NtiTipoDocumentalEnumDto ntiTipoDocumental = null;
			String ntiIdOrigen = null;
			docId = (Long) this.getParam(params, 0);
			data = (Date) this.getParam(params, 1);
			nom = (String) this.getParam(params, 2);
			contingut = (byte[]) this.getParam(params, 3);
			if (this.getParam(params, 4) != null)
				ntiOrigen = (NtiOrigenEnumDto) this.getParam(params, 4);
			if (this.getParam(params, 5) != null)
				ntiEstadoElaboracion = (NtiEstadoElaboracionEnumDto) this.getParam(params, 5);
			if (this.getParam(params, 6) != null)
				ntiTipoDocumental = (NtiTipoDocumentalEnumDto) this.getParam(params, 6);
			if (this.getParam(params, 7) != null)
				ntiIdOrigen = (String) this.getParam(params, 7);
			arxiuContentType = (String) this.getParam(params, 8);
			ambFirma = params.length > 9 ? (Boolean) this.getParam(params, 9) : false;
			firmaSeparada = params.length > 10 ? (Boolean) this.getParam(params, 10) : false;
			firmaContingut = (byte[]) this.getParam(params, 11);
			Document aux = null;
			ExpedientDocumentDto doc = null;
			if (docId != null) {
				aux = documentRepository.findOne(docId);
				doc = documentHelperV3.findOnePerInstanciaProces(exp.getProcessInstanceId(), aux.getCodi());
			}
			String documentCodi = aux != null ? aux.getCodi() : null;
			boolean isAdjunt = documentCodi == null;
			if (contingut == null) {
				// Autogenerar
				if (nom.equals("generate")) {
					mesuresTemporalsHelper.mesuraIniciar("Autogenerar document", "massiva", exp.getTipus().getNom());
					if (doc == null || (!doc.isSignat() && !doc.isRegistrat())) {
						// Genera l'arxiu
						ArxiuDto arxiu = expedientDocumentService.generarAmbPlantilla(exp.getId(),
								exp.getProcessInstanceId(), aux.getCodi());
						// L'actualitza o el crea a la instància de procés
						documentHelper.crearActualitzarDocument(null, exp.getProcessInstanceId(), documentCodi,
								new Date(), arxiu.getNom(), arxiu.getContingut(), arxiuContentType, ambFirma,
								firmaSeparada, firmaContingut, ntiOrigen, ntiEstadoElaboracion, ntiTipoDocumental,
								ntiIdOrigen);
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot modificar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot modificar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Autogenerar document", "massiva", exp.getTipus().getNom());
					// Esborrar
				} else if (nom.equals("delete")) {
					mesuresTemporalsHelper.mesuraIniciar("Esborrar document", "massiva", exp.getTipus().getNom());
					if (doc == null) {
						throw new Exception("Document inexistent: no es pot esborrar");
					} else if (!doc.isSignat() && !doc.isRegistrat()) {
						documentHelperV3.esborrarDocument(null, exp.getProcessInstanceId(), aux.getCodi());
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot esborrar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot esborrar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Esborrar document", "massiva", exp.getTipus().getNom());
					// Modificar data
				} else if (nom.equals("date")) {
					mesuresTemporalsHelper.mesuraIniciar("Canviar data de document", "massiva",
							exp.getTipus().getNom());
					if (doc == null) {
						throw new Exception("Document inexistent: no es pot modificar");
					} else if (!doc.isSignat() && !doc.isRegistrat()) {
						documentHelper.actualitzarDocument(doc.getId(), null, exp.getProcessInstanceId(), data, null,
								doc.getArxiuNom(), null, arxiuContentType, ambFirma, firmaSeparada, firmaContingut,
								ntiOrigen, ntiEstadoElaboracion, ntiTipoDocumental, ntiIdOrigen);
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot modificar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot modificar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Canviar data de document", "massiva",
							exp.getTipus().getNom());
				}
			} else {
				// Adjuntar document
				if (doc == null) {
					mesuresTemporalsHelper.mesuraIniciar("Adjuntar document", "massiva", exp.getTipus().getNom());
					documentHelper.crearDocument(null, exp.getProcessInstanceId(), documentCodi, data, isAdjunt,
							isAdjunt ? nom : null, fileName, contingut, null, // arxiuUuid
							arxiuContentType, ambFirma, firmaSeparada, firmaContingut, ntiOrigen, ntiEstadoElaboracion,
							ntiTipoDocumental, ntiIdOrigen);
					mesuresTemporalsHelper.mesuraCalcular("Adjuntar document", "massiva", exp.getTipus().getNom());
					// Modificar document
				} else {
					mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", exp.getTipus().getNom());
					if (!doc.isSignat() && !doc.isRegistrat()) {
						documentHelper.actualitzarDocument(doc.getId(), null, exp.getProcessInstanceId(), data, null,
								fileName, contingut, arxiuContentType, ambFirma, firmaSeparada, firmaContingut,
								ntiOrigen, ntiEstadoElaboracion, ntiTipoDocumental, ntiIdOrigen);
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot modificar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot modificar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", exp.getTipus().getNom());
				}
			}
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut modificar el document", ex);
			throw ex;
		}
	}

	private void reindexarExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			if (expedientService.luceneReindexarExpedient(exp.getId()))
				ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			else {
				ome.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
				ome.setError("No s'ha reindexat tot l'expedient correctament, cal entrar a revisar les dades");
				ome.setAuxText(
						"El procés de reindexació ha retornat que no s'ha reindexar totalment l'expeient, cal entrar en la gestió de l'expedient per revisar les dades");
			}
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.saveAndFlush(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut reindexar l'expedient", ex);
			throw ex;
		}
	}

	private void reprendreExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			expedientService.desfinalitzar(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut desfer la finalització de l'expedient", ex);
			throw ex;
		}
	}

	private void finalitzarExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			expedientService.finalitzar(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut finalitzar l'expedient", ex);
			throw ex;
		}
	}

	private void migrarExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			if (exp.getTipus().isArxiuActiu() && exp.getArxiuUuid() == null)
				expedientService.migrarArxiu(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut MIGRAR", ex);
			throw ex;
		}
	}

	private void reprendreTramitacio(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			expedientService.reprendre(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut reprendre la tramitació de l'expedient", ex);
			throw ex;
		}
	}

	private void buidarLogExpedient(ExecucioMassivaExpedient ome) throws Exception {
		Expedient exp = ome.getExpedient();
		try {
			ome.setDataInici(new Date());
			expedientRegistreService.registreBuidarLog(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error(
					"OPERACIO:" + ome.getId() + ". No s'ha pogut eliminar la informació de registre de l'expedient",
					ex);
			throw ex;
		}
	}

	private void reassignarTasca(ExecucioMassivaExpedient ome) throws Exception {
		String tascaId = ome.getTascaId();
		try {
			ome.setDataInici(new Date());
			JbpmTask tasca = tascaHelper.getTascaComprovacionsTramitacio(tascaId, false, false);
			if (tasca != null && tasca.isOpen()) {
				ProcessInstanceExpedient piexp = jbpmHelper
						.expedientFindByProcessInstanceId(tasca.getProcessInstanceId());
				expedientTascaService.reassignar(piexp.getId(), tasca.getId(), ome.getExecucioMassiva().getParam1());
			}
			if (tasca == null) {
				ome.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
				ome.setError(messageHelper.getMessage("tasca.massiva.reassignar.buit"));
			} else {
				ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			}
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut reassignar la tasca", ex);
			throw ex;
		}
	}

	/**
	 * Actualitza les plantilles dels documents de les definicions de procés que
	 * tinguin expedients actius amb la informació dels documents plantilla de la
	 * darrera versió de la definició de procés. Aquest tipus d'execucions massives
	 * es programen des de la pipella d'informació del tipus d'expedient a la
	 * interfície vella. Per a la nova interfície les consultes haurien de fer
	 * referència a variables del propi tipus d'expedient.
	 */
	private void propagarPlantilles(ExecucioMassivaExpedient ome) throws Exception {
		try {
			int versionsCount = 0;
			int actualitzacionsCount = 0;

			ome.setDataInici(new Date());
			ExpedientTipus expedientTipus = ome.getExecucioMassiva().getExpedientTipus();
			// Recupera la darrera versió identificada per l'ome.definicioProcesId
			DefinicioProces definicioDarrera = definicioProcesRepository.findById(ome.getDefinicioProcesId());
			// Mira si hi ha documents amb plantilles
			List<Document> documentsPlantilles = new ArrayList<Document>();
			for (Document document : definicioDarrera.getDocuments())
				if (document.isPlantilla())
					documentsPlantilles.add(document);
			if (documentsPlantilles.size() > 0) {
				int expedientsActiusCount;
				// Propaga els documents per a totes les versions anteriors
				for (DefinicioProces definicioAnterior : definicioProcesRepository
						.findByExpedientTipusIJpbmKey(expedientTipus.getId(), definicioDarrera.getJbpmKey())) {
					if (definicioAnterior.getVersio() < definicioDarrera.getVersio()) {
						expedientsActiusCount = expedientHelper.findByFiltreGeneral(expedientTipus.getEntorn(), null,
								null, null, null, expedientTipus, null, true, false).size();
						if (expedientsActiusCount > 0) {
							for (Document documentPlantilla : documentsPlantilles) {
								Document document = documentRepository.findByDefinicioProcesAndCodi(definicioAnterior,
										documentPlantilla.getCodi());
								// Comprova si existeix
								if (document != null && document.isPlantilla()) {
									document.setArxiuNom(documentPlantilla.getArxiuNom());
									document.setArxiuContingut(documentPlantilla.getArxiuContingut());
									documentRepository.saveAndFlush(document);
									actualitzacionsCount++;
								}
							}
						}
					}
					versionsCount++;
				}
			}
			ome.setAuxText(messageHelper.getMessage("exptipus.info.propagar.plantilles.auxText", new Object[] {
					definicioDarrera.getJbpmKey(), versionsCount, documentsPlantilles.size(), actualitzacionsCount }));
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId()
					+ ". No s'han pogut propagar els documents plantilla de la definició de procés", ex);
			throw ex;
		}
	}

	/**
	 * Actualitza els variables tipus filtre i informe de les consultes per a que
	 * facin referència a la darrera versió de la definició de procés.
	 * L'identificador de la consulta es passa com a defProcId aprofitant el camp de
	 * la BBDD per no crear-ne de noves. Aquest tipus d'execucions massives es
	 * programen des de la pipella d'informació del tipus d'expedient a la
	 * interfície vella. Per a la nova interfície les consultes haurien de fer
	 * referència a variables del propi tipus d'expedient.
	 */
	private void propagarConsultes(ExecucioMassivaExpedient ome) throws Exception {
		try {
			int variablesCount = 0;
			int actualitzacionsCount = 0;
			int errorsCount = 0;
			StringBuilder errorMsg = new StringBuilder();

			ExecucioMassivaEstat estat = ExecucioMassivaEstat.ESTAT_FINALITZAT;

			ome.setDataInici(new Date());

			Entorn entorn = entornRepository.findOne(ome.getExecucioMassiva().getEntorn());
			// Recupera la consulta (* s'ha aprofitat l'id de la definició de procés per
			// passar l'id de la consulta
			Consulta consulta = consultaRepository.findById(ome.getDefinicioProcesId());
			Camp camp;
			// Per cada variable de tipus informe o filtre
			for (ConsultaCamp consultaCamp : consulta.getCamps()) {
				if (consultaCamp.getTipus() != TipusConsultaCamp.PARAM && consultaCamp.getDefprocJbpmKey() != null) {
					// Recupera la darrera versió de la definició de procés
					DefinicioProces definicioDarrera = definicioProcesRepository
							.findDarreraVersioAmbEntornIJbpmKey(entorn.getId(), consultaCamp.getDefprocJbpmKey());
					if (consultaCamp.getDefprocVersio() != definicioDarrera.getVersio()) {
						camp = campRepository.findByDefinicioProcesAndCodi(definicioDarrera,
								consultaCamp.getCampCodi());
						if (camp != null) {
							consultaCamp.setDefprocVersio(definicioDarrera.getVersio());
							consultaCampRepository.save(consultaCamp);
							actualitzacionsCount++;
						} else {
							estat = ExecucioMassivaEstat.ESTAT_ERROR;
							// el codi del missatge es troba al fitxer messages.propeties de la 3.1
							if (errorMsg.length() > 0)
								errorMsg.append(". ");

							errorMsg.append(messageHelper.getMessage("exptipus.info.propagar.consultes.error.camp",
									new Object[] { consultaCamp.getCampCodi(), consultaCamp.getTipus(),
											consulta.getCodi(), consultaCamp.getDefprocJbpmKey(),
											consultaCamp.getDefprocVersio(), definicioDarrera.getVersio() }));
							errorsCount++;
						}
					}
					variablesCount++;
				}
			}
			ome.setAuxText(messageHelper.getMessage("exptipus.info.propagar.consultes.auxText",
					new Object[] { consulta.getCodi(), variablesCount, actualitzacionsCount, errorsCount }));
			ome.setEstat(estat);
			ome.setError(errorMsg.length() > 0 ? errorMsg.toString() : null);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId()
					+ ". No s'han pogut propagar els documents plantilla de la definició de procés", ex);
			throw ex;
		}
	}
	
	/**
	 * Dona d'alta un expedient a partir de la informació CSV guardada en el paràmetre2 de l'execució
	 * massiva i el número d'ordre. Un cop donat d'alta informa l'id d'expedient de l'execució
	 * massiva.
	 */
	private void altaMassivaCsv(ExecucioMassivaExpedient ome) throws Exception {
		ExecucioMassivaEstat estat = ExecucioMassivaEstat.ESTAT_FINALITZAT;
		ome.setDataInici(new Date());
		StringBuilder errText = new StringBuilder();
		try {
			ExpedientTipus expedientTipus = ome.getExecucioMassiva().getExpedientTipus();
			DefinicioProces definicioProces = null;
			if (!expedientTipus.isAmbInfoPropia() && ome.getDefinicioProcesId() != null) {
				definicioProces = definicioProcesRepository.findById(ome.getDefinicioProcesId());
			}
			String [][] contingutCsv;
			try {
				CsvHelper csvHelper = new CsvHelper();
				contingutCsv = csvHelper.parse(ome.getExecucioMassiva().getParam2());				
			} catch(Exception e) {
				logger.error("Alta masiva CSV error :" + ome.getId()
				+ ". No s'ha pogut recuperar el contingut del CSV: " + e.getMessage());
				throw e;
			}
			int index = ome.getOrdre() + 1;
			// Any
			Integer any = null;
			try {
				any = Integer.parseInt(contingutCsv[index][0]);
			} catch (Exception e) {
				errText.append("Error en el format de l'any de l'expedient. ");
				logger.error("Alta masiva CSV error :" + ome.getId()
				+ ". Error convertint \"" + contingutCsv[index][0] + "\" a enter per l'any d'inici de l'expedient: " + e.getMessage());
			}
			// Número
			String numero = contingutCsv[index][1];
			// Títol
			String titol = contingutCsv[index][2];
			// Variables
			Map<String, Object> variables = new HashMap<String, Object>();
			String codi;
			String valor;
			Object valorHelium;
			for (int i = 3; i < contingutCsv[0].length; i++) {
				codi = contingutCsv[0][i];
				valor = contingutCsv[index][i];
				try {
					valorHelium = this.getValorSimpleHelium(expedientTipus, definicioProces, codi, valor);
					variables.put(codi, valorHelium);
				} catch (Exception e) {
					logger.error("Error interpretant el text \"" + valor + "\" com a data en l'alta massiva d'expedients per CSV pel camp " +
							codi + " del tipus d'expedient " + expedientTipus.getCodi() + " - " + expedientTipus.getNom() );
				}
			}
			// Alta de l'expedient
			Expedient expedient = expedientHelper.iniciar(
					expedientTipus.getEntorn().getId(), 
					ome.getExecucioMassiva().getUsuari(), 
					expedientTipus.getId(), 
					ome.getDefinicioProcesId(), 
					any, 
					numero, 
					titol, 
					null, 
					null, 
					null, 
					null, 
					false, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					false, 
					null, 
					null, 
					false, 
					variables, 
					null, 
					net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto.INTERN,
					null,
					null,
					null,
					null);
			ome.setExpedient(expedient);
			ome.setAuxText("Expedient " + expedient.getIdentificador() + " creat correctament per CSV");
		} catch (Exception ex) {
			estat = ExecucioMassivaEstat.ESTAT_ERROR;
			String errMsg = "Error no controlat en l'execució massiva d'alta d'expedient per CSV: " + ex.getMessage();
			logger.error(errMsg, ex);
			errText.append(errMsg);
		}
		ome.setError(errText.length() > 0 ? errText.toString() : null);
		ome.setDataFi(new Date());
		ome.setEstat(estat);
		execucioMassivaExpedientRepository.save(ome);
	}

	/** Obté el valor simple d'Helium a partir del codi i valor d'una variable per un tipus d'expedient.
	 * 
	 * @param expedientTipus Expedient tipus que pot tenir informació a nivell de tipus o de definició.
	 * @param definicioProces Definició de procés on buscar el camp si el tipus no té informació a nivell de tipus d'expedient.
	 * @param codi Codi de la variable
	 * @param valor Valor en text
	 * @return Retorna el valor segons el tipus de variable simple.
	 * @throws ParseException Error en el cas de no poder interpretar un enter, booleà, data o decimal.
	 */
	private Object getValorSimpleHelium(
			ExpedientTipus expedientTipus, 
			DefinicioProces definicioProces,
			String codi, 
			String valor) throws ParseException {
		Object valorHelium = null;
		if (expedientTipus != null && valor != null) {
			// Obté la definció del camp
			Camp camp;
			if (expedientTipus.isAmbInfoPropia())
				camp = campRepository.findByExpedientTipusAndCodi(
						expedientTipus.getId(), 
						codi,
						expedientTipus.getExpedientTipusPare() != null);
			else {
				camp = campRepository.findByDefinicioProcesAndCodi(
						definicioProces,
						codi);			
			}
			if (camp != null) {
				switch(camp.getTipus()) {
				case BOOLEAN:
					valor = valor.toLowerCase().trim();
					valorHelium  = new Boolean("s".equals(valor) || "true".equals(valor));
					break;
				case DATE:
					valorHelium = DateUtils.parseDate(valor, new String[]{
							"y-M-d",
							"y-M-d H:m:s",
							"y-M-d H:m:s.S",
							"d/M/y",
							"d/M/y H:m:s",
							"d/M/y H:m:s.S",
							});
					break;
				case FLOAT:
					valorHelium = Double.parseDouble(valor.replace(",", "."));
					break;
				case PRICE:
					valorHelium = new BigDecimal(valor.replace(",", "."));
					break;
				case INTEGER:
					valorHelium = Integer.parseInt(valor);
				case STRING:
				case SELECCIO:
				case SUGGEST:
				case TEXTAREA:
					valorHelium = valor;
					break;
				default:
					valorHelium = null;
					break;
				
				}
			}
		}
		return valorHelium;
	}

	private double getPercent(Long value, Long total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
		return Math.round(value * 100 / total);
	}

	private static final Log logger = LogFactory.getLog(ExecucioMassivaService.class);
}
