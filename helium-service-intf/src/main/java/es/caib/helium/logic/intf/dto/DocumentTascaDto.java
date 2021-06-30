/**
 * 
 */
package es.caib.helium.logic.intf.dto;

/**
 * DTO amb informació d'un document d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentTascaDto extends HeretableDto {

	private static final long serialVersionUID = -8552509655331736854L;

	private Long id;

	private boolean required;
	private boolean readOnly;
	private int order;

	private DocumentDto document;
	
	/** Quan es crea una relació entre un camp i la definició de procés pot ser que el camp sigui 
	 * del tipus d'expedient i la tasca sigui de la definició de procés del tipus expedient pare heretat. Aquest
	 * camp fa referència al tipus d'expedient que posseix la relació camp-tasca.
	 */
	private Long expedientTipusId;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public Long getExpedientTipusId() {
		return expedientTipusId;
	}
	public void setExpedientTipusId(Long expedientTipusId) {
		this.expedientTipusId = expedientTipusId;
	}

}
