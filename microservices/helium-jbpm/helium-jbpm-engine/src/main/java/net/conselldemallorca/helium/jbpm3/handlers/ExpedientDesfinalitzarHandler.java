package net.conselldemallorca.helium.jbpm3.handlers;

import es.caib.helium.api.dto.ExpedientDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import org.jbpm.graph.exe.ExecutionContext;

public class ExpedientDesfinalitzarHandler extends AbstractHeliumActionHandler implements ExpedientDesfinalitzarHandlerInterficie {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String reprendre;
	private String varReprendre;
	
	public void setReprendre(String reprendre) {
		this.reprendre = reprendre;
		
	}

	public void setVarReprendre(String varReprendre) {
		this.varReprendre = varReprendre;
		
	}

	public void execute(ExecutionContext executionContext) throws Exception {
		Boolean reprend = getValorOVariableBoolean(
                executionContext,
                reprendre,
                varReprendre);

		ExpedientDto expedient = getExpedientActual(executionContext);
		Jbpm3HeliumBridge.getInstanceService().desfinalitzarExpedient(expedient.getProcessInstanceId());
		
		if(reprend != null && reprend)
			Jbpm3HeliumBridge.getInstanceService().expedientReprendre(expedient.getProcessInstanceId());
	}

}
