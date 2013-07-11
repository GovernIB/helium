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
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
	
	public String getJsonExecucionsMassivesByUser(String username, Integer results) {
		String json = "[";
		List<ExecucioMassiva> execucions = execucioMassivaDao.getExecucionsMassivesByUser(username, EntornActual.getEntornId(), results);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		for (ExecucioMassiva execucio: execucions) {
			List<ExecucioMassivaExpedient> expedients = execucioMassivaExpedientDao.getExecucioMassivaById(execucio.getId());
			String json_exp = "";
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
			    	String error = expedient.getError();
			    	if (error != null) error = error.replace("'", "&#8217;").replace("\"", "&#8220;").replace("\n", "\\n");
			    	json_exp += "{\"id\":\"" + expedient.getId() + "\",";
			    	json_exp += "\"titol\":\"" + titol + "\",";
			    	json_exp += "\"estat\":\"" + expedient.getEstat().name() + "\",";
			    	json_exp += "\"error\":\"" + StringEscapeUtils.escapeJavaScript(error) + "\"},";
				}
				json_exp = json_exp.substring(0, json_exp.length() - 1);
			}
			
			Long progres = getProgresExecucioMassivaById(execucio.getId());
			json += "{\"id\":\"" + execucio.getId() + "\",";
			json += "\"text\":\"" + getTextExecucioMassiva(execucio, tasca) + "\",";
			json += "\"progres\":\"" + progres + "\",";
			json += "\"data\":\"" + sdf.format(execucio.getDataInici()) + "\",";
			json += "\"tasca\":\"" + tasca + "\",";
			json += "\"expedients\":[" + json_exp + "]},";
		}
		if (json.length() > 1) json = json.substring(0, json.length() - 1);
		json += "]";
		return json;
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
			if (execucioMassiva.getExpedientTipus() == null) {
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
		ExecucioMassivaExpedient expedient = execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaExecucioMassiva);
		return dtoConverter.toOperacioMassiva(expedient);
	}
	
	public List<OperacioMassivaDto> getExecucionsMassivesActivaByUser(String username) {
		List<OperacioMassivaDto> resposta = new ArrayList<OperacioMassivaDto>();
		for (ExecucioMassivaExpedient expedient : execucioMassivaExpedientDao.getExecucioMassivaActivaByUser(username)) {
			resposta.add(dtoConverter.toOperacioMassiva(expedient));
		}
		return resposta;
	}
	
	public void executarExecucioMassiva(OperacioMassivaDto dto) throws Exception {
		logger.debug("Executant la acció massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", expedient=" + dto.getId() + ", acció=" + dto.getTipus());
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
	        SecurityContextHolder.getContext().setAuthentication(authentication);
			
			if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA){
				gestioTasca(dto);
			} else if (tipus == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC){
				actualitzarVersio(dto);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT){
				executarScript(dto);
			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO){
				executarAccio(dto);
			} else if (tipus == ExecucioMassivaTipus.ATURAR_EXPEDIENT){
				aturarExpedient(dto);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_VARIABLE){
				modificarVariable(dto);
			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_DOCUMENT){
				modificarDocument(dto);
			} else if (tipus == ExecucioMassivaTipus.REINDEXAR){
				reindexarExpedient(dto);
			} else if (tipus == ExecucioMassivaTipus.REASSIGNAR){
				reassignarExpedient(dto);
			}
			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
		} catch (Exception ex) {
			logger.error("Error al executar la acció massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", expedient=" + dto.getId() + ", acció=" + dto.getTipus(), ex);
			throw ex;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void gestioTasca(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		//ExpedientDto exp = dto.getExpedient();
		String tascaId = dto.getTascaId();
		String accio = dto.getParam1();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
			eme.setDataInici(new Date());
			if ("Guardar".equals(accio)) {
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				Map<String, Object> valors = (Map<String, Object>)param2[1]; 
				try {
					tascaService.guardarVariables(entornId, tascaId, valors, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'han pogut guardar les dades del formulari en la tasca.");
					throw e;
		        }
			} else if ("Validar".equals(accio)) {
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				Map<String, Object> valors = (Map<String, Object>)param2[1];
				try {
					tascaService.validar(entornId, tascaId, valors, true, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut validar el formulari en la tasca.");
					throw e;
		        }
			} else if ("Completar".equals(accio)) {
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
			} else if ("Restaurar".equals(accio)) {
				Long entornId = (Long)deserialize(dto.getParam2());
				try {
					tascaService.restaurar(entornId, tascaId, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut restaurar el formulari en la tasca.");
					throw e;
		        }
			} else if ("Accio".equals(accio)) {
				Object[] param2 = (Object[])deserialize(dto.getParam2());
				Long entornId = (Long)param2[0];
				String accio_exec = (String)param2[1];
				try {
					tascaService.executarAccio(entornId, tascaId, accio_exec, dto.getUsuari());
				} catch (Exception e) {
					logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut executar l'acció '" + accio_exec + "' en la tasca.");
					throw e;
		        }
			} else if ("DocGuardar".equals(accio)) {
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
			} else if ("DocEsborrar".equals(accio)) {
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
			} else if ("DocGenerar".equals(accio)) {
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
			} else if ("RegEsborrar".equals(accio)) {
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
			} else if ("RegGuardar".equals(accio)) {
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
			op = (String) deserialize(exe.getParam2());
		} catch (Exception ex) {
			logger.error("OPERACIO:" + exe.getId() + ". No s'ha pogut obtenir la operació", ex);
		}
		return op;
	}
	
	private void executarScript(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
			eme.setDataInici(new Date());
			String accioId = (String)deserialize(dto.getParam2());
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
				// Esborrar
				} else if (nom.equals("delete")) {
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
				// Modificar data
				} else if (nom.equals("date")) {
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
				}
			} else {
				// Adjuntar document
				if (docId == null) {
					documentService.guardarAdjunt(
							exp.getProcessInstanceId(),
			        		null,
			        		nom,
			        		data,
			        		fileName,
			        		contingut,
			        		dto.getUsuari());
				// Modificar document
				} else {
//					if (doc == null) {
//						throw new Exception("Document inexistent: no es pot modificar");
//					} else 
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
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
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
	
	private void reassignarExpedient(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
			eme.setDataInici(new Date());
			
			// Paràmetres
			Object[] params = (Object[])deserialize(dto.getParam2());
			Long entornId = null;
			Long tascaId = null;

			if (params[0] != null) entornId = (Long)params[0];
			if (params[1] != null) tascaId = (Long)params[1];
			
			int numReassignar = 0;
			// Obtenim la tasca
			List<InstanciaProcesDto> instanciesProces = expedientService.getArbreInstanciesProces(exp.getProcessInstanceId());
			for (InstanciaProcesDto ip: instanciesProces) {
				List<TascaDto> tasques = expedientService.findTasquesPerInstanciaProces(ip.getId(), false);
				
				for (TascaDto tasca: tasques) {
					if (tasca.getTascaId().equals(tascaId)) {
						numReassignar++;
						if (tasca.isOpen()) {
							// Reassignam la tasca
							expedientService.reassignarTasca(
									entornId,
									tasca.getId(),
									dto.getParam1(),
									dto.getUsuari());
						}
					}
				}
			}
			if (numReassignar == 0) {
				eme.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
				eme.setError(getMessage("expedient.massiva.reassignar.buit"));
			} else {
				eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			}
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut reassignar l'expedient", ex);
			throw ex;
		}
	}
	
	public void actualitzaUltimaOperacio(OperacioMassivaDto dto) {
		if (dto.getUltimaOperacio()) {
			try {
				ExecucioMassiva em = execucioMassivaDao.getById(dto.getExecucioMassivaId(), false);
				em.setDataFi(new Date());
				execucioMassivaDao.saveOrUpdate(em);
			} catch (Exception ex) {
				logger.error("EXPEDIENTMASSIU:"+dto.getExecucioMassivaId()+". No s'ha pogut finalitzar l'expedient massiu", ex);
			}
			try {
				if (dto.getEnviarCorreu()) {
					
					// Correu
					List<String> emailAddresses = new ArrayList<String>();
					
					PersonaDto persona = pluginService.findPersonaAmbCodi(dto.getUsuari());
					emailAddresses.add(persona.getEmail());
					
//					AddressResolver addressResolver = (AddressResolver)JbpmConfiguration.Configs.getObject("jbpm.mail.address.resolver");
//					Object resolvedAddresses = addressResolver.resolveAddress(dto.getUsuari());
//					if (resolvedAddresses != null) {
//						if (resolvedAddresses instanceof String) {
//							emailAddresses.add((String)resolvedAddresses);
//						} else if (resolvedAddresses instanceof Collection) {
//							emailAddresses.addAll((Collection)resolvedAddresses);
//						} else if (resolvedAddresses instanceof String[]) {
//							emailAddresses.addAll(Arrays.asList((String[])resolvedAddresses));
//						} else {
//							throw new JbpmException(
//									"Address resolver '" + addressResolver +
//									"' returned '" + resolvedAddresses.getClass().getName() +
//									"' instead of a String, Collection or String-array: " + resolvedAddresses);
//						}
//					}
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
	
	public void generaInformeError(OperacioMassivaDto operacioMassiva, Exception exception) {
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
		return dtoConverter.toOperacioMassiva(execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaMassiva));
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
