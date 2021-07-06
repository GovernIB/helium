package es.caib.helium.client.integracio.notificacio.model;

import java.util.Date;

import es.caib.helium.client.integracio.notificacio.enums.NotificacioEstat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaConsultaEstatNotificacio {

	private NotificacioEstat estat;
	protected boolean error;
	protected Date errorData;
	protected String errorDescripcio;
}
