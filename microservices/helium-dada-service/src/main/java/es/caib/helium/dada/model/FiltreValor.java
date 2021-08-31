package es.caib.helium.dada.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import es.caib.helium.client.dada.enums.Tipus;
import es.caib.helium.client.dada.enums.TipusFiltre;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/** 
 * Classe per especificar els filtres que s'aplicaran a les dades que no son de capçalera {@link es.caib.helium.client.dada.model.Dada}
 */
@Getter
@Setter
@ToString
@JsonTypeName("filtreValor")
public class FiltreValor extends Filtre {
	
	private String codi;
	private TipusFiltre tipusFiltre; //SIMPLE|RANG
	private Tipus tipus;
	private List<ValorSimple> valor;
}
