/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * DTO amb informació del camp d'una variable de tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampRegistreDto {

	private Long id;
	private Long registreId;
	private Long membreId;
	private String membreCodi;
	private String membreEtiqueta;
	private CampTipusDto membreTipus;
	private boolean obligatori;
	private boolean llistar;
	private int ordre;
	
	public CampRegistreDto() {
		
	}
	
	public CampRegistreDto(Long id, boolean obligatori, boolean llistar, int ordre, Long registreId, Long membreId) {
		super();
		this.id = id;
		this.obligatori = obligatori;
		this.llistar = llistar;
		this.ordre = ordre;
		this.registreId = registreId;
		this.membreId = membreId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public Long getMembreId() {
		return membreId;
	}
	public void setMembreId(Long membreId) {
		this.membreId = membreId;
	}
	public String getMembreCodi() {
		return membreCodi;
	}
	public void setMembreCodi(String membreCodi) {
		this.membreCodi = membreCodi;
	}
	public String getMembreEtiqueta() {
		return membreEtiqueta;
	}
	public void setMembreEtiqueta(String membreEtiqueta) {
		this.membreEtiqueta = membreEtiqueta;
	}
	public CampTipusDto getMembreTipus() {
		return membreTipus;
	}
	public void setMembreTipus(CampTipusDto membreTipus) {
		this.membreTipus = membreTipus;
	}
	public boolean isObligatori() {
		return obligatori;
	}
	public void setObligatori(boolean obligatori) {
		this.obligatori = obligatori;
	}
	public boolean isLlistar() {
		return llistar;
	}
	public void setLlistar(boolean llistar) {
		this.llistar = llistar;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}	
}
