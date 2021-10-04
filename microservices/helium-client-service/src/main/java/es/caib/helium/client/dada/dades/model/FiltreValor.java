package es.caib.helium.client.dada.dades.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import es.caib.helium.client.dada.dades.enums.Tipus;
import es.caib.helium.client.dada.dades.enums.TipusFiltre;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/** 
 * Classe per especificar els filtres que s'aplicaran a les dades que no son de capçalera {@link es.caib.helium.dada.domain.Dada}
 */
@Getter
@Setter
@ToString
@JsonTypeName(FiltreValor.JSON_TYPE_NAME)
public class FiltreValor extends Filtre {

	public static final String JSON_TYPE_NAME = "filtreValor";

	private String codi;
	private TipusFiltre tipusFiltre; //SIMPLE|RANG
	private Tipus tipus;
	private List<ValorSimple> valor;
}
