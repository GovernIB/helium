/**
 * 
 */
package net.conselldemallorca.helium.tests.integracio.ws.formulari;

import java.net.URL;
import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.conselldemallorca.helium.tests.integracio.util.IntegracioFormUtil;
import net.conselldemallorca.helium.v3.core.ws.formext.IniciFormulari;
import net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor;
import net.conselldemallorca.helium.v3.core.ws.formext.RespostaIniciFormulari;

/**
 * Backoffice que retorna sempre la mateixa resposta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "IniciFormulari",
		serviceName = "IniciFormulariService",
		portName = "IniciFormulariPort",
		targetNamespace="http://forms.integracio.helium.conselldemallorca.net/")
public class FormulariExternIniciImpl implements IniciFormulari {

	@Autowired private HttpServletRequest httpServletRequest;
		
	/** Mètode del WS que crida helium per indidiar l'inici del formulari. S'ha de retornar
	 * l'id del nou formulari i altres dades.
	 */
	@Override
	public RespostaIniciFormulari iniciFormulari(
			String codi, 
			String taskId,
			List<net.conselldemallorca.helium.v3.core.ws.formext.ParellaCodiValor> valors) {
		
		StringBuilder valorsSb = new StringBuilder("{");
		boolean primerValor = true;
		for (ParellaCodiValor valor: valors) {
			if (primerValor)
				primerValor = false;
			else
				valorsSb.append(", ");
			primerValor = false;
			valorsSb.append(valor.getCodi());
			valorsSb.append(":");
			valorsSb.append((valor.getValor() != null) ? valor.getValor().toString() : "<null>");
		}
		valorsSb.append("}");
		System.out.println(
				"INICI_FORMULARI: Rebuda petició (" +
				"taskName=" + codi + ", " +
				"taskInstanceId=" + taskId + ", " +
				"valors=" + valorsSb.toString() + ")");
		
		// Guarda la informació de la petició que s'utilitzarà en el formulari.
		String formulariId = IntegracioFormUtil.getInstance().addPeticio(codi, taskId, valors);
		RespostaIniciFormulari resposta = new RespostaIniciFormulari();
		resposta.setWidth(900);
		resposta.setHeight(600);
		try {
			URL aURL = new URL(httpServletRequest.getRequestURL().toString());
			resposta.setUrl("http://" + aURL.getAuthority() + "/helium-selenium-webapp/integracio/formulari/" + formulariId);
		} catch(Exception e) {
			e.printStackTrace();
			resposta.setUrl("http://localhost:8080/helium-selenium-webapp/integracio/formulari/" + formulariId);
		}
		resposta.setFormulariId(formulariId);
		System.out.println(
				"INICI_FORMULARI: Resposta (" +
				"width=" + resposta.getWidth() + ", " +
				"height=" + resposta.getHeight() + ", " +
				"url=" + resposta.getUrl() + ", " +
				"formulariId=" + resposta.getFormulariId() + ")");
		
		return resposta;	
	}

}
