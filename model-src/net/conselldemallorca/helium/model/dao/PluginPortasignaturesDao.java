package net.conselldemallorca.helium.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona;
import net.conselldemallorca.helium.integracio.plugins.portasignatures.PortasignaturesPlugin;
import net.conselldemallorca.helium.model.dto.DocumentDto;
import net.conselldemallorca.helium.model.hibernate.Expedient;
import net.conselldemallorca.helium.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.util.GlobalProperties;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Dao per accedir a la funcionalitat del plugin del portasignatures.
 * 
 * @author Miquel Angel Amengual <miquelaa@limit.es>
 */
@Repository
public class PluginPortasignaturesDao extends HibernateGenericDao<Portasignatures, Long> {

	public PluginPortasignaturesDao() {
		super(Portasignatures.class);
	}
	
	private PortasignaturesPlugin portasignaturesPlugin;
	
	public Integer UploadDocument(
			Persona persona,
			DocumentDto documentDto,
			Expedient expedient,
			String importancia,
			Date dataLimit) throws Exception {
		return getPortasignaturesPlugin().UploadDocument(
				persona,
				documentDto,
				expedient,
				importancia,
				dataLimit);
	}
	
	public byte[] DownloadDocument(
			Integer documentId) throws Exception {
		return getPortasignaturesPlugin().DownloadDocument(
				documentId);
	}
	
	@SuppressWarnings("unchecked")
	private PortasignaturesPlugin getPortasignaturesPlugin() throws Exception {
		if (portasignaturesPlugin == null) {
			String pluginClass = GlobalProperties.getInstance().getProperty("app.portasignatures.plugin.class");
			if ((pluginClass != null) && (pluginClass.length() > 0)) {
				Class clazz = Class.forName(pluginClass);
				portasignaturesPlugin = (PortasignaturesPlugin)clazz.newInstance();
			}
		}
		return portasignaturesPlugin;
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
}
