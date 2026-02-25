package net.conselldemallorca.helium.webapp.v3.rest;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;


public class ProcessDocumentPortafibRunnable implements Runnable {
	
	private Integer documentId;
	private boolean rebujat;
	private String motiuRebuig;
	private String usuariCodi;
	
	@Autowired
	private PortasignaturesService portasignaturesService;

	ProcessDocumentPortafibRunnable(
			Integer documentId,
			boolean rebujat,
			String motiuRebuig,
			String usuariCodi) {
		this.documentId = documentId;
		this.rebujat = rebujat;
		this.motiuRebuig = motiuRebuig;
	}
	
	@Override
	public void run() {
		try {
			// Posa l'usuari al thread per evitar errors guardant el registre d'accions.
			if (usuariCodi == null || usuariCodi.isEmpty()) {
				usuariCodi = "anonymousUser";
			}
	        Principal principal = new Principal() {
				public String getName() {
					return usuariCodi;
				}
			};
			Authentication authentication =  new UsernamePasswordAuthenticationToken(principal, null);
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        // Executa l'acció de forma separada del callback
			//PluginService pluginService = ServiceProxy.getInstance().getPluginService();
			portasignaturesService.processarDocumentCallbackPortasignatures(
					documentId,
					rebujat,
					motiuRebuig);
		} catch (Exception ex) {
			logger.error("Error procés petició callback portasignatures (id=" + documentId + "): " + ex.getMessage());
		}
	}
	
	private static final Log logger = LogFactory.getLog(ProcessDocumentPortafibRunnable.class);

}
