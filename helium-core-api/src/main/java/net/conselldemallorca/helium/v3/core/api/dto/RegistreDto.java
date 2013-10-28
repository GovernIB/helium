/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.Date;

/**
 * Objecte de domini que representa el registre de modificacions a damunt un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreDto {

	public enum Accio {
		CREAR, MODIFICAR, ESBORRAR, CONSULTAR, INICIAR, ATURAR, REPRENDRE, FINALITZAR, CANCELAR, ANULAR
	}

	public enum Entitat {
		EXPEDIENT, INSTANCIA_PROCES, TASCA, TERMINI
	}

	private Long id;
	Date data;
	private Long expedientId;
	private String processInstanceId;
	private String responsableCodi;
	private Accio accio;
	private Entitat entitat;
	private String entitatId;
	private String missatge;
	private String valorVell;
	private String valorNou;

	public RegistreDto() {
	}

	public RegistreDto(Date data, Long expedientId, String responsableCodi, Accio accio, Entitat entitat, String entitatId) {
		this.data = data;
		this.expedientId = expedientId;
		this.responsableCodi = responsableCodi;
		this.accio = accio;
		this.entitat = entitat;
		this.entitatId = entitatId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Long getExpedientId() {
		return expedientId;
	}

	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getResponsableCodi() {
		return responsableCodi;
	}

	public void setResponsableCodi(String responsableCodi) {
		this.responsableCodi = responsableCodi;
	}

	public Accio getAccio() {
		return accio;
	}

	public void setAccio(Accio accio) {
		this.accio = accio;
	}

	public Entitat getEntitat() {
		return entitat;
	}

	public void setEntitat(Entitat entitat) {
		this.entitat = entitat;
	}

	public String getEntitatId() {
		return entitatId;
	}

	public void setEntitatId(String entitatId) {
		this.entitatId = entitatId;
	}

	public String getMissatge() {
		return missatge;
	}

	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

	public String getValorVell() {
		return valorVell;
	}

	public void setValorVell(String valorVell) {
		this.valorVell = valorVell;
	}

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
		result = prime * result + ((entitatId == null) ? 0 : entitatId.hashCode());
		result = prime * result + ((expedientId == null) ? 0 : expedientId.hashCode());
		result = prime * result + ((processInstanceId == null) ? 0 : processInstanceId.hashCode());
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
		RegistreDto other = (RegistreDto) obj;
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
