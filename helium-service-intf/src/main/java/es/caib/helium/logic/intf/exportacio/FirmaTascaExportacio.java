/**
 * 
 */
package es.caib.helium.logic.intf.exportacio;

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
	/** Indica si el camp està lligat al tipus d'expedient.*/
	private boolean tipusExpedient;
	/** Indica si el doucment està heretat pel tipus d'expedient. */
	private boolean documentHeretat;



	public FirmaTascaExportacio(
			String documentCodi,
			boolean required,
			int order,
			boolean tipusExpedient,
			boolean documentHeretat) {
		this.documentCodi = documentCodi;
		this.required = required;
		this.order = order;
		this.setTipusExpedient(tipusExpedient);
		this.documentHeretat = documentHeretat;
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
	public boolean isTipusExpedient() {
		return tipusExpedient;
	}
	public void setTipusExpedient(boolean tipusExpedient) {
		this.tipusExpedient = tipusExpedient;
	}
	public boolean isDocumentHeretat() {
		return documentHeretat;
	}
	public void setDocumentHeretat(boolean documentHeretat) {
		this.documentHeretat = documentHeretat;
	}
	private static final long serialVersionUID = 1L;

}
