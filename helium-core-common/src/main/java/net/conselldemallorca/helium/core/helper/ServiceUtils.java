/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

/**
 * Utilitats comunes pels serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("serviceUtilsV3")
public class ServiceUtils {

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private LuceneHelper luceneHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MetricRegistry metricRegistry;



	/**
	 * Mètodes per a la reindexació d'expedients
	 */
	public void expedientIndexLuceneCreate(String processInstanceId) {
		// Timers LUCENE
		Timer.Context contextDades = null;
		Timer.Context contextIndexar = null;
		final Timer timerDades = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.create.dades"));
		contextDades = timerDades.time();
		Counter countDades = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.create.dades.count"));
		countDades.inc();
		final Timer timerIndexar = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.create.indexar"));
		contextIndexar = timerIndexar.time();
		Counter countIndexar = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.create.indexar.count"));
		countIndexar.inc();
		boolean ctxDadesStoped = false;
		
		try {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
			Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
			contextDades.stop();
			ctxDadesStoped = true;
			luceneHelper.createExpedient(
					expedient,
					getMapDefinicionsProces(expedient.getProcessInstanceId()),
					mapCamps,
					mapValors,
					getMapValorsDomini(mapCamps, mapValors),
					isExpedientFinalitzat(expedient),
					false);
		} finally {
			if (!ctxDadesStoped)
				contextDades.stop();
			contextIndexar.stop();
		}
	}

	public void expedientIndexLuceneUpdate(
			String processInstanceId) {
		expedientIndexLuceneUpdate(
				processInstanceId,
				false,
				null);
	}
	public void expedientIndexLuceneUpdate(
			String processInstanceId,
			boolean perTasca,
			Expedient expedientDeLaTasca) {
		Timer.Context contextTotal = null;
		Timer.Context contextEntorn = null;
		Timer.Context contextTipexp = null;
		if (perTasca) {
			final Timer timerTotal = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar"));
			contextTotal = timerTotal.time();
			Counter countTotal = metricRegistry.counter(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar.count"));
			countTotal.inc();
			final Timer timerEntorn = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar",
							expedientDeLaTasca.getEntorn().getCodi()));
			contextEntorn = timerEntorn.time();
			Counter countEntorn = metricRegistry.counter(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar.count",
							expedientDeLaTasca.getEntorn().getCodi()));
			countEntorn.inc();
			final Timer timerTipexp = metricRegistry.timer(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar",
							expedientDeLaTasca.getEntorn().getCodi(),
							expedientDeLaTasca.getTipus().getCodi()));
			contextTipexp = timerTipexp.time();
			Counter countTipexp = metricRegistry.counter(
					MetricRegistry.name(
							TascaService.class,
							"completar.reindexar.count",
							expedientDeLaTasca.getEntorn().getCodi(),
							expedientDeLaTasca.getTipus().getCodi()));
			countTipexp.inc();
		}
		
		// Timers LUCENE
		Timer.Context contextDades = null;
		Timer.Context contextIndexar = null;
		final Timer timerDades = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.update.dades"));
		contextDades = timerDades.time();
		Counter countDades = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.update.dades.count"));
		countDades.inc();
		final Timer timerIndexar = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.update.indexar"));
		contextIndexar = timerIndexar.time();
		Counter countIndexar = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.update.indexar.count"));
		countIndexar.inc();
		boolean ctxDadesStoped = false;
				
		try {
			JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(rootProcessInstance.getId());
			Map<String, Set<Camp>> mapCamps = getMapCamps(rootProcessInstance.getId());
			Map<String, Map<String, Object>> mapValors = getMapValors(rootProcessInstance.getId());
			contextDades.stop();
			ctxDadesStoped = true;
			luceneHelper.updateExpedientCamps(
					expedient,
					getMapDefinicionsProces(rootProcessInstance.getId()),
					mapCamps,
					mapValors,
					getMapValorsDomini(mapCamps, mapValors),
					isExpedientFinalitzat(expedient));
		} finally {
			if (perTasca) {
				contextTotal.stop();
				contextEntorn.stop();
				contextTipexp.stop();
			}
			if (!ctxDadesStoped)
				contextDades.stop();
			contextIndexar.stop();
		}
	}

	public void expedientIndexLuceneDelete(String processInstanceId) {
		// Timers LUCENE
		Timer.Context contextDades = null;
		Timer.Context contextIndexar = null;
		final Timer timerDades = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.delete.dades"));
		contextDades = timerDades.time();
		Counter countDades = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.delete.dades.count"));
		countDades.inc();
		final Timer timerIndexar = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.delete.indexar"));
		contextIndexar = timerIndexar.time();
		Counter countIndexar = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.delete.indexar.count"));
		countIndexar.inc();
		boolean ctxDadesStoped = false;
		
		try {
			Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(processInstanceId);
			contextDades.stop();
			ctxDadesStoped = true;
			luceneHelper.deleteExpedient(expedient);
		} finally {
			if (!ctxDadesStoped)
				contextDades.stop();
			contextIndexar.stop();
		}
	}

	public void expedientIndexLuceneRecrear(Expedient expedient) {
		// Timers LUCENE
		Timer.Context contextDades = null;
		Timer.Context contextIndexar = null;
		final Timer timerDades = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.recreate.dades"));
		contextDades = timerDades.time();
		Counter countDades = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.recreate.dades.count"));
		countDades.inc();
		final Timer timerIndexar = metricRegistry.timer(
				MetricRegistry.name(
						LuceneHelper.class, 
						"lucene.recreate.indexar"));
		contextIndexar = timerIndexar.time();
		Counter countIndexar = metricRegistry.counter(
				MetricRegistry.name(
						LuceneHelper.class,
						"lucene.recreate.indexar.count"));
		countIndexar.inc();
		boolean ctxDadesStoped = false;
		
		try {
			Map<String, Set<Camp>> mapCamps = getMapCamps(expedient.getProcessInstanceId());
			Map<String, Map<String, Object>> mapValors = getMapValors(expedient.getProcessInstanceId());
			contextDades.stop();
			ctxDadesStoped = true;
			luceneHelper.deleteExpedient(expedient);
			luceneHelper.createExpedient(
					expedient,
					getMapDefinicionsProces(expedient.getProcessInstanceId()),
					mapCamps,
					mapValors,
					getMapValorsDomini(mapCamps, mapValors),
					isExpedientFinalitzat(expedient),
					false);
		} finally {
			if (!ctxDadesStoped)
				contextDades.stop();
			contextIndexar.stop();
		}
	}

	public Object getVariableJbpmProcesValor(
			String processInstanceId,
			String varCodi) {
		Object valor = jbpmHelper.getProcessInstanceVariable(processInstanceId, varCodi);
		if (valor instanceof DominiCodiDescripcio) {
			return ((DominiCodiDescripcio)valor).getCodi();
		} else {
			return valor;
		}
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
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree) {
			resposta.put(
					pi.getId(),
					definicioProcesRepository.findByJbpmId(pi.getProcessDefinitionId()).getCamps());
		}
		return resposta;
	}
	
	private Map<String, Map<String, Object>> getMapValors(String processInstanceId) {
		Map<String, Map<String, Object>> resposta = new HashMap<String, Map<String, Object>>();
		List<JbpmProcessInstance> tree = jbpmHelper.getProcessInstanceTree(processInstanceId);
		for (JbpmProcessInstance pi: tree)
			resposta.put(
					pi.getId(),
					getVariablesJbpmProcesValor(pi.getId()));
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

}
