/**
 * 
 */
package net.conselldemallorca.helium.core.model.dao;

import net.conselldemallorca.helium.integracio.plugins.registre.RegistreEntrada;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreNotificacio;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePlugin;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistrePluginException;
import net.conselldemallorca.helium.integracio.plugins.registre.RegistreSortida;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaAnotacioRegistre;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaConsulta;
import net.conselldemallorca.helium.integracio.plugins.registre.RespostaJustificantRecepcio;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class PluginRegistreDao {

	private RegistrePlugin registrePlugin;



	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) {
		try {
			return getRegistrePlugin().registrarEntrada(registreEntrada);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre d'entrada", ex);
			throw new PluginException("Error al crear el registre d'entrada", ex);
		}
	}
	public RespostaConsulta consultarEntrada(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) {
		try {
			return getRegistrePlugin().consultarEntrada(
					organCodi,
					oficinaCodi,
					numeroRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar un registre d'entrada", ex);
			throw new PluginException("Error al consultar un registre d'entrada", ex);
		}
	}

	public RespostaAnotacioRegistre registrarSortida(RegistreSortida registreSortida) {
		try {
			return getRegistrePlugin().registrarSortida(registreSortida);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear el registre de sortida", ex);
			throw new PluginException("Error al crear el registre de sortida", ex);
		}
	}
	public RespostaConsulta consultarSotida(
			String organCodi,
			String oficinaCodi,
			String numeroRegistre) {
		try {
			return getRegistrePlugin().consultarSortida(
					organCodi,
					oficinaCodi,
					numeroRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al consultar un registre de sortida", ex);
			throw new PluginException("Error al consultar un registre de sortida", ex);
		}
	}

	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio) {
		try {
			return getRegistrePlugin().registrarNotificacio(registreNotificacio);
		} catch (RegistrePluginException ex) {
			logger.error("Error al crear una notificació", ex);
			throw new PluginException("Error al crear una notificació", ex);
		}
	}
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) {
		try {
			return getRegistrePlugin().obtenirJustificantRecepcio(numeroRegistre);
		} catch (RegistrePluginException ex) {
			logger.error("Error al obtenir justificant de recepció", ex);
			throw new PluginException("Error al obtenir justificant de recepció", ex);
		}
	}

	public String obtenirNomOficina(
			String oficinaCodi) {
		try {
			return getRegistrePlugin().obtenirNomOficina(oficinaCodi);
		} catch (RegistrePluginException ex) {
			logger.error("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
			throw new PluginException("Error al obtenir el nom de l'oficina " + oficinaCodi, ex);
		}
	}

	public boolean isRegistreActiu() {
		String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
		return (pluginClass != null && pluginClass.length() > 0);
	}



	@SuppressWarnings("unchecked")
	private RegistrePlugin getRegistrePlugin() {
		if (registrePlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.registre.plugin.class");
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class clazz = Class.forName(pluginClass);
					registrePlugin = (RegistrePlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de registre", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de registre", ex);
				}
			}
		}
		return registrePlugin;
	}

	private static final Log logger = LogFactory.getLog(PluginRegistreDao.class);

}
