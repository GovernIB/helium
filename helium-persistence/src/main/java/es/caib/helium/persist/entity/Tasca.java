/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una definició de tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_tasca",
		uniqueConstraints={@UniqueConstraint(columnNames={"jbpm_name", "definicio_proces_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_tasca",
		indexes = {
				@Index(name = "hel_tasca_defproc_i", columnNames = {"definicio_proces_id"}),
				@Index(name = "hel_tasca_jbpmname_i", columnNames = {"jbpm_name"})})
public class Tasca implements Serializable, GenericEntity<Long> {

	public enum TipusTasca {
		ESTAT,
		FORM,
		SIGNATURA
	}

	private Long id;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@NotNull
	private TipusTasca tipus;
	@MaxLength(255)
	private String missatgeInfo;
	@MaxLength(255)
	private String missatgeWarn;
	@MaxLength(1024)
	private String nomScript;
	@MaxLength(255)
	private String expressioDelegacio;
	@NotBlank
	@MaxLength(255)
	private String jbpmName;
	@MaxLength(255)
	private String recursForm;
	@MaxLength(255)
	private String formExtern;
	private boolean tramitacioMassiva = false;
	private boolean finalitzacioSegonPla = false;
	private boolean ambRepro = false;
	private boolean mostrarAgrupacions = false;

	@NotNull
	private DefinicioProces definicioProces;

	private List<CampTasca> camps = new ArrayList<CampTasca>();
	private List<DocumentTasca> documents = new ArrayList<DocumentTasca>();
	private List<FirmaTasca> firmes = new ArrayList<FirmaTasca>();
	private List<Validacio> validacions = new ArrayList<Validacio>();



	public Tasca() {}
	public Tasca(DefinicioProces definicioProces, String jbpmName, String nom, TipusTasca tipus) {
		this.definicioProces = definicioProces;
		this.jbpmName = jbpmName;
		this.nom = nom;
		this.tipus = tipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_interessat")
	@TableGenerator(name="gen_interessat", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="nom", length=255)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="tipus", nullable=false)
	public TipusTasca getTipus() {
		return tipus;
	}
	public void setTipus(TipusTasca tipus) {
		this.tipus = tipus;
	}

	@Column(name="missatge_info", length=255)
	public String getMissatgeInfo() {
		return missatgeInfo;
	}
	public void setMissatgeInfo(String missatgeInfo) {
		this.missatgeInfo = missatgeInfo;
	}

	@Column(name="missatge_warn", length=255)
	public String getMissatgeWarn() {
		return missatgeWarn;
	}
	public void setMissatgeWarn(String missatgeWarn) {
		this.missatgeWarn = missatgeWarn;
	}

	@Column(name="nom_script", length=1024)
	public String getNomScript() {
		return nomScript;
	}
	public void setNomScript(String nomScript) {
		this.nomScript = nomScript;
	}

	@Column(name="expressio_delegacio", length=255)
	public String getExpressioDelegacio() {
		return expressioDelegacio;
	}
	public void setExpressioDelegacio(String expressioDelegacio) {
		this.expressioDelegacio = expressioDelegacio;
	}

	@Column(name="jbpm_name", length=255, nullable=false)
	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}

	@Column(name="recurs_form", length=255)
	public String getRecursForm() {
		return recursForm;
	}
	public void setRecursForm(String recursForm) {
		this.recursForm = recursForm;
	}

	@Column(name="form_extern", length=255)
	public String getFormExtern() {
		return formExtern;
	}
	public void setFormExtern(String formExtern) {
		this.formExtern = formExtern;
	}

	@Column(name="tram_massiva")
	public boolean isTramitacioMassiva() {
		return tramitacioMassiva;
	}
	public void setTramitacioMassiva(boolean tramitacioMassiva) {
		this.tramitacioMassiva = tramitacioMassiva;
	}

	@Column(name="fin_segon_pla")
	public boolean isFinalitzacioSegonPla() {
		return finalitzacioSegonPla;
	}
	public void setFinalitzacioSegonPla(boolean finalitzacioSegonPla) {
		this.finalitzacioSegonPla = finalitzacioSegonPla;
	}
	
	@Column(name="amb_repro")
	public boolean isAmbRepro() {
		return ambRepro;
	}
	public void setAmbRepro(boolean ambRepro) {
		this.ambRepro = ambRepro;
	}

	@Column(name="mostrar_agrupacions")
	public boolean isMostrarAgrupacions() {
		return mostrarAgrupacions;
	}
	public void setMostrarAgrupacions(boolean mostrarAgrupacions) {
		this.mostrarAgrupacions = mostrarAgrupacions;
	}
	
	@ManyToOne(optional=false)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_tasca_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@OrderBy("order asc")
	public List<CampTasca> getCamps() {
		return this.camps;
	}
	public void setCamps(List<CampTasca> camps) {
		this.camps = camps;
	}
	public void addCamp(CampTasca camp) {
		getCamps().add(camp);
	}
	public void removeCamp(CampTasca camp) {
		getCamps().remove(camp);
	}

	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@OrderBy("order asc")
	public List<DocumentTasca> getDocuments() {
		return this.documents;
	}
	public void setDocuments(List<DocumentTasca> documents) {
		this.documents = documents;
	}
	public void addDocument(DocumentTasca document) {
		getDocuments().add(document);
	}
	public void removeDocument(DocumentTasca document) {
		getDocuments().remove(document);
	}

	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@OrderBy("order asc")
	public List<FirmaTasca> getFirmes() {
		return this.firmes;
	}
	public void setFirmes(List<FirmaTasca> firmes) {
		this.firmes = firmes;
	}
	public void addFirma(FirmaTasca firma) {
		getFirmes().add(firma);
	}
	public void removeFirma(FirmaTasca firma) {
		getFirmes().remove(firma);
	}

	@OneToMany(mappedBy="tasca", cascade={CascadeType.ALL})
	@OrderBy("ordre asc")
	public List<Validacio> getValidacions() {
		return this.validacions;
	}
	public void setValidacions(List<Validacio> validacions) {
		this.validacions = validacions;
	}
	public void addValidacio(Validacio validacio) {
		getValidacions().add(validacio);
	}
	public void removeValidacio(Validacio validacio) {
		getValidacions().remove(validacio);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
		result = prime * result
				+ ((jbpmName == null) ? 0 : jbpmName.hashCode());
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
		Tasca other = (Tasca) obj;
		if (definicioProces == null) {
			if (other.definicioProces != null)
				return false;
		} else if (!definicioProces.equals(other.definicioProces))
			return false;
		if (jbpmName == null) {
			if (other.jbpmName != null)
				return false;
		} else if (!jbpmName.equals(other.jbpmName))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
