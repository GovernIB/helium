package es.caib.helium.api.interna.controller.v1.comanda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import es.caib.helium.api.interna.controller.dto.ApiResponse;
import es.caib.helium.api.security.RoleHelper;
import es.caib.helium.api.util.GlobalProperties;

@Controller
@RequestMapping("/v1/estadistiques")
public class EstadistiquesController {
	String heliumUrl = GlobalProperties.getInstance().getProperty("app.base.url");
	
	@RequestMapping(method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Object estadistiques(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if(!RoleHelper.hasAnyRole("ROLE_COM")) {
			response.setStatus(401);
			return new ApiResponse(401, "Usuari no autoritzat");
		}
		Client jerseyClient = new Client();
		
		ClientResponse res = jerseyClient.
				resource(heliumUrl + "/rest/v1/estadistiques").
				accept("application/json").
				get(ClientResponse.class);
		
		Map<String,Object> result = 
				(new ObjectMapper()).readValue(res.getEntity(String.class), HashMap.class);
		return result;
	}
	
	@RequestMapping(value = "/{ruta:.+}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Object estadistiques(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String ruta) throws Exception {
		if(!RoleHelper.hasAnyRole("ROLE_COM")) {
			response.setStatus(401);
			return new ApiResponse(401, "Usuari no autoritzat");
		}
		Client jerseyClient = new Client();
		ClientResponse res = jerseyClient.
				resource(heliumUrl + "/rest/v1/estadistiques/" + ruta).
				accept("application/json").
				get(ClientResponse.class);
		Map<String,Object> result = 
				(new ObjectMapper()).readValue(res.getEntity(String.class), HashMap.class);
		return result;
	}

	@RequestMapping(value = "/of/{data}", method = RequestMethod.GET)
	@ResponseBody
	public Object estadistiquesOfData(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String data) throws Exception {
		return estadistiques(request, response, "of/" + data);
	}

	@RequestMapping(value = "/from/{dataInici}/to/{dataFi}", method = RequestMethod.GET)
	@ResponseBody
	public Object estadistiques(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String dataInici,
			@PathVariable String dataFi) throws Exception {
		if(!RoleHelper.hasAnyRole("ROLE_COM")) {
			response.setStatus(401);
			return new ApiResponse(401, "Usuari no autoritzat");
		}
		Client jerseyClient = new Client();
		ClientResponse res = jerseyClient.
				resource(heliumUrl + "/rest/v1/estadistiques/from/" + dataInici + "/to/" + dataFi).
				accept("application/json").
				get(ClientResponse.class);
		ArrayList<Object> result = 
				(new ObjectMapper()).readValue(res.getEntity(String.class), ArrayList.class);
		return result;
	}
}
