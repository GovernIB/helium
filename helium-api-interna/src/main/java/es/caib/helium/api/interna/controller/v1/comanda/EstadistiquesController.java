package es.caib.helium.api.interna.controller.v1.comanda;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.api.client.Client;

import es.caib.helium.api.util.GlobalProperties;

@Controller
@RequestMapping("/v1/estadistiques")
public class EstadistiquesController {
	
	
	String heliumUrl = GlobalProperties.getInstance().getProperty("app.base.url");
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object estadistiques(
			HttpServletRequest request) throws IOException {
		Client jerseyClient = new Client();
		return jerseyClient.
				resource(heliumUrl + "/rest/v1/estadistiques").
				accept("application/json").
				get(Object.class);
	}
	
	@RequestMapping(value = "/{ruta:.+}", method = RequestMethod.GET)
	@ResponseBody
	public Object estadistiques(
			HttpServletRequest request,
			@PathVariable String ruta) throws IOException {
		Client jerseyClient = new Client();
		return jerseyClient.
				resource(heliumUrl + "/rest/v1/estadistiques/" + ruta).
				accept("application/json").
				get(Object.class);
	}
}
