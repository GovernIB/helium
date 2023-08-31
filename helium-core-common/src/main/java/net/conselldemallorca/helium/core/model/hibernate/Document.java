/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;

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

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_document")
	@TableGenerator(name="gen_document", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	@NotBlank
	@MaxLength(64)
	@Column(name="codi", length=64, nullable=false)
	private String codi;
	
	@NotBlank
	@MaxLength(255)
	@Column(name="nom", length=255, nullable=false)
	private String nom;
	
	@MaxLength(255)
	@Column(name="descripcio", length=255)
	private String descripcio;
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	@Column(name="arxiu_contingut")
	private byte[] arxiuContingut;
	
	@MaxLength(255)
	@Column(name="arxiu_nom", length=255)
	private String arxiuNom;
	
	@Column(name="plantilla")
	private boolean plantilla;
	
	@Column(name="notificable")
	private boolean notificable;
	
	@MaxLength(255)
	@Column(name="content_type", length=255)
	private String contentType;
	
	@MaxLength(255)
	@Column(name="custodia_codi", length=255)
	private String custodiaCodi;
	
	@Column(name="adjuntar_auto")
	private boolean adjuntarAuto;
	
	@MaxLength(10)
	@Column(name="convertir_ext", length=10)
	private String convertirExtensio;
	
	@MaxLength(255)
	@Column(name="extensions_permeses", length=255)
	private String extensionsPermeses;
	
	// Camps pel portasignatures
	@Column(name="tipus_portasignatures")
	private Integer tipusDocPortasignatures;
	
	/** Opció d'enviar al portasignatures activa des de la gestió de documents. */
	@Column(name = "portafirmes_actiu")
	private boolean portafirmesActiu = false;
	
	/** Tipus de definició d'enviament al Portasignatures per flux simple o definició de flux id. */
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_fluxtip", length = 256)
	private PortafirmesTipusEnumDto portafirmesFluxTipus;	
	
	/** Tipus de seqüència sèrie o paral·lel en cas de flux simple amb n responsables. */
	@Enumerated(EnumType.STRING)
	@Column(name = "portafirmes_seqtip", length = 256)
	private PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus;
	
	/** Responsables de firma en cas de tipus de firme per flux simple. Es guarda la llista de responsables separada per coma */
	@Column(name = "portafirmes_responsables", length = 512)
	private String portafirmesResponsables;
	
	/** Identificador del flux al Portasignatures */
	@MaxLength(64)
	@Column(name="portafirmes_flux_id", length=64)
	private String portafirmesFluxId;


	@ManyToOne(optional=true)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_document_fk")
	private DefinicioProces definicioProces;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_doc_fk")
	private ExpedientTipus expedientTipus;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="camp_data_id")
	@ForeignKey(name="hel_camp_document_fk")
	private Camp campData;

	@OneToMany(mappedBy="document", cascade={CascadeType.ALL})
	private Set<DocumentTasca> tasques = new HashSet<DocumentTasca>();
	
	@OneToMany(mappedBy="document", cascade={CascadeType.ALL})
	private Set<FirmaTasca> firmes = new HashSet<FirmaTasca>();

	/** Indica si permetre o no la retroacció. Si isIgnored = true llavors no es realitzarà la retroacció i no s'esborrarà
	 * el contingut del document. */
	@Column(name="ignored")
	private boolean ignored;

	@Column(name="nti_origen")
	private NtiOrigenEnumDto ntiOrigen;
	
	@Column(name="nti_estado_elab")
	private NtiEstadoElaboracionEnumDto ntiEstadoElaboracion;
	
	@Column(name="nti_tipo_doc")
	private NtiTipoDocumentalEnumDto ntiTipoDocumental;

	/** Flag per permetre generar el document tipus plantilla des de la tasca, no es podrà generar des de la gestió de documents
	 * Als documents de tipus plantilla que tinguin aquest flag informat no els apareixerà 
	 * el check per generar des de plantilla */
	@Column(name="generar_nomes_tasca")
	private boolean generarNomesTasca;


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

	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}

	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
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

	// PORTASIGNATURES 
	
	public Integer getTipusDocPortasignatures() {
		return tipusDocPortasignatures;
	}
	public void setTipusDocPortasignatures(Integer tipusDocPortasignatures) {
		this.tipusDocPortasignatures = tipusDocPortasignatures;
	}
	
	public boolean isPortafirmesActiu() {
		return portafirmesActiu;
	}
	public void setPortafirmesActiu(boolean portafirmesActiu) {
		this.portafirmesActiu = portafirmesActiu;
	}

	public PortafirmesTipusEnumDto getPortafirmesFluxTipus() {
		return portafirmesFluxTipus;
	}
	public void setPortafirmesFluxTipus(PortafirmesTipusEnumDto portafirmesFluxTipus) {
		this.portafirmesFluxTipus = portafirmesFluxTipus;
	}

	public PortafirmesSimpleTipusEnumDto getPortafirmesSequenciaTipus() {
		return portafirmesSequenciaTipus;
	}
	public void setPortafirmesSequenciaTipus(PortafirmesSimpleTipusEnumDto portafirmesSequenciaTipus) {
		this.portafirmesSequenciaTipus = portafirmesSequenciaTipus;
	}

	public String getPortafirmesResponsables() {
		return portafirmesResponsables;
	}
	
	public void setPortafirmesResponsables(String[] portafirmesResponsables) {
		this.portafirmesResponsables = getResponsablesFromArray(portafirmesResponsables);
	}
	
	public String getPortafirmesFluxId() {
		return portafirmesFluxId;
	}
	public void setPortafirmesFluxId(String portafirmesFluxId) {
		this.portafirmesFluxId = portafirmesFluxId;
	}

	public boolean isAdjuntarAuto() {
		return adjuntarAuto;
	}
	public void setAdjuntarAuto(boolean adjuntarAuto) {
		this.adjuntarAuto = adjuntarAuto;
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

	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}
	
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	public Camp getCampData() {
		return campData;
	}
	public void setCampData(Camp campData) {
		this.campData = campData;
	}

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

	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	
	
	public boolean isGenerarNomesTasca() {
		return generarNomesTasca;
	}
	public void setGenerarNomesTasca(boolean generarNomesTasca) {
		this.generarNomesTasca = generarNomesTasca;
	}
	
	private static String getResponsablesFromArray(String[] portafirmesResponsables) {
		StringBuilder responsablesStr = new StringBuilder();
		if (portafirmesResponsables != null) {
			for (String responsable: portafirmesResponsables) {
				if (responsablesStr.length() > 0)
					responsablesStr.append(",");
				responsablesStr.append(responsable);
			}
			return responsablesStr.toString();
		} else {
			return null;
		}

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
