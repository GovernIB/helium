package es.caib.helium.client.integracio.notificacio.model;

import java.util.Date;

import es.caib.helium.client.integracio.notificacio.enums.NotificacioEstat;
import lombok.Data;

@Data
public class RespostaNotificacio {

	private boolean error;
	private String errorDescripcio;
	private String enviamentIdentificador;
	private Date enviatData;
	private NotificacioEstat estat;
	private String enviamentReferencia;
}
