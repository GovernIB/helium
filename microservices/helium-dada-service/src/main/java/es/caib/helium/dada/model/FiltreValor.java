package es.caib.helium.dada.model;

import java.util.List;

import es.caib.helium.dada.domain.ValorSimple;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FiltreValor {
	
	private String codi;
	private String tipusFiltre; //SIMPLE|RANG
	private boolean dadaCapcalera;
	private String tipus; //STRING / DATE / TERMINI / FLOAT / PREU / INTEGER / BOOLEAN / REGISTRE
	private List<ValorSimple> valor;
}
