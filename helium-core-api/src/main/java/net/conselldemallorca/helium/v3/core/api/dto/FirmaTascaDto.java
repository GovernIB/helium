/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Objecte de domini que representa la firma d'un document a una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaTascaDto {
	private Long id;
	private boolean required;
	private int order;
	private DocumentDto document;
	private TascaDto tasca;

	public FirmaTascaDto() {}
	public FirmaTascaDto(
			DocumentDto document,
			TascaDto tasca,
			boolean required,
			int order) {
		this.document = document;
		this.tasca = tasca;
		this.required = required;
		this.order = order;
	}

	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public TascaDto getTasca() {
		return tasca;
	}
	public void setTasca(TascaDto tasca) {
		this.tasca = tasca;
	}
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

	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FirmaTascaDto other = (FirmaTascaDto) obj;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (tasca == null) {
			if (other.tasca != null)
				return false;
		} else if (!tasca.equals(other.tasca))
			return false;
		return true;
	}
}
