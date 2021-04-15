package net.conselldemallorca.helium.ms.domini.client;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;

import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;

/**
 * 
 * Client de l'API REST del Micro Servei de Dominis pel disseny i consulta de 
 * dominis des d'Helium.
 *  
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class DominiMsClient {

	public enum CollectionFormat {
		CSV(","), TSV("\t"), SSV(" "), PIPES("|"), MULTI(null);

		private final String separator;

		private CollectionFormat(String separator) {
			this.separator = separator;
		}

		private String collectionToString(Collection<? extends CharSequence> collection) {
			return StringUtils.collectionToDelimitedString(collection, separator);
		}
	}

	private static final String DOMINI_MS_PATH = "/api/v1/dominis";

	private String baseUrl;
	private String username;
	private String password;

	private boolean autenticacioBasic = true;
	private int connecTimeout = 3000000;
	private int readTimeout = 10000000;

	public DominiMsClient(String baseUrl, String username, String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public DominiMsClient(String baseUrl, String username, String password, int connecTimeout, int readTimeout) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
	}

	public DominiMsClient(String baseUrl, String username, String password, boolean autenticacioBasic) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
	}

	public DominiMsClient(String baseUrl, String username, String password, boolean autenticacioBasic,
			int connecTimeout, int readTimeout) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
		this.connecTimeout = connecTimeout;
		this.readTimeout = readTimeout;
	}

	/**
	 * Crea un nou domini Afegiex un nou domini al sistema
	 * <p>
	 * <b>201</b> - Domini created
	 * <p>
	 * <b>400</b> - Bad request
	 * <p>
	 * <b>409</b> - Conflicte. Ja existeix el domini
	 * 
	 * @param body
	 *            Domini a crear
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public void createDominiV1(Domini body) throws RestClientException {
		Object postBody = body;
		// verify the required parameter 'body' is set
		if (body == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'body' when calling createDominiV1");
		}

		try {
			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(DOMINI_MS_PATH);
			String urlAmbMetode = builder.build().toUriString();

			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(jerseyClient, urlAmbMetode, username, password);
			}
			jerseyClient.resource(urlAmbMetode).type("application/json").post(postBody);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Elimina un domini donat el seu identificador Elimina el domini amb
	 * l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;
	 * <p>
	 * <b>204</b> - Ok. No content
	 * <p>
	 * <b>404</b> - Not found
	 * 
	 * @param dominiId
	 *            Identificador del domini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public void deleteDominiV1(Long dominiId) throws RestClientException {
		try {
			String url = baseUrl + DOMINI_MS_PATH + "/" + dominiId;
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(jerseyClient, url, username, password);
			}
			jerseyClient.resource(url).delete();
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Consulta un domini donat el seu identificador Retorna les dades del
	 * domini amb l&#x27;identificador informat al paràmetre
	 * &#x60;dominiId&#x60;
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>404</b> - Not found
	 * 
	 * @param dominiId
	 *            Identificador del domini
	 * @return Domini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public Domini getDominiV1(Long dominiId) {

		try {
			String url = baseUrl + DOMINI_MS_PATH + "/" + dominiId;
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(jerseyClient, url, username, password);
			}
			String json = jerseyClient.resource(url).type("application/json").get(String.class);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, Domini.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Cerca de dominis Obté una página de la cerca dels **dominis** del
	 * sistema. La cerca pot rebre paràmetres per - ordenar - paginar - filtrar
	 * (utilitzant sintaxi rsql)
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>204</b> - No content
	 * <p>
	 * <b>400</b> - Bad input parameter
	 * 
	 * @param entornId
	 *            Identificador de l&#x27;entorn
	 * @param filtre
	 *            cadena amb format rsql per a definir el filtre a aplicar a la
	 *            consulta
	 * @param expedientTipusId
	 *            Identificador del tipus d&#x27;expedient al que pertany el
	 *            domini
	 * @param expedientTipusPareId
	 *            Identificador del tipus d&#x27;expedient pare al que pertany
	 *            el domini (en cas d\\&#x27;herència)
	 * @param page
	 *            número de pagina a retornar en cas de desitjar paginació
	 * @param size
	 *            mida de la pàgina a retornat en cas de desitjar paginació
	 * @param sort
	 *            ordre a aplicar a la consulta
	 * @return DominiPagedList
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public DominiPagedList listDominisV1(Long entornId, String filtre, Long expedientTipusId, Long expedientTipusPareId,
			Integer page, Integer size, String sort) {

		try {
			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(DOMINI_MS_PATH);
			final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
			queryParams.putAll(this.parameterToMultiValueMap(null, "entornId", entornId));
			queryParams.putAll(this.parameterToMultiValueMap(null, "filtre", filtre));
			queryParams.putAll(this.parameterToMultiValueMap(null, "expedientTipusId", expedientTipusId));
			queryParams.putAll(this.parameterToMultiValueMap(null, "expedientTipusPareId", expedientTipusPareId));
			queryParams.putAll(this.parameterToMultiValueMap(null, "page", page));
			queryParams.putAll(this.parameterToMultiValueMap(null, "size", size));
			queryParams.putAll(this.parameterToMultiValueMap(null, "sort", sort));

			if (queryParams != null) {
				for (String key : queryParams.keySet())
					builder.queryParam(key, queryParams.get(key).toArray());
			}
			String urlAmbMetode = builder.build().toUriString();

			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(jerseyClient, urlAmbMetode, username, password);
			}
			String json = jerseyClient.resource(urlAmbMetode).type("application/json").get(String.class);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, DominiPagedList.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

//	/**
//	 * Actualitza camps concrets d&#x27;un domini donat el seu identificador
//	 * Modifica els camps del domini amb l&#x27;identificador passat al
//	 * paràmetre &#x60;dominiId&#x60;, amb els camps de l&#x27;objecte Domini
//	 * passat al body de la petició. Només modifica els camps informats en
//	 * l&#x27;objecte. Els camps no inclosos en l&#x27;objecte no seran
//	 * modificats.
//	 * <p>
//	 * <b>204</b> - Domini actualitzat
//	 * <p>
//	 * <b>404</b> - Not found
//	 * <p>
//	 * <b>409</b> - Conflicte
//	 * 
//	 * @param body
//	 *            The body parameter
//	 * @param dominiId
//	 *            Identificador del domini
//	 * @throws RestClientException
//	 *             if an error occurs while attempting to invoke the API
//	 */
//	public void patchDominiV1(List<PatchDocument> body, Long dominiId) throws RestClientException {
//		Object postBody = body;
//		// verify the required parameter 'body' is set
//		if (body == null) {
//			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
//					"Missing the required parameter 'body' when calling patchDominiV1");
//		}
//		// verify the required parameter 'dominiId' is set
//		if (dominiId == null) {
//			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
//					"Missing the required parameter 'dominiId' when calling patchDominiV1");
//		}
//		// create path and map variables
//		final Map<String, Object> uriVariables = new HashMap<String, Object>();
//		uriVariables.put("dominiId", dominiId);
//		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
//				.toUriString();
//
//		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//		final HttpHeaders headerParams = new HttpHeaders();
//		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//		final String[] accepts = {};
//		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//		final String[] contentTypes = { "application/json" };
//		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//		String[] authNames = new String[] {};
//
//		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
//		};
//		apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept,
//				contentType, authNames, returnType);
//	}

	/**
	 * Modifica un domini donat el seu identificador Modifica el domini amb
	 * l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;, amb les
	 * totes dades de l&#x27;objecte Domini passat al body de la petició
	 * <p>
	 * <b>204</b> - Domini actualitzat
	 * <p>
	 * <b>404</b> - Not found
	 * <p>
	 * <b>409</b> - Conflicte
	 * 
	 * @param body
	 *            The body parameter
	 * @param dominiId
	 *            Identificador del domini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public void updateDominiV1(Domini body, Long dominiId) throws RestClientException {
//		Object postBody = body;
//		// verify the required parameter 'body' is set
//		if (body == null) {
//			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
//					"Missing the required parameter 'body' when calling updateDominiV1");
//		}
//		// verify the required parameter 'dominiId' is set
//		if (dominiId == null) {
//			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
//					"Missing the required parameter 'dominiId' when calling updateDominiV1");
//		}
//		// create path and map variables
//		final Map<String, Object> uriVariables = new HashMap<String, Object>();
//		uriVariables.put("dominiId", dominiId);
//		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
//				.toUriString();
//
//		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//		final HttpHeaders headerParams = new HttpHeaders();
//		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//		final String[] accepts = {};
//		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//		final String[] contentTypes = { "application/json" };
//		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//		String[] authNames = new String[] {};
//
//		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
//		};
//		apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, contentType,
//				authNames, returnType);
	}

	private Client generarClient() {
		Client jerseyClient = Client.create();
		jerseyClient.setConnectTimeout(connecTimeout);
		jerseyClient.setReadTimeout(readTimeout);
		jerseyClient.addFilter(new LoggingFilter(System.out));
		jerseyClient.addFilter(new ClientFilter() {
			private ArrayList<Object> cookies;

			@Override
			public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
				if (cookies != null) {
					request.getHeaders().put("Cookie", cookies);
				}
				ClientResponse response = getNext().handle(request);
				if (response.getCookies() != null) {
					if (cookies == null) {
						cookies = new ArrayList<Object>();
					}
					cookies.addAll(response.getCookies());
				}
				return response;
			}
		});
		jerseyClient.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
				ClientHandler ch = getNext();
				ClientResponse resp = ch.handle(request);

				if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
					return resp;
				} else {
					String redirectTarget = resp.getHeaders().getFirst("Location");
					request.setURI(UriBuilder.fromUri(redirectTarget).build());
					return ch.handle(request);
				}
			}
		});
		return jerseyClient;
	}

	private void autenticarClient(Client jerseyClient, String urlAmbMetode, String username, String password)
			throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException {
		if (!autenticacioBasic) {
			logger.debug("Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss ("
					+ "urlAmbMetode=" + urlAmbMetode + ", " + "username=" + username + "password=********)");
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.resource(baseUrl + "/j_security_check").type("application/x-www-form-urlencoded").post(form);
		} else {
			logger.debug("Autenticant REST amb autenticació de tipus HTTP basic (" + "urlAmbMetode=" + urlAmbMetode
					+ ", " + "username=" + username + "password=********)");
			jerseyClient.addFilter(new HTTPBasicAuthFilter(username, password));
		}
	}

	/**
	 * Converts a parameter to a {@link MultiValueMap} for use in REST requests
	 * 
	 * @param collectionFormat
	 *            The format to convert to
	 * @param name
	 *            The name of the parameter
	 * @param value
	 *            The parameter's value
	 * @return a Map containing the String value(s) of the input parameter
	 */
	public MultiValueMap<String, String> parameterToMultiValueMap(CollectionFormat collectionFormat, String name,
			Object value) {
		final MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		if (name == null || name.isEmpty() || value == null) {
			return params;
		}

		if (collectionFormat == null) {
			collectionFormat = CollectionFormat.CSV;
		}

		Collection<?> valueCollection = null;
		if (value instanceof Collection) {
			valueCollection = (Collection<?>) value;
		} else {
			params.add(name, parameterToString(value));
			return params;
		}

		if (valueCollection.isEmpty()) {
			return params;
		}

		if (collectionFormat.equals(CollectionFormat.MULTI)) {
			for (Object item : valueCollection) {
				params.add(name, parameterToString(item));
			}
			return params;
		}

		List<String> values = new ArrayList<String>();
		for (Object o : valueCollection) {
			values.add(parameterToString(o));
		}
		params.add(name, collectionFormat.collectionToString(values));

		return params;
	}

	/**
	 * Format the given parameter object into string.
	 * 
	 * @param param
	 *            the object to convert
	 * @return String the parameter represented as a String
	 */
	public String parameterToString(Object param) {
		if (param == null) {
			return "";
		} else if (param instanceof Date) {
			return formatDate((Date) param);
		} else if (param instanceof Collection) {
			StringBuilder b = new StringBuilder();
			for (Object o : (Collection<?>) param) {
				if (b.length() > 0) {
					b.append(",");
				}
				b.append(String.valueOf(o));
			}
			return b.toString();
		} else {
			return String.valueOf(param);
		}
	}

	/**
	 * Format the given Date object into string.
	 */
	public String formatDate(Date date) {
		// vigilar el format
		return new SimpleDateFormat().format(date);
	}

	private static final Logger logger = LoggerFactory.getLogger(DominiMsClient.class);

}
