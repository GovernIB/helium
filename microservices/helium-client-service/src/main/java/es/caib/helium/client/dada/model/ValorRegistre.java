package es.caib.helium.client.dada.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * Clase que representa el Valor tipus ValorRegistre que pot contenir l'atribut valor de {@link es.caib.helium.client.dada.model.Dada}
 */
@Getter
@Setter
@ToString
@JsonTypeName("valorRegistre")
public class ValorRegistre extends Valor {

	private List<Dada> camps;

	public String getValors() {

		var valors = "";
		for (var camp : camps) {
			valors += camp.getValors();
		}
		return valors;
	}
	
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
