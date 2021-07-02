/**
 * 
 */
package es.caib.helium.integracio.plugins.tramitacio;


/**
 * 
 * @author Limit Tecnologies
 */
public class ObtenirDadesTramitRequest {

	protected String numero;
	protected String clau;

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getClau() {
		return clau;
	}
	public void setClau(String clau) {
		this.clau = clau;
	}

	@Override
	public String toString() {
		return "[" + numero + ", " + clau + "]";
	}

}
