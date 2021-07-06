/**
 * 
 */
package es.caib.helium.client.dada.model;

import java.util.List;

import es.caib.helium.client.dada.enums.Tipus;
import lombok.Data;

@Data
public class Dada {

	private String id;
	private String codi;
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private Long procesId;
	   
}
