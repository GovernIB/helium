/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

/**
 * Descripció d'una passa per a la signatura del document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PasSignatura {

	private int minSignataris = 0;
	private String[] signataris;



	public int getMinSignataris() {
		return minSignataris;
	}
	public void setMinSignataris(int minSignataris) {
		this.minSignataris = minSignataris;
	}
	public String[] getSignataris() {
		return signataris;
	}
	public void setSignataris(String[] signataris) {
		this.signataris = signataris;
	}

}
