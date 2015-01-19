/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una versió d'una definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesIniciExpedientDto {

	private Long id;
	private int versio = -1;
	private String etiqueta;
	private List<IdAmbEtiqueta> listIdAmbEtiqueta;

	public class IdAmbEtiqueta {
		private boolean hasStartTask;
		private boolean demanaNumeroTitol;
		private String etiqueta;
		private Long id;
		
		public IdAmbEtiqueta(Long id, String etiqueta, boolean hasStartTask, boolean demanaNumeroTitol)  {
			this.setId(id);
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

	public void addIdAmbEtiquetaId(Long id, String etiqueta, boolean hasStartTask, boolean demanaNumeroTitol) {
		getListIdAmbEtiqueta().add(new IdAmbEtiqueta(id, etiqueta, hasStartTask, demanaNumeroTitol));
	}

	public List<IdAmbEtiqueta> getListIdAmbEtiqueta() {
		if (listIdAmbEtiqueta == null)
			listIdAmbEtiqueta = new ArrayList<IdAmbEtiqueta>();
		return listIdAmbEtiqueta;
	}
}
