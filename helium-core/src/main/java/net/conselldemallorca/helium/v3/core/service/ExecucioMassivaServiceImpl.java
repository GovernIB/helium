package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.helper.DissenyHelper;
import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MailHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.core.helper.TascaHelper;
import net.conselldemallorca.helium.core.helper.TerminiHelper;
import net.conselldemallorca.helium.core.helperv26.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
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
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisDto;
import net.conselldemallorca.helium.v3.core.api.dto.PrincipalTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
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
import net.conselldemallorca.helium.v3.core.repository.ConsultaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
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
	private DissenyHelper dissenyHelper;
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
	private IndexHelper indexHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	
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
		if ((dto.getExpedientIds() != null && !dto.getExpedientIds().isEmpty()) ||
			(dto.getTascaIds() != null && dto.getTascaIds().length > 0) ||
			(dto.getProcInstIds() != null && !dto.getProcInstIds().isEmpty()) ||
			(dto.getDefProcIds() != null && dto.getDefProcIds().length > 0)) {
			String log = "Creació d'execució massiva (dataInici=" + dto.getDataInici();
			if (dto.getExpedientTipusId() != null) log += ", expedientTipusId=" + dto.getExpedientTipusId();
			log += ", numExpedients=";
			if (dto.getExpedientIds() != null) log += dto.getExpedientIds().size();
			else if (dto.getProcInstIds() != null) log += dto.getProcInstIds().size();
			else log += "0";
			log += ")";
			logger.debug(log);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			
			ExecucioMassiva execucioMassiva = new ExecucioMassiva(
					auth.getName(),
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
				
				for (Long expedientId: dto.getExpedientIds()) {
					Expedient expedient = expedientRepository.findOne(expedientId);
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							expedient,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
					if (expedientTipus == null && expedient != null)
						expedientTipus = expedient.getTipus();
				}
			} else if (dto.getTascaIds() != null) {
				for (String tascaId: dto.getTascaIds()) {
					JbpmTask task = tascaHelper.getTascaComprovacionsTramitacio(
							tascaId,
							false,
							false);
					Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(
							task.getProcessInstanceId());
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							expedient,
							tascaId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
					if (expedientTipus == null && expedient != null)
						expedientTipus = expedient.getTipus();
				}
			} else if (dto.getProcInstIds() != null) {
				for (String procinstId: dto.getProcInstIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							procinstId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
					if (expedientTipus == null) {
						Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(procinstId);
						if (expedient != null)
							expedientTipus = expedient.getTipus();
					}
				}
			} else if (dto.getDefProcIds() != null && dto.getDefProcIds().length > 0) {
				for (Long defProcId: dto.getDefProcIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							defProcId,
							ordre++);
					execucioMassiva.addExpedient(eme);
				}
			}
			execucioMassiva.setEntorn(EntornActual.getEntornId());
			
			if (expedients || (dto.getDefProcIds() != null && dto.getDefProcIds().length > 0)) {
				
				execucioMassiva.setRols(getRols(auth, expedientTipus));
				
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
	
	private String getRols(Authentication auth, ExpedientTipus expedientTipus) {

		String rols = "";
		// Rols usuari
		List<String> rolsUsuari = new ArrayList<String>();
		if (auth != null && auth.getAuthorities() != null) {
			for (GrantedAuthority gauth : auth.getAuthorities()) {
				rolsUsuari.add(gauth.getAuthority());
			}
		}
		// Rols tipus expedient
		List<String> rolsTipusExpedient = new ArrayList<String>();
		rolsTipusExpedient.add("ROLE_ADMIN");
		rolsTipusExpedient.add("ROLE_USER");
		rolsTipusExpedient.add("ROLE_WS");
		if (expedientTipus != null) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					expedientTipus.getId(),
					ExpedientTipus.class);
			if (permisos != null)
				for (PermisDto permis: permisos) {
					if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus()))
						rolsTipusExpedient.add(permis.getPrincipalNom());
				}
		}
		rolsUsuari.retainAll(rolsTipusExpedient);
		
		for (String rol : rolsUsuari) {
			rols += rol + ",";
		}
		if (rols.length() > 0) {
			rols = rols.substring(0, rols.length() - 1);
			logger.info(">>> EXECUCIÓ MASSIVA - ROLS Reals: " + rols);
			if (rols.length() > 2000) {
				rols = rols.substring(0, 2000);
				rols = rols.substring(0, rols.lastIndexOf(","));
			}
		} else {
			rols = null;
		}
		
		logger.info(">>> EXECUCIÓ MASSIVA - ROLS Finals: " + rols);
		return rols;
	}
	
	@Transactional
	@Override
	public void cancelarExecucio(Long id) {
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
	public String getJsonExecucionsMassivesByUser(int results, boolean viewAll) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		int page = results < 0 ? 0 : results;
		
		Pageable paginacio = new PageRequest(page,10, Direction.DESC, "dataInici");		
		List<ExecucioMassiva> execucions = null;
		
		if (viewAll){
			execucions = execucioMassivaRepository.findByEntornOrderByDataIniciDesc(EntornActual.getEntornId(), paginacio);
		}else{
			execucions = execucioMassivaRepository.findByUsuariAndEntornOrderByDataIniciDesc(auth.getName(), EntornActual.getEntornId(), paginacio);
		}
		
		//Recuperem els resultats
		final int ID = 0;
		final int ERROR = 1;
		final int PENDENT = 2;
		final int TOTAL = 3;
		
		JSONArray ljson = new JSONArray();	
		
		if (!execucions.isEmpty()) {
			List<Object[]> comptadorsExecucions = execucioMassivaExpedientRepository.findResultatsExecucionsMassives(execucions);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");		
			
			for (ExecucioMassiva execucio: execucions) {
				
				Object[] comptadorTrobat = null;
				for (Object[] comptadorsExecucio: comptadorsExecucions) {
					if (execucio.getId().equals(comptadorsExecucio[ID])) {
						comptadorTrobat = comptadorsExecucio;
						break;
					}
				}
				
				if (comptadorTrobat != null) {
					JSONArray ljson_exp = new JSONArray();
					String tasca = "";
					
					Long id = (Long)comptadorTrobat[ID];
					Long error = (Long)comptadorTrobat[ERROR];
					Long pendent = (Long)comptadorTrobat[PENDENT];
					Long total = (Long)comptadorTrobat[TOTAL];
					Long ok = (total - error - pendent);
					
					Map<String, Serializable> mjson = new LinkedHashMap<String, Serializable>();
					mjson.put("id", id);
					mjson.put("tipus", execucio.getTipus() != null ? execucio.getTipus().toString() : "");
					mjson.put("expedientTipusId", execucio.getExpedientTipus() != null ? execucio.getExpedientTipus().getId() : "");
					mjson.put("text", JSONValue.escape(getTextExecucioMassiva(execucio, tasca)));
					
					mjson.put("error", error);
					mjson.put("pendent", pendent);
					mjson.put("ok", ok);
					mjson.put("total", total);
					
					mjson.put("executades", getPercent((total - pendent), total));
					
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
						e.printStackTrace();
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
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		return obj;
	}
	
	@Override
	public byte[] serialize(Object obj){
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
				e.printStackTrace();
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
		Long success = 0L;
		Long danger = 0L;
		Long pendent = 0L;
		if (!expedients.isEmpty()) {
			ExecucioMassivaExpedient em = expedients.get(0);
			if (em.getTascaId() != null) {
				JbpmTask task = jbpmHelper.getTaskById(em.getTascaId());					
				if (task != null){
					if (task.isCacheActiu())
						tasca = task.getFieldFromDescription("titol");
					else
						tasca = task.getTaskName();
				}
			}
			for (ExecucioMassivaExpedient expedient: expedients) {
				Expedient exp = expedient.getExpedient();
				String titol = "";
				if (exp != null) { 
					if (exp.getNumero() != null)
						titol = "[" + exp.getNumero() + "]";
		    		if (exp.getTitol() != null) 
		    			titol += (titol.length() > 0 ? " " : "") + exp.getTitol();
		    		if (titol.length() == 0)
		    			titol = exp.getNumeroDefault();
				} else if (execucio.getTipus() == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC){
					titol = messageHelper.getMessage("expedient.massiva.actualitzar.dp") + " " + expedient.getExecucioMassiva().getParam1();
				} else if (execucio.getTipus() == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC){
					DefinicioProces dp = definicioProcesRepository.findOne(expedient.getDefinicioProcesId());
					String idPerMostrar = dp != null ? dp.getIdPerMostrar() : expedient.getAuxText();
					titol = messageHelper.getMessage("expedient.massiva.eliminar.dp") + " (" + idPerMostrar + ")";
				}
				
				Map<String, Object> mjson_exp = new LinkedHashMap<String, Object>();
				mjson_exp.put("id", expedient.getId());
				mjson_exp.put("titol", titol);
				mjson_exp.put("estat", expedient.getEstat().name());
				
				if (expedient.getDataFi() != null){
					mjson_exp.put("dataFi", sdf.format(expedient.getDataFi()));
				}
				
				String error = expedient.getError();
				if ( error != null || expedient.getEstat() == ExecucioMassivaEstat.ESTAT_ERROR) {
					if (error != null)
						error = error.replace("'", "&#8217;").replace("\"", "&#8220;");
					danger++;
				} else if (expedient.getDataFi() == null && ExecucioMassivaEstat.ESTAT_PENDENT.equals(expedient.getEstat())){
					pendent++;
				} else {
					success++;
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
		mjson.put("ok", success);
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
			Object obj = (Object)deserialize(exe.getParam2());
			Long dfId = null;
			if (obj instanceof Long) {
				dfId = (Long)obj;
			} else {
				Object[] arobj = (Object[]) deserialize(exe.getParam2());
				if (arobj[0] instanceof Long) {
					dfId = (Long)arobj[0];
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
		if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_TASCA)){
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
				label += messageHelper.getMessage("expedient.massiva.tasca.accio") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocGuardar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.guardar") + (param2 == null ? "" : " &#8216;'" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocEsborrar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.borrar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocGenerar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.doc.generar") + (param2 == null ? "" : " &#8216;'" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("RegEsborrar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.reg.borrar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("RegGuardar")) {
				label += messageHelper.getMessage("expedient.massiva.tasca.reg.guardar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC)){
			if (execucioMassiva.getExpedientTipus() == null && execucioMassiva.getParam1() != null) {
				String versio = "";
				try { versio += (Integer)deserialize(execucioMassiva.getParam2()); } catch (Exception e){} 
				label = messageHelper.getMessage("expedient.massiva.actualitzar") + " (" + execucioMassiva.getParam1() + " v." + versio + ")";
			} else {
				DefinicioProces definicioProces = getDefinicioProces(execucioMassiva);
				label = messageHelper.getMessage("expedient.massiva.actualitzar") + (definicioProces == null ? "" : " (" + definicioProces.getJbpmKey() + " v." + definicioProces.getVersio() + ")");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC)){
				label = messageHelper.getMessage("expedient.massiva.eliminar.versio.dp") + " (" + execucioMassiva.getExpedientTipus().getNom() + ")";
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_SCRIPT)){
			String script = "";
			if (execucioMassiva.getParam2() != null) {
				try {
					Object param2 = deserialize(execucioMassiva.getParam2());
					if (param2 instanceof Object[]) {
						script = (String)((Object[])param2)[0];
					} else if (param2 instanceof String) {
						script = (String)param2;
					} else {
						script = param2.toString();
					}
					script = script.replace("'", "&#39;").replace("\"", "&#34;");
				} catch (Exception ex) {
					logger.error("OPERACIO:" + execucioMassiva.getId() + ". No s'ha pogut obtenir la operació", ex);
				}
			}
			//String script = ((String) deserialize(execucioMassiva.getParam2())).replace("'", "&#39;").replace("\"", "&#34;");
			label = messageHelper.getMessage("expedient.massiva.executarScriptMas") + " " + (script.length() > 20 ? script.substring(0,20) : script);
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_ACCIO)){
			String accio = "";
			if (execucioMassiva.getParam2() != null) {
				try {
					Object param2 = deserialize(execucioMassiva.getParam2());
					if (param2 instanceof Object[]) {
						accio = (String)((Object[])param2)[0];
					} else {
						accio = (String) param2;
					}
				} catch (Exception ex) {
					logger.error("OPERACIO:" + execucioMassiva.getId() + ". No s'ha pogut obtenir la operació", ex);
				}
			}
			label = messageHelper.getMessage("expedient.massiva.accions") + " " + accio;
		} else if (tipus.equals(ExecucioMassivaTipus.ATURAR_EXPEDIENT)){
			Object paramDos = deserialize(execucioMassiva.getParam2());
			String motiu = null;
			if (paramDos != null)
				motiu = paramDos.toString();
			label = messageHelper.getMessage("expedient.massiva.aturar")+ (motiu == null ? "" : ": "+ (motiu.length() > 20 ? motiu.substring(0,20) : motiu));
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_VARIABLE)){
			label = messageHelper.getMessage("expedient.massiva.modificar_variables") + " " + execucioMassiva.getParam1();
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_DOCUMENT)){
			label = messageHelper.getMessage("expedient.massiva.documents");
		} else if (tipus.equals(ExecucioMassivaTipus.REINDEXAR)){
			label = messageHelper.getMessage("expedient.eines.reindexar.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.BUIDARLOG)){
			label = messageHelper.getMessage("expedient.eines.buidarlog.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.REPRENDRE_EXPEDIENT)){
			label = messageHelper.getMessage("expedient.eines.reprendre_expedient");
		} else if (tipus.equals(ExecucioMassivaTipus.REASSIGNAR)){
			label = messageHelper.getMessage("expedient.eines.reassignar.expedients");
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
				execucioMassivaExpedientId = execucioMassivaExpedientRepository.findNextExecucioMassivaExpedient(nextMassiu);
			}
		}
		
		if (execucioMassivaExpedientId == null && !nextFound)
			execucioMassivaExpedientId = execucioMassivaExpedientRepository.findExecucioMassivaExpedientId(ara);
		
		if (execucioMassivaExpedientId == null) {
			// Comprobamos si es una ejecución masiva sin expedientes asociados. En ese caso actualizamos la fecha de fin
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
	public void executarExecucioMassiva(Long ome_id) {
		ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
		if (ome == null)
			throw new NoTrobatException(ExecucioMassivaExpedient.class, ome_id);
		
		ExecucioMassiva exm = ome.getExecucioMassiva();
		ExecucioMassivaTipus tipus = exm.getTipus();
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				exm.getEntorn(),
				false); 
		
		Expedient expedient = null;
		if (ome.getExpedient() != null) {
			expedient = ome.getExpedient();
		} else if (tipus != ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC){
			expedient = expedientHelper.findExpedientByProcessInstanceId(ome.getProcessInstanceId());
		}
		
		ExpedientTipus expedientTipus;
		if (expedient == null && tipus == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC)
			expedientTipus = exm.getExpedientTipus();
		else
			expedientTipus = expedient.getTipus();
		
		logger.debug(
				"Executant la acció massiva (" +
				"expedientTipusId=" + (expedientTipus != null ? expedientTipus.getId() : "") + ", " +
				"dataInici=" + ome.getDataInici() + ", " +
				"expedient=" + ome.getId() + ", " +
				"acció=" + exm.getTipus());
		
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"executar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"executar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"executar",
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"executar.count",
						entorn.getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"completar",
						entorn.getCodi(),
						(expedientTipus != null ? expedientTipus.getCodi() : "")));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"completar.count",
						entorn.getCodi(),
						(expedientTipus != null ? expedientTipus.getCodi() : "")));
		countTipexp.inc();
		try {
			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
			
//			final String user = exm.getUsuari();
//	        Principal principal = new Principal() {
//				public String getName() {
//					return user;
//				}
//			};
			
			Authentication authentication =  new UsernamePasswordAuthenticationToken (
					ome.getExecucioMassiva().getAuthenticationPrincipal(),
					"N/A",	//	ome.getExecucioMassiva().getAuthenticationCredentials(),
					ome.getExecucioMassiva().getAuthenticationRoles());
			
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			
			String expedient_s = null;
	        if (MesuresTemporalsHelper.isActiu())
        		expedient_s = (expedientTipus != null ? expedientTipus.getNom() : "");
	        
			if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA){
				gestioTasca(ome);
			} else if (tipus == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC){
				mesuresTemporalsHelper.mesuraIniciar("Actualitzar", "massiva", expedient_s);
				actualitzarVersio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC){
				mesuresTemporalsHelper.mesuraIniciar("Eliniar", "massiva", expedient_s);
				eliminarVersio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT){
				mesuresTemporalsHelper.mesuraIniciar("Executar script", "massiva", expedient_s);
				executarScript(ome);
				mesuresTemporalsHelper.mesuraCalcular("Executar script", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO){
				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva", expedient_s);
				executarAccio(ome);
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.ATURAR_EXPEDIENT){
				mesuresTemporalsHelper.mesuraIniciar("Aturar expedient", "massiva", expedient_s);
				aturarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Aturar expedient", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_VARIABLE){
				mesuresTemporalsHelper.mesuraIniciar("Modificar variable", "massiva", expedient_s);
				modificarVariable(ome);
				mesuresTemporalsHelper.mesuraCalcular("Modificar variable", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_DOCUMENT){
				mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", expedient_s);
				modificarDocument(ome);
				mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REINDEXAR){
				mesuresTemporalsHelper.mesuraIniciar("Reindexar", "massiva", expedient_s);
				reindexarExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Reindexar", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.BUIDARLOG){
				mesuresTemporalsHelper.mesuraIniciar("Buidar log", "massiva", expedient_s);
				buidarLogExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("Buidar log", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE_EXPEDIENT){
				mesuresTemporalsHelper.mesuraIniciar("desfer fi process instance", "massiva", expedient_s);
				reprendreExpedient(ome);
				mesuresTemporalsHelper.mesuraCalcular("desfer fi process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE){
				mesuresTemporalsHelper.mesuraIniciar("reprendre tramitació process instance", "massiva", expedient_s);
				reprendreTramitacio(ome);
				mesuresTemporalsHelper.mesuraCalcular("reprendre tramitació process instance", "massiva", expedient_s);
			} else if (tipus == ExecucioMassivaTipus.REASSIGNAR){
				mesuresTemporalsHelper.mesuraIniciar("Reassignar", "massiva", expedient_s);
				//reassignarExpedient(ome);
				reassignarTasca(ome);
				mesuresTemporalsHelper.mesuraCalcular("Reassignar", "massiva", expedient_s);
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
		} catch (Exception ex) {
			logger.error("Error al executar la acció massiva (expedientTipusId=" + (expedientTipus != null ? expedientTipus.getId() : "") + ", dataInici=" + ome.getDataInici() + ", expedient=" + (expedient == null ? null : expedient.getId()) + ", acció=" + ome, ex);
			
			Throwable excepcioRetorn = ex;
			if (tipus != ExecucioMassivaTipus.ELIMINAR_VERSIO_DEFPROC && ExceptionUtils.getRootCause(ex) != null) {
				excepcioRetorn = ExceptionUtils.getRootCause(ex);
			}
			
			TascaProgramadaServiceImpl.saveError(ome_id, excepcioRetorn, exm.getTipus());
			throw new ExecucioMassivaException(
					entorn.getId(), 
					entorn.getCodi(), 
					entorn.getNom(), 
					expedient == null ? null : expedient.getId(), 
					expedient == null ? null : expedient.getTitol(), 
					expedient == null ? null : expedient.getNumero(), 
					expedientTipus == null ? null : expedientTipus.getId(),
					expedientTipus == null ? null : expedientTipus.getCodi(),
					expedientTipus == null ? null : expedientTipus.getNom(),
					ome.getExecucioMassiva().getId(), 
					ome.getId(), 
					"Error al executar la acció massiva", 
					ex);
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
				if (em.getTipus() == ExecucioMassivaTipus.BUIDARLOG && em.getParam1() != null && !em.getParam1().isEmpty()) {
					
					//Establim les dades d'autenticació i d'entorn per a poder crear i executar una execució massiva programada
					Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
					Authentication authentication =  new UsernamePasswordAuthenticationToken (
							ome.getExecucioMassiva().getAuthenticationPrincipal(),
							"N/A",
							ome.getExecucioMassiva().getAuthenticationRoles());

					SecurityContextHolder.getContext().setAuthentication(authentication);
					
					Long ea = EntornActual.getEntornId();
					EntornActual.setEntornId(em.getEntorn());
					
					//programem l'execució massiva
					programarEliminarDp(Long.parseLong(em.getParam1()), em.getExpedientTipus().getId());
					
					//torem a deixar els valors d'entor i autenticació tal i com estaven abans
					EntornActual.setEntornId(ea);
					SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU:" + ome.getExecucioMassiva().getId() + ". No s'ha pogut finalitzar l'expedient massiu", ex);
				throw new ExecucioMassivaException(
						ome.getExpedient().getEntorn().getId(), 
						ome.getExpedient().getEntorn().getCodi(), 
						ome.getExpedient().getEntorn().getNom(),
						ome.getExpedient().getId(),
						ome.getExpedient().getTitol(), 
						ome.getExpedient().getNumero(), 
						ome.getExpedient().getTipus().getId(), 
						ome.getExpedient().getTipus().getCodi(), 
						ome.getExpedient().getTipus().getNom(), 
						ome.getExecucioMassiva().getId(), 
						ome.getId(), 
						"EXPEDIENTMASSIU: No s'ha pogut finalitzar l'expedient massiu", 
						ex);
			}
			try {
				if (ome.getExecucioMassiva().getEnviarCorreu()) {
					
					// Correu
					List<String> emailAddresses = new ArrayList<String>();
					
					Persona persona = personaRepository.findByCodi(ome.getExecucioMassiva().getUsuari());
					emailAddresses.add(persona.getEmail());
	
					mailHelper.send(
							GlobalProperties.getInstance().getProperty("app.correu.remitent"), 
							emailAddresses, 
							null,
							null,
							"Execució massiva",
							"L'execució massiva ha finalitzat.");
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
				throw new ExecucioMassivaException(
						ome.getExpedient().getEntorn().getId(), 
						ome.getExpedient().getEntorn().getCodi(), 
						ome.getExpedient().getEntorn().getNom(), 
						ome.getExpedient().getId(), 
						ome.getExpedient().getTitol(), 
						ome.getExpedient().getNumero(), 
						ome.getExpedient().getTipus().getId(), 
						ome.getExpedient().getTipus().getCodi(), 
						ome.getExpedient().getTipus().getNom(), 
						ome.getExecucioMassiva().getId(), 
						ome.getId(), 
						"EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", 
						ex);
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
	
	private void programarEliminarDp(Long dpId, Long expedientTipusId) throws Exception {
		Date dInici = new Date();
		ExecucioMassivaDto emdto = new ExecucioMassivaDto();
		
		emdto.setDataInici(dInici);
		emdto.setEnviarCorreu(false);
		emdto.setExpedientTipusId(expedientTipusId);
		emdto.setTipus(ExecucioMassivaTipusDto.ELIMINAR_VERSIO_DEFPROC);
		emdto.setDefProcIds(new Long[]{dpId});
		
		this.crearExecucioMassiva(emdto);
	}
	
	@SuppressWarnings("unchecked")
	private void gestioTasca(ExecucioMassivaExpedient ome) {
		String tascaId = ome.getTascaId();
		String accio = ome.getExecucioMassiva().getParam1();
		String tasca = null;
		String expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			ExpedientTascaDto task = tascaService.findAmbIdPerTramitacio(tascaId);
			if (task != null) tasca = task.getTascaNom(); 
			expedient = ome.getExpedient().getTipus().getNom();
		}
		try {
			ome.setDataInici(new Date());
			if ("Guardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				Map<String, Object> valors = (Map<String, Object>)param2[1]; 
				tascaService.guardar(tascaId, valors);
				mesuresTemporalsHelper.mesuraCalcular("Guardar", "massiva_tasca", expedient, tasca);
			} else if ("Validar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Validar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				Map<String, Object> valors = (Map<String, Object>)param2[1];
				tascaService.validar(tascaId, valors);
				mesuresTemporalsHelper.mesuraCalcular("Validar", "massiva_tasca", expedient, tasca);
			} else if ("Completar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Completar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				Long entornId = (Long)param2[0];
				String transicio = (String)param2[1];
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
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				String accio_exec = (String)param2[1];
				tascaService.executarAccio(tascaId, accio_exec);
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva_tasca", expedient, tasca);
			} else if ("DocGuardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				Long entornId = (Long)param2[0];
				String codi = (String)param2[1];
				Date data = (Date)param2[2];
				byte[] contingut = (byte[])param2[3];
				String nomArxiu = (String)param2[4];
				if (tascaService.isTascaValidada(tascaId)) {
					tascaService.guardarDocumentTasca(entornId, tascaId, codi, data, nomArxiu, contingut, ome.getExecucioMassiva().getUsuari());
				} else {
					throw new ValidacioException("OPERACIO:" + ome.getId() + ". No s'ha pogut guardar el document a la tasca. Perquè la tasca no està validada");
				}
				mesuresTemporalsHelper.mesuraCalcular("Guardar document", "massiva_tasca", expedient, tasca);
			} else if ("DocEsborrar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Esborrar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				String codi = (String)param2[1];
				if (tascaService.isTascaValidada(tascaId)) {
					tascaService.esborrarDocument(tascaId, codi, ome.getExecucioMassiva().getUsuari());
				} else {
					throw new ValidacioException("OPERACIO:" + ome.getId() + ". No s'ha pogut esborrar el document a la tasca perquè la tasca no està validada");
				}
				mesuresTemporalsHelper.mesuraCalcular("Esborrar document", "massiva_tasca", expedient, tasca);
			} else if ("DocGenerar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Generar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				Long documentId = (Long)param2[1];
				TascaDocumentDto document = tascaService.findDocument(tascaId, documentId);
				expedientDocumentService.generarAmbPlantillaPerTasca(
						tascaId,
						document.getDocumentCodi());
				mesuresTemporalsHelper.mesuraCalcular("Generar document", "massiva_tasca", expedient, tasca);
			}

			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut executar '" + accio + "' de la tasca.", ex);
			throw new ExecucioMassivaException(
					ome.getExpedient().getEntorn().getId(), 
					ome.getExpedient().getEntorn().getCodi(), 
					ome.getExpedient().getEntorn().getNom(), 
					ome.getExpedient().getId(), 
					ome.getExpedient().getTitol(), 
					ome.getExpedient().getNumero(), 
					ome.getExpedient().getTipus().getId(), 
					ome.getExpedient().getTipus().getCodi(), 
					ome.getExpedient().getTipus().getNom(), 
					ome.getExecucioMassiva().getId(), 
					ome.getId(), 
					"OPERACIO:" + ome.getId() + ". No s'ha pogut executar '" + accio + "' de la tasca.", 
					ex);
		}
	}
	
	private void actualitzarVersio(ExecucioMassivaExpedient ome) throws Exception {
		try {
			ome.setDataInici(new Date());
			if (ome.getExpedient() != null) {
				
				Expedient exp = ome.getExpedient();
				Object[] param2 = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
				// Proces principal
				Long definicioProcesId = (Long)param2[0];
				Long expedientProcesInstanceId = Long.parseLong(exp.getProcessInstanceId());
				InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(exp.getProcessInstanceId());
				DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
				int versioActual = instanciaProces.getDefinicioProces().getVersio();
				int versioNova = definicioProces.getVersio();
				
				if (versioActual != versioNova)
					expedientService.procesDefinicioProcesActualitzar(exp.getProcessInstanceId(), definicioProces.getVersio());
				
				// Subprocessos
				Long[] subProcesIds = (Long[])param2[1];
				if (subProcesIds != null) {
					String[] keys = (String[])param2[2];
					List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(expedientProcesInstanceId);
					for (InstanciaProcesDto ip : arbreProcessos) {
						int versio = findVersioDefProcesActualitzar(keys, subProcesIds, ip.getDefinicioProces().getJbpmKey());
						if (versio != -1 && versio != ip.getDefinicioProces().getVersio())
							expedientService.procesDefinicioProcesActualitzar(ip.getId(), versio);
					}
				}
			} else {
				Integer versio = (Integer)deserialize(ome.getExecucioMassiva().getParam2());
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
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				false);
		DefinicioProces definicioProces = definicioProcesRepository.findById(ome.getDefinicioProcesId());
		List<Consulta> consultes = consultaRepository.findByEntorn(entorn);
		boolean esborrar = true;
		if (consultes.isEmpty()) {
			esborrarDf = true;
		} else {
			for(Consulta consulta: consultes){
				Set<ConsultaCamp> llistat = consulta.getCamps();
				for(ConsultaCamp c: llistat){
					if((definicioProces.getVersio() == c.getDefprocVersio()) && (definicioProces.getJbpmKey().equals(c.getDefprocJbpmKey()))){
						esborrar = false;
					}
				}
				if(!esborrar){
					throw new Exception(messageHelper.getMessage("error.exist.cons.df", new Object[]{consulta.getNom(), definicioProces.getIdPerMostrar(), definicioProces.getVersio()}));
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
				if (ex instanceof DataIntegrityViolationException || ex.getCause() instanceof DataIntegrityViolationException || (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof DataIntegrityViolationException) ||
					"ConstraintViolationException".equalsIgnoreCase(ex.getClass().getSimpleName()) || (ex.getCause() != null && "ConstraintViolationException".equalsIgnoreCase(ex.getCause().getClass().getSimpleName())) || (ex.getCause() != null && ex.getCause().getCause() != null && "ConstraintViolationException".equalsIgnoreCase(ex.getCause().getCause().getClass().getSimpleName()))) {
					
					String msg = (ex instanceof DataIntegrityViolationException || "ConstraintViolationException".equalsIgnoreCase(ex.getClass().getSimpleName())) ? getErrorMsg(ex) : 
								  (ex.getCause() instanceof DataIntegrityViolationException || "ConstraintViolationException".equalsIgnoreCase(ex.getCause().getClass().getSimpleName())) ? getErrorMsg(ex.getCause()) : 
									  getErrorMsg(ex.getCause().getCause());
								  
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
					
					
					if (GraphSession.errorsDelete.containsKey(processInstanceId)){
						
						msg += "####exp_afectats###" + definicioProces.getId().toString() + "###";
						for (ProcessInstanceExpedient expedient: GraphSession.errorsDelete.get(processInstanceId)) {
							msg += "&&&" + (expedient.getIdentificador().equals("[null] null") ? expedient.getNumeroDefault() : expedient.getIdentificador()) + "@" + expedient.getId();
						}
						
						GraphSession.errorsDelete.remove(processInstanceId);
					}
					
					throw new Exception(messageHelper.getMessage("error.defpro.eliminar.constraint", new Object[] {definicioProces.getIdPerMostrar(), ""}) + ": " + msg);
					
				} else { 
					throw new Exception(messageHelper.getMessage("error.proces.peticio") + ": " + ExceptionUtils.getRootCauseMessage(ex), ExceptionUtils.getRootCause(ex));
				}
			}
		}
	}
	
	@CacheEvict(value = "consultaCache", allEntries=true)
	private void undeploy(
			Entorn entorn,
			Long expedientTipusId,
			DefinicioProces definicioProces) {
		if (expedientTipusId == null) {
			jbpmHelper.esborrarDesplegament(definicioProces.getJbpmId());
			for (Document doc: definicioProces.getDocuments())
				documentRepository.delete(doc.getId());
			for (Termini termini: definicioProces.getTerminis())
				dissenyHelper.deleteTermini(termini.getId());
			definicioProcesRepository.delete(definicioProces);
		} else {
			if (comprovarExpedientTipus(expedientTipusId, definicioProces.getId())) {
				jbpmHelper.esborrarDesplegament(definicioProces.getJbpmId());
				for (Document doc: definicioProces.getDocuments()) {
					documentRepository.delete(doc);
				}
				definicioProces.setDocuments(null);
				for (Termini termini: definicioProces.getTerminis()) {
					dissenyHelper.deleteTermini(termini.getId());
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
		ex.printStackTrace(new PrintWriter(errors));
		String errorFull = errors.toString();	
		errorFull = errorFull.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
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
				script = (String)((Object[])param2)[0];
			} else {
				script = (String)param2;
			}
			expedientService.procesScriptExec(
					exp.getId(),
					exp.getProcessInstanceId(),
					script);
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
			Long accioId;
			if (param2 instanceof Object[]) {
				accioId = (Long)((Object[])param2)[0];
			} else {
				accioId = (Long)param2;
			}
			
			expedientService.accioExecutar(exp.getId(), exp.getProcessInstanceId(), accioId);
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
			String motiu = (ome.getExecucioMassiva().getParam2() == null ? null : (String)deserialize(ome.getExecucioMassiva().getParam2()));
			if (!exp.isAturat()) {
				expedientService.aturar(exp.getId(), motiu);
				ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			} else {
				ome.setError( messageHelper.getMessage("error.expedient.ja.aturat"));
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
			Object[] params = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
			String idPI = exp.getProcessInstanceId();
			if (idPI != null) {
				expedientDadaService.update(exp.getId(), idPI, var, params[2]);
			} else {
				tascaService.updateVariable(exp.getId(), (String)params[1], var, params[2]);
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
			Object[] params = (Object[])deserialize(ome.getExecucioMassiva().getParam2());
			Date data = null;
			String nom = null;
			byte[] contingut = null;
			Long docId = null;
			if (params[0] != null) docId = (Long)params[0];
			if (params[1] != null) data = (Date)params[1];
			if (params[2] != null) nom = (String)params[2];
			if (params[3] != null) contingut = (byte[])params[3];

			Document aux = null;
			ExpedientDocumentDto doc = null;
			if (docId != null) {
				aux = documentRepository.findOne(docId);
				doc =  documentHelperV3.findOnePerInstanciaProces(
						exp.getProcessInstanceId(),
						aux.getCodi());
			}
			if (contingut == null) {
				// Autogenerar
				if (nom.equals("generate")) {
					mesuresTemporalsHelper.mesuraIniciar("Autogenerar document", "massiva", exp.getTipus().getNom());
					if (doc == null || (!doc.isSignat() && !doc.isRegistrat())) {
						expedientDocumentService.generarAmbPlantilla(
								exp.getId(),
								exp.getProcessInstanceId(),
								aux.getCodi());
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
					} else 	if (!doc.isSignat() && !doc.isRegistrat()) {
						documentHelperV3.esborrarDocument(null, exp.getProcessInstanceId(), aux.getCodi());
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot esborrar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot esborrar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Esborrar document", "massiva", exp.getTipus().getNom());
				// Modificar data
				} else if (nom.equals("date")) {
					mesuresTemporalsHelper.mesuraIniciar("Canviar data de document", "massiva", exp.getTipus().getNom());
					if (doc == null) {
						throw new Exception("Document inexistent: no es pot modificar");
					} else 	if (!doc.isSignat() && !doc.isRegistrat()) {
						expedientDocumentService.createOrUpdate(
								exp.getId(),
								exp.getProcessInstanceId(),
								docId,
								null,
								doc.getDocumentNom(),
								doc.getArxiuNom(),
								null,
								data);
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot modificar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot modificar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Canviar data de document", "massiva", exp.getTipus().getNom());
				}
			} else {
				// Adjuntar document
				if (docId == null) {
					mesuresTemporalsHelper.mesuraIniciar("Adjuntar document", "massiva", exp.getTipus().getNom());
					expedientDocumentService.createOrUpdate(
							exp.getId(),
							exp.getProcessInstanceId(),
							null,
							null,
							nom,
							fileName,
							contingut,
							data);
					mesuresTemporalsHelper.mesuraCalcular("Adjuntar document", "massiva", exp.getTipus().getNom());
				// Modificar document
				} else {
					mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", exp.getTipus().getNom());
					if (doc == null || (!doc.isSignat() && !doc.isRegistrat())) {
						expedientDocumentService.createOrUpdate(
								exp.getId(),
								exp.getProcessInstanceId(),
								docId,
								null,
								nom,
								fileName,
								contingut,
								data);
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
			indexHelper.expedientIndexLuceneUpdate(exp.getProcessInstanceId());
//			expedientService.luceneReindexarExpedient(exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
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
			expedientRegistreService.registreBuidarLog(
					exp.getId());
			ome.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			ome.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(ome);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + ome.getId() + ". No s'ha pogut eliminar la informació de registre de l'expedient", ex);
			throw ex;
		}
	}

	private void reassignarTasca(ExecucioMassivaExpedient ome) throws Exception {
		String tascaId = ome.getTascaId();
		try {
			ome.setDataInici(new Date());
			JbpmTask tasca = tascaHelper.getTascaComprovacionsTramitacio(tascaId, false, false);
			if (tasca != null && tasca.isOpen()) {
				ProcessInstanceExpedient piexp = jbpmHelper.expedientFindByProcessInstanceId(
						tasca.getProcessInstanceId());
				expedientTascaService.reassignar(
						piexp.getId(),
						tasca.getId(),
						ome.getExecucioMassiva().getParam1());
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

	private double getPercent(Long value, Long total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
	    return Math.round(value * 100 / total);
	}

	private static final Log logger = LogFactory.getLog(ExecucioMassivaService.class);

}
