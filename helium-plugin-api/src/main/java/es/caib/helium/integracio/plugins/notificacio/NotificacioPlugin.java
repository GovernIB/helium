package es.caib.helium.integracio.plugins.notificacio;


import es.caib.helium.client.integracio.notificacio.model.ConsultaEnviament;
import es.caib.helium.client.integracio.notificacio.model.ConsultaNotificacio;
import es.caib.helium.client.integracio.notificacio.model.DadesNotificacioDto;
import es.caib.helium.client.integracio.notificacio.model.RespostaEnviar;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatNotificacio;
import es.caib.helium.client.integracio.notificacio.model.RespostaConsultaEstatEnviament;

/**
 * Plugin per a l'enviament de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface NotificacioPlugin {

	/**
	 * Envia una notificació.
	 * 
	 * @param notificacio
	 * @return la informació resultant de l'enviament de la notificació.
	 * @throws NotificacioPluginException
	 */
	RespostaEnviar enviar(
			DadesNotificacioDto notificacio) throws NotificacioPluginException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param identificador
	 * @return l'estat de la notificació.
	 * @throws NotificacioPluginException
	 */
	RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador,
			ConsultaNotificacio consulta) throws NotificacioPluginException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param referencia la referència de l'enviament a consultar.
	 * @return l'estat de l'enviament.
	 * @throws NotificacioPluginException
	 */
	RespostaConsultaEstatEnviament consultarEnviament(
			String referencia,
			ConsultaEnviament consulta) throws NotificacioPluginException;

}
