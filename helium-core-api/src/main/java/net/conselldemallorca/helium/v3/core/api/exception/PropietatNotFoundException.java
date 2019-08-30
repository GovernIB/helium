/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es produeix al accedir a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class PropietatNotFoundException extends RuntimeException {

	private String propietatCodi;

	public PropietatNotFoundException(
			String propietatCodi) {
		super(crearMessage(propietatCodi));
		this.propietatCodi = propietatCodi;
	}

	public String getPropietatCodi() {
		return propietatCodi;
	}

	private static String crearMessage(String propietatCodi) {
		return "La propietat \"" + propietatCodi + "\" no està definida o no te valor";
	}

}
