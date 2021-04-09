package es.caib.helium.dada.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ValorSimple extends Valor {

	private String valor;
	private String valorText;
}
