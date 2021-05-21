package es.caib.helium.integracio.domini.notificacio;

import java.util.Date;

import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
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
