/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.helperv26.LuceneHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.jbpm3.integracio.DominiCodiDescripcio;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.jbpm3.integracio.Registre;
import net.conselldemallorca.helium.v3.core.api.dto.DadaIndexadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.IndexacioException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("serviceUtilsV3")
public class IndexHelper {

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private LuceneHelper luceneHelper;
//	@Resource
//	private MongoDBHelper mongoDBHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MetricRegistry metricRegistry;



	/**
	 * Mètodes per a la reindexació d'expedients
	 */
	public void expedientIndexLuceneCreate(String processInstanceId) {
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		// Mètriques - Timers
		Timer.Context contextDadesTotal = null;
		Timer.Context contextDadesEntorn = null;
		Timer.Context contextDadesTipExp = null;
		Timer.Context contextIndexarTotal = null;
		Timer.Context contextIndexarEntorn = null;
		Timer.Context contextIndexarTipExp = null;
		
		final Timer timerDadesTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.dades"));
		final Timer timerDadesEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.dades", expedient.getEntorn().getCodi()));
		final Timer timerDadesTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.dades", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		final Timer timerIndexarTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.indexar"));
		final Timer timerIndexarEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.indexar", expedient.getEntorn().getCodi()));
		final Timer timerIndexarTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.create.indexar", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		// Mètriques - Comptadors
		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.create.count"));
		Counter countEntorn = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.create.count", expedient.getEntorn().getCodi()));
		Counter countTipExp = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.create.count", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		boolean ctxDadesStoped = false;
		
		try {
			// Incrementam els comptadors de les mètriques
			countTotal.inc();
			countEntorn.inc();
			countTipExp.inc();
			
			// Iniciam els timers de obtenció de dades
			contextDadesTotal = timerDadesTotal.time();
			contextDadesEntorn = timerDadesEntorn.time();
			contextDadesTipExp = timerDadesTipExp.time();
			
			// Obtenim les dades
			Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
			Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
			Map<String, DefinicioProces> mapDefinicioProces = getMapDefinicionsProces(expedient.getProcessInstanceId());
			Map<String, Map<String, String>> mapValorsDomini = getMapValorsDomini(mapCamps, mapValors);
			boolean isExpedientFinalitzat = isExpedientFinalitzat(expedient);
			
			// Aturam els timers de obtenció de dades
			contextDadesTotal.stop();
			contextDadesEntorn.stop();
			contextDadesTipExp.stop();
			
			ctxDadesStoped = true;
			
			// Iniciam els timers de indexació amb Lucene
			contextIndexarTotal = timerIndexarTotal.time();
			contextIndexarEntorn = timerIndexarEntorn.time();
			contextIndexarTipExp = timerIndexarTipExp.time();
			
			boolean success = luceneHelper.createExpedient(
					expedient,
					mapDefinicioProces,
					mapCamps,
					mapValors,
					mapValorsDomini,
					isExpedientFinalitzat,
					false);
			expedient.setReindexarError(!success);
			expedientRepository.saveAndFlush(expedient);
		} catch (Exception ex) {
			throw new IndexacioException("Crear Indexació", ex);
		} finally {
			if (!ctxDadesStoped) {
				// Aturam els timers de obtenció de dades
				contextDadesTotal.stop();
				contextDadesEntorn.stop();
				contextDadesTipExp.stop();
			}
			// Aturam els timers de indexació amb Lucene
			if (contextIndexarTotal != null) {
				contextIndexarTotal.stop();
				contextIndexarEntorn.stop();
				contextIndexarTipExp.stop();
			}
		}
	}

