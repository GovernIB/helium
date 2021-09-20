/**
 * 
 */
package es.caib.helium.dada.model;

import es.caib.helium.client.dada.dades.enums.Tipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@Document
//@DadaValidacio
public class Dada {

	@Id
	private String id;
	@NotNull
	@Size(max=255)
	private String codi;
	@NotNull
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private String procesId;
	
	@Override
	public boolean equals(Object dada) {
		
		if (dada == this) {
			return true;
		}

		if (!(dada instanceof Dada)) {
			return false;
		}
		
		var dad = (Dada) dada;
		
		return (codi != null && dad.getCodi() != null) ? codi.equals(dad.getCodi()) : true
			   && (tipus != null && dad.getTipus() != null) ? tipus.equals(dad.getTipus()) : true
			   &&  multiple == dad.isMultiple()
			   && (expedientId != null && dad.getExpedientId() != null) ? expedientId.equals(dad.getExpedientId()) : true
			   && (procesId != null && dad.getProcesId() != null) ? procesId.equals(dad.getProcesId()) : true
			   && (valor != null && !valor.isEmpty() && dad.getValor() != null && !dad.getValor().isEmpty()) 
			   ? Arrays.equals(valor.toArray(), dad.getValor().toArray()) : true;		   
	}
}
