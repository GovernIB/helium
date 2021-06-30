/**
 * 
 */
package es.caib.helium.client.dada.model;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Dada {

	private String id;
	private String codi;
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private Long procesId;

	@Data
	public static class Valor {

		private String valor;
		private String valorText;

	}

	public enum Tipus {

		Long("Long"),
		String("String"),
		Date("Date"),
		Float("Float"),
		Termini("Termini"),
		Preu("Preu"),
		Integer("Integer"),
		Boolean("Boolean"),
		Registre("Registre");

		private String valor;

		private Tipus(String valor) {
			this.valor = valor;
		}
	}

}
