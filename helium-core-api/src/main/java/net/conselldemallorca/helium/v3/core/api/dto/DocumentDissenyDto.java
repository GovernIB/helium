/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;



/**
 * DTO amb informació d'un document de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentDissenyDto {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private boolean plantilla;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;
	private boolean adjuntarAuto;
	private String convertirExtensio;
	private String extensionsPermeses;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}
	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}
	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}
	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}
	public String getConvertirExtensio() {
		return convertirExtensio;
	}
	public void setConvertirExtensio(String convertirExtensio) {
		this.convertirExtensio = convertirExtensio;
	}
	public String getExtensionsPermeses() {
		return extensionsPermeses;
	}
	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
	}

}
