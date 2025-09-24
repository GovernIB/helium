package net.conselldemallorca.helium.webapp.v3.rest.comanda;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.SalutInfo;
import net.conselldemallorca.helium.v3.core.api.service.SalutService;

@Controller
@RequestMapping("/rest")
public class SalutController extends ComandaBaseController {
	
	@Autowired
	private SalutService salutService;

	@RequestMapping(value = "/appInfo", method = RequestMethod.GET)
	@ResponseBody
	public AppInfo appInfo(HttpServletRequest request) throws IOException {
		ManifestInfo manifestInfo = getManifestInfo();
		return AppInfo.builder()
		.codi("HEL")
		.nom("Helium")
		.data(manifestInfo.getBuildDate())
		.versio(manifestInfo.getVersion())
		.revisio(manifestInfo.getBuildScmRevision())
		.jdkVersion(manifestInfo.getBuildJDK())
		.integracions(salutService.getIntegracions())
		.subsistemes(salutService.getSubsistemes())
		.contexts(salutService.getContexts(getBaseUrl(request)))
		.build();
	}
	
	public String getBaseUrl(HttpServletRequest request) {
		return ServletUriComponentsBuilder
				.fromRequestUri(request)
				.replacePath(null)
				.build()
				.toUriString();
	}
	
	@RequestMapping(value = "/salut", method = RequestMethod.GET)
	@ResponseBody
	public SalutInfo health(HttpServletRequest request) throws IOException {
		ManifestInfo manifestInfo = getManifestInfo();
		return salutService.checkSalut(
				manifestInfo.getVersion(), 
				request.getRequestURL().toString() + "Performance");
	}

	@RequestMapping(value = "/salutPerformance", method = RequestMethod.GET)
	@ResponseBody
	public String healthCheck(HttpServletRequest request) {
		return "OK";
	}
	
}
