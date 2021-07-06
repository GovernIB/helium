/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.util.HashSet;
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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una àrea de l'organigrama.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(	name="hel_area",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_area",
		indexes = {
				@Index(name = "hel_area_tipus_i", columnNames = {"tipus_id"}),
				@Index(name = "hel_area_entorn_i", columnNames = {"entorn_id"}),
				@Index(name = "hel_area_pare_i", columnNames = {"pare_id"})})
public class Area implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(255)
	private String descripcio;

	@NotNull
	private AreaTipus tipus;
	@NotNull
	private Entorn entorn;
	private Area pare;

	private Set<Area> fills = new HashSet<Area>();
	private Set<Carrec> carrecs = new HashSet<Carrec>();
	private Set<AreaMembre> membres = new HashSet<AreaMembre>();



	public Area() {}
	public Area(String codi, String nom, Entorn entorn) {
		this.codi = codi;
		this.nom = nom;
		this.entorn = entorn;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_area")
	@TableGenerator(name="gen_area", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@ManyToOne(optional=false)
	@JoinColumn(name="tipus_id")
	@ForeignKey(name="hel_areatipus_area_fk")
	public AreaTipus getTipus() {
		return tipus;
	}
	public void setTipus(AreaTipus tipus) {
		this.tipus = tipus;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_area_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="pare_id")
	@ForeignKey(name="hel_area_area_fk")
	public Area getPare() {
		return pare;
	}
	public void setPare(Area pare) {
		this.pare = pare;
	}

	@OneToMany(mappedBy="pare")
	public Set<Area> getFills() {
		return this.fills;
	}
	public void setFills(Set<Area> fills) {
		this.fills = fills;
	}
	public void addFill(Area fill) {
		getFills().add(fill);
	}
	public void removeFill(Area fill) {
		getFills().remove(fill);
	}

	@OneToMany(mappedBy="area", cascade=CascadeType.REMOVE)
	public Set<Carrec> getCarrecs() {
		return this.carrecs;
	}
	public void setCarrecs(Set<Carrec> carrecs) {
		this.carrecs = carrecs;
	}
	public void addCarrec(Carrec carrec) {
		getCarrecs().add(carrec);
	}
	public void removeCarrec(Carrec carrec) {
		getCarrecs().remove(carrec);
	}

	@OneToMany(mappedBy="area", cascade=CascadeType.ALL)
	public Set<AreaMembre> getMembres() {
		return this.membres;
	}
	public void setMembres(Set<AreaMembre> membres) {
		this.membres = membres;
	}
	public void addMembre(AreaMembre membre) {
		getMembres().add(membre);
	}
	public void removeMembre(AreaMembre membre) {
		getMembres().remove(membre);
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
		Area other = (Area) obj;
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
