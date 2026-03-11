package es.caib.helium.service.utils.ws;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.helium.commons.domini.FilaResultat;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.persistence.entity.Domini;
import es.caib.helium.persistence.entity.Domini.OrigenCredencials;
import es.caib.helium.service.utils.GlobalProperties;

/**
 * 
 * Client per fer consultes a dominis de tipus REST
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class RestClient {

	/**
	 * Consulta a un domini per metode GET
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static List<FilaResultat> get(Domini domini, String id, Map<String, Object>params) {
		String username = null;
		String password = null;
		if (OrigenCredencials.PROPERTIES.equals(domini.getOrigenCredencials())) {
			username = GlobalProperties.getInstance().getProperty(domini.getUsuari());
			password = GlobalProperties.getInstance().getProperty(domini.getContrasenya());
		} else {
			username = domini.getUsuari();
			password = domini.getContrasenya();
		}
		Client client = generateClient(
								domini.getTipusAuth().toString(),
								username, 
								password);
		List<FilaResultat> res = null;
		TypeReference<List<FilaResultat>> typeRef = 
                new TypeReference<List<FilaResultat>>() {};
        MultivaluedMap<String,String> mvm = new MultivaluedMapImpl();
                
        // S'itera damunt els parametres i es guarden en un MultivaluedMap
		for(Map.Entry<String, Object> entry : params.entrySet())
			mvm.add(entry.getKey(), entry.getValue().toString());
		if(id != null)
			mvm.add("dominicodi", id);
		// S'afegeixen al webResource
		WebResource webResource = client.resource(domini.getUrl()).queryParams(mvm);
		
		// Es fa la crida
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		try {
			if(response.getStatus() != 200) {
				throw new Exception(response.getEntity(String.class));
			}
			// Es converteix la resposta de JSON a List<FilaResultat>
			res = new ObjectMapper().readValue(response.getEntity(String.class), typeRef);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SistemaExternException(
					domini.getEntorn().getId(),
					domini.getEntorn().getCodi(), 
					domini.getEntorn().getNom(), 
					null, 
					null, 
					null, 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
					"(Domini '" + domini.getCodi() + "')", 
					e);
		}
		return res;
	}
	
	/**
	 * Consulta a un domini per metode POST
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public static List<FilaResultat> post(Domini domini, String id, Map<String, Object>params) throws SistemaExternException {
		ObjectMapper op =  new ObjectMapper();
		Client client = generateClient(
								domini.getTipusAuth().toString(),
								domini.getUsuari(), 
								domini.getContrasenya());
		String jsonParams = null;
		
		WebResource webResource = client.resource(domini.getUrl());
		TypeReference<List<FilaResultat>> typeRef = 
				new TypeReference<List<FilaResultat>>() {};
		List<FilaResultat> res = null;
		 MultivaluedMap<String,String> mvm = new MultivaluedMapImpl();
		 
		if(id != null)
			mvm.add("dominicodi", id);
		
		try {
			// Es converteixen els parametres a JSON
			if(!params.isEmpty())
				jsonParams = op.writeValueAsString(params);
			// Es fa la crida
			ClientResponse response = webResource.queryParams(mvm).type("application/json").post(ClientResponse.class, jsonParams);
			// Es converteix la resposta de JSON a List<FilaResultat>
			res =op.readValue(response.getEntity(String.class), typeRef);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SistemaExternException(
					domini.getEntorn().getId(),
					domini.getEntorn().getCodi(), 
					domini.getEntorn().getNom(), 
					null, 
					null, 
					null, 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getId(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getCodi(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(), 
					domini.getExpedientTipus() == null ? null : domini.getExpedientTipus().getNom(),
					"(Domini '" + domini.getCodi() + "')", 
					e);
		} 
		
		return res;
		
	}
	
	
	private static Client generateClient(String authType, String userName, String password){
		
		Client client = Client.create();

		if ("HTTP_BASIC".equalsIgnoreCase(authType))
			client.addFilter(new HTTPBasicAuthFilter(userName,password));

		return client; 
	}
	
}
