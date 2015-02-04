/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient.ExecucioMassivaEstat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.v3.core.helper.MessageHelper;
import net.conselldemallorca.helium.v3.core.helper.TascaHelper;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
	private ExpedientHelper expedientHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private TascaHelper tascaHelper;
	@Resource
	private MessageHelper messageHelper;
	
	@Transactional
	@Override
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
						expedientTipusRepository.findById(dto.getExpedientTipusId())
				);
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
				execucioMassivaRepository.save(execucioMassiva);
			else 
				throw new Exception("S'ha intentat crear una execució massiva sense assignar expedients.");
		}
	}
	
	@Transactional
	@Override
	public void cancelarExecucio(Long id) throws Exception {
		ExecucioMassivaExpedient eme = null;
		try {
			eme = execucioMassivaExpedientRepository.findOne(id);
			eme.setEstat(ExecucioMassivaEstat.ESTAT_CANCELAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientRepository.save(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" +id + ". No s'ha pogut canviar el estat del procés", ex);
			throw ex;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public String getJsonExecucionsMassivesByUser(int results) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Pageable paginacio = new PageRequest(0,results, Direction.DESC, "dataInici");		
		List<ExecucioMassiva> execucions = execucioMassivaRepository.findByUsuariAndEntorn(auth.getName(), EntornActual.getEntornId(), paginacio);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");		
		JSONArray ljson = new JSONArray();	
		for (ExecucioMassiva execucio: execucions) {
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
							tasca = task.getName();
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
					} else {
						titol = messageHelper.getMessage("expedient.massiva.actualitzar.dp") + " " + expedient.getExecucioMassiva().getParam1();
					}
					
					Map<String, Object> mjson_exp = new LinkedHashMap<String, Object>();
					mjson_exp.put("id", expedient.getId());
					mjson_exp.put("titol", titol);
					mjson_exp.put("estat", expedient.getEstat().name());
					String error = expedient.getError();
					if (error != null) {
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
			mjson.put("text", JSONValue.escape(getTextExecucioMassiva(execucio, tasca)));
			mjson.put("success", getPercent(success, expedients.size()));
			mjson.put("pendent", getPercent(pendent, expedients.size()));
			mjson.put("danger", getPercent(danger, expedients.size()));
			mjson.put("data", sdf.format(execucio.getDataInici()));
			mjson.put("tasca", tasca);
			mjson.put("expedients", ljson_exp);
			ljson.add(mjson);
		}
		String ojson = JSONValue.toJSONString(ljson);
		return ojson;
	}
	
	private double getPercent(Long value, int total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
	    return Math.round(value * 100 / (double) total);
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
		} else if (tipus.equals(ExecucioMassivaTipus.EXECUTAR_SCRIPT)){
			String script = ((String) deserialize(execucioMassiva.getParam2())).replace("'", "&#39;").replace("\"", "&#34;");
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
			String motiu = ((String) deserialize(execucioMassiva.getParam2()));
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

	private static final Log logger = LogFactory.getLog(ExecucioMassivaService.class);
}
