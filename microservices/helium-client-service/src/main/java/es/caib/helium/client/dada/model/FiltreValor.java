package es.caib.helium.client.dada.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import es.caib.helium.client.dada.enums.TipusFiltre;
import es.caib.helium.client.dada.model.Dada.Tipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 
 * Classe per especificar els filtres que s'aplicaran a les dades que no son de capçalera {@link es.caib.helium.dada.domain.Dada}
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
