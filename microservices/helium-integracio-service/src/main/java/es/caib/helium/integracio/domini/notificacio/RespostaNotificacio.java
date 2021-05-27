package es.caib.helium.integracio.domini.notificacio;

import java.util.Date;

import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
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
