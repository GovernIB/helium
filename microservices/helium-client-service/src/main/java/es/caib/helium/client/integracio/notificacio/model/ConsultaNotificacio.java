package es.caib.helium.client.integracio.notificacio.model;

import lombok.Data;

@Data
public class ConsultaNotificacio {

	private String identificador;
	private Long expedientId;
	private Long entornId;
	private String enviamentReferencia;
}
