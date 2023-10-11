package net.conselldemallorca.helium.v3.core.api.dto;

/** Classe per obtenir el resultat d'una petició de firma per passarel·la web.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class FirmaResultatDto {
	
	
	public FirmaResultatDto(
			StatusEnumDto status,
			String fitxerFirmatNom,
			byte[] fitxerFirmatContingut) {
		this.status = status;
		this.fitxerFirmatNom = fitxerFirmatNom;
		this.fitxerFirmatContingut = fitxerFirmatContingut;
	}
	
	public FirmaResultatDto(
			StatusEnumDto status,
			String msg) {
		this.status = status;
		this.msg = msg;
	}

	private StatusEnumDto status;
	private String msg;
	private String fitxerFirmatNom; 
	private byte[] fitxerFirmatContingut;
	
	
	public StatusEnumDto getStatus() {
		return status;
	}
	public String getMsg() {
		return msg;
	}
	public String getFitxerFirmatNom() {
		return fitxerFirmatNom;
	}
	public byte[] getFitxerFirmatContingut() {
		return fitxerFirmatContingut;
	}
}
