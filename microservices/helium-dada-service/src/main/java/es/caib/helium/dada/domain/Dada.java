/**
 * 
 */
package es.caib.helium.dada.domain;

import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@Size(max=255) //TODO canviar-ho per ValorsValidacio si es pot
	private String codi;
	@NotNull
	private Tipus tipus;
	private boolean multiple;
	private List<Valor> valor;
	
	private Long expedientId;
	private Long procesId;
}
