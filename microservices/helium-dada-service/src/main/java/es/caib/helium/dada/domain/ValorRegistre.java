package es.caib.helium.dada.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Clase que representa el Valor tipus ValorRegistre que pot contenir l'atribut valor de {@link es.caib.helium.dada.domain.Dada}
 */
@Getter
@Setter
@ToString
@Document
@JsonTypeName("valorRegistre")
public class ValorRegistre extends Valor {

	private List<Dada> camps;
	
	@Override
	public boolean equals(Object valorRegistre) {
		
		if (valorRegistre == this) {
			return true;
		}

		if (!(valorRegistre instanceof ValorSimple)) {
			return false;
		}
		
		var vs = (ValorRegistre) valorRegistre;
		
		return (camps != null && !camps.isEmpty()) ? Arrays.equals(camps.toArray(), vs.getCamps().toArray()): true;
	}
}