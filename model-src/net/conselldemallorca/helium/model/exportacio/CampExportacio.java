/**
 * 
 */
package net.conselldemallorca.helium.model.exportacio;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;



/**
 * DTO amb informació d'un camp per exportar
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CampExportacio implements Serializable {

	private String codi;
	private TipusCamp tipus;
	private String etiqueta;
	private String observacions;
	private String dominiId;
	private String dominiParams;
	private String dominiCampText;
	private String dominiCampValor;
	private boolean multiple;
	private boolean ocult;
	private String codiEnumeracio;
	private String codiDomini;
	private String agrupacioCodi;
	private Set<ValidacioExportacio> validacions = new HashSet<ValidacioExportacio>();



	public CampExportacio(
			String codi,
			TipusCamp tipus,
			String etiqueta,
			String observacions,
			String dominiId,
			String dominiParams,
			String dominiCampText,
			String dominiCampValor,
			boolean multiple,
			boolean ocult,
			String codiEnumeracio,
			String codiDomini,
			String agrupacioCodi) {
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
		this.observacions = observacions;
		this.dominiId = dominiId;
		this.dominiParams = dominiParams;
		this.dominiCampText = dominiCampText;
		this.dominiCampValor = dominiCampValor;
		this.multiple = multiple;
		this.ocult = ocult;
		this.codiEnumeracio = codiEnumeracio;
		this.codiDomini = codiDomini;
		this.agrupacioCodi = agrupacioCodi;
	}

	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public TipusCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusCamp tipus) {
		this.tipus = tipus;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Set<ValidacioExportacio> getValidacions() {
		return validacions;
	}
	public void setValidacions(Set<ValidacioExportacio> validacions) {
		this.validacions = validacions;
	}
	public void addValidacio(ValidacioExportacio validacio) {
		getValidacions().add(validacio);
	}
	public String getDominiId() {
		return dominiId;
	}
	public void setDominiId(String dominiId) {
		this.dominiId = dominiId;
	}
	public String getDominiParams() {
		return dominiParams;
	}
	public void setDominiParams(String dominiParams) {
		this.dominiParams = dominiParams;
	}
	public String getDominiCampText() {
		return dominiCampText;
	}
	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}
	public String getDominiCampValor() {
		return dominiCampValor;
	}
	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}
	public String getCodiEnumeracio() {
		return codiEnumeracio;
	}
	public void setCodiEnumeracio(String codiEnumeracio) {
		this.codiEnumeracio = codiEnumeracio;
	}
	public String getCodiDomini() {
		return codiDomini;
	}
	public void setCodiDomini(String codiDomini) {
		this.codiDomini = codiDomini;
	}
	public String getAgrupacioCodi() {
		return agrupacioCodi;
	}

	public void setAgrupacioCodi(String agrupacioCodi) {
		this.agrupacioCodi = agrupacioCodi;
	}



	private static final long serialVersionUID = 1L;

}
