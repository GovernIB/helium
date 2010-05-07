/**
 * 
 */
package net.conselldemallorca.helium.model.dto;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;

/**
 * DTO amb informació d'una definició de procés
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class DefinicioProcesDto extends DefinicioProces {

	private String jbpmName;
	private Long[] idsWithSameKey;
	private String[] idsMostrarWithSameKey;
	private String[] jbpmIdsWithSameKey;
	private Boolean[] hasStartTaskWithSameKey;
	private boolean lastVersion;
	private boolean hasStartTask;

	private ExpedientTipus expedientTipus;



	public String getJbpmName() {
		return jbpmName;
	}
	public void setJbpmName(String jbpmName) {
		this.jbpmName = jbpmName;
	}

	public Long[] getIdsWithSameKey() {
		return idsWithSameKey;
	}
	public void setIdsWithSameKey(Long[] idsWithSameKey) {
		this.idsWithSameKey = idsWithSameKey;
	}

	public String[] getIdsMostrarWithSameKey() {
		return idsMostrarWithSameKey;
	}
	public void setIdsMostrarWithSameKey(String[] idsMostrarWithSameKey) {
		this.idsMostrarWithSameKey = idsMostrarWithSameKey;
	}

	public String[] getJbpmIdsWithSameKey() {
		return jbpmIdsWithSameKey;
	}
	public void setJbpmIdsWithSameKey(String[] jbpmIdsWithSameKey) {
		this.jbpmIdsWithSameKey = jbpmIdsWithSameKey;
	}

	public Boolean[] getHasStartTaskWithSameKey() {
		return hasStartTaskWithSameKey;
	}
	public void setHasStartTaskWithSameKey(Boolean[] hasStartTaskWithSameKey) {
		this.hasStartTaskWithSameKey = hasStartTaskWithSameKey;
	}

	public boolean isLastVersion() {
		return lastVersion;
	}
	public void setLastVersion(boolean lastVersion) {
		this.lastVersion = lastVersion;
	}

	public boolean isHasStartTask() {
		return hasStartTask;
	}
	public void setHasStartTask(boolean hasStartTask) {
		this.hasStartTask = hasStartTask;
	}

	public int getNumIdsWithSameKey() {
		if (jbpmIdsWithSameKey == null)
			return 0;
		return jbpmIdsWithSameKey.length;
	}

	public ExpedientTipus getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipus expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	public List<JbpmIdAmbDescripcio> getJbpmIdsAmbDescripcio() {
		List<JbpmIdAmbDescripcio> resposta = new ArrayList<JbpmIdAmbDescripcio>();
		for (int i = 0; i < getNumIdsWithSameKey(); i++) {
			resposta.add(new JbpmIdAmbDescripcio(
					getIdsWithSameKey()[i],
					getIdsMostrarWithSameKey()[i]));
		}
		return resposta;
	}



	public class JbpmIdAmbDescripcio {
		private Long jbpmId;
		private String descripcio;
		public JbpmIdAmbDescripcio(Long jbpmId, String descripcio)  {
			this.jbpmId = jbpmId;
			this.descripcio = descripcio;
		}
		public Long getJbpmId() {
			return jbpmId;
		}
		public void setJbpmId(Long jbpmId) {
			this.jbpmId = jbpmId;
		}
		public String getDescripcio() {
			return descripcio;
		}
		public void setDescripcio(String descripcio) {
			this.descripcio = descripcio;
		}
	}

	private static final long serialVersionUID = 1L;

}
