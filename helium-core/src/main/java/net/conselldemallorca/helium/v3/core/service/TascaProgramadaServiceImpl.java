package net.conselldemallorca.helium.v3.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.model.hibernate.ExecucioMassivaExpedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.ExecucioMassivaService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;
import net.conselldemallorca.helium.v3.core.repository.ExecucioMassivaExpedientRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servei per gestionar els terminis dels expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaProgramadaServiceV3")
public class TascaProgramadaServiceImpl implements TascaProgramadaService {
	
	@Resource
	private ExecucioMassivaExpedientRepository execucioMassivaExpedientRepository;
	
	@Resource
	private ExpedientRepository expedientRepository;
	
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	
	@Resource
	private IndexHelper indexHelper;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
	@Scheduled(fixedDelayString = "${app.massiu.periode.noves}")
	public void comprovarExecucionsMassives() {
		// NO PUJAR ARXIU!!!
	}
	
	/*** REINDEXACIO ASÍNCRONA ***/
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${app.reindexacio.asincrona.periode}")
	public void comprovarReindexacioAsincrona() {
		List<Expedient> expedients = expedientRepository.findByReindexarDataNotNullOrderByReindexarDataAsc();
		for (Expedient expedient: expedients) {
			try {
				System.out.println("Reindexant ==> " + expedient.getIdentificador());
				indexHelper.expedientIndexLuceneUpdate(
						expedient.getProcessInstanceId(),
						false,
						null);
			} catch (Exception ex) {
				logger.error(
						"Error reindexant l'expedient " + expedient.getIdentificador(),
						ex);
				expedient.setReindexarError(true);
			} finally {
				expedient.setReindexarData(null);
				expedientRepository.save(expedient);
			}
		}
	}

	/**************************/
	
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
