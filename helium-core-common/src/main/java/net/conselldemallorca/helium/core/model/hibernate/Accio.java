/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import net.conselldemallorca.helium.v3.core.api.dto.AccioTipusEnumDto;

/**
 * Objecte de domini que representa un document de la definició
 * de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_accio",
		uniqueConstraints={@UniqueConstraint(columnNames={"codi", "definicio_proces_id", "expedient_tipus_id"})})
@org.hibernate.annotations.Table(
		appliesTo = "hel_accio",
		indexes = { 
				@Index(name = "hel_accio_defproc_i", columnNames = {"definicio_proces_id"}),
				@Index(name = "hel_accio_extip_i", columnNames = {"expedient_tipus_id"})
		})
public class Accio implements Serializable, GenericEntity<Long> {

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
	private AccioTipusEnumDto tipus;

	@NotBlank
	@MaxLength(255)
	private String jbpmAction;
	private boolean publica;
	private boolean oculta;
	@MaxLength(512)
	private String rols;
	private String cron;

	private DefinicioProces definicioProces;
	private ExpedientTipus expedientTipus;
	
	@MaxLength(255)
	private String defprocJbpmKey;
	
	@MaxLength(255)
	private String handlerClasse;
	@Lob
	@MaxLength(20000)
	private String handlerDades;
	
	@MaxLength(1024)
	private String script;


	public Accio() {}
	public Accio(
			DefinicioProces definicioProces, 
			String codi, 
			String nom, 
			AccioTipusEnumDto tipus) {
		this.definicioProces = definicioProces;
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
	}
	public Accio(
			ExpedientTipus expedientTipus, 
			String codi, 
			String nom, 
			AccioTipusEnumDto tipus) {
		this.expedientTipus = expedientTipus;
		this.codi = codi;
		this.nom = nom;
		this.tipus = tipus;
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

    @Column(name="tipus")
    @Enumerated(EnumType.STRING)
	public AccioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(AccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	
	@Column(name="jbpm_action", length=255, nullable=false)
	public String getJbpmAction() {
		return jbpmAction;
	}
	public void setJbpmAction(String jbpmAction) {
		this.jbpmAction = jbpmAction;
	}

    @Column(name="script")
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
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
	@JoinColumn(name="definicio_proces_id")
	@ForeignKey(name="hel_defproc_accio_fk")
	public DefinicioProces getDefinicioProces() {
		return definicioProces;
	}
	public void setDefinicioProces(DefinicioProces definicioProces) {
		this.definicioProces = definicioProces;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptip_accio")
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

	@Column(name="handler_classe", length=255)
	public String getHandlerClasse() {
		return handlerClasse;
	}
	public void setHandlerClasse(String handlerClasse) {
		this.handlerClasse = handlerClasse;
	}
	
	@Column(name="handler_dades", length=255)
	public String getHandlerDades() {
		return handlerDades;
	}
	public void setHandlerDades(String handlerDades) {
		this.handlerDades = handlerDades;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		result = prime * result
				+ ((tipus == null) ? 0 : tipus.hashCode());
		result = prime * result
				+ ((definicioProces == null) ? 0 : definicioProces.hashCode());
		result = prime * result
				+ ((expedientTipus == null) ? 0 : expedientTipus.hashCode());
		result = prime * result
				+ ((defprocJbpmKey == null) ? 0 : defprocJbpmKey.hashCode());
		result = prime * result
				+ ((handlerClasse == null) ? 0 : handlerClasse.hashCode());
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
		if (tipus == null) {
			if (other.tipus != null)
				return false;
		} else if (!tipus.equals(other.tipus))
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
		if (handlerClasse == null) {
			if (other.handlerClasse != null)
				return false;
		} else if (!handlerClasse.equals(other.handlerClasse))
			return false;
		if (handlerDades == null) {
			if (other.handlerDades != null)
				return false;
		} else if (!handlerDades.equals(other.handlerDades))
			return false;
		
		return true;
	}

	private static final long serialVersionUID = 1L;
}
