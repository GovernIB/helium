package net.conselldemallorca.helium.integracio.plugins.dadesext;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;




public class RestClientBaseUnitats {

	private static final  String SESSION_COOKIE_NAME = "JSESSIONID";
	
	
	protected int connecTimeout = 20000;
	protected int readTimeout = 120000;
	protected String baseUrl;
    protected String username;
    protected String password;
    
    protected boolean autenticacioBasic = true;
    
    protected Client jerseyClient;

	protected Client generarClient(String urlAmbMetode) throws Exception {
		if (this.jerseyClient == null) {
			this.jerseyClient = generarClient();
		} else {
			return this.jerseyClient;
		}
		if (username != null) {
	        autenticarClient(
	        		this.jerseyClient,
	                urlAmbMetode,
	                username,
	                password);
	    }
	    return this.jerseyClient;
	}

	protected Client generarClient() {
	    this.jerseyClient = Client.create();
	    jerseyClient.setConnectTimeout(connecTimeout);
	    jerseyClient.setReadTimeout(readTimeout);
	    jerseyClient.addFilter(
	            new ClientFilter() {
	                private List<Object> cookies  = new ArrayList<Object>();
	                @Override
	                public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
	
	                	synchronized(cookies) {
	                        if (!cookies.isEmpty()) {
	                            request.getHeaders().put("Cookie", cookies);
	                        }
	                	}
	                    
	                	ClientResponse response = getNext().handle(request);
	                    
	                    if (response.getCookies() != null) {
	                    	List<Object> novesCookies = new ArrayList<Object>();
	                    	for (Object cookie : response.getCookies()) {
	                    		if (! SESSION_COOKIE_NAME.equals( ((NewCookie) cookie).getName()) ) {
	                    			novesCookies.add(cookie);
	                    		}
	                    	}
	                    	if (!novesCookies.isEmpty()) {
	                        	synchronized(cookies) {
	                        		cookies.clear();
	                        		cookies.addAll(novesCookies);
	                        	}
	                    	}
	                    }
	                    return response;
	                }
	            }
	    );
	    jerseyClient.addFilter(
	            new ClientFilter() {
	                @Override
	                public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
	                    ClientHandler ch = getNext();
	                    ClientResponse resp = ch.handle(request);
	
	                    if (resp.getStatus()/100 != 3) {
	                        return resp;
	                    } else {
	                        String redirectTarget = resp.getHeaders().getFirst("Location");
	                        request.setURI(UriBuilder.fromUri(redirectTarget).build());
	                        return ch.handle(request);
	                    }
	                }
	            }
	    );
	    return jerseyClient;
	}

	protected void autenticarClient(Client jerseyClient, String urlAmbMetode, String username, String password) throws Exception {
	    if (!autenticacioBasic) {
	        logger.debug(
	                "Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
	                        "urlAmbMetode=" + urlAmbMetode + ", " +
	                        "username=" + username +
	                        "password=********)");
	        jerseyClient.resource(urlAmbMetode).get(String.class);
	        Form form = new Form();
	        form.putSingle("j_username", username);
	        form.putSingle("j_password", password);
	        jerseyClient.
	                resource(baseUrl + "/j_security_check").
	                type("application/x-www-form-urlencoded").
	                post(form);
	    } else {
	    	 logger.debug(
	                "Autenticant REST amb autenticació de tipus HTTP basic (" +
	                        "urlAmbMetode=" + urlAmbMetode + ", " +
	                        "username=" + username +
	                        "password=********)");
	     
	        jerseyClient.addFilter(
	                new HTTPBasicAuthFilter(username, password));
	    }
	}

	protected ObjectMapper getMapper() {
	    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected Client generarIAuthenticarClient(String urlAmbMetode) throws Exception {
		Client jerseyClient = generarClient();
		if (username != null) {
			autenticarClient(
					jerseyClient,
					urlAmbMetode,
					username,
					password);
		}
		return jerseyClient;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private static final Log logger = LogFactory.getLog(RestClientBaseUnitats.class);

}
