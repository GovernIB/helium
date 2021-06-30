package es.caib.helium.client.dada.enums;

import lombok.Getter;

/**
 * Enum per representar la direcció de la ordenació
 */
@Getter
public enum DireccioOrdre {

	ASC("asc"),
	DESC("desc");
	
	private String ordre;
	
	private DireccioOrdre(String ordre) {
		this.ordre = ordre;
	}
}
