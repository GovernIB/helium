package es.caib.helium.dada.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import es.caib.helium.dada.domain.ValorSimple;
import es.caib.helium.enums.Tipus;
import es.caib.helium.enums.TipusFiltre;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
