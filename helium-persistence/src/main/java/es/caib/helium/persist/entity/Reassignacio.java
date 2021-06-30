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

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

/**
 * Objecte que representa una reassignació temporal de tasques d'un usuari a un altre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@SuppressWarnings("serial")
@Entity
@Table(name="hel_redir")
public class Reassignacio implements Serializable, GenericEntity<Long> {

	private Long id;
	@NotBlank
	private String usuariOrigen;
	@NotBlank
	private String usuariDesti;
	@NotBlank
	private Date dataInici;
	@NotBlank
	private Date dataFi;
	private Date dataCancelacio;
	private Long tipusExpedientId;

	public Reassignacio() {}
	public Reassignacio(String usuariOrigen, String usuariDesti, Date dataInici, Date dataFi, Long tipusExpedientId) {
		this.usuariOrigen = usuariOrigen;
		this.usuariDesti = usuariDesti;
		this.dataInici = dataInici;
		this.dataFi = dataFi;
		this.tipusExpedientId=tipusExpedientId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_reassignar")
	@TableGenerator(name="gen_reassignar", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="usuari_origen", nullable=false)
	public String getUsuariOrigen() {
		return usuariOrigen;
	}
	public void setUsuariOrigen(String usuariOrigen) {
		this.usuariOrigen = usuariOrigen;
	}

	@Column(name="usuari_desti", nullable=false)
	public String getUsuariDesti() {
		return usuariDesti;
	}
	public void setUsuariDesti(String usuariDesti) {
		this.usuariDesti = usuariDesti;
	}

	@Column(name="data_inici", nullable=false)
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	@Column(name="data_fi", nullable=false)
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	@Column(name="data_cancelacio")
	public Date getDataCancelacio() {
		return dataCancelacio;
	}
	public void setDataCancelacio(Date dataCancelacio) {
		this.dataCancelacio = dataCancelacio;
	}

	@Column(name="expedient_tipus_id")
	public Long getTipusExpedientId() {
		return tipusExpedientId;
	}
	public void setTipusExpedientId(Long tipusExpedientId) {
		this.tipusExpedientId = tipusExpedientId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataFi == null) ? 0 : dataFi.hashCode());
		result = prime * result
				+ ((dataInici == null) ? 0 : dataInici.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((usuariDesti == null) ? 0 : usuariDesti.hashCode());
		result = prime * result
				+ ((usuariOrigen == null) ? 0 : usuariOrigen.hashCode());
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
		Reassignacio other = (Reassignacio) obj;
		if (dataFi == null) {
			if (other.dataFi != null)
				return false;
		} else if (!dataFi.equals(other.dataFi))
			return false;
		if (dataInici == null) {
			if (other.dataInici != null)
				return false;
		} else if (!dataInici.equals(other.dataInici))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (usuariDesti == null) {
			if (other.usuariDesti != null)
				return false;
		} else if (!usuariDesti.equals(other.usuariDesti))
			return false;
		if (usuariOrigen == null) {
			if (other.usuariOrigen != null)
				return false;
		} else if (!usuariOrigen.equals(other.usuariOrigen))
			return false;
		return true;
	}

}
