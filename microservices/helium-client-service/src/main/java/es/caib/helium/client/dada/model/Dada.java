/**
 * 
 */
package es.caib.helium.client.dada.model;

import es.caib.helium.client.dada.enums.Tipus;
import lombok.Data;

import java.util.List;

@Data
public class Dada {

	private String id;
	private String codi;
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private String procesId;

	public String getValors() {

			var valorString = "";
			for (var v : valor) {
				valorString += v instanceof ValorSimple ? ((ValorSimple) v).getValor() : "";
			}

			return valorString;
	}
}
