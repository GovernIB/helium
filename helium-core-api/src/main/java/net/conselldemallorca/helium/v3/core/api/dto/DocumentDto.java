/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO amb informació d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentDto extends HeretableDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	private boolean plantilla;
	private boolean notificable;
	private boolean required;
	private boolean readOnly;

	private Long documentId;
	private String documentCodi;
	private String documentNom;
	private String documentContentType;
	private String documentCustodiaCodi;
	private Integer documentTipusDocPortasignatures;
	private boolean documentPendentSignar = false;
	private boolean signatRequired = false;

	private boolean signat = false;
	private Long portasignaturesId;
	private String signaturaUrlVerificacio;

	private boolean registrat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;

	private boolean adjunt = false;
	private String adjuntId;
	private String adjuntTitol;

	private String arxiuNom;
	private byte[] arxiuContingut;
	private String signatNom;
	private byte[] signatContingut;
	private String vistaNom;
	private byte[] vistaContingut;
	
	private String arxiuCsv;
	private String arxiuUuid;

	private String convertirExtensio;
	private String extensionsPermeses;

	private String processInstanceId;
	private String contentType;
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;

	private String tokenSignatura;
	private String tokenSignaturaMultiple;
	private boolean signatEnTasca;
	private boolean adjuntarAuto;
	private String urlVerificacioCustodia;

	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;

	private ExpedientTipusDto expedientTipus;
	private CampDto campData;
	/* Indica si permetre o no la retroacció.
	 * Si isIgnored = true llavors no es realitzarà la retroacció i no s'esborrarà
	 * el contingut del document. */
	private boolean ignored;

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
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	public boolean isNotificable() {
		return notificable;
	}
	public void setNotificable(boolean notificable) {
		this.notificable = notificable;
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
	public String getDocumentContentType() {
		return documentContentType;
	}
	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}
	public String getDocumentCustodiaCodi() {
		return documentCustodiaCodi;
	}
	public void setDocumentCustodiaCodi(String documentCustodiaCodi) {
		this.documentCustodiaCodi = documentCustodiaCodi;
	}
	public Integer getDocumentTipusDocPortasignatures() {
		return documentTipusDocPortasignatures;
	}
	public void setDocumentTipusDocPortasignatures(
			Integer documentTipusDocPortasignatures) {
		this.documentTipusDocPortasignatures = documentTipusDocPortasignatures;
	}
	public boolean isDocumentPendentSignar() {
		return documentPendentSignar;
	}
	public void setDocumentPendentSignar(boolean documentPendentSignar) {
		this.documentPendentSignar = documentPendentSignar;
	}
	public boolean isSignatRequired() {
		return signatRequired;
	}
	public void setSignatRequired(boolean signatRequired) {
		this.signatRequired = signatRequired;
	}
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}
	public Long getPortasignaturesId() {
		return portasignaturesId;
	}
	public void setPortasignaturesId(Long portasignaturesId) {
		this.portasignaturesId = portasignaturesId;
	}
	public String getSignaturaUrlVerificacio() {
		return signaturaUrlVerificacio;
	}
	public void setSignaturaUrlVerificacio(String signaturaUrlVerificacio) {
		this.signaturaUrlVerificacio = signaturaUrlVerificacio;
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
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}
	public String getAdjuntId() {
		return adjuntId;
	}
	public void setAdjuntId(String adjuntId) {
		this.adjuntId = adjuntId;
	}
	public String getAdjuntTitol() {
		return adjuntTitol;
	}
	public void setAdjuntTitol(String adjuntTitol) {
		this.adjuntTitol = adjuntTitol;
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
	public String getVistaNom() {
		return vistaNom;
	}
	public void setVistaNom(String vistaNom) {
		this.vistaNom = vistaNom;
	}
	public byte[] getVistaContingut() {
		return vistaContingut;
	}
	public void setVistaContingut(byte[] vistaContingut) {
		this.vistaContingut = vistaContingut;
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
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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
	public String getTokenSignatura() {
		return tokenSignatura;
	}
	public void setTokenSignatura(String tokenSignatura) {
		this.tokenSignatura = tokenSignatura;
	}
	public String getTokenSignaturaMultiple() {
		return tokenSignaturaMultiple;
	}
	public void setTokenSignaturaMultiple(String tokenSignaturaMultiple) {
		this.tokenSignaturaMultiple = tokenSignaturaMultiple;
	}
	public boolean isSignatEnTasca() {
		return signatEnTasca;
	}
	public void setSignatEnTasca(boolean signatEnTasca) {
		this.signatEnTasca = signatEnTasca;
	}
	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}
	public String getUrlVerificacioCustodia() {
		return urlVerificacioCustodia;
	}
	public void setUrlVerificacioCustodia(String urlVerificacioCustodia) {
		this.urlVerificacioCustodia = urlVerificacioCustodia;
	}
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public NtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	public CampDto getCampData() {
		return campData;
	}
	public void setCampData(CampDto campData) {
		this.campData = campData;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	public String getArxiuCsv() {
		return arxiuCsv;
	}
	public void setArxiuCsv(String arxiuCsv) {
		this.arxiuCsv = arxiuCsv;
	}
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
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

	private static final long serialVersionUID = 774909297938469787L;

}
