/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una entrada de log d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="HEL_REPRO")
public class Repro implements Serializable {

	private Long id;
	@NotBlank
	@MaxLength(255)
	private String usuari;
	@NotNull
	private ExpedientTipus expedientTipus;
	@NotBlank
	private String nom;
	@Lob
	@MaxLength(20000)
	private String valors;
	private String tascaCodi;
	
	

	public Repro() {
		super();
	};
	
	public Repro(
			String usuari, 
			ExpedientTipus expedientTipus, 
			String nom,
			String valors,
			String tascaCodi) {
		super();
		this.usuari = usuari;
		this.expedientTipus = expedientTipus;
		this.nom = nom;
		this.valors = valors;
		this.tascaCodi = tascaCodi;
	}

	public Repro(
			String usuari, 
			ExpedientTipus expedientTipus, 
			String nom,
			String valors) {
		super();
		this.usuari = usuari;
		this.expedientTipus = expedientTipus;
		this.nom = nom;
		this.valors = valors;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_repro")
	@TableGenerator(name="gen_repro", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="usuari", length=255, nullable=false)
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	@JoinColumn(name="exptip_id")
	@ForeignKey(name="hel_exptip_repro_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}
	
	@Column(name="tasca_codi", nullable=true)
	public String getTascaCodi() {
		return tascaCodi;
	}
	public void setTascaCodi(String tascaCodi) {
		this.tascaCodi = tascaCodi;
	}

	@Column(name="nom", length=255, nullable=true)
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	@Column(name="valors")
	public String getValors() {
		return valors;
	}
	public void setValors(String valors) {
		this.valors = valors;
	}


	private static final long serialVersionUID = 1L;

}
