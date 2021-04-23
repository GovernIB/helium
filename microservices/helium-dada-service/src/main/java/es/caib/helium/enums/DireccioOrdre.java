package es.caib.helium.enums;

import lombok.Getter;

@Getter
public enum DireccioOrdre {

	ASC("asc"),
	DESC("desc");
	
	private String ordre;
	
	private DireccioOrdre(String ordre) {
		this.ordre = ordre;
	}
}
