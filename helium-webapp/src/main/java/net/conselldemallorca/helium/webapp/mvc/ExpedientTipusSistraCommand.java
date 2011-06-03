/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

/**
 * Command per iniciar un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusSistraCommand {

	private Long expedientTipusId;
	private boolean actiu;
	private String codiTramit;
	//private String infoMapeigCamps;
	/*private List<MapeigSistra> mapeigSistras;
	private String infoMapeigDocuments;
	private String infoMapeigAdjunts;
	
	private String codiHelium;
	private String codiSistra;
	private TipusMapeig tipus;*/



	public ExpedientTipusSistraCommand() {}

	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

	public String getCodiTramit() {
		return codiTramit;
	}
	public void setCodiTramit(String codiTramit) {
		this.codiTramit = codiTramit;
	}

	/*public List<MapeigSistra> getMapeigSistras() {
		return this.mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistra> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}
	public void addMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().add(mapeigSistra);
	}
	public void removeMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().remove(mapeigSistra);
	}

	public String getInfoMapeigDocuments() {
		return infoMapeigDocuments;
	}
	public void setInfoMapeigDocuments(String infoMapeigDocuments) {
		this.infoMapeigDocuments = infoMapeigDocuments;
	}

	public String getInfoMapeigAdjunts() {
		return infoMapeigAdjunts;
	}
	public void setInfoMapeigAdjunts(String infoMapeigAdjunts) {
		this.infoMapeigAdjunts = infoMapeigAdjunts;
	}

	public String getCodiHelium() {
		return codiHelium;
	}
	public void setCodiHelium(String codiHelium) {
		this.codiHelium = codiHelium;
	}

	public String getCodiSistra() {
		return codiSistra;
	}
	public void setCodiSistra(String codiSistra) {
		this.codiSistra = codiSistra;
	}

	public TipusMapeig getTipus() {
		return tipus;
	}
	public void setTipus(TipusMapeig tipus) {
		this.tipus = tipus;
	}
*/
	
}
