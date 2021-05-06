package es.caib.helium.dada.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
public class Expedient {
	 
	@Id
	private String id;
	@NotNull
	private Long expedientId;
	@NotNull
	private Long entornId;
	@NotNull
	private Long tipusId;
	@Size(max = 64) //TODO canviar-ho per ValorsValidacio si es pot
	private String numero;
	@Size(max = 255) //TODO canviar-ho per ValorsValidacio si es pot
	private String titol;
	@NotNull
	private Long procesPrincipalId;
	private Integer estatId;
	@NotNull
	private Date dataInici;
	private Date dataFi;
	
	private List<Dada> dades;
}
