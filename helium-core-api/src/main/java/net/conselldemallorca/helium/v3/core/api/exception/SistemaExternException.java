/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.exception;

/**
 * Excepció que es llança si hi ha algun error crindant al plugin.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends HeliumException {
	private String codiSistemaExtern;
	private String missatgeError;
	private Exception excepcioEncapsulada;
	
	public SistemaExternException() {
		super();
	}
	public SistemaExternException(Long entornId, String entornCodi,
			String entornNom, Long expedientId, String expedientTitol,
			String expedientNumero, Long expedientTipusId,
			String expedientTipusCodi, String expedientTipusNom) {
		super(entornId, entornCodi, entornNom, expedientId, expedientTitol,
				expedientNumero, expedientTipusId, expedientTipusCodi,
				expedientTipusNom);
	}
	public SistemaExternException(Long entornId, String entornCodi,
			String entornNom, Long expedientId, String expedientTitol,
			String expedientNumero, Long expedientTipusId,
			String expedientTipusCodi, String expedientTipusNom,
			String codiSistemaExtern,
			String missatgeError, Exception excepcioEncapsulada) {
		super(entornId, entornCodi, entornNom, expedientId, expedientTitol,
				expedientNumero, expedientTipusId, expedientTipusCodi,
				expedientTipusNom);
		this.codiSistemaExtern = codiSistemaExtern;
		this.missatgeError = missatgeError;
		this.excepcioEncapsulada = excepcioEncapsulada;
	}
	public SistemaExternException(String codiSistemaExtern,
			String missatgeError, Exception excepcioEncapsulada) {
		super();
		this.codiSistemaExtern = codiSistemaExtern;
		this.missatgeError = missatgeError;
		this.excepcioEncapsulada = excepcioEncapsulada;
	}
	public String getCodiSistemaExtern() {
		return codiSistemaExtern;
	}
	public void setCodiSistemaExtern(String codiSistemaExtern) {
		this.codiSistemaExtern = codiSistemaExtern;
	}
	public String getMissatgeError() {
		return missatgeError;
	}
	public void setMissatgeError(String missatgeError) {
		this.missatgeError = missatgeError;
	}
	public Exception getExcepcioEncapsulada() {
		return excepcioEncapsulada;
	}
	public void setExcepcioEncapsulada(Exception excepcioEncapsulada) {
		this.excepcioEncapsulada = excepcioEncapsulada;
	}
}
