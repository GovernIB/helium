/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.ExecucioMassivaDao;
import net.conselldemallorca.helium.core.model.dao.ExecucioMassivaExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.MailDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient.ExecucioMassivaEstat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Servei per a gestionar la tramitació massiva d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@Transactional(noRollbackForClassName = "java.lang.Exception")
public class ExecucioMassivaService {

	private ExecucioMassivaDao execucioMassivaDao;
	private ExecucioMassivaExpedientDao execucioMassivaExpedientDao;
	private ExpedientTipusDao expedientTipusDao;
	private ExpedientDao expedientDao;
	private DefinicioProcesDao definicioProcesDao;
	private MailDao mailDao;
	private DtoConverter dtoConverter;
	private MessageSource messageSource;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private DocumentService documentService;
	private PluginService pluginService;
	
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private MetricRegistry metricRegistry;


	public void crearExecucioMassiva(ExecucioMassivaDto dto) throws Exception {
		if ((dto.getExpedientIds() != null && !dto.getExpedientIds().isEmpty()) ||
			(dto.getTascaIds() != null && dto.getTascaIds().length > 0) ||
			(dto.getProcInstIds() != null && !dto.getProcInstIds().isEmpty())) {
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
			if (dto.getExpedientTipusId() != null) {
				execucioMassiva.setExpedientTipus(
						expedientTipusDao.getById(
								dto.getExpedientTipusId(),
								false));
			}
			int ordre = 0;
			boolean expedients = false;
			if (dto.getExpedientIds() != null) {
				
				for (Long expedientId: dto.getExpedientIds()) {
					Expedient expedient = expedientDao.getById(expedientId, false);
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							expedient,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			} else if (dto.getTascaIds() != null) {
				for (String tascaId: dto.getTascaIds()) {
					TascaDto tasca = tascaService.getByIdSenseComprovacio(tascaId);
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							tasca.getExpedient(),
							tascaId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			} else if (dto.getProcInstIds() != null) {
				for (String procinstId: dto.getProcInstIds()) {
					ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
							execucioMassiva,
							procinstId,
							ordre++);
					execucioMassiva.addExpedient(eme);
					expedients = true;
				}
			}
			execucioMassiva.setEntorn(EntornActual.getEntornId());
			if (expedients)
				execucioMassivaDao.saveOrUpdate(execucioMassiva);
			else 
				throw new Exception("S'ha intentat crear una execució massiva sense assignar expedients.");
		}
	}

	public List<ExecucioMassivaDto> getExecucionsMassivesActives() {
		List<ExecucioMassivaDto> resposta = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassiva massiva: execucioMassivaDao.getExecucionsMassivesActives())
			resposta.add(dtoConverter.toExecucioMassivaDto(massiva));
		return resposta;
	}

