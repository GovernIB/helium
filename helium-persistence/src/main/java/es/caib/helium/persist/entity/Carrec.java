/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import es.caib.helium.persist.entity.Persona.Sexe;

/**
 * Objecte de domini que representa un càrrec de l'organigrama.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(	name="hel_carrec",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_carrec",
		indexes = {
				@Index(name = "hel_carrec_area_i", columnNames = {"area_id"}),
				@Index(name = "hel_carrec_entorn_i", columnNames = {"entorn_id"})})
public class Carrec implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nomHome;
	@NotBlank
	@MaxLength(255)
	private String nomDona;
	@NotBlank
	@MaxLength(255)
	private String tractamentHome;
	@NotBlank
	@MaxLength(255)
	private String tractamentDona;
	@MaxLength(255)
	private String descripcio;
	@MaxLength(64)
	private String personaCodi;
	private Sexe personaSexe;

	@NotNull
	private Area area;
	@NotNull
	private Entorn entorn;



	public Carrec() {}
	public Carrec(String codi, String nomHome, String nomDona, String tractamentHome, String tractamentDona, Entorn entorn) {
		this.codi = codi;
		this.nomHome = nomHome;
		this.nomDona = nomDona;
		this.tractamentHome = tractamentHome;
		this.tractamentDona = tractamentDona;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_carrec")
	@TableGenerator(name="gen_carrec", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="nom_home", length=255, nullable=false)
	public String getNomHome() {
		return nomHome;
	}
	public void setNomHome(String nomHome) {
		this.nomHome = nomHome;
	}
	
	@Column(name="nom_dona", length=255, nullable=false)
	public String getNomDona() {
		return nomDona;
	}
	public void setNomDona(String nomDona) {
		this.nomDona = nomDona;
	}
	
	@Column(name="tractament_home", length=255, nullable=false)
	public String getTractamentHome() {
		return tractamentHome;
	}
	public void setTractamentHome(String tractamentHome) {
		this.tractamentHome = tractamentHome;
	}
	
	@Column(name="carrec_dona", length=255, nullable=false)
	public String getTractamentDona() {
		return tractamentDona;
	}
	public void setTractamentDona(String tractamentDona) {
		this.tractamentDona = tractamentDona;
	}

	@Column(name="descripcio", length=255)
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Column(name="persona_codi", length=64)
	public String getPersonaCodi() {
		return personaCodi;
	}
	public void setPersonaCodi(String personaCodi) {
		this.personaCodi = personaCodi;
	}

	@Column(name="persona_sexe")
	public Sexe getPersonaSexe() {
		return personaSexe;
	}
	public void setPersonaSexe(Sexe personaSexe) {
		this.personaSexe = personaSexe;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="area_id")
	@ForeignKey(name="hel_area_carrec_fk")
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_carrec_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@Transient
	public String getNomHomeDona() {
		return nomHome + "/" + nomDona;
	}
	@Transient
	public String getTractamentPerSexe() {
		if (personaSexe == null)
			return tractamentHome;
		return (personaSexe.equals(Sexe.SEXE_HOME)) ? tractamentHome.trim() : tractamentDona.trim();
	}
	@Transient
	public String getNomPerSexe() {
		if (personaSexe == null)
			return tractamentHome;
		return (personaSexe.equals(Sexe.SEXE_HOME)) ? nomHome.trim() : nomDona.trim();
	}
	@Transient
	public String getNomAmbTractamentPerSexe() {
		if (personaSexe == null)
			return tractamentHome;
		if (personaSexe.equals(Sexe.SEXE_HOME))
			return tractamentHome.trim() + " " + nomHome.trim();
		else
			return tractamentDona.trim() + " " + nomDona.trim();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
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
		Carrec other = (Carrec) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