//	mètode per a marcar les reindexacions en segon pla si es el cas
	
	public void expedientIndexLuceneUpdate(
  			String processInstanceId) {
 		expedientIndexLuceneUpdate(processInstanceId, false);
 	}
	
	public void expedientIndexLuceneUpdate(
			String processInstanceId,
			boolean isExecucioMassiva) {
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (expedient.getTipus().isReindexacioAsincrona() && !isExecucioMassiva) {
			if (expedient.getReindexarData() == null) {
				expedient.setReindexarData(new Date());
				expedientRepository.save(expedient);
			}
		} else {
			expedientIndexLuceneUpdate(
					processInstanceId,
					false,
					null);
		}
	}
	
	public void expedientIndexLuceneUpdate(
			String processInstanceId,
			boolean perTasca,
			Expedient expedientDeLaTasca) {
		
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
		
		Timer.Context contextTotal = null;
		Timer.Context contextEntorn = null;
		Timer.Context contextTipexp = null;
		if (perTasca) {
			final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(TascaService.class, "completar.reindexar"));
			final Timer timerEntorn = metricRegistry.timer(MetricRegistry.name(TascaService.class, "completar.reindexar", expedientDeLaTasca.getEntorn().getCodi()));
			final Timer timerTipexp = metricRegistry.timer(MetricRegistry.name(TascaService.class, "completar.reindexar", expedientDeLaTasca.getEntorn().getCodi(), expedientDeLaTasca.getTipus().getCodi()));
			
			Counter countTotal = metricRegistry.counter(MetricRegistry.name(TascaService.class, "completar.reindexar.count"));
			Counter countEntorn = metricRegistry.counter(MetricRegistry.name(TascaService.class, "completar.reindexar.count", expedientDeLaTasca.getEntorn().getCodi()));
			Counter countTipexp = metricRegistry.counter(MetricRegistry.name(TascaService.class, "completar.reindexar.count", expedientDeLaTasca.getEntorn().getCodi(), expedientDeLaTasca.getTipus().getCodi()));
		
			countTotal.inc();
			countEntorn.inc();
			countTipexp.inc();
			
			contextTotal = timerTotal.time();
			contextEntorn = timerEntorn.time();
			contextTipexp = timerTipexp.time();
		}
		
		// Mètriques - Timers
		Timer.Context contextDadesTotal = null;
		Timer.Context contextDadesEntorn = null;
		Timer.Context contextDadesTipExp = null;
		Timer.Context contextIndexarTotal = null;
		Timer.Context contextIndexarEntorn = null;
		Timer.Context contextIndexarTipExp = null;
		
		final Timer timerDadesTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.dades"));
		final Timer timerDadesEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.dades", expedient.getEntorn().getCodi()));
		final Timer timerDadesTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.dades", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		final Timer timerIndexarTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.indexar"));
		final Timer timerIndexarEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.indexar", expedient.getEntorn().getCodi()));
		final Timer timerIndexarTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.update.indexar", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		// Mètriques - Comptadors
		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.update.count"));
		Counter countEntorn = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.update.count", expedient.getEntorn().getCodi()));
		Counter countTipExp = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.update.count", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		boolean ctxDadesStoped = false;
		boolean actualitzat = false;		
		try {
			// Incrementam els comptadors de les mètriques
			countTotal.inc();
			countEntorn.inc();
			countTipExp.inc();
			
			// Iniciam els timers de obtenció de dades
			contextDadesTotal = timerDadesTotal.time();
			contextDadesEntorn = timerDadesEntorn.time();
			contextDadesTipExp = timerDadesTipExp.time();
			
			// Obtenim les dades
			Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
			Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
			Map<String, DefinicioProces> mapDefinicioProces = getMapDefinicionsProces(rootProcessInstance.getId());
			Map<String, Map<String, String>> mapValorsDomini = getMapValorsDomini(mapCamps, mapValors);
			boolean isExpedientFinalitzat = isExpedientFinalitzat(expedient);

			// Aturam els timers de obtenció de dades
			contextDadesTotal.stop();
			contextDadesEntorn.stop();
			contextDadesTipExp.stop();
			
			ctxDadesStoped = true;
			
			// Iniciam els timers de indexació amb Lucene
			contextIndexarTotal = timerIndexarTotal.time();
			contextIndexarEntorn = timerIndexarEntorn.time();
			contextIndexarTipExp = timerIndexarTipExp.time();
			
			actualitzat = luceneHelper.updateExpedientCamps(
					expedient,
					mapDefinicioProces,
					mapCamps,
					mapValors,
					mapValorsDomini,
					isExpedientFinalitzat);
		} catch (Exception ex) {
			throw new IndexacioException("Update Indexació", ex);
		} finally {
			if (perTasca) {
				contextTotal.stop();
				contextEntorn.stop();
				contextTipexp.stop();
			}
			if (!ctxDadesStoped) {
				// Aturam els timers de obtenció de dades
				contextDadesTotal.stop();
				contextDadesEntorn.stop();
				contextDadesTipExp.stop();
			}
			// Aturam els timers de indexació amb Lucene
			if (contextIndexarTotal != null) {
				contextIndexarTotal.stop();
				contextIndexarEntorn.stop();
				contextIndexarTipExp.stop();
			}
			expedient.setReindexarData(null);
		}
		expedient.setReindexarError(!actualitzat);
		expedientRepository.saveAndFlush(expedient);
	}

	public void expedientIndexLuceneDelete(String processInstanceId) {
		
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		
		// Mètriques - Timers
		Timer.Context contextIndexarTotal = null;
		Timer.Context contextIndexarEntorn = null;
		Timer.Context contextIndexarTipExp = null;
		
		final Timer timerIndexarTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.delete.indexar"));
		final Timer timerIndexarEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.delete.indexar", expedient.getEntorn().getCodi()));
		final Timer timerIndexarTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.delete.indexar", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		// Mètriques - Comptadors
		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.delete.count"));
		Counter countEntorn = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.delete.count", expedient.getEntorn().getCodi()));
		Counter countTipExp = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.delete.count", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		try {
			// Incrementam els comptadors de les mètriques
			countTotal.inc();
			countEntorn.inc();
			countTipExp.inc();
						
			// Iniciam els timers de indexació amb Lucene
			contextIndexarTotal = timerIndexarTotal.time();
			contextIndexarEntorn = timerIndexarEntorn.time();
			contextIndexarTipExp = timerIndexarTipExp.time();
						
			luceneHelper.deleteExpedient(expedient);
		} catch (Exception ex) {
			throw new IndexacioException("Delete indexació", ex);
		} finally {
			// Aturam els timers de indexació amb Lucene
			contextIndexarTotal.stop();
			contextIndexarEntorn.stop();
			contextIndexarTipExp.stop();
		}
	}

	public boolean expedientIndexLuceneRecrear(Expedient expedient) {
		// Mètriques - Timers
		Timer.Context contextDadesTotal = null;
		Timer.Context contextDadesEntorn = null;
		Timer.Context contextDadesTipExp = null;
		Timer.Context contextIndexarTotal = null;
		Timer.Context contextIndexarEntorn = null;
		Timer.Context contextIndexarTipExp = null;
//		Timer.Context contextMongoTotal = null;
//		Timer.Context contextMongoEntorn = null;
//		Timer.Context contextMongoTipExp = null;
		
		final Timer timerDadesTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.dades"));
		final Timer timerDadesEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.dades", expedient.getEntorn().getCodi()));
		final Timer timerDadesTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.dades", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		final Timer timerIndexarTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.indexar"));
		final Timer timerIndexarEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.indexar", expedient.getEntorn().getCodi()));
		final Timer timerIndexarTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.indexar", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
