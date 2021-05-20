/**
 * 
 */
package es.caib.helium.jbpm3.handlers;

import es.caib.helium.jbpm3.integracio.Termini;
import net.conselldemallorca.helium.jbpm3.handlers.TerminiVariableGuardarHandlerInterface;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Handler per a configurar una variable de tipus termini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class TerminiVariableGuardarHandler extends AbstractHeliumActionHandler implements TerminiVariableGuardarHandlerInterface {

	private String varTermini;
	private String anys;
	private String varAnys;
	private String mesos;
	private String varMesos;
	private String dies;
	private String varDies;



	public void execute(ExecutionContext executionContext) throws Exception {
		if (varTermini == null)
			throw new JbpmException("S'han d'especificar la variable a on guardar el termini");
		Integer a = getValorOVariableInteger(executionContext, anys, varAnys);
		Integer m = getValorOVariableInteger(executionContext, mesos, varMesos);
		Integer d = getValorOVariableInteger(executionContext, dies, varDies);
		if (a != null && m != null && d != null) {
			Termini termini = new Termini();
			termini.setAnys(a.intValue());
			termini.setMesos(m.intValue());
			termini.setDies(d.intValue());
			executionContext.setVariable(varTermini, termini);
		} else {
			throw new JbpmException("S'han d'especificar anys, mesos i dies");
		}
	}



	public void setVarTermini(String varTermini) {
		this.varTermini = varTermini;
	}
	public void setAnys(String anys) {
		this.anys = anys;
	}
	public void setVarAnys(String varAnys) {
		this.varAnys = varAnys;
	}
	public void setMesos(String mesos) {
		this.mesos = mesos;
	}
	public void setVarMesos(String varMesos) {
		this.varMesos = varMesos;
	}
	public void setDies(String dies) {
		this.dies = dies;
	}
	public void setVarDies(String varDies) {
		this.varDies = varDies;
	}

}
