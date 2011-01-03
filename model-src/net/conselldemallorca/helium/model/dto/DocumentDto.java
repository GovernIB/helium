/**
 * 
 */
package net.conselldemallorca.helium.model.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe per retornar informació d'un document
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DocumentDto implements Serializable {

	private Long id;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	private String arxiuNom;
	private byte[] arxiuContingut;
	private boolean signat = false;
	private String signatNom;
	private byte[] signatContingut;
	private boolean adjunt = false;
	private String adjuntTitol;

	private String processInstanceId;
	
	private Long documentId;
	private String documentCodi;
	private String documentNom;
	private String contentType;
	private String custodiaCodi;
	private Long portasignaturesId;
	private Integer tipusDocPortasignatures;

	private String registreNumero;
	private String registreAny;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;
	private boolean registrat = false;

	private String adjuntId;

	private String tokenSignatura;
	private boolean signatEnTasca;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}
	public Date getDataModificacio() {
		return dataModificacio;
	}
	public void setDataModificacio(Date dataModificacio) {
		this.dataModificacio = dataModificacio;
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
	public String getSignatNom() {
		return signatNom;
	}
	public void setSignatNom(String signatNom) {
		this.signatNom = signatNom;
	}
	public byte[] getSignatContingut() {
		return signatContingut;
	}
	public void setSignatContingut(byte[] signatContingut) {
		this.signatContingut = signatContingut;
	}
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}
	public String getAdjuntTitol() {
		return adjuntTitol;
	}
	public void setAdjuntTitol(String adjuntTitol) {
		this.adjuntTitol = adjuntTitol;
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
	public String getAdjuntId() {
		return adjuntId;
	}
	public void setAdjuntId(String adjuntId) {
		this.adjuntId = adjuntId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getDocumentCodi() {
		return documentCodi;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public String getDocumentNom() {
		return documentNom;
	}
	public void setDocumentNom(String documentNom) {
		this.documentNom = documentNom;
	}
	public String getTokenSignatura() {
		return tokenSignatura;
	}
	public void setTokenSignatura(String tokenSignatura) {
		this.tokenSignatura = tokenSignatura;
	}
	public boolean isSignatEnTasca() {
		return signatEnTasca;
	}
	public void setSignatEnTasca(boolean signatEnTasca) {
		this.signatEnTasca = signatEnTasca;
	}
	public Long getPortasignaturesId() {
		return portasignaturesId;
	}
	public void setPortasignaturesId(Long portasignaturesId) {
		this.portasignaturesId = portasignaturesId;
	}
	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}
	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public String getRegistreAny() {
		return registreAny;
	}
	public void setRegistreAny(String registreAny) {
		this.registreAny = registreAny;
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

	public String getArxiuNomSenseExtensio() {
		if (getArxiuNom() == null)
			return null;
		int indexPunt = getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			return getArxiuNom().substring(0, indexPunt);
		} else {
			return getArxiuNom();
		}
	}
	public String getArxiuExtensio() {
		if (getArxiuNom() == null)
			return null;
		int indexPunt = getArxiuNom().lastIndexOf(".");
		if (indexPunt != -1) {
			return getArxiuNom().substring(indexPunt + 1);
		} else {
			return null;
		}
	}
	public boolean isArxiuConvertiblePdf() {
		String extensio = getArxiuExtensio();
		if (extensio != null) {
			for (int i = 0; i < extensionsConvertiblesPdf.length; i++) {
				if (extensio.equalsIgnoreCase(extensionsConvertiblesPdf[i]))
					return true;
			}
		}
		return false;
	}



	private String[] extensionsConvertiblesPdf = {
			"pdf", "odt", "sxw", "rtf", "doc", "wpd", "txt", "ods",
			"sxc", "xls", "csv", "tsv", "odp", "sxi", "ppt"};

	private static final long serialVersionUID = 774909297938469787L;

}
