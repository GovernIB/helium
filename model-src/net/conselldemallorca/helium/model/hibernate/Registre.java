/**
 * 
 */
package net.conselldemallorca.helium.model.hibernate;

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
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * Objecte de domini que representa el registre de modificacions a 
 * damunt un expedient.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Entity
@Table(name="hel_registre")
public class Registre implements Serializable, GenericEntity<Long> {

	public enum Accio {
		CREAR,
		MODIFICAR,
		ESBORRAR,
		CONSULTAR,
		INICIAR,
		ATURAR,
		REPRENDRE,
		FINALITZAR,
		CANCELAR
	}
	public enum Entitat {
		EXPEDIENT,
		INSTANCIA_PROCES,
		TASCA,
		TERMINI
	}

	private Long id;
	@NotNull
	Date data;
	@NotNull
	private Long expedientId;
	@MaxLength(255)
	private String processInstanceId;
	@NotBlank
	@MaxLength(64)
	private String responsableCodi;
	@NotNull
	private Accio accio;
	@NotNull
	private Entitat entitat;
	@NotBlank
	@MaxLength(255)
	private String entitatId;
	@MaxLength(1024)
	private String missatge;
	@MaxLength(1024)
	private String valorVell;
	@MaxLength(1024)
	private String valorNou;



	public Registre() {}
	public Registre(
			Date data,
			Long expedientId,
			String responsableCodi,
			Accio accio,
			Entitat entitat,
			String entitatId) {
		this.data = data;
		this.expedientId = expedientId;
		this.responsableCodi = responsableCodi;
		this.accio = accio;
		this.entitat = entitat;
		this.entitatId = entitatId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_registre")
	@TableGenerator(name="gen_registre", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
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

	@Column(name="process_instance_id", length=255)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name="responsable_codi", length=64, nullable=false)
	public String getResponsableCodi() {
		return responsableCodi;
	}
	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	@Column(name="accio", nullable=false)
	public Accio getAccio() {
		return accio;
	}
	public void setAccio(Accio accio) {
		this.accio = accio;
	}

	@Column(name="entitat", nullable=false)
	public Entitat getEntitat() {
		return entitat;
	}
	public void setEntitat(Entitat entitat) {
		this.entitat = entitat;
	}

	@Column(name="entitat_id", length=255, nullable=false)
	public String getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(String entitatId) {
		this.entitatId = entitatId;
	}

	@Column(name="missatge", length=1024)
	public String getMissatge() {
		return missatge;
	}
	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

	@Column(name="valor_vell", length=1024)
	public String getValorVell() {
		return valorVell;
	}
	public void setValorVell(String valorVell) {
		this.valorVell = valorVell;
	}

	@Column(name="valor_nou", length=1024)
	public String getValorNou() {
		return valorNou;
	}
	public void setValorNou(String valorNou) {
		this.valorNou = valorNou;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accio == null) ? 0 : accio.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((entitat == null) ? 0 : entitat.hashCode());
		result = prime * result
				+ ((entitatId == null) ? 0 : entitatId.hashCode());
		result = prime * result
				+ ((expedientId == null) ? 0 : expedientId.hashCode());
		result = prime
				* result
				+ ((processInstanceId == null) ? 0 : processInstanceId
						.hashCode());
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
		Registre other = (Registre) obj;
		if (accio == null) {
			if (other.accio != null)
				return false;
		} else if (!accio.equals(other.accio))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (entitat == null) {
			if (other.entitat != null)
				return false;
		} else if (!entitat.equals(other.entitat))
			return false;
		if (entitatId == null) {
			if (other.entitatId != null)
				return false;
		} else if (!entitatId.equals(other.entitatId))
			return false;
		if (expedientId == null) {
			if (other.expedientId != null)
				return false;
		} else if (!expedientId.equals(other.expedientId))
			return false;
		if (processInstanceId == null) {
			if (other.processInstanceId != null)
				return false;
		} else if (!processInstanceId.equals(other.processInstanceId))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
