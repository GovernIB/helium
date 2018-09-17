/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO amb informació d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesDto extends HeretableDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private EntornDto entorn;
	private String jbpmId;
	private String jbpmKey;
	
	private int versio = -1;
	private String etiqueta;
	private Date dataCreacio = new Date();

	private String jbpmName;
	private Long[] idsWithSameKey;
	private String[] idsMostrarWithSameKey;
	private String[] jbpmIdsWithSameKey;
	private Boolean[] hasStartTaskWithSameKey;
	private boolean lastVersion;
	private boolean hasStartTask;
	private String startTaskName;

	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public Date getDataCreacio() {
		return dataCreacio;
	}
	public void setDataCreacio(Date dataCreacio) {
		this.dataCreacio = dataCreacio;
	}



	private ExpedientTipusDto expedientTipus;
	
	/** Comptador per mostrar el número de versions al llistat de definicions de procés. */
	private Long versioCount;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public EntornDto getEntorn() {
		return entorn;
	}
	public void setEntorn(EntornDto entorn) {
		this.entorn = entorn;
	}
	public String getJbpmId() {
		return jbpmId;
	}
	public void setJbpmId(String jbpmId) {
		this.jbpmId = jbpmId;
	}
	public String getJbpmKey() {
		return jbpmKey;
	}
	public void setJbpmKey(String jbpmKey) {
		this.jbpmKey = jbpmKey;
	}

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

	public String getStartTaskName() {
		return startTaskName;
	}
	public void setStartTaskName(String startTaskName) {
		this.startTaskName = startTaskName;
	}

	public int getNumIdsWithSameKey() {
		if (jbpmIdsWithSameKey == null)
			return 0;
		return jbpmIdsWithSameKey.length;
	}

	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
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



	public int getVersio() {
		return versio;
	}
	public void setVersio(int versio) {
		this.versio = versio;
	}

	public String getIdPerMostrar() {
		if (etiqueta != null) {
			return etiqueta + " (" + jbpmKey + " v." + versio + ")";
		} else {
			return jbpmKey + " v." + versio;
		}
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

	public void setVersioCount(Long count) {
		this.versioCount = count;
	}
	public Long getVersioCount() {
		return this.versioCount;
	}
}
