/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto;

/**
 * Helper per a gestionar les tasques dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MassivaHelper {

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;


	public OperacioMassivaDto getExecucionsMassivesActiva(Long ultimaExecucioMassiva) {
//		Date ara = new Date();
//		ExecucioMassivaExpedient expedient = execucioMassivaExpedientDao.getExecucioMassivaActiva(ultimaExecucioMassiva, ara);
//		
//		if (expedient == null) {
//			// Comprobamos si es una ejecución masiva sin expedientes asociados. En ese caso actualizamos la fecha de fin
//			Long mas = execucioMassivaDao.getMinExecucioMassiva(ara);
//			if (mas != null) {
//				ExecucioMassiva massiva = execucioMassivaDao.getById(mas, false);
//				if (massiva != null) {
//					massiva.setDataFi(new Date());
//					execucioMassivaDao.merge(massiva);
//					execucioMassivaDao.flush();
//				}
//			}
//		}
//		return dtoConverter.toOperacioMassiva(expedient);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void executarExecucioMassiva(OperacioMassivaDto dto) throws Exception {
//		logger.debug(
//				"Executant la acció massiva (" +
//				"expedientTipusId=" + dto.getExpedientTipusId() + ", " +
//				"dataInici=" + dto.getDataInici() + ", " +
//				"expedient=" + dto.getId() + ", " +
//				"acció=" + dto.getTipus());
//		final Timer timerTotal = metricRegistry.timer(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"executar"));
//		final Timer.Context contextTotal = timerTotal.time();
//		Counter countTotal = metricRegistry.counter(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"executar.count"));
//		countTotal.inc();
//		final Timer timerEntorn = metricRegistry.timer(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"executar",
//						dto.getExpedient().getEntorn().getCodi()));
//		final Timer.Context contextEntorn = timerEntorn.time();
//		Counter countEntorn = metricRegistry.counter(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"executar.count",
//						dto.getExpedient().getEntorn().getCodi()));
//		countEntorn.inc();
//		final Timer timerTipexp = metricRegistry.timer(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"completar",
//						dto.getExpedient().getEntorn().getCodi(),
//						dto.getExpedient().getTipus().getCodi()));
//		final Timer.Context contextTipexp = timerTipexp.time();
//		Counter countTipexp = metricRegistry.counter(
//				MetricRegistry.name(
//						ExecucioMassivaService.class,
//						"completar.count",
//						dto.getExpedient().getEntorn().getCodi(),
//						dto.getExpedient().getTipus().getCodi()));
//		countTipexp.inc();
//		try {
//			ExecucioMassivaTipus tipus = dto.getTipus();
//			
//			Authentication orgAuthentication = SecurityContextHolder.getContext().getAuthentication();
//			
//			final String user = dto.getUsuari();
//			
//	        Principal principal = new Principal() {
//				public String getName() {
//					return user;
//				}
//			};
//			
//			Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, null);
//			
//			if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO || 
//					tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT){
//				Object param2 = deserialize(dto.getParam2());
//				if (param2 instanceof Object[]) {
//					Object credentials = ((Object[])param2)[1];
//					List<String> rols = (List<String>)((Object[])param2)[2];
//					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//					if (!rols.isEmpty()) {
//						for (String rol: rols) {
//							authorities.add(new SimpleGrantedAuthority(rol));
//						}
//					}
//					authentication =  new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
//				}
//			}
//			
//	        SecurityContextHolder.getContext().setAuthentication(authentication);
//			
//			String expedient = null;
//	        if (MesuresTemporalsHelper.isActiu()) {
//	        	try {
//		        	if (dto.getExpedient() != null)
//		        		expedient = dto.getExpedient().getTipus().getNom();
//	        	} catch (Exception e) {}
//			}
//	        
//			if (tipus == ExecucioMassivaTipus.EXECUTAR_TASCA){
//				// Authentication
//				String param = dto.getParam1();
//				Object param2 = deserialize(dto.getParam2());
//
//				if (param2 instanceof Object[]) {
//					Object credentials = null;					
//					List<String> rols = null;
//					if (param.equals("Guardar")) {
//						credentials = ((Object[]) param2)[2];
//						rols = (List<String>) ((Object[]) param2)[3];
//					} else if (param.equals("Validar")) {
//						credentials = ((Object[]) param2)[2];
//						rols = (List<String>) ((Object[]) param2)[3];
//					} else if (param.equals("Completar")) {
//						credentials = ((Object[]) param2)[2];
//						rols = (List<String>) ((Object[]) param2)[3];
//					} else if (param.equals("Restaurar")) {
//						credentials = ((Object[]) param2)[1];
//						rols = (List<String>) ((Object[]) param2)[2];
//					} else if (param.equals("Accio")) {
//						credentials = ((Object[]) param2)[2];
//						rols = (List<String>) ((Object[]) param2)[3];
//					} else if (param.equals("DocGuardar")) {
//						credentials = ((Object[]) param2)[5];
//						rols = (List<String>) ((Object[]) param2)[6];
//					} else if (param.equals("DocEsborrar")) {
//						credentials = ((Object[]) param2)[2];
//						rols = (List<String>) ((Object[]) param2)[3];
//					} else if (param.equals("DocGenerar")) {
//						credentials = ((Object[]) param2)[3];
//						rols = (List<String>) ((Object[]) param2)[4];
//					} else if (param.equals("RegEsborrar")) {
//						credentials = ((Object[]) param2)[3];
//						rols = (List<String>) ((Object[]) param2)[4];
//					} else if (param.equals("RegGuardar")) {
//						credentials = ((Object[]) param2)[4];
//						rols = (List<String>) ((Object[]) param2)[5];
//					}
//
//					List<GrantedAuthority> authorities = null;
//					if (!rols.isEmpty()) {						
//						authorities = new ArrayList<GrantedAuthority>();
//						if (!rols.isEmpty()) {
//							for (String rol: rols) {
//								authorities.add(new SimpleGrantedAuthority(rol));
//							}
//						}
//					}
//					authentication =  new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
//				}
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//
//				gestioTasca(dto);
//			} else if (tipus == ExecucioMassivaTipus.ACTUALITZAR_VERSIO_DEFPROC){
//				mesuresTemporalsHelper.mesuraIniciar("Actualitzar", "massiva", expedient);
//				actualitzarVersio(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Actualitzar", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_SCRIPT){
//				mesuresTemporalsHelper.mesuraIniciar("Executar script", "massiva", expedient);
//				executarScript(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Executar script", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.EXECUTAR_ACCIO){
//				mesuresTemporalsHelper.mesuraIniciar("Executar accio", "massiva", expedient);
//				executarAccio(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Executar accio", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.ATURAR_EXPEDIENT){
//				mesuresTemporalsHelper.mesuraIniciar("Aturar expedient", "massiva", expedient);
//				aturarExpedient(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Aturar expedient", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_VARIABLE){
//				mesuresTemporalsHelper.mesuraIniciar("Modificar variable", "massiva", expedient);
//				modificarVariable(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Modificar variable", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.MODIFICAR_DOCUMENT){
////				mesuresTemporalsHelper.mesuraIniciar("Modificar document", "massiva", expedient);
//				modificarDocument(dto);
////				mesuresTemporalsHelper.mesuraCalcular("Modificar document", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.REINDEXAR){
//				mesuresTemporalsHelper.mesuraIniciar("Reindexar", "massiva", expedient);
//				reindexarExpedient(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Reindexar", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.BUIDARLOG){
//				mesuresTemporalsHelper.mesuraIniciar("Buidar log", "massiva", expedient);
//				buidarLogExpedient(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Buidar log", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.REPRENDRE_EXPEDIENT){
//				mesuresTemporalsHelper.mesuraIniciar("desfer fi process instance", "massiva", expedient);
//				reprendreExpedient(dto);
//				mesuresTemporalsHelper.mesuraCalcular("desfer fi process instance", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.REPRENDRE){
//				mesuresTemporalsHelper.mesuraIniciar("reprendre tramitació process instance", "massiva", expedient);
//				reprendreTramitacio(dto);
//				mesuresTemporalsHelper.mesuraCalcular("reprendre tramitació process instance", "massiva", expedient);
//			} else if (tipus == ExecucioMassivaTipus.REASSIGNAR){
//				mesuresTemporalsHelper.mesuraIniciar("Reassignar", "massiva", expedient);
//				//reassignarExpedient(dto);
//				reassignarTasca(dto);
//				mesuresTemporalsHelper.mesuraCalcular("Reassignar", "massiva", expedient);
//			}
//			SecurityContextHolder.getContext().setAuthentication(orgAuthentication);
//		} catch (Exception ex) {
//			logger.error("Error al executar la acció massiva (expedientTipusId=" + dto.getExpedientTipusId() + ", dataInici=" + dto.getDataInici() + ", expedient=" + dto.getId() + ", acció=" + dto.getTipus(), ex);
//			throw ex;
//		} finally {
//			contextTotal.stop();
//			contextEntorn.stop();
//			contextTipexp.stop();
//		}
	}
	
	public void actualitzaUltimaOperacio(net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto operacioMassiva) {
//		if (operacioMassiva.getUltimaOperacio()) {
//			try {
//				ExecucioMassiva em = execucioMassivaDao.getById(operacioMassiva.getExecucioMassivaId(), false);
//				em.setDataFi(new Date());
//				execucioMassivaDao.saveOrUpdate(em);
//			} catch (Exception ex) {
//				logger.error("EXPEDIENTMASSIU:"+operacioMassiva.getExecucioMassivaId()+". No s'ha pogut finalitzar l'expedient massiu", ex);
//			}
//			try {
//				if (operacioMassiva.getEnviarCorreu()) {
//					
//					// Correu
//					List<String> emailAddresses = new ArrayList<String>();
//					
//					PersonaDto persona = pluginService.findPersonaAmbCodi(operacioMassiva.getUsuari());
//					emailAddresses.add(persona.getEmail());
//
//					mailDao.send(
//							GlobalProperties.getInstance().getProperty("app.correu.remitent"),
//							emailAddresses,
//							null,
//							null,
//							"Execució massiva",
//							"L'execució massiva ha finalitzat.");
//				}
//			} catch (Exception ex) {
//				logger.error("EXPEDIENTMASSIU: No s'ha pogut enviar el correu de finalització", ex);
//			}
//		}
	}
	
	public void generaInformeError(net.conselldemallorca.helium.v3.core.api.dto.OperacioMassivaDto operacioMassiva, Exception exception) {
//		ExecucioMassivaExpedient eme = execucioMassivaExpedientDao.getById(operacioMassiva.getId(), false);
//		Date ara = new Date();
//		eme.setDataInici(new Date());
//		eme.setDataFi(ara);
//		eme.setEstat(ExecucioMassivaEstat.ESTAT_ERROR);
//		
//		StringWriter out = new StringWriter();
//		exception.printStackTrace(new PrintWriter(out));
//		eme.setError(out.toString());
//		execucioMassivaExpedientDao.saveOrUpdate(eme);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MassivaHelper.class);

}
