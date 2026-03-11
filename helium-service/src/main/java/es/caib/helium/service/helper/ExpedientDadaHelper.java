/**
 * 
 */
package es.caib.helium.service.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.Camp.TipusCamp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;

/**
 * Helper per les dades d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@Component
public class ExpedientDadaHelper {
	
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
//		JbpmProcessDefinition jpd = workflowEngineApi.findProcessDefinitionWithProcessInstanceId(processInstanceId);
//		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
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

}
