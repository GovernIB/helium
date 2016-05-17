package net.conselldemallorca.helium.v3.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaProgramadaServiceV3")
public class TascaProgramadaServiceImpl implements TascaProgramadaService {
	
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
	@Scheduled(fixedDelayString = "${app.massiu.periode.noves}")
	public void comprovarExecucionsMassives() {
		boolean active = true;
		Long ultimaExecucioMassiva = null;
		
		int timeBetweenExecutions = 500;
		try {
			timeBetweenExecutions = Integer.parseInt(
					GlobalProperties.getInstance().getProperty("app.massiu.periode.execucions")); 
		} catch (Exception ex) {}
		
		while (active) {
			try {
				Long ome_id = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
				if (ome_id != null) {
					try {
						execucioMassivaService.executarExecucioMassiva(ome_id);
					}
					catch (Exception e) {
						// si s'ha produit una excepció, deseram l'error a la operació
						execucioMassivaService.generaInformeError(ome_id, getError(ome_id));
					}
					ExecucioMassivaExpedient ome = execucioMassivaExpedientRepository.findOne(ome_id);
					ultimaExecucioMassiva = ome.getExecucioMassiva().getId();
					execucioMassivaService.actualitzaUltimaOperacio(ome.getId());
				} else {
					active = false;
				}
				Thread.sleep(timeBetweenExecutions);
			} catch (Exception e) {
				logger.error("La execució de execucions massives ha estat interromput");
				active = false;
			}
		}
	}
	
	public static void saveError(Long operacioMassivaId, Throwable error) {
		StringWriter out = new StringWriter();
		error.printStackTrace(new PrintWriter(out));
		errorsMassiva.put(operacioMassivaId, out.toString());
	}
	
	private static String getError(Long operacioMassivaId) {
		String error = errorsMassiva.get(operacioMassivaId);
		errorsMassiva.remove(operacioMassivaId);
		return error;
	}
	
	private static final Log logger = LogFactory.getLog(TascaProgramadaService.class);
}
