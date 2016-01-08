/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una enumeració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(	name="hel_enumeracio",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "entorn_id", "expedient_tipus_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_enumeracio",
		indexes = @Index(name = "hel_enum_entorn_i", columnNames = {"entorn_id"}))
public class Enumeracio implements Serializable {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String codi;
	@NotBlank
	@MaxLength(255)
	private String nom;
	@MaxLength(4000)
	private String valors;

	@NotNull
	private Entorn entorn;
	private ExpedientTipus expedientTipus;

	private Set<Camp> camps = new HashSet<Camp>();
	private List<EnumeracioValors> enumeracioValors = new ArrayList<EnumeracioValors>();



	public Enumeracio() {}
	public Enumeracio(Entorn entorn, String codi, String nom) {
		this.entorn = entorn;
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_enumeracio")
	@TableGenerator(name="gen_enumeracio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codi", length=64, nullable=false)
	public String getCodi() {
		return this.codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}

	@Column(name="nom", length=255, nullable=false)
	public String getNom() {
		return this.nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

	@Column(name="valors", length=4000)
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="entorn_id")
	@ForeignKey(name="hel_entorn_enumeracio_fk")
	public Entorn getEntorn() {
		return entorn;
	}
	public void setEntorn(Entorn entorn) {
		this.entorn = entorn;
	}

	@ManyToOne
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_enumeracio_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	@OneToMany(mappedBy="enumeracio", cascade={CascadeType.ALL})
	public Set<Camp> getCamps() {
		return this.camps;
	}
	public void setCamps(Set<Camp> camps) {
		this.camps = camps;
	}
	public void addCamp(Camp camp) {
		getCamps().add(camp);
	}
	public void removeCamp(Camp camp) {
		getCamps().remove(camp);
	}

	@Transient
	public List<String[]> getLlistaValors() {
		List<String[]> resposta = new ArrayList<String[]>();
		if (valors != null) {
			String[] parelles = valors.split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parts = parelles[i].split(":");
				if (parts.length == 2) {
					String[] codiValor = new String[2];
					codiValor[0] = parts[0];
					codiValor[1] = parts[1];
					resposta.add(codiValor);
				}
			}
		}
		return resposta;
	}

	@OneToMany(mappedBy="enumeracio", cascade={CascadeType.ALL})
	@OrderBy("ordre asc, id asc")
	public List<EnumeracioValors> getEnumeracioValors() {
		return this.enumeracioValors;
	}
	public void setEnumeracioValors(List<EnumeracioValors> enumeracioValors) {
		this.enumeracioValors = enumeracioValors;
	}
	public void addEnumeracioValors(EnumeracioValors enumeracioValors) {
		getEnumeracioValors().add(enumeracioValors);
	}
	public void removeEnumeracioValors(EnumeracioValors enumeracioValors) {
		getEnumeracioValors().remove(enumeracioValors);
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
		Enumeracio other = (Enumeracio) obj;
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

	private static final long serialVersionUID = -4869633305652583392L;

}
