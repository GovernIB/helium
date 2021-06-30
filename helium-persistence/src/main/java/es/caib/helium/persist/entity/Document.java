/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import es.caib.helium.logic.intf.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.logic.intf.dto.NtiOrigenEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoDocumentalEnumDto;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_document",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id", "expedient_tipus_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_document",
		indexes = {
				@Index(name = "hel_document_defproc_i", columnNames = {"definicio_proces_id"}),
				@Index(name = "hel_document_campdata_i", columnNames = {"camp_data_id"}),
				@Index(name = "hel_document_exptip_i", columnNames = {"expedient_tipus_id"})
				})
public class Document implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	private byte[] arxiuContingut;
	@MaxLength(255)
	private String arxiuNom;
	private boolean plantilla;
	private boolean notificable;
	@MaxLength(255)
	private String contentType;
	@MaxLength(255)
	private String custodiaCodi;
	private Integer tipusDocPortasignatures;
	private boolean adjuntarAuto;
	@MaxLength(10)
	private String convertirExtensio;
	@MaxLength(255)
	private String extensionsPermeses;

	private DefinicioProces definicioProces;
	private ExpedientTipus expedientTipus;
	private Camp campData;

	private Set<DocumentTasca> tasques = new HashSet<DocumentTasca>();
	private Set<FirmaTasca> firmes = new HashSet<FirmaTasca>();

	/** Indica si permetre o no la retroacció. Si isIgnored = true llavors no es realitzarà la retroacció i no s'esborrarà
	 * el contingut del document. */
	private boolean ignored;

	private NtiOrigenEnumDto ntiOrigen;
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;



	public Document() {
		this.notificable = false;
	}
	public Document(DefinicioProces definicioProces, String codi, String nom) {
		this();
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
	}
	public Document(ExpedientTipus expedientTipus, String codi, String nom) {
		this();
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document")
	@TableGenerator(name="gen_document", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="descripcio", length=255)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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

	@Column(name="arxiu_nom", length=255)
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

	@Column(name="plantilla")
	public boolean isPlantilla() {
		return plantilla;
	}
	public void setPlantilla(boolean plantilla) {
		this.plantilla = plantilla;
	}
	
	@Column(name="notificable")
	public boolean isNotificable() {
		return notificable;
	}
	public void setNotificable(boolean notificable) {
		this.notificable = notificable;
	}
	
	@Column(name="content_type", length=255)
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Column(name="custodia_codi", length=255)
	public String getCustodiaCodi() {
		return custodiaCodi;
	}
	public void setCustodiaCodi(String custodiaCodi) {
		this.custodiaCodi = custodiaCodi;
	}

	@Column(name="tipus_portasignatures")
	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}
	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}

	@Column(name="adjuntar_auto")
	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
	}

	@Column(name="convertir_ext", length=10)
	public String getConvertirExtensio() {
		return convertirExtensio;
	}
	public void setConvertirExtensio(String convertirExtensio) {
		this.convertirExtensio = convertirExtensio;
	}

	@Column(name="extensions_permeses", length=255)
	public String getExtensionsPermeses() {
		return extensionsPermeses;
	}
	public void setExtensionsPermeses(String extensionsPermeses) {
		this.extensionsPermeses = extensionsPermeses;
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

	@Column(name="nti_tipo_doc")
	public NtiTipoDocumentalEnumDto getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(NtiTipoDocumentalEnumDto ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_document_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}
	
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_doc_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="camp_data_id")
	@ForeignKey(name="hel_camp_document_fk")
	public Camp getCampData() {
		return campData;
	}
	public void setCampData(Camp campData) {
		this.campData = campData;
	}

	@OneToMany(mappedBy="document", cascade={CascadeType.ALL})
	public Set<DocumentTasca> getTasques() {
		return this.tasques;
	}
	public void setTasques(Set<DocumentTasca> tasques) {
		this.tasques = tasques;
	}
	public void addTasca(DocumentTasca tasca) {
		getTasques().add(tasca);
	}
	public void removeTasca(DocumentTasca tasca) {
		getTasques().remove(tasca);
	}

	@OneToMany(mappedBy="document", cascade={CascadeType.ALL})
	public Set<FirmaTasca> getFirmes() {
		return this.firmes;
	}
	public void setFirmes(Set<FirmaTasca> firmes) {
		this.firmes = firmes;
	}
	public void addFirma(FirmaTasca firma) {
		getFirmes().add(firma);
	}
	public void removeFirma(FirmaTasca firma) {
		getFirmes().remove(firma);
	}

	@Column(name="ignored")
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	
	
	@Transient
	public String getCodiNom() {
		return codi + "/" + nom;
	}
	@Transient
	public boolean isExtensioPermesa(String ext) {
		if (extensionsPermeses != null && extensionsPermeses.trim().length() > 0) {
			String[] extPermeses = extensionsPermeses.split(",");
			if (extPermeses.length > 0) {
				boolean hiEs = false;
				for (int i = 0; i < extPermeses.length; i++) {
					if (extPermeses[i].trim().equalsIgnoreCase(ext.trim())) {
						hiEs = true;
						break;
					}
				}
				return hiEs;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
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
		Document other = (Document) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (definicioProces == null) {
			if (other.definicioProces != null)
				return false;
		} else if (!definicioProces.equals(other.definicioProces))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
}
