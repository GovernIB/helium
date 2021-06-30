package es.caib.helium.client.dada.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonTypeName;

import es.caib.helium.client.dada.model.Dada.Valor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Clase que representa el Valor tipus ValorSimple que pot contenir l'atribut valor de {@link es.caib.helium.dada.domain.Dada}
 */
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
