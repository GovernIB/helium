/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.util.Date;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaConsultaEstatEnviament {

	private EnviamentEstat estat;
	private Date estatData;
	private String estatDescripcio;
	private String estatOrigen;
	private String receptorNif;
	private String receptorNom;
	private Date certificacioData;
	private String certificacioOrigen;
	private byte[] certificacioContingut;
	private String certificacioHash;
	private String certificacioMetadades;
	private String certificacioCsv;
	private String certificacioTipusMime;
	private boolean error;
	private String errorDescripcio;

	public EnviamentEstat getEstat() {
		return estat;
	}
	public void setEstat(EnviamentEstat estat) {
		this.estat = estat;
	}
	public Date getEstatData() {
		return estatData;
	}
	public void setEstatData(Date estatData) {
		this.estatData = estatData;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
	}
	public String getEstatOrigen() {
		return estatOrigen;
	}
	public void setEstatOrigen(String estatOrigen) {
		this.estatOrigen = estatOrigen;
	}
	public String getReceptorNif() {
		return receptorNif;
	}
	public void setReceptorNif(String receptorNif) {
		this.receptorNif = receptorNif;
	}
	public String getReceptorNom() {
		return receptorNom;
	}
	public void setReceptorNom(String receptorNom) {
		this.receptorNom = receptorNom;
	}
	public Date getCertificacioData() {
		return certificacioData;
	}
	public void setCertificacioData(Date certificacioData) {
		this.certificacioData = certificacioData;
	}
	public String getCertificacioOrigen() {
		return certificacioOrigen;
	}
	public void setCertificacioOrigen(String certificacioOrigen) {
		this.certificacioOrigen = certificacioOrigen;
	}
	public byte[] getCertificacioContingut() {
		return certificacioContingut;
	}
	public void setCertificacioContingut(byte[] certificacioContingut) {
		this.certificacioContingut = certificacioContingut;
	}
	public String getCertificacioHash() {
		return certificacioHash;
	}
	public void setCertificacioHash(String certificacioHash) {
		this.certificacioHash = certificacioHash;
	}
	public String getCertificacioMetadades() {
		return certificacioMetadades;
	}
	public void setCertificacioMetadades(String certificacioMetadades) {
		this.certificacioMetadades = certificacioMetadades;
	}
	public String getCertificacioCsv() {
		return certificacioCsv;
	}
	public void setCertificacioCsv(String certificacioCsv) {
		this.certificacioCsv = certificacioCsv;
	}
	public String getCertificacioTipusMime() {
		return certificacioTipusMime;
	}
	public void setCertificacioTipusMime(String certificacioTipusMime) {
		this.certificacioTipusMime = certificacioTipusMime;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

}
