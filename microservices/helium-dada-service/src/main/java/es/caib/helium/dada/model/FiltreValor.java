package es.caib.helium.dada.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import es.caib.helium.dada.domain.ValorSimple;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonTypeName("filtreValor")
public class FiltreValor extends Filtre {
	
	private String codi;
	private String tipusFiltre; //SIMPLE|RANG
	private String tipus; //STRING / DATE / TERMINI / FLOAT / PREU / INTEGER / BOOLEAN
	private List<ValorSimple> valor;
}
