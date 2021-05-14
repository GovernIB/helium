package es.caib.helium.dada.model;

import es.caib.helium.enums.DireccioOrdre;
import es.caib.helium.enums.Collections;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe que representa l'ordre en les {@link es.caib.helium.dada.model.Consulta}
 */
@Getter
@Setter
@ToString
public class Ordre {
	
	private int ordre;
	private DireccioOrdre direccio;
	private Collections tipus;
}
