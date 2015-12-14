/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;



/**
 * DTO amb informació d'un camp per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
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
	private String consultaParams;
	private String consultaCampText;
	private String consultaCampValor;
	private boolean multiple;
	private boolean ocult;
	private boolean dominiIntern;
	private boolean dominiCacheText;
	private String codiEnumeracio;
	private String codiDomini;
	private String agrupacioCodi;
	private String jbpmAction;
	private Integer ordre;
	private Set<ValidacioExportacio> validacions = new HashSet<ValidacioExportacio>();
	private Set<RegistreMembreExportacio> registreMembres = new HashSet<RegistreMembreExportacio>();
	private boolean isIgnored;
	
	
	public CampExportacio(
			String codi,
			TipusCamp tipus,
			String etiqueta,
			String observacions,
			String dominiId,
			String dominiParams,
			String dominiCampText,
			String dominiCampValor,
			String consultaParams,
			String consultaCampText,
			String consultaCampValor,
			boolean multiple,
			boolean ocult,
			boolean dominiIntern,
			boolean dominiCacheText,
			String codiEnumeracio,
			String codiDomini,
			String agrupacioCodi,
			String jbpmAction,
			Integer ordre,
			boolean isIgnored) {
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
		this.observacions = observacions;
		this.dominiId = dominiId;
		this.dominiParams = dominiParams;
		this.dominiCampText = dominiCampText;
		this.dominiCampValor = dominiCampValor;
		this.consultaParams = consultaParams;
		this.consultaCampText = consultaCampText;
		this.consultaCampValor = consultaCampValor;
		this.multiple = multiple;
		this.ocult = ocult;
		this.dominiIntern = dominiIntern;
		this.dominiCacheText = dominiCacheText;
		this.codiEnumeracio = codiEnumeracio;
		this.codiDomini = codiDomini;
		this.agrupacioCodi = agrupacioCodi;
		this.jbpmAction = jbpmAction;
		this.ordre = ordre;
		this.isIgnored = isIgnored;
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
	public Set<RegistreMembreExportacio> getRegistreMembres() {
		return registreMembres;
	}
	public void setRegistreMembres(Set<RegistreMembreExportacio> registreMembres) {
		this.registreMembres = registreMembres;
	}
	public void addRegistreMembre(RegistreMembreExportacio registreMembre) {
		getRegistreMembres().add(registreMembre);
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
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}
	public Integer getOrdre() {
		return ordre;
	}
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}
	public String getConsultaParams() {
		return consultaParams;
	}
	public void setConsultaParams(String consultaParams) {
		this.consultaParams = consultaParams;
	}
	public String getConsultaCampValor() {
		return consultaCampValor;
	}
	public void setConsultaCampValor(String consultaCampValor) {
		this.consultaCampValor = consultaCampValor;
	}
	public String getConsultaCampText() {
		return consultaCampText;
	}
	public void setConsultaCampText(String consultaCampText) {
		this.consultaCampText = consultaCampText;
	}

	public boolean isIgnored() {
		return isIgnored;
	}

	public void setIgnored(boolean isIgnored) {
		this.isIgnored = isIgnored;
	}

	public boolean isDominiIntern() {
		return dominiIntern;
	}

	public void setDominiIntern(boolean dominiIntern) {
		this.dominiIntern = dominiIntern;
	}

	public boolean isDominiCacheText() {
		return dominiCacheText;
	}

	public void setDominiCacheText(boolean dominiCacheText) {
		this.dominiCacheText = dominiCacheText;
	}



	private static final long serialVersionUID = 1L;

}
