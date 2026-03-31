/**
 * 
 */
package es.caib.helium.integracio.plugins.portasignatures;

import java.util.List;

/**
 * Descripció d'un pas per a la signatura del document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesFluxBloc {

	private int minSignataris = 0;
	private boolean[] obligatorietats;
	private String[] destinataris;//signataris
	
	public PortafirmesFluxBloc () {
		
	}
	
	public PortafirmesFluxBloc (int minSignataris, String[] destinataris ) {
		this.setMinSignataris(minSignataris);
		this.setDestinataris(destinataris);
	}
	
	
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
