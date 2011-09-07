package net.conselldemallorca.helium.core.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.PersonaDto;
import net.conselldemallorca.helium.core.model.exception.PluginException;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Repository
public class PluginPortasignaturesDao extends HibernateGenericDao<Portasignatures, Long> {

	private PortasignaturesPlugin portasignaturesPlugin;



	public PluginPortasignaturesDao() {
		super(Portasignatures.class);
	}

	public Integer uploadDocument(
			PersonaDto persona,
			String arxiuDescripcio,
			String arxiuNom,
			byte[] arxiuContingut,
			Integer tipusDocPortasignatures,
			Expedient expedient,
			String importancia,
			Date dataLimit) throws Exception {
		try {
			String signatariId = persona.getDni();
			if (isIdUsuariPerCodi())
				signatariId = persona.getCodi();
			if (isIdUsuariPerDni())
				signatariId = persona.getDni();
			return getPortasignaturesPlugin().uploadDocument(
					signatariId,
					arxiuDescripcio,
					arxiuNom,
					arxiuContingut,
					tipusDocPortasignatures,
					expedient.getTitol(),
					importancia,
					dataLimit);
		} catch (PortasignaturesPluginException ex) {
			logger.error("Error al enviar el document al portasignatures", ex);
			throw new PluginException("Error al enviar el document al portasignatures", ex);
		}
	}

	public List<byte[]> obtenirSignaturesDocument(
			Integer documentId) throws Exception {
		try {
			return getPortasignaturesPlugin().obtenirSignaturesDocument(
					documentId);
		} catch (PortasignaturesPluginException ex) {
			logger.error("Error al rebre el document del portasignatures", ex);
			throw new PluginException("Error al rebre el document del portasignatures", ex);
		}
		
	}

	@SuppressWarnings("unchecked")
	public Portasignatures findByDocument(Integer id) {
		List<Portasignatures> list = getSession()
			.createCriteria(getPersistentClass())
			.add(Restrictions.eq("documentId", id))
			.list();
		
		if (list.size() > 0) {
			return list.get(0);
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	private PortasignaturesPlugin getPortasignaturesPlugin() {
		if (portasignaturesPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				try {
					Class clazz = Class.forName(pluginClass);
					portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					logger.error("No s'ha pogut crear la instància del plugin de portasignatures", ex);
					throw new PluginException("No s'ha pogut crear la instància del plugin de portasignatures", ex);
				}
			}
		}
		return portasignaturesPlugin;
	}

	private boolean isIdUsuariPerDni() {
		return "dni".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}
	private boolean isIdUsuariPerCodi() {
		return "codi".equalsIgnoreCase(GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.usuari.id"));
	}

	private static final Log logger = LogFactory.getLog(PluginPortasignaturesDao.class);

}
