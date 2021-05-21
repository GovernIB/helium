package es.caib.helium.integracio.domini.notificacio;

import java.util.List;

import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
import es.caib.notib.ws.notificacio.EnviamentReferencia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaEnviar {

	private boolean error;
	private String errorDescripcio;
	private String identificador;
	private NotificacioEstat estat;
	private List<EnviamentReferencia> referencies;
}
