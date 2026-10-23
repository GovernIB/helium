/**
 *
 */
package es.caib.helium.commons.dto;

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
	private String bpmId;
	private String bpmKey;

	private int versio = -1;
	private String etiqueta;
	private Date dataCreacio = new Date();

	private String bpmName;
	private Long[] idsWithSameKey;
	private String[] idsMostrarWithSameKey;
	private String[] bpmIdsWithSameKey;
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
	public String getBpmId() {
		return bpmId;
	}
	public void setBpmId(String bpmId) {
		this.bpmId = bpmId;
	}
	public String getBpmKey() {
		return bpmKey;
	}
	public void setBpmKey(String bpmKey) {
		this.bpmKey = bpmKey;
	}

	public String getBpmName() {
		return bpmName;
	}
	public void setBpmName(String bpmName) {
		this.bpmName = bpmName;
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

	public String[] getBpmIdsWithSameKey() {
		return bpmIdsWithSameKey;
	}
	public void setBpmIdsWithSameKey(String[] bpmIdsWithSameKey) {
		this.bpmIdsWithSameKey = bpmIdsWithSameKey;
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
		if (bpmIdsWithSameKey == null)
			return 0;
		return bpmIdsWithSameKey.length;
	}

	public ExpedientTipusDto getExpedientTipus() {
		return expedientTipus;
	}
	public void setExpedientTipus(ExpedientTipusDto expedientTipus) {
		this.expedientTipus = expedientTipus;
	}

	public List<BpmIdAmbDescripcio> getBpmIdsAmbDescripcio() {
		List<BpmIdAmbDescripcio> resposta = new ArrayList<BpmIdAmbDescripcio>();
		for (int i = 0; i < getNumIdsWithSameKey(); i++) {
			resposta.add(new BpmIdAmbDescripcio(
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
			return etiqueta + " (" + bpmKey + " v." + versio + ")";
		} else {
			return bpmKey + " v." + versio;
		}
	}

	public class BpmIdAmbDescripcio {
		private Long bpmId;
		private String descripcio;
		public BpmIdAmbDescripcio(Long bpmId, String descripcio)  {
			this.bpmId = bpmId;
			this.descripcio = descripcio;
		}
		public Long getBpmId() {
			return bpmId;
		}
		public void setBpmId(Long bpmId) {
			this.bpmId = bpmId;
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
