/**
 *
 */
package es.caib.helium.integracio.plugins.notificacio;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Informació retornada per l'alta d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Setter
@Getter
public class RespostaEnviar {
	private boolean error;
	private String errorDescripcio;
	private String identificador;
	private NotificacioEstat estat;
	private List<EnviamentReferencia> referencies;
}
