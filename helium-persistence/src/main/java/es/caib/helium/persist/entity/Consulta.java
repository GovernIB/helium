/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una consulta d'expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_consulta",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_consulta",
		indexes = {
				@Index(name = "hel_consulta_entorn_i", columnNames = {"entorn_id"}),
				@Index(name = "hel_consulta_exptip_i", columnNames = {"expedient_tipus_id"})})
public class Consulta implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;
	@MaxLength(1024)
	private String valorsPredefinits;
	@MaxLength(255)
	private String informeNom;
	private byte[] informeContingut;
	private boolean exportarActiu;
	private boolean ocultarActiu;
	private boolean generica;
	private int ordre;
	private Map<String, String> mapValorsPredefinits = new HashMap<String, String>();

	@NotNull
	private Entorn entorn;
	@NotNull
	private ExpedientTipus expedientTipus;

	private Set<ConsultaCamp> camps = new HashSet<ConsultaCamp>();
	
	/** Camps de tipus consulta relacionats amb aquesta consulta. */
	private Set<Camp> campsConsulta = new HashSet<Camp>();

	private String formatExport;


	public Consulta() {}
	public Consulta(String codi, String nom) {
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_consulta")
	@TableGenerator(name="gen_consulta", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="valors_predef", length=1024)
	public String getValorsPredefinits() {
		return valorsPredefinits;
	}

	@Transient
	public Map<String, String> getMapValorsPredefinits() {
		if (mapValorsPredefinits.isEmpty() && valorsPredefinits != null) {
			String[] parelles = valorsPredefinits.split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parella = (parelles[i].contains(":")) ? parelles[i].split(":") : parelles[i].split("=");
				if (parella.length == 2) {
					mapValorsPredefinits.put(parella[0], parella[1]);
				}
			}
		}
		return mapValorsPredefinits;
	}
	
	public void setValorsPredefinits(String valorsPredefinits) {
		this.valorsPredefinits = valorsPredefinits;
	}

	@Column(name="informe_nom", length=255)
	public String getInformeNom() {
		return informeNom;
	}
	public void setInformeNom(String informeNom) {
		this.informeNom = informeNom;
	}

	@Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	@Column(name="informe_contingut")
	public byte[] getInformeContingut() {
		return informeContingut;
	}
	public void setInformeContingut(byte[] informeContingut) {
		this.informeContingut = informeContingut;
	}

	@Column(name="exportar_actiu")
	public boolean isExportarActiu() {
		return exportarActiu;
	}
	public void setExportarActiu(boolean exportarActiu) {
		this.exportarActiu = exportarActiu;
	}

	@Column(name="ocultar_actiu")
	public boolean isOcultarActiu() {
		return ocultarActiu;
	}
	public void setOcultarActiu(boolean ocultarActiu) {
		this.ocultarActiu = ocultarActiu;
	}

	@Column(name="generica")
	public boolean isGenerica() {
		return generica;
	}
	public void setGenerica(boolean generica) {
		this.generica = generica;
	}

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	
	@Column(name="format_exportacio", length=4)
	public String getFormatExport() {
		return formatExport;
	}
	public void setFormatExport(String formatExport) {
		this.formatExport = formatExport;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_consulta_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_consulta_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="consulta", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	public Set<ConsultaCamp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<ConsultaCamp> camps) {
		this.camps= camps;
	}
	public void addCamp(ConsultaCamp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(ConsultaCamp camp) {
		getCamps().remove(camp);
	}
	
	@OneToMany(mappedBy="consulta", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	public Set<Camp> getCampsConsulta() {
		return this.campsConsulta;
	}
	public void setCampsConsulta(Set<Camp> campsConsulta) {
		this.campsConsulta= campsConsulta;
	}	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result + ((entorn == null) ? 0 : entorn.hashCode());
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
		Consulta other = (Consulta) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (entorn == null) {
			if (other.entorn != null)
				return false;
		} else if (!entorn.equals(other.entorn))
			return false;
		return true;
	}
	
	
	private static final long serialVersionUID = 1L;

}
