/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.ExecucioMassivaDao;
import net.conselldemallorca.helium.core.model.dao.ExecucioMassivaExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.MailDao;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExecucioMassivaDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassiva.ExecucioMassivaTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient.ExecucioMassivaEstat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.mail.AddressResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
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



	public void crearExecucioMassiva(ExecucioMassivaDto dto) {
		logger.debug("Creació d'execució massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", numExpedients=" + ((dto.getExpedientIds() != null) ? dto.getExpedientIds().size() : 0) + ")");
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
		execucioMassiva.setExpedientTipus(
				expedientTipusDao.getById(
						dto.getExpedientTipusId(),
						false));
		if (dto.getExpedientIds() != null) {
			int ordre = 0;
			for (Long expedientId: dto.getExpedientIds()) {
				Expedient expedient = expedientDao.getById(expedientId, false);
				ExecucioMassivaExpedient eme = new ExecucioMassivaExpedient(
						execucioMassiva,
						expedient,
						ordre++);
				execucioMassiva.addExpedient(eme);
			}
		}
		execucioMassivaDao.saveOrUpdate(execucioMassiva);
	}

	public List<ExecucioMassivaDto> getExecucionsMassivesActives() {
		List<ExecucioMassivaDto> resposta = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassiva massiva: execucioMassivaDao.getExecucionsMassivesActives())
			resposta.add(dtoConverter.toExecucioMassicaDto(massiva));
		return resposta;
	}
	
	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
		ExecucioMassivaExpedient expedient = execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaExecucioMassiva);
		return dtoConverter.toOperacioMassiva(expedient);
	}
	
	public void executarExecucioMassiva(OperacioMassivaDto dto) throws Exception {
		logger.debug("Executant la acció massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", expedient=" + dto.getId() + ", acció=" + dto.getTipus());
		ExecucioMassivaTipus tipus = dto.getTipus();
		if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA){
			// TODO
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
		}
	}
	
	private void actualitzarVersio(OperacioMassivaDto dto) throws Exception {
		ExecucioMassivaExpedient eme = null;
		ExpedientDto exp = dto.getExpedient();
		try {
			eme = execucioMassivaExpedientDao.getById(dto.getId(), false);
			eme.setDataInici(new Date());
			Long definicioProcesId = (Long)deserialize(dto.getParam2());
			DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
			expedientService.changeProcessInstanceVersion(exp.getProcessInstanceId(), definicioProces.getVersio());
			eme.setEstat(ExecucioMassivaEstat.ESTAT_FINALITZAT);
			eme.setDataFi(new Date());
			execucioMassivaExpedientDao.saveOrUpdate(eme);
		} catch (Exception ex) {
			logger.error("OPERACIO:" + dto.getId() + ". No s'ha pogut canviar la versió del procés", ex);
			throw ex;
		}
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
					
					// TODO: Correu
					List<String> emailAddresses = new ArrayList<String>();
					AddressResolver addressResolver = (AddressResolver)JbpmConfiguration.Configs.getObject("jbpm.mail.address.resolver");
					Object resolvedAddresses = addressResolver.resolveAddress(dto.getUsuari());
					if (resolvedAddresses != null) {
						if (resolvedAddresses instanceof String) {
							emailAddresses.add((String)resolvedAddresses);
						} else if (resolvedAddresses instanceof Collection) {
							emailAddresses.addAll((Collection)resolvedAddresses);
						} else if (resolvedAddresses instanceof String[]) {
							emailAddresses.addAll(Arrays.asList((String[])resolvedAddresses));
						} else {
							throw new JbpmException(
									"Address resolver '" + addressResolver +
									"' returned '" + resolvedAddresses.getClass().getName() +
									"' instead of a String, Collection or String-array: " + resolvedAddresses);
						}
					}
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
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		obj = ois.readObject();
		return obj;
	}
	
	public OperacioMassivaDto getOperacioMassivaActiva(Long ultimaMassiva) {
		return dtoConverter.toOperacioMassiva(execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaMassiva));
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
