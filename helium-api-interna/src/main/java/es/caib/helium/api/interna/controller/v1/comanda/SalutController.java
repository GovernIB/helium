package es.caib.helium.api.interna.controller.v1.comanda;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.api.client.Client;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.helium.api.util.GlobalProperties;

@Controller
@RequestMapping("/v1/salut")
public class SalutController {
	
	String heliumUrl = GlobalProperties.getInstance().getProperty("app.base.url");
	
	@RequestMapping(method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Object health(HttpServletRequest request) throws IOException {
		Client jerseyClient = new Client();
		String response = jerseyClient.
				resource(heliumUrl + "/rest/v1/salut").
				accept("application/json").
				get(String.class);
		Map<String,Object> result = 
				(new ObjectMapper()).readValue(response, HashMap.class);
		return result;
	}
	
	@RequestMapping(value = "/info", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public AppInfo appInfo(HttpServletRequest request) throws IOException {
		Client jerseyClient = new Client();
		String response = jerseyClient.
				resource(heliumUrl + "/rest/v1/salut/info").
				accept("application/json").
				get(String.class);
		AppInfo result = 
				(new ObjectMapper()).readValue(response, AppInfo.class);
		return result;
	}
}
