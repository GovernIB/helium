/**
 * 
 */
package es.caib.helium.logic.intf.extern.formulari;

import java.io.Serializable;

/**
 * Resposta per a l'inici de formularis externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaIniciFormulari implements Serializable {

	private String formulariId;
	private String url;
	private int width = -1;
	private int height = -1;

	public RespostaIniciFormulari() {
	}
	public RespostaIniciFormulari(String formulariId, String url) {
		this.formulariId = formulariId;
		this.url = url;
	}

	public String getFormulariId() {
		return formulariId;
	}
	public void setFormulariId(String formulariId) {
		this.formulariId = formulariId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	private static final long serialVersionUID = 1L;

}