	public List<ExecucioMassivaDto> getExecucionsMassivesActivesByUser(String username) {
		List<ExecucioMassivaDto> resposta = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassiva massiva: execucioMassivaDao.getExecucionsMassivesActivesByUser(username))
			resposta.add(dtoConverter.toExecucioMassivaDto(massiva));
		return resposta;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getJsonExecucionsMassivesByUser(String username, Integer results) {
		JSONArray ljson = new JSONArray();
		
		List<ExecucioMassiva> execucions = execucioMassivaDao.getExecucionsMassivesByUser(username, EntornActual.getEntornId(), results);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (ExecucioMassiva execucio: execucions) {
			List<ExecucioMassivaExpedient> expedients = execucioMassivaExpedientDao.getExecucioMassivaById(execucio.getId());
			JSONArray ljson_exp = new JSONArray();
			String tasca = "";
			if (!expedients.isEmpty()) {
				ExecucioMassivaExpedient em = expedients.get(0);
				if (em.getTascaId() != null) {
					TascaDto t = tascaService.getByIdSenseComprovacio(em.getTascaId(), execucio.getUsuari());
					if (t != null) tasca = t.getNom();
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
					} else {
						titol = getMessage("expedient.massiva.actualitzar.dp") + " " + expedient.getExecucioMassiva().getParam1();
					}
					
					Map mjson_exp = new LinkedHashMap();
					mjson_exp.put("id", expedient.getId());
					mjson_exp.put("titol", titol);
					mjson_exp.put("estat", expedient.getEstat().name());
					String error = expedient.getError();
					if (error != null) error = error.replace("'", "&#8217;").replace("\"", "&#8220;");
					mjson_exp.put("error", JSONValue.escape(error));
					ljson_exp.add(mjson_exp);
					
//			    	String error = expedient.getError();
//			    	if (error != null) error = error.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
//			    	json_exp += "\"error\":\"" + StringEscapeUtils.escapeJavaScript(error) + "\"},";
				}
			}
			
			Long progres = getProgresExecucioMassivaById(execucio.getId());
			Map mjson = new LinkedHashMap();
			mjson.put("id", execucio.getId());
			mjson.put("text", JSONValue.escape(getTextExecucioMassiva(execucio, tasca)));
			mjson.put("progres", progres);
			mjson.put("data", sdf.format(execucio.getDataInici()));
			mjson.put("tasca", tasca);
			mjson.put("expedients", ljson_exp);
			ljson.add(mjson);
		}
		String ojson = JSONValue.toJSONString(ljson);
		return ojson;
	}
	
	private String getTextExecucioMassiva(ExecucioMassiva execucioMassiva, String tasca) {
		String label = null;
		
		ExecucioMassivaTipus tipus = execucioMassiva.getTipus();
		if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_TASCA)){
			label = getMessage("expedient.massiva.tasca") + " " + tasca + ": ";
			String param = execucioMassiva.getParam1();
			Object param2 = null;
			try {
				param2 = deserialize(execucioMassiva.getParam2());
			} catch (Exception e) {};
			
			if (param.equals("Guardar")) {
				label += getMessage("expedient.massiva.tasca.guardar");
			} else if (param.equals("Validar")) {
				label += getMessage("expedient.massiva.tasca.validar");
			} else if (param.equals("Completar")) {
				label += getMessage("expedient.massiva.tasca.completar");
			} else if (param.equals("Restaurar")) {
				label += getMessage("expedient.massiva.tasca.restaurar");
			} else if (param.equals("Accio")) {
				label += getMessage("expedient.massiva.tasca.accio") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocGuardar")) {
				label += getMessage("expedient.massiva.tasca.doc.guardar") + (param2 == null ? "" : " &#8216;'" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocEsborrar")) {
				label += getMessage("expedient.massiva.tasca.doc.borrar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("DocGenerar")) {
				label += getMessage("expedient.massiva.tasca.doc.generar") + (param2 == null ? "" : " &#8216;'" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("RegEsborrar")) {
				label += getMessage("expedient.massiva.tasca.reg.borrar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			} else if (param.equals("RegGuardar")) {
				label += getMessage("expedient.massiva.tasca.reg.guardar") + (param2 == null ? "" : " &#8216;" + ((Object[])param2)[1] + "&#8217;");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC)){
			if (execucioMassiva.getExpedientTipus() == null && execucioMassiva.getParam1() != null) {
				String versio = "";
				try { versio += (Integer)deserialize(execucioMassiva.getParam2()); } catch (Exception e){} 
				label = getMessage("expedient.massiva.actualitzar") + " (" + execucioMassiva.getParam1() + " v." + versio + ")";
			} else {
				DefinicioProces definicioProces = getDefinicioProces(execucioMassiva);
				label = getMessage("expedient.massiva.actualitzar") + (definicioProces == null ? "" : " (" + definicioProces.getJbpmKey() + " v." + definicioProces.getVersio() + ")");
			}
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_SCRIPT)){
			String script = getOperacio(execucioMassiva).replace("'", "&#39;").replace("\"", "&#34;");
			label = getMessage("expedient.massiva.executarScriptMas") + " " + (script.length() > 20 ? script.substring(0,20) : script);
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_ACCIO)){
			String accio = getOperacio(execucioMassiva);
			label = getMessage("expedient.massiva.accions") + " " + accio;
		} else if (tipus.equals(ExecucioMassivaTipus.ATURAR_EXPEDIENT)){
			String motiu = getOperacio(execucioMassiva);
			label = getMessage("expedient.massiva.aturar")+ (motiu == null ? "" : ": "+ (motiu.length() > 20 ? motiu.substring(0,20) : motiu));
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_VARIABLE)){
			label = getMessage("expedient.massiva.modificar_variables") + " " + execucioMassiva.getParam1();
		} else if (tipus.equals(ExecucioMassivaTipus.MODIFICAR_DOCUMENT)){
			label = getMessage("expedient.massiva.documents");
		} else if (tipus.equals(ExecucioMassivaTipus.REINDEXAR)){
			label = getMessage("expedient.eines.reindexar.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.BUIDARLOG)){
			label = getMessage("expedient.eines.buidarlog.expedients");
		} else if (tipus.equals(ExecucioMassivaTipus.REPRENDRE_EXPEDIENT)){
			label = getMessage("expedient.eines.reprendre_expedient");
		} else if (tipus.equals(ExecucioMassivaTipus.REPRENDRE)){
			label = getMessage("expedient.eines.reprendre_tramitacio");
		} else if (tipus.equals(ExecucioMassivaTipus.REASSIGNAR)){
			label = getMessage("expedient.eines.reassignar.expedients");
		} else {
			label = tipus.name();
		}
		return label;
	}
		
	public List<OperacioMassivaDto> getExecucionsMassivesActivaByIds(List<Long> attribute) {
		List<OperacioMassivaDto> resposta = new ArrayList<OperacioMassivaDto>();
		for (Long id : attribute) {
			for (ExecucioMassivaExpedient expedient: execucioMassivaExpedientDao.getExecucioMassivaActivaById(id)) {
				resposta.add(dtoConverter.toOperacioMassiva(expedient));
			}
		}
		return resposta;
	}
	
	public List<OperacioMassivaDto> getExecucionsMassivesActivaById(Long id) {
		List<OperacioMassivaDto> resposta = new ArrayList<OperacioMassivaDto>();
		for (ExecucioMassivaExpedient expedient: execucioMassivaExpedientDao.getExecucioMassivaActivaById(id)) {
			resposta.add(dtoConverter.toOperacioMassiva(expedient));
		}
		return resposta;
	}
	
	public List<OperacioMassivaDto> getExecucionsMassivesById(Long id) {
		List<OperacioMassivaDto> resposta = new ArrayList<OperacioMassivaDto>();
		for (ExecucioMassivaExpedient expedient: execucioMassivaExpedientDao.getExecucioMassivaById(id)) {
			resposta.add(dtoConverter.toOperacioMassiva(expedient));
		}
		return resposta;
	}
	
	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		Date ara = new Date();
		ExecucioMassivaExpedient expedient = execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaExecucioMassiva, ara);
		
		if (expedient == null) {
			// Comprobamos si es una ejecución masiva sin expedientes asociados. En ese caso actualizamos la fecha de fin
			Long mas = execucioMassivaDao.getMinExecucioMassiva(ara);
			if (mas != null) {
				ExecucioMassiva massiva = execucioMassivaDao.getById(mas, false);
				if (massiva != null) {
					massiva.setDataFi(new Date());
					execucioMassivaDao.merge(massiva);
					execucioMassivaDao.flush();
				}
			}
		}
		return dtoConverter.toOperacioMassiva(expedient);
	}
	
	public List<OperacioMassivaDto> getExecucionsMassivesActivaByUser(String username) {
		List<OperacioMassivaDto> resposta = new ArrayList<OperacioMassivaDto>();
		for (ExecucioMassivaExpedient expedient : execucioMassivaExpedientDao.getExecucioMassivaActivaByUser(username)) {
			resposta.add(dtoConverter.toOperacioMassiva(expedient));
		}
		return resposta;
	}
	
	@SuppressWarnings("unchecked")
	public void executarExecucioMassiva(OperacioMassivaDto dto) throws Exception {
		logger.debug(
				"Executant la acció massiva (" +
				"expedientTipusId=" + dto.getExpedientTipusId() + ", " +
				"dataInici=" + dto.getDataInici() + ", " +
				"expedient=" + dto.getId() + ", " +
				"acció=" + dto.getTipus());
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
						dto.getExpedient().getEntorn().getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"executar.count",
						dto.getExpedient().getEntorn().getCodi()));
		countEntorn.inc();
		final Timer timerTipexp = metricRegistry.timer(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"completar",
						dto.getExpedient().getEntorn().getCodi(),
						dto.getExpedient().getTipus().getCodi()));
		final Timer.Context contextTipexp = timerTipexp.time();
		Counter countTipexp = metricRegistry.counter(
				MetricRegistry.name(
						ExecucioMassivaService.class,
						"completar.count",
						dto.getExpedient().getEntorn().getCodi(),
						dto.getExpedient().getTipus().getCodi()));
		countTipexp.inc();
		try {
			ExecucioMassivaTipus tipus = dto.getTipus();
			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
			final String user = dto.getUsuari();
	        Principal principal = new Principal() {
				public String getName() {
					return user;
				}
			};
			Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, null);
			
			if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO){
				Object param2 = deserialize(dto.getParam2());
				if (param2 instanceof Object[]) {
					Object credentials = ((Object[])param2)[1];
					List<String> rols = (List<String>)((Object[])param2)[2];
					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
					if (!rols.isEmpty()) {
						for (String rol: rols) {
							authorities.add(new SimpleGrantedAuthority(rol));
						}
					}
					authentication =  new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
				}
			}
			
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			
			String expedient = null;
	        if (MesuresTemporalsHelper.isActiu()) {
	        	try {
		        	if (dto.getExpedient() != null)
		        		expedient = dto.getExpedient().getTipus().getNom();
	        	} catch (Exception e) {}
			}
	        
			if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA){
				// Authentication
				String param = dto.getParam1();
				Object param2 = deserialize(dto.getParam2());

				if (param2 instanceof Object[]) {
					Object credentials = null;					
					List<String> rols = null;
					if (param.equals("Guardar")) {
						credentials = ((Object[]) param2)[2];
						rols = (List<String>) ((Object[]) param2)[3];
					} else if (param.equals("Validar")) {
						credentials = ((Object[]) param2)[2];
						rols = (List<String>) ((Object[]) param2)[3];
					} else if (param.equals("Completar")) {
						credentials = ((Object[]) param2)[2];
						rols = (List<String>) ((Object[]) param2)[3];
					} else if (param.equals("Restaurar")) {
						credentials = ((Object[]) param2)[1];
						rols = (List<String>) ((Object[]) param2)[2];
					} else if (param.equals("Accio")) {
						credentials = ((Object[]) param2)[2];
						rols = (List<String>) ((Object[]) param2)[3];
					} else if (param.equals("DocGuardar")) {
						credentials = ((Object[]) param2)[5];
						rols = (List<String>) ((Object[]) param2)[6];
					} else if (param.equals("DocEsborrar")) {
						credentials = ((Object[]) param2)[2];
						rols = (List<String>) ((Object[]) param2)[3];
					} else if (param.equals("DocGenerar")) {
						credentials = ((Object[]) param2)[3];
						rols = (List<String>) ((Object[]) param2)[4];
					} else if (param.equals("RegEsborrar")) {
						credentials = ((Object[]) param2)[3];
						rols = (List<String>) ((Object[]) param2)[4];
					} else if (param.equals("RegGuardar")) {
						credentials = ((Object[]) param2)[4];
						rols = (List<String>) ((Object[]) param2)[5];
					}

					List<GrantedAuthority> authorities = null;
					if (!rols.isEmpty()) {						
						authorities = new ArrayList<GrantedAuthority>();
						if (!rols.isEmpty()) {
							for (String rol: rols) {
								authorities.add(new SimpleGrantedAuthority(rol));
							}
						}
					}
					authentication =  new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
				}
				SecurityContextHolder.getContext().setAuthentication(authentication);

				gestioTasca(dto);
			} else if (tipus == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC){
				mesuresTemporalsHelper.mesuraIniciar("Actualitzar", "massiva", expedient);
				actualitzarVersio(dto);
				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT){
				mesuresTemporalsHelper.mesuraIniciar("Executar script", "massiva", expedient);
				executarScript(dto);
				mesuresTemporalsHelper.mesuraCalcular("Executar script", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO){
				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva", expedient);
				executarAccio(dto);
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.ATURAR_EXPEDIENT){
				mesuresTemporalsHelper.mesuraIniciar("Aturar expedient", "massiva", expedient);
				aturarExpedient(dto);
				mesuresTemporalsHelper.mesuraCalcular("Aturar expedient", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_VARIABLE){
				mesuresTemporalsHelper.mesuraIniciar("Modificar variable", "massiva", expedient);
				modificarVariable(dto);
				mesuresTemporalsHelper.mesuraCalcular("Modificar variable", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_DOCUMENT){
//				mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", expedient);
				modificarDocument(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.REINDEXAR){
				mesuresTemporalsHelper.mesuraIniciar("Reindexar", "massiva", expedient);
				reindexarExpedient(dto);
				mesuresTemporalsHelper.mesuraCalcular("Reindexar", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.BUIDARLOG){
				mesuresTemporalsHelper.mesuraIniciar("Buidar log", "massiva", expedient);
				buidarLogExpedient(dto);
				mesuresTemporalsHelper.mesuraCalcular("Buidar log", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE_EXPEDIENT){
				mesuresTemporalsHelper.mesuraIniciar("desfer fi process instance", "massiva", expedient);
				reprendreExpedient(dto);
				mesuresTemporalsHelper.mesuraCalcular("desfer fi process instance", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.REPRENDRE){
				mesuresTemporalsHelper.mesuraIniciar("reprendre tramitació process instance", "massiva", expedient);
				reprendreTramitacio(dto);
				mesuresTemporalsHelper.mesuraCalcular("reprendre tramitació process instance", "massiva", expedient);
			} else if (tipus == ExecucioMassivaTipus.REASSIGNAR){
				mesuresTemporalsHelper.mesuraIniciar("Reassignar", "massiva", expedient);
				//reassignarExpedient(dto);
				reassignarTasca(dto);
				mesuresTemporalsHelper.mesuraCalcular("Reassignar", "massiva", expedient);
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
		} catch (Exception ex) {
			logger.error("Error al executar la acció massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", expedient=" + dto.getId() + ", acció=" + dto.getTipus(), ex);
			throw ex;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextTipexp.stop();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void gestioTasca(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		String tascaId = dto.getTascaId();
		String accio = dto.getParam1();
		String tasca = null;
		String expedient = null;
		if (MesuresTemporalsHelper.isActiu()) {
			try {
				TascaDto task = tascaService.getByIdSenseComprovacioIDades(tascaId);
				if (task != null) tasca = task.getNom(); 
				expedient = dto.getExpedient().getTipus().getNom();
			} catch (Exception e) {}
		}
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			if ("Guardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				Map<String, Object> valors = (Map<String, Object>)param2[1]; 
				try {
					tascaService.guardarVariables(entornId, tascaId, valors, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'han pogut guardar les dades del formulari en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Guardar", "massiva_tasca", expedient, tasca);
			} else if ("Validar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Validar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				Map<String, Object> valors = (Map<String, Object>)param2[1];
				try {
					tascaService.validar(entornId, tascaId, valors, true, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut validar el formulari en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Validar", "massiva_tasca", expedient, tasca);
			} else if ("Completar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Completar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String transicio = (String)param2[1];
				try {
					Long ea = EntornActual.getEntornId();
					EntornActual.setEntornId(entornId);
					tascaService.completar(entornId, tascaId, true, dto.getUsuari(), transicio);
					EntornActual.setEntornId(ea);
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut finalitzar la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Completar", "massiva_tasca", expedient, tasca);
			} else if ("Restaurar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Restaurar", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				try {
					tascaService.restaurar(entornId, tascaId, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut restaurar el formulari en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Restaurar", "massiva_tasca", expedient, tasca);
			} else if ("Accio".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String accio_exec = (String)param2[1];
				try {
					tascaService.executarAccio(entornId, tascaId, accio_exec, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut executar l'acció '" + accio_exec + "' en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva_tasca", expedient, tasca);
			} else if ("DocGuardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String codi = (String)param2[1];
				Date data = (Date)param2[2];
				byte[] contingut = (byte[])param2[3];
				String nomArxiu = (String)param2[4];
				try {
					tascaService.comprovarTascaAssignadaIValidada(entornId, tascaId, dto.getUsuari());
					documentService.guardarDocumentTasca(
							entornId,
							tascaId,
							codi,
							data,
							nomArxiu,
							contingut,
							dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut guardar el document a la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Guardar document", "massiva_tasca", expedient, tasca);
			} else if ("DocEsborrar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Esborrar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String codi = (String)param2[1];
				try {
					tascaService.comprovarTascaAssignadaIValidada(entornId, tascaId, dto.getUsuari());
					documentService.esborrarDocument(
							tascaId,
							null,
							codi,
							dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut esborrar el document de la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Esborrar document", "massiva_tasca", expedient, tasca);
			} else if ("DocGenerar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Generar document", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				Long documentId = (Long)param2[1];
				Date data = (Date)param2[2];
				try {
					documentService.generarDocumentPlantilla(
							entornId,
							documentId,
							tascaId,
							null,
							data,
							false,
							dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut restaurar el formulari en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Generar document", "massiva_tasca", expedient, tasca);
			} else if ("RegEsborrar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Esborrar registre", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String campCodi = (String)param2[1];
				Integer index = (Integer)param2[2];
				try {
					tascaService.esborrarRegistre(entornId, tascaId, campCodi, index, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut esborrar el registe en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Esborrar registre", "massiva_tasca", expedient, tasca);
			} else if ("RegGuardar".equals(accio)) {
				mesuresTemporalsHelper.mesuraIniciar("Guardar registre", "massiva_tasca", expedient, tasca);
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String campCodi = (String)param2[1];
				Object[] valors = (Object[])param2[2];
				Integer index = (Integer)param2[3];
				try {
					tascaService.guardarRegistre(entornId, tascaId, campCodi, valors, index, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut guardar el registre en la tasca.");
					throw e;
		        }
				mesuresTemporalsHelper.mesuraCalcular("Guardar registre", "massiva_tasca", expedient, tasca);
			}

			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut executar '" + accio + "' de la tasca.", ex);
			throw ex;
		}
	}
	
	private void actualitzarVersio(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			if (dto.getExpedient() != null) {
				
				ExpedientDto exp = dto.getExpedient();
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				// Proces principal
				Long definicioProcesId = (Long)param2[0];
				DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
				expedientService.changeProcessInstanceVersion(exp.getProcessInstanceId(), definicioProces.getVersio());
				
				// Subprocessos
				Long[] subProcesIds = (Long[])param2[1];
				String[] keys = (String[])param2[2];
				List<InstanciaProcesDto> arbreProcessos = expedientService.getArbreInstanciesProces(exp.getProcessInstanceId());
				for (InstanciaProcesDto ip : arbreProcessos) {
					int versio = findVersioDefProcesActualitzar(keys, subProcesIds, ip.getDefinicioProces().getJbpmKey());
					if (versio != -1)
						expedientService.changeProcessInstanceVersion(ip.getId(), versio);
				}
			} else {
				Integer versio = (Integer)deserialize(dto.getParam2());
				ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(dto.getProcessInstanceId());
				eme.setExpedient(expedient);
				expedientService.changeProcessInstanceVersion(dto.getProcessInstanceId(), versio);
			}
			
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut canviar la versió del procés", ex);
			throw ex;
		}
	}
	
	private int findVersioDefProcesActualitzar(String[] keys, Long[] defProces, String key) {
		int versio = -1;
		int i = 0;
		while (i < keys.length && !keys[i].equals(key)) 
			i++;
		if (i < keys.length && defProces[i] != null) {
			DefinicioProces definicioProces = definicioProcesDao.getById(defProces[i], false);
			if (definicioProces != null) 
				versio = definicioProces.getVersio();
		}
		return versio;
	}
	
	public void cancelarExecucio(Long id) throws Exception {
		ExecucioMassivaExpedient eme = null;
		try {
			eme = execucioMassivaExpedientDao.getById(id, false);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_CANCELAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" +id + ". No s'ha pogut canviar el estat del procés", ex);
			throw ex;
		}
	}
	
	public DefinicioProces getDefinicioProces(OperacioMassivaDto dto) {
		DefinicioProces definicioProces = null;
		try {
			Object[] obj = (Object[]) deserialize(dto.getParam2());
			if (obj[0] instanceof Long) {
				definicioProces = definicioProcesDao.getById((Long) obj[0], false);
			}
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut obtenir la definicioProces del procés", ex);
		}
		return definicioProces;
	}
	
	public DefinicioProces getDefinicioProces(ExecucioMassiva exe) {
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
				definicioProces = definicioProcesDao.getById(dfId, false);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + exe.getId() + ". No s'ha pogut obtenir la definicioProces del procés", ex);
		}
		return definicioProces;
	}
	
	public String getOperacio(OperacioMassivaDto dto) {
		String op = null;
		try {
			op = (String) deserialize(dto.getParam2());
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut obtenir la operació", ex);
		}
		return op;
	}
	
	public String getOperacio(ExecucioMassiva exe) {
		String op = null;
		try {
			Object param2 = deserialize(exe.getParam2());
			if (param2 instanceof Object[]) {
				op = (String)((Object[])param2)[0];
			} else {
				op = (String)param2;
			}
//			op = (String) deserialize(exe.getParam2());
		} catch (Exception ex) {
			logger.error("OPERACIO:" + exe.getId() + ". No s'ha pogut obtenir la operació", ex);
		}
		return op;
	}
	
	private void executarScript(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			String script = (String) deserialize(dto.getParam2());
			expedientService.evaluateScript(exp.getProcessInstanceId(), script,	null);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut executar l'script", ex);
			throw ex;
		}
	}
	
	private void executarAccio(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			Object param2 = deserialize(dto.getParam2());
			String accioId = "";
			if (param2 instanceof Object[]) {
				accioId = (String)((Object[])param2)[0];
			} else {
				accioId = (String)param2;
			}
			
			expedientService.executarAccio(exp.getProcessInstanceId(), accioId);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut executar l'accio", ex);
			throw ex;
		}
	}
		
	private void aturarExpedient(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			String motiu = (dto.getParam2() == null ? null : (String)deserialize(dto.getParam2()));
			if (!exp.isAturat()) {
				expedientService.aturar(exp.getProcessInstanceId(), motiu, dto.getUsuari());
				eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			} else {
				eme.setError(getMessage("error.expedient.ja.aturat"));
				eme.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
			}
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut aturar l'expedient", ex);
			throw ex;
		}
	}
	
	private void modificarVariable(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			String var = dto.getParam1();
			Object[] params = (Object[])deserialize(dto.getParam2());
			String idPI = exp.getProcessInstanceId();
			if (idPI != null) {
				expedientService.updateVariable(idPI, var, params[2], dto.getUsuari());
			} else {
				tascaService.updateVariable((Long)params[0], (String)params[1], var, params[2], dto.getUsuari());
			}
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut modificat la variable", ex);
			throw ex;
		}
	}
	
	private void modificarDocument(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			String fileName = dto.getParam1();
			
			// Paràmetres
			Object[] params = (Object[])deserialize(dto.getParam2());
			Date data = null;
			String nom = null;
			byte[] contingut = null;
			Long docId = null;
			if (params[0] != null) docId = (Long)params[0];
			if (params[1] != null) data = (Date)params[1];
			if (params[2] != null) nom = (String)params[2];
			if (params[3] != null) contingut = (byte[])params[3];

			DocumentDto doc = null;
			if (docId != null) {
				doc =documentService.documentPerProces(
						exp.getProcessInstanceId(),
						docId, 
						false);
			}
			if (contingut == null) {
				// Autogenerar
				if (nom.equals("generate")) {
					mesuresTemporalsHelper.mesuraIniciar("Autogenerar document", "massiva", exp.getTipus().getNom());
					if (doc == null || (!doc.isSignat() && !doc.isRegistrat())) {
						documentService.generarDocumentPlantilla(
								exp.getEntorn().getId(), 
								docId, 
								null, 
								dto.getExpedient().getProcessInstanceId(), 
								data, 
								true,
								dto.getUsuari());
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
						documentService.esborrarDocument(
								null,
								exp.getProcessInstanceId(),
								doc.getDocumentCodi(),
								dto.getUsuari());
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
						documentService.guardarDocumentProces(
								exp.getProcessInstanceId(),
								doc.getDocumentCodi(),
								null,
								data,
								doc.getArxiuNom(),
								null,
								false,
								dto.getUsuari());
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
					documentService.guardarAdjunt(
							exp.getProcessInstanceId(),
			        		null,
			        		nom,
			        		data,
			        		fileName,
			        		contingut,
			        		dto.getUsuari());
					mesuresTemporalsHelper.mesuraCalcular("Adjuntar document", "massiva", exp.getTipus().getNom());
				// Modificar document
				} else {
//					if (doc == null) {
//						throw new Exception("Document inexistent: no es pot modificar");
//					} else 
					mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", exp.getTipus().getNom());
					if (doc == null || (!doc.isSignat() && !doc.isRegistrat())) {
						documentService.guardarDocumentProces(
								exp.getProcessInstanceId(),
//								doc.getDocumentCodi(),
								nom,
								null,
								data,
								fileName,
								contingut,
								false,
								dto.getUsuari());
					} else if (doc.isSignat()) {
						throw new Exception("Document signat: no es pot modificar");
					} else if (doc.isRegistrat()) {
						throw new Exception("Document registrat: no es pot modificar");
					}
					mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", exp.getTipus().getNom());
				}
			}
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut modificar el document", ex);
			throw ex;
		}
	}
		
	private void reindexarExpedient(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			expedientService.luceneReindexarExpedient(exp.getProcessInstanceId());
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut reindexar l'expedient", ex);
			throw ex;
		}
	}
	
	private void reprendreExpedient(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			expedientService.reprendreExpedient(exp.getProcessInstanceId());
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut desfer la finalització de l'expedient", ex);
			throw ex;
		}
	}
	
	private void reprendreTramitacio(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			expedientService.reprendre(
					exp.getProcessInstanceId(),
					dto.getUsuari());
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut reprendre la tramitació de l'expedient", ex);
			throw ex;
		}
	}
	
	private void buidarLogExpedient(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			expedientService.buidarLogExpedient(exp.getProcessInstanceId());
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut eliminar la informació de registre de l'expedient", ex);
			throw ex;
		}
	}
	
	private void reassignarTasca(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		String tascaId = dto.getTascaId();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), true);
			eme.setDataInici(new Date());
			
			// Paràmetres
			Object[] params = (Object[])deserialize(dto.getParam2());
			Long entornId = null;
			if (params[0] != null) entornId = (Long)params[0];
			
			// Obtenim la tasca
			TascaDto tasca = tascaService.getByIdSenseComprovacioIDades(tascaId);
			if (tasca != null && tasca.isOpen()) {
				// Reassignam la tasca
				expedientService.reassignarTasca(
						entornId,
						tasca.getId(),
						dto.getParam1(),
						dto.getUsuari());
			}
			if (tasca == null) {
				eme.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
				eme.setError(getMessage("tasca.massiva.reassignar.buit"));
			} else {
				eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			}
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut reassignar la tasca", ex);
			throw ex;
		}
	}
	
	public void actualitzaUltimaOperacio(net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto operacioMassiva) {
		if (operacioMassiva.getUltimaOperacio()) {
			try {
				ExecucioMassiva em = execucioMassivaDao.getById(operacioMassiva.getExecucioMassivaId(), false);
				em.setDataFi(new Date());
				execucioMassivaDao.saveOrUpdate(em);
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU:"+operacioMassiva.getExecucioMassivaId()+". No s'ha pogut finalitzar l'expedient massiu", ex);
			}
			try {
				if (operacioMassiva.getEnviarCorreu()) {
					
					// Correu
					List<String> emailAddresses = new ArrayList<String>();
					
					PersonaDto persona = pluginService.findPersonaAmbCodi(operacioMassiva.getUsuari());
					emailAddresses.add(persona.getEmail());

					mailDao.send(
							GlobalProperties.getInstance().getProperty("app.correu.remitent"),
							emailAddresses,
							null,
							null,
							"Execució massiva",
							"L'execució massiva ha finalitzat.");
				}
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
			}
		}
	}
	
	public void generaInformeError(net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto operacioMassiva, Exception exception) {
		ExecucioMassivaExpedient eme = execucioMassivaExpedientDao.getById(operacioMassiva.getId(), false);
		Date ara = new Date();
		eme.setDataInici(new Date());
		eme.setDataFi(ara);
		eme.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
		
		StringWriter out = new StringWriter();
		exception.printStackTrace(new PrintWriter(out));
		eme.setError(out.toString());
		execucioMassivaExpedientDao.saveOrUpdate(eme);
	}
	
	public Object deserialize(byte[] bytes) throws Exception{
		Object obj = null;
		if (bytes != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		}
		return obj;
	}
	
	public byte[] serialize(Object obj) throws Exception{
		byte[] bytes = null;
		if (obj != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			bos.close();
			bytes = bos.toByteArray();
		}
		return bytes;
	}
	
	public OperacioMassivaDto getOperacioMassivaActiva(Long ultimaMassiva) {
		return getExecucionsMassivesActiva(ultimaMassiva);
	}
	
	public Long getNombreExecucionsMassivesActivesByUser(String username) {
		 return execucioMassivaDao.getNombreExecucionsMassivesActivesByUser(username);
	}
	
	public Long getProgresExecucioMassivaByUser(String username) {
		return execucioMassivaExpedientDao.getProgresExecucioMassivaByUser(username);
	}
	
	public Long getProgresExecucioMassivaById(Long execucioMassivaId) {
		return execucioMassivaExpedientDao.getProgresExecucioMassivaById(execucioMassivaId);
	}
	
//	public ExecucioMassivaExpedient getExecucioMassivaById(Long operacioMassivaId) {
//		return execucioMassivaExpedientDao.getById(operacioMassivaId, false);
//	}

	@Autowired
	public void setExecucioMassivaDao(ExecucioMassivaDao execucioMassivaDao) {
		this.execucioMassivaDao = execucioMassivaDao;
	}
	@Autowired
	public void setExecucioMassivaExpedientDao(
			ExecucioMassivaExpedientDao execucioMassivaExpedientDao) {
		this.execucioMassivaExpedientDao = execucioMassivaExpedientDao;
	}

	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setMailDao(MailDao mailDao) {
		this.mailDao = mailDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}
	@Autowired
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	@Autowired
	public void setPluginService(PluginService pluginService) {
		this.pluginService = pluginService;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected String getMessage(String key, Object[] vars) {
		try {
			return messageSource.getMessage(
					key,
					vars,
					null);
		} catch (NoSuchMessageException ex) {
			return "???" + key + "???";
		}
	}

	protected String getMessage(String key) {
		return getMessage(key, null);
	}

	private static final Log logger = LogFactory.getLog(ExecucioMassivaService.class);
}
