/**
 * 
 */
package net.conselldemallorca.helium.core.model.exportacio;

import java.io.Serializable;



/**
 * DTO amb informació d'un camp de formulari per exportar
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaTascaExportacio implements Serializable {

	private String documentCodi;

	private boolean required;
	private int order;



	public FirmaTascaExportacio(
			String documentCodi,
			boolean required,
			int order) {
		this.documentCodi = documentCodi;
		this.required = required;
		this.order = order;
	}

	public String getDocumentCodi() {
		return documentCodi;
	}
	public void setDocumentCodi(String documentCodi) {
		this.documentCodi = documentCodi;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}



	private static final long serialVersionUID = 1L;

}
