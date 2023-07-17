/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO amb informació d'un document de l'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientDocumentDto implements Serializable {

	private Long id;
	private String varCodi;
	private Date dataCreacio;
	private Date dataModificacio;
	private Date dataDocument;
	private String arxiuNom;
	private String processInstanceId;

	private Long documentId;
	private String documentCodi;
	private String documentNom;

	private boolean adjunt = false;
	private String adjuntId;
	private String adjuntTitol;

	private boolean signat = false;
	private Long signaturaPortasignaturesId;
	private String signaturaUrlVerificacio;

	private boolean registrat = false;
	private String registreNumero;
	private Date registreData;
	private String registreOficinaCodi;
	private String registreOficinaNom;
	private boolean registreEntrada = true;

	private boolean plantilla = false;
	private String error;

	private String ntiVersion;
	private String ntiIdentificador;
	private String ntiOrgano;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiDocumentoFormato ntiNombreFormato;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private String ntiIdOrigen;
	private String ntiIdDocumentoOrigen;
	private NtiTipoFirmaEnumDto ntiTipoFirma;
	private String ntiCsv;
	private String ntiDefinicionGenCsv;
	private String arxiuUuid;
	
	private boolean notificable;
	
	private boolean notificat;
	
	private String custodiaCodi;
	
	/** Per marcar a la pipella de documents si prové d'una anotació. */
	private Long anotacioId = null;
	private String anotacioIdentificador = null;
	private Long anotacioAnnexId = null;
	private String anotacioAnnexTitol = null;
	
	/** Informació provinent del document de l'anotació original */
	private boolean documentValid = true;
	private String documentError;
	
	public boolean isNotificable() {
		return notificable;
	}
	public void setNotificable(boolean notificable) {
		this.notificable = notificable;
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
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
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
	public NtiDocumentoFormato getNtiNombreFormato() {
		return ntiNombreFormato;
	}
	public void setNtiNombreFormato(NtiDocumentoFormato ntiNombreFormato) {
		this.ntiNombreFormato = ntiNombreFormato;
	}
	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public String getNtiIdOrigen() {
		return ntiIdOrigen;
	}
	public void setNtiIdOrigen(String ntiIdOrigen) {
		this.ntiIdOrigen = ntiIdOrigen;
	}
	public String getNtiIdDocumentoOrigen() {
		return ntiIdDocumentoOrigen;
	}
	public void setNtiIdDocumentoOrigen(String ntiIdDocumentoOrigen) {
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
	}
	public NtiTipoFirmaEnumDto getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(NtiTipoFirmaEnumDto ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}
	public String getNtiCsv() {
		return ntiCsv;
	}
	public void setNtiCsv(String ntiCsv) {
		this.ntiCsv = ntiCsv;
	}
	public String getNtiDefinicionGenCsv() {
		return ntiDefinicionGenCsv;
	}
	public void setNtiDefinicionGenCsv(String ntiDefinicionGenCsv) {
		this.ntiDefinicionGenCsv = ntiDefinicionGenCsv;
	}
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}

	public boolean isArxiuActiu() {
		return this.arxiuUuid != null;
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

	public boolean isNotificat() {
		return notificat;
	}
	public void setNotificat(boolean notificat) {
		this.notificat = notificat;
	}

	public Long getAnotacioId() {
		return anotacioId;
	}
	public void setAnotacioId(Long anotacioId) {
		this.anotacioId = anotacioId;
	}

	public String getAnotacioIdentificador() {
		return anotacioIdentificador;
	}
	public void setAnotacioIdentificador(String anotacioNumero) {
		this.anotacioIdentificador = anotacioNumero;
	}

	public Long getAnotacioAnnexId() {
		return anotacioAnnexId;
	}
	public void setAnotacioAnnexId(Long anotacioAnnexId) {
		this.anotacioAnnexId = anotacioAnnexId;
	}
	public String getAnotacioAnnexTitol() {
		return anotacioAnnexTitol;
	}
	public void setAnotacioAnnexTitol(String anotacioAnnexTitol) {
		this.anotacioAnnexTitol = anotacioAnnexTitol;
	}
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}
	public boolean isDocumentValid() {
		return documentValid;
	}
	public void setDocumentValid(boolean documentValid) {
		this.documentValid = documentValid;
	}
	public String getDocumentError() {
		return documentError;
	}
	public void setDocumentError(String documentError) {
		this.documentError = documentError;
	}

	private static final long serialVersionUID = -4307890997577367155L;

}
