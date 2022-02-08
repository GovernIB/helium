/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.command;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Create;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Massiu;
import net.conselldemallorca.helium.webapp.v3.command.DocumentExpedientCommand.Update;
import net.conselldemallorca.helium.webapp.v3.validator.DocumentExpedient;

/**
 * Command per gestionar els documents d'un expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@DocumentExpedient(groups = {Create.class, Update.class, Massiu.class})
public class DocumentExpedientCommand {
	
	/** Cadena per diferenciar quan s'adjunta un document adjunt en comptes d'un document definit al tipus d'expedient. */
	public static String ADJUNTAR_ARXIU_CODI = "##adjuntar_arxiu##";

	@NotNull(groups = {Create.class, Update.class})
	private Long expedientId;
	private Long docId;
	private String documentCodi;
	private String codi;
	private String nom;
	@NotNull(groups = {Create.class, Update.class})
	private Date data;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	private String ntiIdOrigen;
	private String arxiuNom;
	private boolean generarPlantilla = false;
	private MultipartFile arxiu;
	private boolean ntiActiu;
	private boolean ambFirma = false;
	private MultipartFile firma;
	private DocumentTipusFirmaEnumDto tipusFirma = DocumentTipusFirmaEnumDto.ADJUNT;
	/** Indica al validador si ha de validar que els arxius s'hagin adjuntat */
	private boolean validarArxius = false;

	public DocumentExpedientCommand() {}

	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public String getDocumentCodi() {
 		return documentCodi;
 	}
 	public void setDocumentCodi(String documentCodi) {
 		this.documentCodi = documentCodi;
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
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
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
	public String getNtiIdOrigen() {
		return ntiIdOrigen;
	}
	public void setNtiIdOrigen(String ntiidOrigen) {
		this.ntiIdOrigen = ntiidOrigen;
	}
	public MultipartFile getArxiu() {
		return arxiu;
	}
	public void setArxiu(MultipartFile arxiu) {
		this.arxiu = arxiu;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	public boolean isGenerarPlantilla() {
		return generarPlantilla;
	}

	public void setGenerarPlantilla(boolean generarPlantilla) {
		this.generarPlantilla = generarPlantilla;
	}

	public boolean isNtiActiu() {
		return ntiActiu;
	}

	public void setNtiActiu(boolean ntiActiu) {
		this.ntiActiu = ntiActiu;
	}
	public boolean isAmbFirma() {
		return ambFirma;
	}
	public void setAmbFirma(boolean ambFirma) {
		this.ambFirma = ambFirma;
	}
	public MultipartFile getFirma() {
		return firma;
	}
	public void setFirma(MultipartFile firma) {
		this.firma = firma;
	}
	public DocumentTipusFirmaEnumDto getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(DocumentTipusFirmaEnumDto tipusFirma) {
		this.tipusFirma = tipusFirma;
	}
	
	public boolean isValidarArxius() {
		return validarArxius;
	}

	public void setValidarArxius(boolean validarArxius) {
		this.validarArxius = validarArxius;
	}

	public interface Create {}
	public interface Update {}
	public interface Massiu {}
}
