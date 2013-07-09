/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per modificar la georeferència de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class ExpedientGeorefModificarHandler extends AbstractHeliumActionHandler {

	private String posx;
	private String varPosx;
	private String posy;
	private String varPosy;
	private String referencia;
	private String varReferencia;



	public void execute(ExecutionContext executionContext) throws Exception {
		logger.debug("Inici execució handler modificació geo-ref expedient");
		String posx = (String)getValorOVariable(
				executionContext,
				this.posx,
				varPosx);
		String posy = (String)getValorOVariable(
				executionContext,
				this.posy,
				varPosy);
		String referencia = (String)getValorOVariable(
				executionContext,
				this.referencia,
				varReferencia);
		ExpedientDto expedient = getExpedientActual(executionContext);
		logger.debug("Modificant geo-ref de l'expedient (exp=" + expedient.getIdentificacioPerLogs() + ", posx=" + posx + ", posy=" + posy + ", referencia=" + referencia + ")");
		try {
			Jbpm3HeliumBridge.getInstanceService().expedientModificarGeoref(
					getProcessInstanceId(executionContext),
					new Double(posx),
					new Double(posy),
					referencia);
		} catch (Exception ex) {
			throw new JbpmException("Error al modificar l'expedient", ex);
		}
		logger.debug("Handler modificació geo-ref finalitzat amb èxit");
	}

	public void setPosx(String posx) {
		this.posx = posx;
	}
	public void setVarPosx(String varPosx) {
		this.varPosx = varPosx;
	}
	public void setPosy(String posy) {
		this.posy = posy;
	}
	public void setVarPosy(String varPosy) {
		this.varPosy = varPosy;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public void setVarReferencia(String varReferencia) {
		this.varReferencia = varReferencia;
	}

	private static final Log logger = LogFactory.getLog(ExpedientGeorefModificarHandler.class);

}
