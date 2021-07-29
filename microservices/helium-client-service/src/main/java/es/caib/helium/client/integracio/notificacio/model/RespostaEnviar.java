package es.caib.helium.client.integracio.notificacio.model;

import es.caib.helium.client.integracio.notificacio.enums.NotificacioEstat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RespostaEnviar {

	private boolean error;
	private String errorDescripcio;
	private String identificador;
	private NotificacioEstat estat;
	private List<EnviamentReferencia> referencies;
}
