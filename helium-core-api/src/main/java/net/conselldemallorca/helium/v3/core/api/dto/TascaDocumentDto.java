/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;


/**
 * DTO amb informació d'un document d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaDocumentDto {
	
	private Long id;
	private Long documentStoreId;
	private String varCodi;

	private String documentCodi;
	private String documentNom;

	private boolean required;
	private boolean readOnly;

	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;

	private boolean signat = false;
	
	private Long signaturaPortasignaturesId;
	private String signaturaUrlVerificacio;
	private String ntiCsv;
	private String tokenSignatura;

	private boolean registrat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;
	private boolean adjuntarAuto;
	
	private boolean arxiuContingutDefinit;
	
	private boolean plantilla;
	
	private String arxiuNom;

	private String error;
	
	private String urlVerificacioCustodia;
		
	private String extensionsPermeses;

	public TascaDocumentDto() {
	}
	
	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}
	public boolean isArxiuContingutDefinit() {
		return arxiuContingutDefinit;
	}

	public void setArxiuContingutDefinit(boolean arxiuContingutDefinit) {
		this.arxiuContingutDefinit = arxiuContingutDefinit;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getVarCodi() {
		return varCodi;
	}
	public void setVarCodi(String varCodi) {
		this.varCodi = varCodi;
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
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}
	public Long getSignaturaPortasignaturesId() {
		return signaturaPortasignaturesId;
	}
	public void setSignaturaPortasignaturesId(Long signaturaPortasignaturesId) {
		this.signaturaPortasignaturesId = signaturaPortasignaturesId;
	}
	public String getSignaturaUrlVerificacio() {
		return signaturaUrlVerificacio;
	}
	public void setSignaturaUrlVerificacio(String signaturaUrlVerificacio) {
		this.signaturaUrlVerificacio = signaturaUrlVerificacio;
	}
	public String getNtiCsv() {
		return ntiCsv;
	}
	public void setNtiCsv(String eniCsv) {
		this.ntiCsv = eniCsv;
	}
	public boolean isRegistrat() {
		return registrat;
	}
	public void setRegistrat(boolean registrat) {
		this.registrat = registrat;
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
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getTokenSignatura() {
		return tokenSignatura;
	}
	public void setTokenSignatura(String tokenSignatura) {
		this.tokenSignatura = tokenSignatura;
	}
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	public String getUrlVerificacioCustodia() {
		return urlVerificacioCustodia;
	}
	public void setUrlVerificacioCustodia(String urlVerificacioCustodia) {
		this.urlVerificacioCustodia = urlVerificacioCustodia;
	}
	public Long getDocumentStoreId() {
		return documentStoreId;
	}
	public void setDocumentStoreId(Long documentStoreId) {
		this.documentStoreId = documentStoreId;
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

	public String getExtensionsPermeses() {
		if (extensionsPermeses == null || extensionsPermeses.isEmpty())
			return null;		
		StringBuffer exts = new StringBuffer();
		for (String ext : extensionsPermeses.split(","))
			exts.append("."+ext+",");
		return exts.substring(0, exts.length()-1);
	}

	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
	}
}
