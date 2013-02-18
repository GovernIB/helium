/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;

import org.springframework.stereotype.Service;

/**
 * Servei per gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("tascaServiceV3")
public class TascaServiceImpl implements TascaService {

	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public List<CampTascaDto> findCampsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException {
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		String processDefinitionId = task.getProcessDefinitionId();
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(processDefinitionId);
		Tasca tasca = tascaRepository.findByNomAndDefinicioProces(
				task.getName(),
				definicioProces);
		return conversioTipusHelper.convertirLlista(
				tasca.getCamps(),
				CampTascaDto.class);
	}
	public List<DocumentTascaDto> findDocumentsPerTaskInstance(
			long taskInstanceId) throws TaskInstanceNotFoundException {
		JbpmTask task = jbpmHelper.getTaskById(new Long(taskInstanceId).toString());
		String processDefinitionId = task.getProcessDefinitionId();
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(processDefinitionId);
		Tasca tasca = tascaRepository.findByNomAndDefinicioProces(
				task.getName(),
				definicioProces);
		return conversioTipusHelper.convertirLlista(
				tasca.getDocuments(),
				DocumentTascaDto.class);
	}

}
