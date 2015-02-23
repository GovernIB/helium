package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

/**
 * DTO amb la informació per a obrir una finestra amb el
 * formulari extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FormulariExternDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String formulariId;
	private String url;
	private int width;
	private int height;

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

}
