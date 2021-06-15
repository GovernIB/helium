/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO amb informació d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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

	private ExpedientTipusDto expedientTipus;
	
	/** Comptador per mostrar el número de versions al llistat de definicions de procés. */
	private Long versioCount;

	public int getNumIdsWithSameKey() {
		if (jbpmIdsWithSameKey == null)
			return 0;
		return jbpmIdsWithSameKey.length;
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

}
