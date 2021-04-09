/**
 * 
 */
package es.caib.helium.dada.domain;

import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
public class Dada extends DadaMS {

	@Id
	private String id;
	private String codi;
	private String tipus;
	private boolean multiple;
	private List<Object> valor;
	
	private Long expedientId;
	private Long procesId;
}
