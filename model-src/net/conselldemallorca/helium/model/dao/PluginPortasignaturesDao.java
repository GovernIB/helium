package net.conselldemallorca.helium.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPluginException;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.exception.PluginException;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin del portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
@Repository
public class PluginPortasignaturesDao extends HibernateGenericDao<Portasignatures, Long> {

	private PortasignaturesPlugin portasignaturesPlugin;



	public PluginPortasignaturesDao() {
		super(Portasignatures.class);
	}

	public Integer UploadDocument(
			Persona persona,
			DocumentDto documentDto,
			Expedient expedient,
			String importancia,
			Date dataLimit) throws Exception {
		try {
			return getPortasignaturesPlugin().UploadDocument(
					persona,
					documentDto.getArxiuNom(),
					documentDto.getArxiuContingut(),
					documentDto.getTipusDocPortasignatures(),
					expedient.getTitol(),
					importancia,
					dataLimit);
		} catch (PortasignaturesPluginException ex) {
			logger.error("Error al enviar el document al portasignatures", ex);
			throw new PluginException("Error al enviar el document al portasignatures", ex);
		}
	}

	public byte[] DownloadDocument(
			Integer documentId) throws Exception {
		try {
			return getPortasignaturesPlugin().DownloadDocument(
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

	@SuppressWarnings("unchecked")
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

	private static final Log logger = LogFactory.getLog(PluginPortasignaturesDao.class);

}
