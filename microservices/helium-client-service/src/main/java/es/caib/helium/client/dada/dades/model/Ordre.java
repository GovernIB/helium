package es.caib.helium.client.dada.dades.model;

import es.caib.helium.client.dada.dades.enums.ColleccionsMongo;
import es.caib.helium.client.dada.dades.enums.DireccioOrdre;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Classe que representa l'ordre en les {@link es.caib.helium.client.dada.dades.model.Consulta}
 */
@Getter
@Setter
@ToString
public class Ordre {
	
	private int ordre;
	private DireccioOrdre direccio;
	private ColleccionsMongo tipus;
}
