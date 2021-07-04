/**
 * 
 */
package es.caib.helium.persist.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_accio",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id", "expedient_tipus_id"})},
		indexes = {
				@Index(name = "hel_accio_defproc_i", columnList = "definicio_proces_id"),
				@Index(name = "hel_accio_extip_i", columnList = "expedient_tipus_id")
		}
)
public class Accio implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@Size(max = 64)
	private String codi;
	@NotBlank
	@Size(max = 255)
	private String nom;
	@Size(max = 255)
	private String descripcio;
	@NotBlank
	@Size(max = 255)
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	@Size(max = 512)
	private String rols;
	private String cron;

	private DefinicioProces definicioProces;
	private ExpedientTipus expedientTipus;

	@Size(max = 255)
	private String defprocJbpmKey;

	public Accio() {}
	public Accio(DefinicioProces definicioProces, String codi, String nom, String jbpmAction) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
		this.jbpmAction = jbpmAction;
	}
	public Accio(ExpedientTipus expedientTipus, String codi, String nom, String defprocJbpmKey, String jbpmAction) {
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.nom = nom;
		this.defprocJbpmKey = defprocJbpmKey;
		this.jbpmAction = jbpmAction;
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

	@Column(name="jbpm_action", length=255, nullable=false)
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}

	@Column(name="publica")
	public boolean isPublica() {
		return publica;
	}
	public void setPublica(boolean publica) {
		this.publica = publica;
	}

	@Column(name="oculta")
	public boolean isOculta() {
		return oculta;
	}
	public void setOculta(boolean oculta) {
		this.oculta = oculta;
	}

	@Column(name="cron", length=255)
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}

	@Column(name="rols", length=512)
	public String getRols() {
		return rols;
	}
	public void setRols(String rols) {
		this.rols = rols;
	}

	@ManyToOne(optional=true)
	@JoinColumn(
			name="definicio_proces_id",
			foreignKey = @ForeignKey(name="hel_defproc_accio_fk"))
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@ManyToOne(optional=true)
	@JoinColumn(
			name="expedient_tipus_id",
			foreignKey = @ForeignKey(name="hel_exptip_accio"))
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	@Column(name="defproc_jbpmkey", length=255)
	public String getDefprocJbpmKey() {
		return defprocJbpmKey;
	}
	public void setDefprocJbpmKey(String defprocJbpmKey) {
		this.defprocJbpmKey = defprocJbpmKey;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		result = prime * result
				+ ((defprocJbpmKey == null) ? 0 : defprocJbpmKey.hashCode());
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
		Accio other = (Accio) obj;
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
		if (expedientTipus == null) {
			if (other.expedientTipus != null)
				return false;
		} else if (!expedientTipus.equals(other.expedientTipus))
			return false;
		if (defprocJbpmKey == null) {
			if (other.defprocJbpmKey != null)
				return false;
		} else if (!defprocJbpmKey.equals(other.defprocJbpmKey))
			return false;
		
		return true;
	}

	private static final long serialVersionUID = 1L;
}
