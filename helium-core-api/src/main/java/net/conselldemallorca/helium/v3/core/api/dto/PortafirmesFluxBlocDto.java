/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.List;

/**
 * Descripció d'un pas per a la signatura del document
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesFluxBlocDto {

	private int minSignataris = 0;
	private boolean[] obligatorietats;
	private List<String> destinataris;

	public PortafirmesFluxBlocDto () {
		
	}
	
	public PortafirmesFluxBlocDto (int minSignataris, List<String> destinataris ) {
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
	public List<String> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<String> destinataris) {
		this.destinataris = destinataris;
	}


}
