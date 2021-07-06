/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Index;

/**
 * Classe del model de dades que representa reindexació pendent per un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "hel_expedient_reindexacio")
@org.hibernate.annotations.Table(
		appliesTo = "hel_expedient_reindexacio",
		indexes = {
				@Index(name = "hel_reindexacio_expedient_fk_i", columnNames = {"expedient_id"})})
public class ExpedientReindexacio implements Serializable, GenericEntity<Long> {

	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_reindexacio")
	@TableGenerator(name="gen_reindexacio", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	private Long id;
	
	/** Id de l'expedient al qual s'associa la reindexació. */
	@Column(name="expedient_id")
	private Long expedientId;

	@Column(name = "data_reindexacio", nullable = false)
	private Date dataReindexacio;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public Date getDataReindexacio() {
		return dataReindexacio;
	}
	public void setDataReindexacio(Date dataReindexacio) {
		this.dataReindexacio = dataReindexacio;
	}
	
	private static final long serialVersionUID = 1815997738055924981L;
}
