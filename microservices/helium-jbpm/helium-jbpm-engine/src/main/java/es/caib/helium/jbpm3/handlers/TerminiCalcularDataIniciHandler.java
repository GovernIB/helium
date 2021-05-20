/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import java.util.Calendar;
import java.util.Date;

import es.caib.helium.api.dto.TerminiDto;
import es.caib.helium.jbpm3.integracio.Jbpm3HeliumBridge;
import es.caib.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.jbpm3.handlers.TerminiCalcularDataIniciHandlerInterface;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;


/**
 * Handler per calcular la data d'inici d'un termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiCalcularDataIniciHandler extends AbstractHeliumActionHandler implements TerminiCalcularDataIniciHandlerInterface {

	private String terminiCodi;
	private String varTerminiCodi;
	private String varData;
	private String restarUnDia;
	private String varTermini;
	private String varDataInici;



	public void execute(ExecutionContext executionContext) throws Exception {
		TerminiDto termini = getTerminiAmbCodi(
				executionContext,
				(String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi));
		if (termini != null) {
			Date dataInici;
			if (varTermini != null) {
				Object valorTermini = executionContext.getVariable(varTermini);
				if (valorTermini == null)
					throw new JbpmException("No s'ha pogut llegir el termini de la variable '" + varTermini + "'");
				
				Termini vt = null;
				if (valorTermini instanceof Termini) {
					vt = (Termini)valorTermini;
				} else {
					vt = Termini.valueFromString((String) valorTermini);
				}
				
				dataInici = Jbpm3HeliumBridge.getInstanceService().terminiCalcularDataInici(
						getDataFi(executionContext),
						vt.getAnys(),
						vt.getMesos(),
						vt.getDies(),
						termini.isLaborable(),
						String.valueOf(executionContext.getProcessInstance().getId()));
			} else {
				dataInici = Jbpm3HeliumBridge.getInstanceService().terminiCalcularDataInici(
						getDataFi(executionContext),
						termini.getAnys(),
						termini.getMesos(),
						termini.getDies(),
						termini.isLaborable(),
						String.valueOf(executionContext.getProcessInstance().getId()));
			}
			if (executionContext.getTaskInstance() != null)
				executionContext.getTaskInstance().setVariableLocally(varDataInici, dataInici);
			else
				executionContext.setVariable(varDataInici, dataInici);
		} else {
			throw new JbpmException("No existeix cap termini amb aquest codi '" + (String)getValorOVariable(executionContext, terminiCodi, varTerminiCodi) + "'");
		}
	}

	public void setTerminiCodi(String terminiCodi) {
		this.terminiCodi = terminiCodi;
	}
	public void setVarTerminiCodi(String varTerminiCodi) {
		this.varTerminiCodi = varTerminiCodi;
	}
	public void setVarData(String varData) {
		this.varData = varData;
	}
	public void setRestarUnDia(String restarUnDia) {
		this.restarUnDia = restarUnDia;
	}
	public void setVarTermini(String varTermini) {
		this.varTermini = varTermini;
	}
	public void setVarDataInici(String varDataInici) {
		this.varDataInici = varDataInici;
	}



	private Date getDataFi(ExecutionContext executionContext) {
		Date data;
		if (varData != null && varData.length() > 0) {
			data = getVariableComData(executionContext, varData);
		} else {
			data = new Date();
		}
		if (restarUnDia != null && restarUnDia.length() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			return cal.getTime();
		} else {
			return data;
		}
	}

}
