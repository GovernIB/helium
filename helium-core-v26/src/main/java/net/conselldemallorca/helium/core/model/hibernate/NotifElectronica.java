/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_expedient_notif_electr")
public class NotifElectronica implements Serializable, GenericEntity<Long> {
	private Long id;
	@NotNull
	private Date data;
	@NotNull
	private Long expedientId;
	@MaxLength(255)
	private String numero;
	@MaxLength(255)
	private Long RDSCodi;
	@MaxLength(255)
	private String RDSClave;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_notif_electronica")
	@TableGenerator(name="gen_notif_electronica", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="data", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	@Column(name="expedient_id", nullable=false)
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	
	@Column(name="numero", length=255)
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name="rds_codi", length=255)
	public Long getRDSCodi() {
		return RDSCodi;
	}
	public void setRDSCodi(Long rDSCodi) {
		RDSCodi = rDSCodi;
	}
	
	@Column(name="rds_clave", length=255)
	public String getRDSClave() {
		return RDSClave;
	}
	public void setRDSClave(String rDSClave) {
		RDSClave = rDSClave;
	}

	private static final long serialVersionUID = 1L;
}