//		final Timer timerMongoTotal = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "mongoDB.recreate.indexar"));
//		final Timer timerMongoEntorn = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "mongoDB.recreate.indexar", expedient.getEntorn().getCodi()));
//		final Timer timerMongoTipExp = metricRegistry.timer(MetricRegistry.name(LuceneHelper.class, "mongoDB.recreate.indexar", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));
		
		// Mètriques - Comptadors
		Counter countTotal = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.count"));
		Counter countEntorn = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.count", expedient.getEntorn().getCodi()));
		Counter countTipExp = metricRegistry.counter(MetricRegistry.name(LuceneHelper.class, "lucene.recreate.count", expedient.getEntorn().getCodi(), expedient.getTipus().getCodi()));

		boolean actualitzat = false;
		boolean ctxDadesStoped = false;
//		boolean ctxLuceneStoped = false;
		
		try {
			
			// Incrementam els comptadors de les mètriques
			countTotal.inc();
			countEntorn.inc();
			countTipExp.inc();
			
			// Iniciam els timers de obtenció de dades
			contextDadesTotal = timerDadesTotal.time();
			contextDadesEntorn = timerDadesEntorn.time();
			contextDadesTipExp = timerDadesTipExp.time();
			
			// Obtenim les dades
			Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
			Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
			Map<String, DefinicioProces> mapDefinicioProces = getMapDefinicionsProces(expedient.getProcessInstanceId());
			Map<String, Map<String, String>> mapValorsDomini = getMapValorsDomini(mapCamps, mapValors);
			boolean isExpedientFinalitzat = isExpedientFinalitzat(expedient);

			// Aturam els timers de obtenció de dades
			contextDadesTotal.stop();
			contextDadesEntorn.stop();
			contextDadesTipExp.stop();
						
			ctxDadesStoped = true;
			
			// Iniciam els timers de indexació amb Lucene
			contextIndexarTotal = timerIndexarTotal.time();
			contextIndexarEntorn = timerIndexarEntorn.time();
			contextIndexarTipExp = timerIndexarTipExp.time();
			
			luceneHelper.deleteExpedient(expedient);
			actualitzat = luceneHelper.createExpedient(
					expedient,
					mapDefinicioProces,
					mapCamps,
					mapValors,
					mapValorsDomini,
					isExpedientFinalitzat,
					false);
			
			// Aturam els timers de obtenció de dades
			contextIndexarTotal.stop();
			contextIndexarEntorn.stop();
			contextIndexarTipExp.stop();
						
//			ctxLuceneStoped = true;
			
//			// Iniciam els timers de indexació amb MongoDB
//			contextMongoTotal = timerMongoTotal.time();
//			contextMongoEntorn = timerMongoEntorn.time();
//			contextMongoTipExp = timerMongoTipExp.time();
//			
//			mongoDBHelper.deleteExpedient(expedient);
//			mongoDBHelper.createExpedient(
//					expedient,
//					mapDefinicioProces,
//					mapCamps,
//					mapValors,
//					mapValorsDomini,
//					isExpedientFinalitzat,
//					false);
		} catch (Exception ex) {
			throw new IndexacioException("Indexació", ex);
		} finally {
			if (!ctxDadesStoped) {
				// Aturam els timers de obtenció de dades
				contextDadesTotal.stop();
				contextDadesEntorn.stop();
				contextDadesTipExp.stop();
			}
//			if (!ctxLuceneStoped) {
				// Aturam els timers de indexació amb Lucene
			if (contextIndexarTotal != null) {
				contextIndexarTotal.stop();
				contextIndexarEntorn.stop();
				contextIndexarTipExp.stop();
			}
//			// Aturam els timers de indexació amb MongoDB
//			contextMongoTotal.stop();
//			contextMongoEntorn.stop();
//			contextMongoTipExp.stop();
			expedient.setReindexarData(null);
		}
		expedient.setReindexarError(!actualitzat);
		expedientRepository.saveAndFlush(expedient);
		return actualitzat;
	}

	/** Mètode per esborrar una camp específic de l'índex. */
	public void expedientIndexLuceneDelete(String processInstanceId, String camp) {
		
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
		boolean success = luceneHelper.deleteExpedientCamp(expedient, camp);
		expedient.setReindexarError(!success);		
	}

	private Map<String, Object> getVariablesJbpmProcesValor(
			String processInstanceId) {
		Map<String, Object> valors = jbpmHelper.getProcessInstanceVariables(processInstanceId);
		Map<String, Object> valorsRevisats = new HashMap<String, Object>();
		if (valors != null) {
			for (String varCodi: valors.keySet()) {
				Object valor = valors.get(varCodi);
				valorsRevisats.put(varCodi, valorVariableJbpmRevisat(valor));
			}
		}
		return valorsRevisats;
	}

	private boolean isExpedientFinalitzat(Expedient expedient) {
		if (expedient.getProcessInstanceId() != null) {
			JbpmProcessInstance processInstance = jbpmHelper.getProcessInstance(expedient.getProcessInstanceId());
			return processInstance.getEnd() != null;
		}
		return false;
	}

	private Map<String, DefinicioProces> getMapDefinicionsProces(String processInstanceId) {
		Map<String, DefinicioProces> resposta = new HashMap<String, DefinicioProces>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()));
		return resposta;
	}
	
	private Map<String, Set<Camp>> getMapCamps(String processInstanceId) {
		Map<String, Set<Camp>> resposta = new HashMap<String, Set<Camp>>();
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		if (! expedient.getTipus().isAmbInfoPropia()) {
			// Definicions de procés
			List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
			for (JbpmProcessInstance pi: tree) {
				resposta.put(
						pi.getId(),
						definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()).getCamps());
			}
		} else {
			// Tipus d'expedient
			resposta.put(
						processInstanceId,
						new HashSet<Camp>(campRepository.findByExpedientTipusOrderByCodiAsc(expedient.getTipus())));
		}
		return resposta;
	}
	
	private Map<String, Map<String, Object>> getMapValors(String processInstanceId) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		Expedient expedient = expedientRepository.findByProcessInstanceId(processInstanceId);
		boolean ambInfoPropia = expedient.getTipus().isAmbInfoPropia();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		if (! ambInfoPropia) {
			for (JbpmProcessInstance pi: tree)
				resposta.put(
						pi.getId(),
						getVariablesJbpmProcesValor(pi.getId()));
		} else {
			// Guarda els valors dels processos però si es repeteix el codi només guarda la del principal
			Map<String, Object> valors = new HashMap<String, Object>();
			Map<String, Object> valorsProcessInstance;			
			for (JbpmProcessInstance pi: tree) { 
				valorsProcessInstance = getVariablesJbpmProcesValor(pi.getId());
				for (String varCodi : valorsProcessInstance.keySet())
					if (!valors.containsKey(varCodi) || processInstanceId.equals(pi.getId()))
						valors.put(varCodi, valorsProcessInstance.get(varCodi));
			}
			resposta.put(
					processInstanceId,
					valors);
		}
		return resposta;
	}
	
	private Map<String, Map<String, String>> getMapValorsDomini(
			Map<String, Set<Camp>> mapCamps,
			Map<String, Map<String, Object>> mapValors) {
		Map<String, Map<String, String>> resposta = new HashMap<String, Map<String, String>>();
		for (String clau: mapCamps.keySet()) {
			Map<String, String> textDominis = new HashMap<String, String>();
			for (Camp camp: mapCamps.get(clau)) {
				if (mapValors.get(clau) != null) {
					Object valor = mapValors.get(clau).get(camp.getCodi());
					if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
						if (valor != null) {
							String[] columnesRegistre = new String[camp.getRegistreMembres().size()];
							for (int i = 0; i < camp.getRegistreMembres().size(); i++) {
								columnesRegistre[i] = camp.getRegistreMembres().get(i).getMembre().getCodi();
							}
							List<Registre> registres = new ArrayList<Registre>();
							if (camp.isMultiple()) {
								Object[] filesValor = (Object[])valor;
								for (int i = 0; i < filesValor.length; i++) {
									registres.add(
											new Registre(columnesRegistre, (Object[])filesValor[i]));
								}
							} else {
								registres.add(
										new Registre(columnesRegistre, (Object[])valor));
							}
							for (Registre registre: registres) {
								for (CampRegistre campRegistre: camp.getRegistreMembres()) {
									guardarValorDominiPerCamp(
											textDominis,
											clau,
											campRegistre.getMembre(),
											registre.getValor(campRegistre.getMembre().getCodi()));
								}
							}
						}
					} else {
						guardarValorDominiPerCamp(
								textDominis,
								clau,
								camp,
								valor);
					}
				}
			}
			resposta.put(clau, textDominis);
		}
		return resposta;
	}

	private void guardarValorDominiPerCamp(
			Map<String, String> textDominis,
			String processInstanceId,
			Camp camp,
			Object valor) {
		if (!(valor instanceof String) || ((String)valor).length() > 0) {
			if (camp.getTipus().equals(TipusCamp.SELECCIO) || camp.getTipus().equals(TipusCamp.SUGGEST)) {
				if (valor != null) {
					String valorDomini = variableHelper.getTextPerCamp(
							camp,
							valor,
							null,
							null, 
							processInstanceId);
					textDominis.put(
							camp.getCodi() + "@" + valor.toString(),
							valorDomini);
				}
			}
		}
	}

	private Object valorVariableJbpmRevisat(Object valor) {
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
	}
	
	/** Obté les dades de l'índex per a un expedient en concret.
	 * 
	 * @param processInstanceId
	 * @return 
	 */
	public List<Map<String, DadaIndexadaDto>> expedientIndexLuceneGetDades(String processInstanceId) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
		List<Camp> informeCamps = new ArrayList<Camp>();
		Map<String, Set<Camp>> camps = getMapCamps(processInstanceId);
		for (String clau: camps.keySet()) {
			for (Camp camp: camps.get(clau))
				informeCamps.add(camp);
		}
		informeCamps.addAll(
				expedientHelper.findAllCampsExpedientConsulta());
		
		return luceneHelper.expedientIndexLuceneGetDades(expedient, informeCamps);
	}
}
