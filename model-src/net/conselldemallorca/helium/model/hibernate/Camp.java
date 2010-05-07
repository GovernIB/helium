/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import net.conselldemallorca.helium.jbpm3.integracio.Termini;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa un camp de la definició de procés.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(	name="hel_camp",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id"})})
public class Camp implements Serializable, GenericEntity<Long> {

	public enum TipusCamp {
		STRING,
		INTEGER,
		FLOAT,
		BOOLEAN,
		TEXTAREA,
		DATE,
		PRICE,
		TERMINI,
		SELECCIO,
		SUGGEST
	}

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotNull
	private TipusCamp tipus;
	@NotBlank
	@MaxLength(255)
	private String etiqueta;
	@MaxLength(255)
	private String observacions;
	@MaxLength(255)
	private String dominiId;
	@MaxLength(255)
	private String dominiParams;
	@MaxLength(64)
	private String dominiCampText;
	@MaxLength(64)
	private String dominiCampValor;
	private boolean multiple;
	private boolean ocult;

	private Domini domini;
	private Enumeracio enumeracio;
	@NotNull
	private DefinicioProces definicioProces;
	private CampAgrupacio agrupacio;

	private Set<CampTasca> campsTasca = new HashSet<CampTasca>();
	private Set<Document> documentDates = new HashSet<Document>();
	private List<Validacio> validacions = new ArrayList<Validacio>();
	



	public Camp() {}
	public Camp(DefinicioProces definicioProces, String codi, TipusCamp tipus, String etiqueta) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.tipus = tipus;
		this.etiqueta = etiqueta;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_camp")
	@TableGenerator(name="gen_camp", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="tipus", length=255, nullable=false)
	public TipusCamp getTipus() {
		return tipus;
	}
	public void setTipus(TipusCamp tipus) {
		this.tipus = tipus;
	}

	@Column(name="etiqueta", length=255, nullable=false)
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	@Column(name="observacions", length=255)
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

	@Column(name="domini_paramid", length=255)
	public String getDominiId() {
		return dominiId;
	}
	public void setDominiId(String dominiId) {
		this.dominiId = dominiId;
	}

	@Column(name="domini_params", length=255)
	public String getDominiParams() {
		return dominiParams;
	}
	public void setDominiParams(String dominiParams) {
		this.dominiParams = dominiParams;
	}

	@Column(name="domini_camp_text", length=64)
	public String getDominiCampText() {
		return dominiCampText;
	}
	public void setDominiCampText(String dominiCampText) {
		this.dominiCampText = dominiCampText;
	}

	@Column(name="domini_camp_valor", length=64)
	public String getDominiCampValor() {
		return dominiCampValor;
	}
	public void setDominiCampValor(String dominiCampValor) {
		this.dominiCampValor = dominiCampValor;
	}

	@Column(name="multiple")
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@Column(name="ocult")
	public boolean isOcult() {
		return ocult;
	}
	public void setOcult(boolean ocult) {
		this.ocult = ocult;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_camp_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="camp_agrupacio_id")
	@ForeignKey(name="hel_campagrup_camp_fk")
	public CampAgrupacio getAgrupacio() {
		return agrupacio;
	}
	public void setAgrupacio(CampAgrupacio agrupacio) {
		this.agrupacio = agrupacio;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="domini_id")
	@ForeignKey(name="hel_domini_camp_fk")
	public Domini getDomini() {
		return domini;
	}
	public void setDomini(Domini domini) {
		this.domini = domini;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="enumeracio_id")
	@ForeignKey(name="hel_enumeracio_camp_fk")
	public Enumeracio getEnumeracio() {
		return enumeracio;
	}
	public void setEnumeracio(Enumeracio enumeracio) {
		this.enumeracio = enumeracio;
	}

	@OneToMany(mappedBy="camp", cascade={CascadeType.ALL})
	public Set<CampTasca> getCampsTasca() {
		return this.campsTasca;
	}
	public void setCampsTasca(Set<CampTasca> campsTasca) {
		this.campsTasca = campsTasca;
	}
	public void addCampTasca(CampTasca campTasca) {
		getCampsTasca().add(campTasca);
	}
	public void removeCampTasca(CampTasca campTasca) {
		getCampsTasca().remove(campTasca);
	}

	@OneToMany(mappedBy="campData")
	public Set<Document> getDocumentDates() {
		return documentDates;
	}
	public void setDocumentDates(Set<Document> documentDates) {
		this.documentDates = documentDates;
	}
	public void addDocumentData(Document documentData) {
		getDocumentDates().add(documentData);
	}
	public void removeDocumentData(Document documentData) {
		getDocumentDates().remove(documentData);
	}

	@OneToMany(mappedBy="camp", cascade={CascadeType.ALL})
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

	@Transient
	public String getCodiEtiqueta() {
		return codi + "/" + etiqueta;
	}
	@SuppressWarnings("unchecked")
	@Transient
	public Class getJavaClass() {
		if (TipusCamp.STRING.equals(tipus)) {
			return String.class;
		} else if (TipusCamp.INTEGER.equals(tipus)) {
			return Long.class;
		} else if (TipusCamp.FLOAT.equals(tipus)) {
			return Double.class;
		} else if (TipusCamp.BOOLEAN.equals(tipus)) {
			return Boolean.class;
		} else if (TipusCamp.TEXTAREA.equals(tipus)) {
			return String.class;
		} else if (TipusCamp.DATE.equals(tipus)) {
			return Date.class;
		} else if (TipusCamp.PRICE.equals(tipus)) {
			return BigDecimal.class;
		} else if (TipusCamp.TERMINI.equals(tipus)) {
			return Termini.class;
		} else {
			return String.class;
		}
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
		Camp other = (Camp) obj;
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
