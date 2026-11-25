package es.caib.helium.logic.bpmn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import es.caib.helium.logic.helper.ConsultaPinbalHelper;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import es.caib.helium.persistence.repository.ExpedientRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import lombok.Getter;

/**
 * Serveis que fa servir {@link HeliumApiImpl} per a les operacions dels handlers predefinits, a més dels helpers que
 * rep pel constructor. S'injecten en diferit per no crear dependències circulars amb els helpers que creen la
 * {@code HeliumApi}.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Component
public class HeliumApiServeis {

	@Autowired @Lazy
	private WorkflowEngineApi workflowEngineApi;
	@Autowired @Lazy
	private ExpedientRepository expedientRepository;
	@Autowired @Lazy
	private ExpedientTipusRepository expedientTipusRepository;
	@Autowired @Lazy
	private ConsultaPinbalHelper consultaPinbalHelper;

}
