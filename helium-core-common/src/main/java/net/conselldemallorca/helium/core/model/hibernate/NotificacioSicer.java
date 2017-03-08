/**
 * 
 */
package net.conselldemallorca.helium.core.model.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;

import net.conselldemallorca.helium.v3.core.api.dto.NotificacioSicerEstatEnumDto;

/**
 * Objecte de domini que representa una notificació electronica de un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_notificacio_sicer",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {
				"expedient_id"})})
public class NotificacioSicer implements Serializable, GenericEntity<Long> {

	private Long id;
	private Expedient expedient;
	private NotificacioSicerEstatEnumDto estat;
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_notificacio_sicer")
	@TableGenerator(name="gen_notificacio_sicer", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "hel_expedient_sicer_fk")
	public Expedient getExpedient() {
		return expedient;
	}
	public void setExpedient(Expedient expedient) {
		this.expedient = expedient;
	}
	
	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	public NotificacioSicerEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(NotificacioSicerEstatEnumDto estat) {
		this.estat = estat;
	}

	private static final long serialVersionUID = 1L;
}
