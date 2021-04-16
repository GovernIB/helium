package es.caib.helium.dada.model;

import es.caib.helium.enums.DireccioOrdre;
import es.caib.helium.enums.Coleccions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Ordre {
	
	private String columna;
	private int ordre;
	private DireccioOrdre direccio;
	private Coleccions tipus;
}
