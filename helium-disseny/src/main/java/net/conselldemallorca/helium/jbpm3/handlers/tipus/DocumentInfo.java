/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe per retornar la informació d'un document als handlers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String titol;
	private Date dataCreacio;
	private Date dataDocument;
	private String arxiuNom;
	private byte[] arxiuContingut;

	private boolean signat = false;

	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;
	private boolean registrat = false;

	private String processInstanceId;
	private String codiDocument;

	private String tipusDocument;
	private String tipusDocumental;
	private Integer origen; 
	private Integer modeFirma;
	private String validesa;
	private String observacions;

	private String csv;
	private String urlVerificacioSignatures;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataDocument() {
		return dataDocument;
	}
	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
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
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}
	public String getRegistreOficinaCodi() {
		return registreOficinaCodi;
	}
	public void setRegistreOficinaCodi(String registreOficinaCodi) {
		this.registreOficinaCodi = registreOficinaCodi;
	}
	public String getRegistreOficinaNom() {
		return registreOficinaNom;
	}
	public void setRegistreOficinaNom(String registreOficinaNom) {
		this.registreOficinaNom = registreOficinaNom;
	}
	public boolean isRegistreEntrada() {
		return registreEntrada;
	}
	public void setRegistreEntrada(boolean registreEntrada) {
		this.registreEntrada = registreEntrada;
	}
	public boolean isRegistrat() {
		return registrat;
	}
	public void setRegistrat(boolean registrat) {
		this.registrat = registrat;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getCodiDocument() {
		return codiDocument;
	}
	public void setCodiDocument(String codiDocument) {
		this.codiDocument = codiDocument;
	}
	public String getTipusDocument() {
		return tipusDocument;
	}
	public void setTipusDocument(String tipusDocument) {
		this.tipusDocument = tipusDocument;
	}
	public String getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(String tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	public Integer getOrigen() {
		return origen;
	}
	public void setOrigen(Integer origen) {
		this.origen = origen;
	}
	public Integer getModeFirma() {
		return modeFirma;
	}
	public void setModeFirma(Integer modeFirma) {
		this.modeFirma = modeFirma;
	}
	public String getValidesa() {
		return validesa;
	}
	public void setValidesa(String validesa) {
		this.validesa = validesa;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getUrlVerificacioSignatures() {
		return urlVerificacioSignatures;
	}
	public void setUrlVerificacioSignatures(String urlVerificacioSignatures) {
		this.urlVerificacioSignatures = urlVerificacioSignatures;
	}

}
