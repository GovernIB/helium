package es.caib.helium.api.controller.rest;

import es.caib.helium.logic.intf.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

@Slf4j
public class ProcessDocumentPortafibRunnable implements Runnable {

	@Autowired
	private DocumentService documentService;

	private Integer documentId;
	private boolean rebujat;
	private String motiuRebuig;
	private String usuariCodi;

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
//			PluginService pluginService = ServiceProxy.getInstance().getPluginService();
//			pluginService.processarDocumentCallbackPortasignatures(
//				documentId,
//				rebujat,
//				motiuRebuig);
		} catch (Exception ex) {
			log.error("Error procés petició callback portasignatures (id=" + documentId + "): " + ex.getMessage());
		}
	}
}
