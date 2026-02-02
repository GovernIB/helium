package es.caib.helium.api.interna.controller.v1.comanda;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.api.client.Client;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import net.conselldemallorca.helium.core.util.GlobalProperties;

@Controller
@RequestMapping("/v1/salut")
public class SalutController {
	
	String heliumUrl = GlobalProperties.getInstance().getProperty("app.base.url");
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object health(HttpServletRequest request) throws IOException {
		Client jerseyClient = new Client();
		return jerseyClient.
				resource(heliumUrl + "/rest/v1/salut").
				accept("application/json").
				get(Object.class);
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ResponseBody
	public AppInfo appInfo(HttpServletRequest request) throws IOException {
		Client jerseyClient = new Client();
		return jerseyClient.
				resource(heliumUrl + "/rest/v1/salut/info").
				accept("application/json").
				get(AppInfo.class);
	}
}
