/**
 * 
 */
package net.conselldemallorca.helium.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;

/**
 * Helper per les dades d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
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
	@Resource
	private JbpmHelper jbpmHelper;
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
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp;
		if (expedientTipus.isAmbInfoPropia())
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(), 
					varName,
					expedientTipus.getExpedientTipusPare() != null);
		else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
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
					
					jbpmHelper.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		jbpmHelper.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}

}
