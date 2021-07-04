/**
 * 
 */
package es.caib.helium.persist.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Objecte de domini que representa l'estat d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(	name="hel_estat",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "expedient_tipus_id"})},
		indexes = @Index(name = "hel_estat_exptip_i", columnList = "expedient_tipus_id")
)
public class Estat implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 64)
	private String codi;
	@NotBlank
	@Size(max = 255)
	private String nom;
	@NotNull
	private int ordre;

	@NotNull
	private ExpedientTipus expedientTipus;



	public Estat() {}
	public Estat(ExpedientTipus expedientTipus, String codi, String nom) {
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.nom = nom;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_estat")
	@TableGenerator(name="gen_estat", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="ordre", nullable=false)
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	@Transient
	public String getTipusAmbNom() {
		if (expedientTipus == null)
			return nom;
		else
			return expedientTipus.getNom() + "/" + nom;
	}

	@ManyToOne(optional=false)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_exptipus_estat_fk"))
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
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
		Estat other = (Estat) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
