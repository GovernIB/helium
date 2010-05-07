/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear expedients a dins la zona personal del
 * ciutadà.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientCrearHandler extends AbstractHeliumActionHandler {

	private String descripcio;
	private String varDescripcio;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
		if (ex != null) {
			String d = (String)getValorOVariable(executionContext, descripcio, varDescripcio);
			getSistraService().zonaperExpedientIniciar(ex, d);
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			String d = (String)getValorOVariable(executionContext, descripcio, varDescripcio);
			getSistraService().zonaperExpedientIniciar(expedient, d);
		}
		
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public void setVarDescripcio(String varDescripcio) {
		this.varDescripcio = varDescripcio;
	}

}
