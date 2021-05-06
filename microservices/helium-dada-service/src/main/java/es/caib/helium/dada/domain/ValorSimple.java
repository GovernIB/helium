package es.caib.helium.dada.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorSimple")
public class ValorSimple extends Valor {

	private String valor;
	private String valorText;

	@Override
	public boolean equals(Object valorSimple) {
		
		if (valorSimple == this) {
			return true;
		}

		if (!(valorSimple instanceof ValorSimple)) {
			return false;
		}
		
		var vs = (ValorSimple) valorSimple;
		
		return (valor != null && vs.getValor() != null) ? valor.equals(vs.getValor()): true
			&& (valorText != null && vs.getValorText() != null) ? valorText.equals(vs.getValorText()): true;
	}
}
