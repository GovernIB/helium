/**
 * 
 */
package es.caib.helium.service.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.commons.dades.DadesValor;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.Camp.TipusCamp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDades;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.ExpedientDadesRepository;

/**
 * Helper per les dades d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientDadaHelper {
	
	@Resource
	private ExpedientDadesRepository expedientDadesRepository;
	@Resource
	private CampRepository campRepository;

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
//	@Resource
//	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private VariableHelper variableHelper;

	// Objectes estàtics per fer la conversió JSON/Map
	private static ObjectMapper mapper; 
    private static TypeReference<HashMap<String,DadesValor>> typeRef;
	static {
		mapper = new ObjectMapper();
		typeRef = new TypeReference<HashMap<String,DadesValor>>() {};
	}

	public List<Camp> findCampsDisponiblesOrdenatsPerCodi(ExpedientTipus expedientTipus, DefinicioProces definicioProces) {
		if (expedientTipus.isAmbInfoPropia()) {
			return campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
		} else {
			return campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		}
	}
	
	public void optimitzarValorPerConsultesDominiGuardar(
			ExpedientTipus expedientTipus, 
			String processInstanceId,
			String varName,
			Object varValue) {
		Camp camp;
		if (expedientTipus.isAmbInfoPropia())
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(), 
					varName,
					expedientTipus.getExpedientTipusPare() != null);
		else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					null, //definicioProces,
					varName);			
		}
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text;
					try {
						// Consultem el valor de la variable
						text = variableHelper.getTextPerCamp(
								camp, 
								varValue, 
								null, 
								null,
								processInstanceId);
					} catch (Exception e) {
						text = "";
					}
					
//					workflowEngineApi.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
//		workflowEngineApi.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}
	

	/** Estableix el valor per una dada de l'expedient
	 * 
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 * @param varValor
	 */
	@Transactional
	public void setDada(Expedient expedient, String processId, String taskId, String varCodi, Object varValor) {
		
		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			expedientDadesEntity = new ExpedientDades();
			expedientDadesEntity.setExpedient(expedient);
			expedientDadesEntity.setExpedientTipus(expedient.getTipus());
			expedientDadesEntity.setProcessId(processId);
			expedientDadesEntity.setTaskId(taskId);
			expedientDadesRepository.save(expedientDadesEntity);
		}
		// Fixa el valor de la dada
		this.optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processId, varCodi, varValor);

		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
			} else {
				dades = new HashMap<String, DadesValor>();
			}
			dades.put(varCodi, new DadesValor(varValor, "s"));
			expedientDadesEntity.setDades(MapToDades(dades));
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}
	
	/** Obté el valor per una dada de l'expedient
	 * 
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 */
	@Transactional
	public Object getDada(Expedient expedient, String processId, String taskId, String varCodi) {
		
		Object valor = null;
		
		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return valor;
		}
		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
				if (dades.containsKey(varCodi)) {
					valor = dades.get(varCodi).getV();
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valor;
	}

	/** Obté tots els valors de les variables l'expedient
	 * 
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 */
	@Transactional
	public Map<String, Object> getDadesValors(Expedient expedient, String processId, String taskId) {
		
		Map<String, Object> valors = new HashMap<String, Object>();
		
		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return valors;
		}
		try {
			if (expedientDadesEntity.getDades() != null) {
				Map<String, DadesValor> dades = DadesToMap(expedientDadesEntity.getDades());
				for (String varCodi : dades.keySet()) {
					valors.put(varCodi, dades.get(varCodi).getV());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valors;
	}

	
	private String MapToDades(Map<String, DadesValor> dades) throws Exception {
		String json = mapper.writeValueAsString(dades);
		return json;
	}

	private Map<String, DadesValor> DadesToMap(String dades) throws Exception {
	    HashMap<String,DadesValor> map = mapper.readValue(dades, typeRef);
	    return map;
	}

	/** Consulta les dades segons la tasca, procés i expedient. */
	private ExpedientDades getDades(Expedient expedient, String processId, String taskId) {
		ExpedientDades expedientDades = null;
		if (taskId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndTaskId(expedient, taskId);
		} else if (processId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndProcessId(expedient, processId);
		} else {
			expedientDades = expedientDadesRepository.findByExpedient(expedient);
		}
		return expedientDades;
	}

	/** Mètode per esborrar una dada de l'expedient donat el seu codi. */
	public void deleteDada(Expedient expedient, String processId, String taskId, String varCodi) {
		
		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return;
		}
		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
				if (dades.containsKey(varCodi)) {
					dades.remove(varCodi);
				}
				expedientDadesEntity.setDades(MapToDades(dades));
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}

}
