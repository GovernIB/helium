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
public class DefinicioProcesVersioDto {

	private Long id;
	
	private int versio = -1;
	private String etiqueta;
	private List<VersioAmbEtiqueta> listVersioAmbEtiqueta;

	public class VersioAmbEtiqueta {
		private int versio;
		private String etiqueta;
		
		public VersioAmbEtiqueta(int versio, String etiqueta)  {
			this.setVersio(versio);
			this.setEtiqueta(etiqueta);
		}

		public String getEtiqueta() {
			return etiqueta;
		}

		public void setEtiqueta(String etiqueta) {
			this.etiqueta = etiqueta;
		}

		public int getVersio() {
			return versio;
		}

		public void setVersio(int versio) {
			this.versio = versio;
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

	public void addVersioAmbEtiqueta(int versio, String etiqueta) {
		getListVersioAmbEtiqueta().add(new VersioAmbEtiqueta(versio, etiqueta));
	}

	public List<VersioAmbEtiqueta> getListVersioAmbEtiqueta() {
		if (listVersioAmbEtiqueta == null)
			listVersioAmbEtiqueta = new ArrayList<VersioAmbEtiqueta>();
		return listVersioAmbEtiqueta;
	}
}
