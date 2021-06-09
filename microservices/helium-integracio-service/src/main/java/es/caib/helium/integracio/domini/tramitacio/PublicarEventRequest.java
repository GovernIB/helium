package es.caib.helium.integracio.domini.tramitacio;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PublicarEventRequest {
	
	//TODO @VALIDS
	@NotNull @NotEmpty
	private String expedientIdentificador;
	@NotNull @NotEmpty
	private String expedientClau;
	private Long unitatAdministrativa;
	private String representatNif;
	private String representatNom;
	private String representatApe1;
	private String representatApe2;
	@NotNull
	private Event event;
}
