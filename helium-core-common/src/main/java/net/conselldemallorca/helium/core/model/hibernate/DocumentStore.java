/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.v3.core.api.dto.NtiDocumentoFormato;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoFirmaEnumDto;

/**
 * Objecte de domini que representa una entrada al magatzem de documents
 * de la BBDD
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_document_store")
public class DocumentStore implements Serializable, GenericEntity<Long> {
	
	public enum DocumentFont {
		INTERNA,
		ALFRESCO
		}

	private Long id;
	@NotNull
	private DocumentFont font;
	@NotBlank
	@MaxLength(64)
	private String processInstanceId;
	@NotBlank
	@MaxLength(255)
	private String jbpmVariable;
	@NotNull
	private Date dataCreacio;
	private Date dataModificacio;
	@NotNull
	private Date dataDocument;
	@MaxLength(255)
	private String arxiuNom;
	private byte[] arxiuContingut;
	private boolean signat = false;

	@MaxLength(255)
	private String referenciaCustodia;
	@MaxLength(255)
	private String referenciaFont;
	@MaxLength(255)
	private String registreNumero;
	private Date registreData;
	@MaxLength(255)
	private String registreOrganCodi;
	@MaxLength(255)
	private String registreOficinaCodi;
	@MaxLength(255)
	private String registreOficinaNom;
	private boolean registreEntrada;

	private boolean adjunt = false;
	@MaxLength(255)
	private String adjuntTitol;

	@MaxLength(256)
	private String ntiVersion;
	@MaxLength(52)
	private String ntiIdentificador;
	@MaxLength(256)
	private String ntiOrgano;
	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiDocumentoFormato ntiNombreFormato;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;
	@MaxLength(52)
	private String ntiIdDocumentoOrigen;
	private NtiTipoFirmaEnumDto ntiTipoFirma;
	@MaxLength(256)
	private String ntiCsv;
	@MaxLength(256)
	private String ntiDefinicionGenCsv;
	@MaxLength(32)
	private String arxiuUuid;
	
	/** Indica si en la consulta Distribucio el marca com a válid o invàlid */
	private Boolean documentValid;
	/** Camp on distribucio informa dels possibles errors que pugui tenir el document. */
	@MaxLength(100)
	private String documentError;

	/** Identificador de l'annex de l'anotació del qual prové el document o adjunt. */
	private Long annexId;

	/** Llista de documents continguts en el zip guardats a laa taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStore> continguts = new ArrayList<DocumentStore>();
	
	/** Llista de documents zip que contenen aquest document guardats a la taula hel_document_contingut. S'usa en les notificacions de zips. */
	private List<DocumentStore> zips = new ArrayList<DocumentStore>();

	
	public DocumentStore() {}
	public DocumentStore(
			DocumentFont font,
			String processInstanceId,
			String jbpmVariable,
			Date dataCreacio,
			Date dataDocument,
			String arxiuNom) {
		this.font = font;
		this.processInstanceId = processInstanceId;
		this.jbpmVariable = jbpmVariable;
		this.dataCreacio = dataCreacio;
		this.dataModificacio = dataCreacio;
		this.dataDocument = dataDocument;
		this.arxiuNom = arxiuNom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document_store")
	@TableGenerator(name="gen_document_store", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="font", nullable=false)
	public DocumentFont getFont() {
		return font;
	}
	public void setFont(DocumentFont font) {
		this.font = font;
	}

	@Column(name="process_instance_id", length=64, nullable=false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="jbpm_variable", length=255, nullable=false)
	public String getJbpmVariable() {
		return jbpmVariable;
	}
	public void setJbpmVariable(String jbpmVariable) {
		this.jbpmVariable = jbpmVariable;
	}

	@Column(name="data_creacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}

	@Column(name="data_modificacio", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataModificacio() {
		return dataModificacio;
	}
	public void setDataModificacio(Date dataModificacio) {
		this.dataModificacio = dataModificacio;
	}

	@Column(name="data_document", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getDataDocument() {
		return dataDocument;
	}
	public void setDataDocument(Date dataDocument) {
		this.dataDocument = dataDocument;
	}

	@Column(name="arxiu_nom", length=255, nullable=false)
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	@Column(name="arxiu_contingut")
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}

	@Column(name="signat")
	public boolean isSignat() {
		return signat;
	}
	public void setSignat(boolean signat) {
		this.signat = signat;
	}

	@Column(name="ref_custodia", length=255)
	public String getReferenciaCustodia() {
		return referenciaCustodia;
	}
	public void setReferenciaCustodia(String referenciaCustodia) {
		this.referenciaCustodia = referenciaCustodia;
	}

	@Column(name="ref_font", length=255)
	public String getReferenciaFont() {
		return referenciaFont;
	}
	public void setReferenciaFont(String referenciaFont) {
		this.referenciaFont = referenciaFont;
	}

	@Column(name="registre_num", length=255)
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}

	@Column(name="registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getRegistreData() {
		return registreData;
	}
	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}

	@Column(name="registre_orgcodi", length=255)
	public String getRegistreOrganCodi() {
		return registreOrganCodi;
	}
	public void setRegistreOrganCodi(String registreOrganCodi) {
		this.registreOrganCodi = registreOrganCodi;
	}

	@Column(name="registre_ofcodi", length=255)
	public String getRegistreOficinaCodi() {
		return registreOficinaCodi;
	}
	public void setRegistreOficinaCodi(String registreOficinaCodi) {
		this.registreOficinaCodi = registreOficinaCodi;
	}

	@Column(name="registre_ofnom", length=255)
	public String getRegistreOficinaNom() {
		return registreOficinaNom;
	}
	public void setRegistreOficinaNom(String registreOficinaNom) {
		this.registreOficinaNom = registreOficinaNom;
	}

	@Column(name="registre_entrada")
	public boolean isRegistreEntrada() {
		return registreEntrada;
	}
	public void setRegistreEntrada(boolean registreEntrada) {
		this.registreEntrada = registreEntrada;
	}

	@Column(name="adjunt")
	public boolean isAdjunt() {
		return adjunt;
	}
	public void setAdjunt(boolean adjunt) {
		this.adjunt = adjunt;
	}

	@Column(name="adjunt_titol", length=255)
	public String getAdjuntTitol() {
		return adjuntTitol;
	}
	public void setAdjuntTitol(String adjuntTitol) {
		this.adjuntTitol = adjuntTitol;
	}

	@Column(name="nti_version", length=256)
	public String getNtiVersion() {
		return ntiVersion;
	}
	public void setNtiVersion(String ntiVersion) {
		this.ntiVersion = ntiVersion;
	}

	@Column(name="nti_identificador", length=52)
	public String getNtiIdentificador() {
		return ntiIdentificador;
	}
	public void setNtiIdentificador(String ntiIdentificador) {
		this.ntiIdentificador = ntiIdentificador;
	}

	@Column(name="nti_organo", length=256)
	public String getNtiOrgano() {
		return ntiOrgano;
	}
	public void setNtiOrgano(String ntiOrgano) {
		this.ntiOrgano = ntiOrgano;
	}

	@Column(name="nti_origen")
	public NtiOrigenEnumDto getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigenEnumDto ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}

	@Column(name="nti_estado_elab")
	public NtiEstadoElaboracionEnumDto getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(NtiEstadoElaboracionEnumDto ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}

	@Column(name="nti_nombre_fmt")
	public NtiDocumentoFormato getNtiNombreFormato() {
		return ntiNombreFormato;
	}
	public void setNtiNombreFormato(NtiDocumentoFormato ntiNombreFormato) {
		this.ntiNombreFormato = ntiNombreFormato;
	}

	@Column(name="nti_tipo_doc")
	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}

	@Column(name="nti_iddoc_orig", length=52)
	public String getNtiIdDocumentoOrigen() {
		return ntiIdDocumentoOrigen;
	}
	public void setNtiIdDocumentoOrigen(String ntiIdDocumentoOrigen) {
		this.ntiIdDocumentoOrigen = ntiIdDocumentoOrigen;
	}

	@Column(name="nti_tipo_firma")
	public NtiTipoFirmaEnumDto getNtiTipoFirma() {
		return ntiTipoFirma;
	}
	public void setNtiTipoFirma(NtiTipoFirmaEnumDto ntiTipoFirma) {
		this.ntiTipoFirma = ntiTipoFirma;
	}

	@Column(name="nti_csv", length=256)
	public String getNtiCsv() {
		return ntiCsv;
	}
	public void setNtiCsv(String ntiCsv) {
		this.ntiCsv = ntiCsv;
	}

	@Column(name="nti_def_gen_csv", length=256)
	public String getNtiDefinicionGenCsv() {
		return ntiDefinicionGenCsv;
	}
	public void setNtiDefinicionGenCsv(String ntiDefinicionGenCsv) {
		this.ntiDefinicionGenCsv = ntiDefinicionGenCsv;
	}

	@Column(name="arxiu_uuid", length=36)
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}

	@Transient
	public String getCodiDocument() {
		if (getJbpmVariable() == null || !getJbpmVariable().startsWith(JbpmVars.PREFIX_DOCUMENT)) {
			return null;
		}
		return getJbpmVariable().substring(JbpmVars.PREFIX_DOCUMENT.length());
	}
	@Transient
	public boolean isRegistrat() {
		return (registreNumero != null) || (registreData != null);
	}
	@Transient
	public boolean isRegistreSortida() {
		return !isRegistreEntrada();
	}

	@Column(name = "document_valid")
	public boolean isDocumentValid() {
		return documentValid != null ? documentValid.booleanValue() : true;
	}
	public void setDocumentValid(Boolean documentValid) {
		this.documentValid = documentValid;
	}
	
	@Column(name = "document_error", length = 1000)
	public String getDocumentError() {
		return documentError;
	}
	public void setDocumentError(String documentError) {
		this.documentError = documentError;
	}
	
	@Column(name = "annex_id")
	public Long getAnnexId() {
		return annexId;
	}
	public void setAnnexId(Long annexId) {
		this.annexId = annexId;
	}
	
	
	@JoinTable(name = "hel_document_contingut", joinColumns = {
	@JoinColumn(name = "id", referencedColumnName = "id", nullable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "document_contingut", referencedColumnName = "id", nullable = false) })
	@ManyToMany
	public List<DocumentStore> getContinguts() {
		return continguts;
	}
	public void setContinguts(List<DocumentStore> continguts) {
		this.continguts = continguts;
	}
	
	@ManyToMany(mappedBy = "continguts")
	public List<DocumentStore> getZips() {
		return zips;
	}
	public void setZips(List<DocumentStore> zips) {
		this.zips = zips;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentStore other = (DocumentStore) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DocumentStore [id=" + id + ", jbpmVariable=" + jbpmVariable + ", arxiuNom=" + arxiuNom + ", signat="
				+ signat + ", adjunt=" + adjunt + ", arxiuUuid=" + arxiuUuid + ", font=" + font + "]";
	}

	private static final long serialVersionUID = 1L;
}