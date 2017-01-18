/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import net.conselldemallorca.helium.v3.core.api.dto.TramitSistraEnumDto;

/**
 * Objecte de domini que representa un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name="hel_tramit_sistra")
public class TramitSistra implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	@MaxLength(64)
	private String sistraTramitCodi;
	@NotBlank
	@MaxLength(64)
	private TramitSistraEnumDto tipus;
	@MaxLength(255)
	private String codiVarIdentificadorExpedient;
	private Accio accio;
	
	private List<MapeigSistra> mapeigSistras = new ArrayList<MapeigSistra>();
	
	private ExpedientTipus expedientTipus;
	
	public TramitSistra(){}
	public TramitSistra(String sistraTramitCodi, TramitSistraEnumDto tipus, String codiVarIdentificadorExpedient,
			Accio accio, List<MapeigSistra> mapeigSistras, ExpedientTipus expedientTipus) {
		super();
		this.sistraTramitCodi = sistraTramitCodi;
		this.tipus = tipus;
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
		this.accio = accio;
		this.mapeigSistras = mapeigSistras;
		this.expedientTipus = expedientTipus;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_tramit_sistra")
	@TableGenerator(name="gen_tramit_sistra", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="sistra_codtra", length=64, unique=true)
	public String getSistraTramitCodi() {
		return sistraTramitCodi;
	}
	public void setSistraTramitCodi(String sistraTramitCodi) {
		this.sistraTramitCodi = sistraTramitCodi;
	}

	@OneToMany(mappedBy="tramitSistra", cascade={CascadeType.ALL})
	@OrderBy("codiHelium asc")
	public List<MapeigSistra> getMapeigSistras() {
		return this.mapeigSistras;
	}
	public void setMapeigSistras(List<MapeigSistra> mapeigSistras) {
		this.mapeigSistras = mapeigSistras;
	}
	public void addMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().add(mapeigSistra);
	}
	public void removeMapeigSistras(MapeigSistra mapeigSistra) {
		getMapeigSistras().remove(mapeigSistra);
	}
	
	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	public TramitSistraEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(TramitSistraEnumDto tipus) {
		this.tipus = tipus;
	}

	@Column(name = "codi_var_idexpedient")
	public String getCodiVarIdentificadorExpedient() {
		return codiVarIdentificadorExpedient;
	}
	public void setCodiVarIdentificadorExpedient(String codiVarIdentificadorExpedient) {
		this.codiVarIdentificadorExpedient = codiVarIdentificadorExpedient;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="accio_id")
	@ForeignKey(name="hel_accio_trasistra_fk")
	public Accio getAccio() {
		return accio;
	}
	public void setAccio(Accio accio) {
		this.accio = accio;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name="expedient_tipus_id")
	@ForeignKey(name="hel_exptipus_trasistra_fk")
	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	private static final long serialVersionUID = 1L;

}
