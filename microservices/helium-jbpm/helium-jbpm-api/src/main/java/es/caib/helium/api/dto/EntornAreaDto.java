package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EntornAreaDto {

	private Long id;
	private String codi;
	private String descripcio;
	private String nom;
	private Long entornId;
	private Long pareId;
	private Long tipusId;
	
	private EntornAreaDto pare;
	private EntornTipusAreaDto tipus;
	
}
