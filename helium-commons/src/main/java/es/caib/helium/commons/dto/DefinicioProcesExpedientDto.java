/**
 *
 */
package es.caib.helium.commons.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una versió d'una definició de procés.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesExpedientDto {

	private Long id;
	private String bpmId;
	private String bpmKey;
	private int versio = -1;
	private String etiqueta;
	private List<IdAmbEtiqueta> listIdAmbEtiqueta;

	public class IdAmbEtiqueta {
		private boolean hasStartTask;
		private boolean demanaNumeroTitol;
		private String etiqueta;
		private String bpmId;
		private Long id;

		public IdAmbEtiqueta(Long id, String etiqueta, String bpmId, boolean hasStartTask, boolean demanaNumeroTitol)  {
			this.setId(id);
			this.setBpmId(bpmId);
			this.setEtiqueta(etiqueta);
			this.setHasStartTask(hasStartTask);
			this.setDemanaNumeroTitol(demanaNumeroTitol);
		}

		public String getEtiqueta() {
			return etiqueta;
		}

		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public boolean isHasStartTask() {
			return hasStartTask;
		}

		public void setHasStartTask(boolean hasStartTask) {
			this.hasStartTask = hasStartTask;
		}

		public boolean isDemanaNumeroTitol() {
			return demanaNumeroTitol;
		}

		public void setDemanaNumeroTitol(boolean demanaNumeroTitol) {
			this.demanaNumeroTitol = demanaNumeroTitol;
		}

		public String getBpmId() {
			return bpmId;
		}

		public void setBpmId(String bpmId) {
			this.bpmId = bpmId;
		}
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersio() {
		return versio;
	}

	public void setVersio(int versio) {
		this.versio = versio;
	}

	public void addIdAmbEtiquetaId(Long id, String etiqueta, String bpmId, boolean hasStartTask, boolean demanaNumeroTitol) {
		getListIdAmbEtiqueta().add(new IdAmbEtiqueta(id, bpmId, etiqueta, hasStartTask, demanaNumeroTitol));
	}

	public List<IdAmbEtiqueta> getListIdAmbEtiqueta() {
		if (listIdAmbEtiqueta == null)
			listIdAmbEtiqueta = new ArrayList<IdAmbEtiqueta>();
		return listIdAmbEtiqueta;
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
}
