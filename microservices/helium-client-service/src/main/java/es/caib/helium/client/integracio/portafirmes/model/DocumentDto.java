package es.caib.helium.client.integracio.portafirmes.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DocumentDto {

	private String documentNom;
	private Long id;
	private String vistaNom;
	private byte[]  vistaContingut;
	private Integer tipusDocPortasignatures;
	private boolean signat;
}
