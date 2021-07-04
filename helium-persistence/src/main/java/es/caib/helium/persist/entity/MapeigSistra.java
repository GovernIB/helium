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
@Table(	name="hel_map_sistra",
		uniqueConstraints={@UniqueConstraint(columnNames={"codiHelium", "expedient_tipus_id"})},
		indexes = @Index(name = "hel_map_sistra_exptip_i", columnList = "expedient_tipus_id")
)
public class MapeigSistra implements Serializable, GenericEntity<Long> {

	public enum TipusMapeig {
		Variable,
		Document,
		Adjunt
	}
	
	private Long id;
	@NotBlank
	@Size(max = 255)
	private String codiHelium;
	@NotBlank
	@Size(max = 255)
	private String codiSistra;
	@NotNull
	private TipusMapeig tipus;

	@NotNull
	private ExpedientTipus expedientTipus;


	public MapeigSistra() {}
	public MapeigSistra(ExpedientTipus expedientTipus, String codiHelium, String codiSistra, TipusMapeig tipus) {
		this.expedientTipus = expedientTipus;
		this.codiHelium = codiHelium;
		this.codiSistra = codiSistra;
		this.tipus = tipus;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_map_sistra")
	@TableGenerator(name="gen_map_sistra", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="codiHelium", length=255, nullable=false)
	public String getCodiHelium() {
		return codiHelium;
	}
	public void setCodiHelium(String codiHelium) {
		this.codiHelium = codiHelium;
	}

	@Column(name="codiSistra", length=255, nullable=false)
	public String getCodiSistra() {
		return codiSistra;
	}
	public void setCodiSistra(String codiSistra) {
		this.codiSistra = codiSistra;
	}

	@Column(name="tipus", length=255, nullable=false)
	public TipusMapeig getTipus() {
		return tipus;
	}
	public void setTipus(TipusMapeig tipus) {
		this.tipus = tipus;
	}

	/*@Transient
	public String getTipusAmbNom() {
		if (expedientTipus == null)
			return nom;
		else
			return expedientTipus.getNom() + "/" + nom;
	}*/

	@ManyToOne(optional=false)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_exptipus_map_sistra_fk"))
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
		result = prime * result + ((codiHelium == null) ? 0 : codiHelium.hashCode());
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
		MapeigSistra other = (MapeigSistra) obj;
		if (codiHelium == null) {
			if (other.codiHelium != null)
				return false;
		} else if (!codiHelium.equals(other.codiHelium))
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
