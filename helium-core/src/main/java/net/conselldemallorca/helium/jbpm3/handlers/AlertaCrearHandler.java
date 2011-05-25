/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.hibernate.Alerta;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a crear una alerta a un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class AlertaCrearHandler extends AbstractHeliumActionHandler implements AlertaCrearHandlerInterface {

	private String usuari;
	private String varUsuari;
	private String text;
	private String varText;



	public void execute(ExecutionContext executionContext) throws Exception {
		ExpedientDto expedient = getExpedient(executionContext);
		Alerta alerta = new Alerta(
				new Date(),
				(String)getValorOVariable(executionContext, usuari, varUsuari),
				(String)getValorOVariable(executionContext, text, varText),
				expedient.getEntorn());
		alerta.setExpedient(expedient);
		getAlertaService().create(alerta);
	}



	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public void setVarUsuari(String varUsuari) {
		this.varUsuari = varUsuari;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setVarText(String varText) {
		this.varText = varText;
	}

}
