package es.caib.helium.logic.intf.integracio.notificacio;


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
	public RespostaEnviar enviar(
			Notificacio notificacio) throws NotificacioPluginException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param identificador
	 * @return l'estat de la notificació.
	 * @throws NotificacioPluginException
	 */
	public RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador) throws NotificacioPluginException;

	/**
	 * Consulta l'estat d'una notificació.
	 * 
	 * @param referencia la referència de l'enviament a consultar.
	 * @return l'estat de l'enviament.
	 * @throws NotificacioPluginException
	 */
	public RespostaConsultaEstatEnviament consultarEnviament(
			String referencia) throws NotificacioPluginException;

}
