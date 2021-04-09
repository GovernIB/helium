package es.caib.helium.dada.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
public class ExpedientCapcalera extends DadaMS {
	 
	@Id
	private String id;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;
	private String numero;
	private String titol;
	private Long procesPrincipalId;
	private Integer estatId;
	private Date dataInici;
	private Date dataFi;
}
