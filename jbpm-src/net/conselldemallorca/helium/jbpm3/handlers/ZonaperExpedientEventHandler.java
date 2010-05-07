/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers;

import java.util.Date;

import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.util.ExpedientIniciant;

import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a afegir un event a un expedient de la zona
 * personal del ciutadà.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@SuppressWarnings("serial")
public class ZonaperExpedientEventHandler extends AbstractHeliumActionHandler {

	private String titol;
	private String varTitol;
	private String text;
	private String varText;
	private String textSms;
	private String varTextSms;
	private String enllasConsulta;
	private String varEnllasConsulta;
	private Date data;
	private String varData;



	public void execute(ExecutionContext executionContext) throws Exception {
		Expedient ex = ExpedientIniciant.getExpedient();
		if (ex != null) {
			getSistraService().zonaperExpedientEvent(
					ex,
					(String)getValorOVariable(executionContext, titol, varTitol),
					(String)getValorOVariable(executionContext, text, varText),
					(String)getValorOVariable(executionContext, textSms, varTextSms),
					(String)getValorOVariable(executionContext, enllasConsulta, varEnllasConsulta),
					(Date)getValorOVariable(executionContext, data, varData));
		} else {
			ExpedientDto expedient = getExpedient(executionContext);
			getSistraService().zonaperExpedientEvent(
					expedient,
					(String)getValorOVariable(executionContext, titol, varTitol),
					(String)getValorOVariable(executionContext, text, varText),
					(String)getValorOVariable(executionContext, textSms, varTextSms),
					(String)getValorOVariable(executionContext, enllasConsulta, varEnllasConsulta),
					(Date)getValorOVariable(executionContext, data, varData));
		}
	}

	public void setTitol(String titol) {
		this.titol = titol;
	}
	public void setVarTitol(String varTitol) {
		this.varTitol = varTitol;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setVarText(String varText) {
		this.varText = varText;
	}
	public void setTextSms(String textSms) {
		this.textSms = textSms;
	}
	public void setVarTextSms(String varTextSms) {
		this.varTextSms = varTextSms;
	}
	public void setEnllasConsulta(String enllasConsulta) {
		this.enllasConsulta = enllasConsulta;
	}
	public void setVarEnllasConsulta(String varEnllasConsulta) {
		this.varEnllasConsulta = varEnllasConsulta;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}

}
