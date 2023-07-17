/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

public class PortafirmesFluxBloc {

	private int minSignataris = 0;
	private boolean[] obligatorietats;
	private String[] destinataris;



	public int getMinSignataris() {
		return minSignataris;
	}
	public void setMinSignataris(int minSignataris) {
		this.minSignataris = minSignataris;
	}
	public boolean[] getObligatorietats() {
		return obligatorietats;
	}
	public void setObligatorietats(boolean[] obligatorietats) {
		this.obligatorietats = obligatorietats;
	}
	public String[] getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(String[] destinataris) {
		this.destinataris = destinataris;
	}

}
