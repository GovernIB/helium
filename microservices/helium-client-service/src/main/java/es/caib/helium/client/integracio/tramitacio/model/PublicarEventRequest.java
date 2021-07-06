package es.caib.helium.client.integracio.tramitacio.model;

import lombok.Data;

@Data
public class PublicarEventRequest {
	
	private String expedientIdentificador;
	private String expedientClau;
	private Long unitatAdministrativa;
	private String representatNif;
	private String representatNom;
	private String representatApe1;
	private String representatApe2;
	private Event event;
}
