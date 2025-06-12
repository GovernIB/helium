package net.conselldemallorca.helium.webapp.v3.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.conselldemallorca.helium.core.model.service.PluginService;
import net.conselldemallorca.helium.core.model.service.ServiceProxy;

public class ProcessDocumentPortafibRunnable implements Runnable {
	
	private Integer documentId;
	private boolean rebujat;
	private String motiuRebuig;

	ProcessDocumentPortafibRunnable(
			Integer documentId,
			boolean rebujat,
			String motiuRebuig) {
		this.documentId = documentId;
		this.rebujat = rebujat;
		this.motiuRebuig = motiuRebuig;
	}
	
	@Override
	public void run() {
		try {
			PluginService pluginService = ServiceProxy.getInstance().getPluginService();
			pluginService.processarDocumentCallbackPortasignatures(
					documentId,
					rebujat,
					motiuRebuig);
		} catch (Exception ex) {
			logger.error("Error procés petició callback portasignatures (id=" + documentId + "): " + ex.getMessage());
		}
	}
	
	private static final Log logger = LogFactory.getLog(ProcessDocumentPortafibRunnable.class);

}
