/**
 * 
 */
package es.caib.helium.dada.domain;

import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import es.caib.helium.enums.Tipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
public class Dada {

	@Id
	private String id;
	@NotNull
	private String codi;
	@NotNull
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	@NotNull
	private Long expedientId;
	@NotNull
	private Long procesId;
}
