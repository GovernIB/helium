package es.caib.helium.integracio.domini.portafirmes;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DocumentDto {

	@NotNull
	private String documentNom;
	@NotNull
	private Long id;
	@NotNull
	private String vistaNom;
	@NotNull
	private byte[]  vistaContingut;
	@NotNull
	private Integer tipusDocPortasignatures;
	private boolean signat;
}
