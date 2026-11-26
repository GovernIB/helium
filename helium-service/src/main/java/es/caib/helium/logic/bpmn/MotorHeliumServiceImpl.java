package es.caib.helium.logic.bpmn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import es.caib.comanda.model.management.TascaEstat;
import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.engine.WTaskInstance;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.intf.bpmn.MotorHeliumService;
import es.caib.helium.bpmn.service.BpmnHeliumService;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.logic.helper.ComandaHelper;
import es.caib.helium.logic.helper.ExpedientDadaHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.ExpedientTipusRecursHelper;
import es.caib.helium.logic.helper.PluginHelper;
import es.caib.helium.logic.intf.service.TascaService;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;

/**
 * Serveis de Helium per a la implementació del motor BPMN 2.0 ({@link MotorHeliumService}). Les dependències
 * s'injecten en diferit perquè els helpers de Helium també depenen del motor.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MotorHeliumServiceImpl implements MotorHeliumService {

	@Autowired @Lazy
	private ExpedientDadaHelper expedientDadaHelper;
	@Autowired @Lazy
	private BpmnHeliumService bpmnHeliumService;
	@Autowired @Lazy
	private HeliumHandlerFactory heliumHandlerFactory;
	@Autowired @Lazy
	private ExpedientHelper expedientHelper;
	@Autowired @Lazy
	private DefinicioProcesRepository definicioProcesRepository;
	@Autowired @Lazy
	private ExpedientTipusRecursHelper expedientTipusRecursHelper;
	@Autowired @Lazy
	private PluginHelper pluginHelper;
	@Autowired @Lazy
	private TascaService tascaService;
	@Autowired @Lazy
	private ComandaHelper comandaHelper;

	@Override
	public Map<String, Object> getDadesProces(String processInstanceId) {
		return expedientDadaHelper.getDadesValors(null, processInstanceId, null);
	}

	@Override
	public HeliumApi getHeliumApi(String processInstanceId) {
		return bpmnHeliumService.buildHeliumApi(processInstanceId, null);
	}

	@Override
	public Class<? extends HeliumBpmnHandler> resoldreClasseHandler(
			String classe,
			String processInstanceId,
			String processDefinitionId) {
		// El context dels recursos només es calcula si la classe no és a l'aplicació (handlers propis)
		return heliumHandlerFactory.resoldreClasse(classe, () -> {
			Long expedientTipusId = expedientHelper
				.findExpedientByProcessInstanceId(processInstanceId)
				.getTipus()
				.getId();
			DefinicioProces definicioProces = definicioProcesRepository.findByBpmId(processDefinitionId);
			return new HeliumHandlerFactory.ContextRecursos(
				expedientTipusId,
				definicioProces != null ? definicioProces.getId() : null);
		});
	}

	@Override
	public byte[] getRecursDefinicioProces(String processDefinitionId, String recursNom) {
		return expedientTipusRecursHelper.loadResource(processDefinitionId, recursNom);
	}

	@Override
	public List<String> getUsuarisGrup(String grupCodi) {
		return pluginHelper.personaFindAmbGrup(grupCodi)
			.stream()
			.map(PersonaDto::getCodi)
			.collect(Collectors.toList());
	}

	@Override
	public void tascaCreada(String taskId) {
		tascaService.refrescar(taskId);
	}

	@Override
	public void tascaEstatModificat(WTaskInstance tasca, EstatTasca estat) {
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(tasca.getProcessInstanceId());
		comandaHelper.upsertTasca(
			tasca.getId(),
			tasca.getName(),
			expedient != null ? expedient.getNumero() : "",
			null,
			tasca,
			estat != null ? TascaEstat.valueOf(estat.name()) : null);
	}

}
