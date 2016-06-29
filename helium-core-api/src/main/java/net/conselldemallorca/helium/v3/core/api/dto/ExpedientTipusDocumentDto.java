/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientTipusDocumentDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private boolean plantilla;
	private String convertirExtensio;
	private boolean adjuntarAuto;
	private CampDto campData;
	private String extensionsPermeses;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;


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


	public String getArxiuNom() {
		return arxiuNom;
	}


	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}


	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}


	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}


	public boolean isPlantilla() {
		return plantilla;
	}


	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}


	public String getConvertirExtensio() {
		return convertirExtensio;
	}


	public void setConvertirExtensio(String convertirExtensio) {
		this.convertirExtensio = convertirExtensio;
	}


	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}


	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}


	public CampDto getCampData() {
		return campData;
	}


	public void setCampData(CampDto campData) {
		this.campData = campData;
	}


	public String getExtensionsPermeses() {
		return extensionsPermeses;
	}


	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
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


	private static final long serialVersionUID = 774909297938469787L;

}
