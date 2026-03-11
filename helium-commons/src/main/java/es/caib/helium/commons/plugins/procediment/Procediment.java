package es.caib.helium.commons.plugins.procediment;

import java.util.Date;

import es.caib.helium.commons.dto.procediment.ProcedimentTipusEnumDto;

/**
 * Classe per retornar informació d'un procediment del plugin de procediments.
 *
 */
public class Procediment {

	private String codi;
	private String codiSia;
	private String nom;
	private String unitatAdministrativacodi;
	private boolean comu;
	private Date dataActualitzacio;
	private ProcedimentTipusEnumDto tipus;
	

    public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getUnitatAdministrativacodi() {
		return unitatAdministrativacodi;
	}
	public void setUnitatAdministrativacodi(String unitatAdministrativacodi) {
		this.unitatAdministrativacodi = unitatAdministrativacodi;
	}
	public boolean isComu() {
		return comu;
	}
	public void setComu(boolean comu) {
		this.comu = comu;
	}
	public Date getDataActualitzacio() {
		return dataActualitzacio;
	}
	public void setDataActualitzacio(Date dataActualitzacio) {
		this.dataActualitzacio = dataActualitzacio;
	}
	public ProcedimentTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ProcedimentTipusEnumDto tipus) {
		this.tipus = tipus;
	}
}
