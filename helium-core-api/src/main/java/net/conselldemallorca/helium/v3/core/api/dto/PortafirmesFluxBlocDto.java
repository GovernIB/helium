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
//	private String[] destinataris;
	private List<PersonaDto> destinataris;

	public PortafirmesFluxBlocDto () {
		
	}
	
	public PortafirmesFluxBlocDto (int minSignataris, List<PersonaDto> destinataris ) {
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
	public List<PersonaDto> getDestinataris() {
		return destinataris;
	}
	public void setDestinataris(List<PersonaDto> destinataris) {
		this.destinataris = destinataris;
	}


}
