package es.caib.helium.dada.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
public class ExpedientDto {
	
	private String id;
	@NotNull
	private Long entornId;
	@NotNull
	private Long tipusId;
	private String numero;
	private String titol;
	@NotNull
	private long procesPrincipalId;
	private int estat;
	@NotNull
	private Date dataInici;
	private Date dataFi;
}
