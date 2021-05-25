package net.conselldemallorca.helium.ms.domini.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import net.conselldemallorca.helium.ms.ApiClient;
import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;
import net.conselldemallorca.helium.ms.domini.client.model.PatchDocument;
import net.conselldemallorca.helium.ms.domini.client.model.ResultatDomini;

/**
 * 
 * Client de l'API REST del Micro Servei de Dominis pel disseny i consulta de
 * dominis des d'Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class DominiApiClient {

	private static final String DOMINI_MS_PATH = "/api/v1/dominis";

	private ApiClient apiClient;

	/**
	 * Constructor amb els paràmtres d'autenticació i de url base.
	 * 
	 * @param baseUrl
	 *            Per exemple: http://localhost:8082
	 * @param usuari
	 *            Si no s'informa no hi ha autenticació.
	 * @param password
	 */
	public DominiApiClient(String baseUrl, String username, String password, boolean debugging) {
		super();

		apiClient = new ApiClient();
		apiClient.setBasePath(baseUrl);
		if (username != null && !username.isEmpty()) {
			apiClient.setUsername(username);
			apiClient.setPassword(password);
		}
		apiClient.setDebugging(debugging);
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
	public Long createDominiV1(Domini body) throws RestClientException {

		Object postBody = body;
		String path = UriComponentsBuilder.fromPath(DominiApiClient.DOMINI_MS_PATH).build().toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

		final String[] accepts = {};
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = { "application/json" };
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
		};
		apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType,
				authNames, returnType);
		// TODO DANIEL: un cop vagi bé llegir l'id del header response
		return 0L;
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
		Object postBody = null;
		// verify the required parameter 'dominiId' is set
		if (dominiId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'dominiId' when calling deleteDominiV1");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("dominiId", dominiId);
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
				.toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

		final String[] accepts = {};
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
		};
		apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
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

		Object postBody = null;
		// verify the required parameter 'dominiId' is set
		if (dominiId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'dominiId' when calling getDominiV1");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("dominiId", dominiId);
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
				.toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Domini> returnType = new ParameterizedTypeReference<Domini>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
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

		Object postBody = null;
		// verify the required parameter 'entornId' is set
		if (entornId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'entornId' when calling listDominisV1");
		}
		String path = UriComponentsBuilder.fromPath(DominiApiClient.DOMINI_MS_PATH).build().toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "entornId", entornId));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "filtre", filtre));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusId", expedientTipusId));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusPareId", expedientTipusPareId));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<DominiPagedList> returnType = new ParameterizedTypeReference<DominiPagedList>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}

	 /**
	 * Actualitza camps concrets d&#x27;un domini donat el seu identificador
	 * Modifica els camps del domini amb l&#x27;identificador passat al
	 * paràmetre &#x60;dominiId&#x60;, amb els camps de l&#x27;objecte Domini
	 * passat al body de la petició. Només modifica els camps informats en
	 * l&#x27;objecte. Els camps no inclosos en l&#x27;objecte no seran
	 * modificats.
	 * <p>
	 * <b>204</b> - Domini actualitzat
	 * <p>
	 * <b>404</b> - Not found
	 * <p>
	 * <b>409</b> - Conflicte
	 *
	 * @param body
	 * The body parameter
	 * @param dominiId
	 * Identificador del domini
	 * @throws RestClientException
	 * if an error occurs while attempting to invoke the API
	 */
	 public void patchDominiV1(List<PatchDocument> body, Long dominiId) throws
	 RestClientException {
	 Object postBody = body;
	 // verify the required parameter 'body' is set
	 if (body == null) {
	 throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
	 "Missing the required parameter 'body' when calling patchDominiV1");
	 }
	 // verify the required parameter 'dominiId' is set
	 if (dominiId == null) {
	 throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
	 "Missing the required parameter 'dominiId' when calling patchDominiV1");
	 }
	 // create path and map variables
	 final Map<String, Object> uriVariables = new HashMap<String, Object>();
	 uriVariables.put("dominiId", dominiId);
	 String path =
	 UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
	 .toUriString();
	
	 final MultiValueMap<String, String> queryParams = new
	 LinkedMultiValueMap<String, String>();
	 final HttpHeaders headerParams = new HttpHeaders();
	 final MultiValueMap<String, Object> formParams = new
	 LinkedMultiValueMap<String, Object>();
	
	 final String[] accepts = {};
	 final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
	 final String[] contentTypes = { "application/json" };
	 final MediaType contentType =
	 apiClient.selectHeaderContentType(contentTypes);
	
	 String[] authNames = new String[] {};
	
	 ParameterizedTypeReference<Void> returnType = new
	 ParameterizedTypeReference<Void>() {
	 };
	 apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody,
	 headerParams, formParams, accept,
	 contentType, authNames, returnType);
	 }

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
		Object postBody = body;
		// verify the required parameter 'body' is set
		if (body == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'body' when calling updateDominiV1");
		}
		// verify the required parameter 'dominiId' is set
		if (dominiId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'dominiId' when calling updateDominiV1");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("dominiId", dominiId);
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables)
				.toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

		final String[] accepts = {};
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = { "application/json" };
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {
		};
		apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, contentType,
				authNames, returnType);
	}

	/**
	 * Consulta un domini Realitza la consulta del domini amb
	 * l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;. Per a
	 * realitzar la consulta utilitza la configuració del domini, juntament amb
	 * els paràmetres passats. Els paràmetres es passaran com un mapa amb la
	 * clau de tipus string, i un valor en format string. - Si
	 * l&#x27;identificador de domini &#x3D; 0 es tractarà com a un domini
	 * intern. - En la consulta de dominis interns, és obligatori informar el
	 * codi de l&#x27;entorn en un paràmetre de nom &#x60;entorn&#x60; - En la
	 * consulta de dominis SQL, si es passa algun paràmetre que no sigui de
	 * tipus string, s&#x27;haurà d&#x27;especificar el seu tipus utilitzant un
	 * altre paràmetre amb el mateix nom, acabat amb -tipus. Ex. paràmetre de
	 * nom num, valor 24 i tipus enter: *num&#x3D;24&amp;num-tipus&#x3D;int* Els
	 * tipus de dades possibles son: - int - float - boolean - date - price En
	 * cas de no especificar el tipus es considera de tipus string.
	 * <p>
	 * <b>200</b> - Ok
	 * 
	 * @param dominiId
	 *            Identificador del domini
	 * @param identificador
	 *            identificador del domini al sistema remot
	 * @param parametres
	 *            Map&lt;String, Object&gt; amb els paràmetres que es volen
	 *            passar en la consulta del domini
	 * @return ResultatDomini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public ResultatDomini consultaDominiV1(Long dominiId, String identificador, Map<String, Object> parametres)
			throws RestClientException {
		Object postBody = null;
		// verify the required parameter 'dominiId' is set
		if (dominiId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'dominiId' when calling consultaDominiV1");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("dominiId", dominiId);
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}/resultats").buildAndExpand(uriVariables)
				.toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "identificador", identificador));
		queryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase()),
				"parametres", parametres));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<ResultatDomini> returnType = new ParameterizedTypeReference<ResultatDomini>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}

	/**
	 * Consulta un domini Obté una pàgina de la cerca dels **dominis** d&#x27;un
	 * entorn. La cerca pot rebre paràmetres per - ordenar - paginar - filtrar
	 * (utilitzant sintaxi rsql) No té en compte la herència
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
	public DominiPagedList llistaDominiByEntorn(Long entornId, String filtre, Integer page, Integer size, String sort)
			throws RestClientException {
		Object postBody = null;
		// verify the required parameter 'entornId' is set
		if (entornId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'entornId' when calling llistaDominiByEntorn");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("entornId", entornId);
		String path = UriComponentsBuilder.fromPath("/api/v1/entorn/{entornId}/dominis").buildAndExpand(uriVariables)
				.toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "filtre", filtre));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<DominiPagedList> returnType = new ParameterizedTypeReference<DominiPagedList>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}

	/**
	 * Consulta un domini global d&#x27;un entorn Obté el domini global donat
	 * l&#x27;entorn al que pertany, i el seu codi.
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>204</b> - No content
	 * <p>
	 * <b>400</b> - Bad input parameter
	 * 
	 * @param entornId
	 *            Identificador de l&#x27;entorn
	 * @param codi
	 *            Codi del domini
	 * @param expedientTipusPareId
	 *            Identificador del tipus d&#x27;expedient pare al que pertany
	 *            el domini (en cas d\\&#x27;herència)
	 * @return Domini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public Domini getDominiByEntornAndCodi(Long entornId, String codi, Long expedientTipusPareId)
			throws RestClientException {
		Object postBody = null;
		// verify the required parameter 'entornId' is set
		if (entornId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'entornId' when calling getDominiByEntornAndCodi");
		}
		// verify the required parameter 'codi' is set
		if (codi == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'codi' when calling getDominiByEntornAndCodi");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("entornId", entornId);
		uriVariables.put("codi", codi);
		String path = UriComponentsBuilder.fromPath("/api/v1/entorn/{entornId}/dominis/{codi}")
				.buildAndExpand(uriVariables).toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusPareId", expedientTipusPareId));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Domini> returnType = new ParameterizedTypeReference<Domini>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}

	/**
	 * Consulta un domini Obté una pàgina de la cerca dels **dominis** d&#x27;un
	 * entorn. La cerca pot rebre paràmetres per - ordenar - paginar - filtrar
	 * (utilitzant sintaxi rsql) No té en compte la herència
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>204</b> - No content
	 * <p>
	 * <b>400</b> - Bad input parameter
	 * 
	 * @param expedientTipusId
	 *            Identificador del tipus d&#x27;expedient
	 * @param filtre
	 *            cadena amb format rsql per a definir el filtre a aplicar a la
	 *            consulta
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
	public DominiPagedList llistaDominiByExpedientTipus(Long expedientTipusId, String filtre, Integer page,
			Integer size, String sort) throws RestClientException {
		Object postBody = null;
		// verify the required parameter 'expedientTipusId' is set
		if (expedientTipusId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'expedientTipusId' when calling llistaDominiByExpedientTipus");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("expedientTipusId", expedientTipusId);
		String path = UriComponentsBuilder.fromPath("/api/v1/expedientTipus/{expedientTipusId}/dominis")
				.buildAndExpand(uriVariables).toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "filtre", filtre));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<DominiPagedList> returnType = new ParameterizedTypeReference<DominiPagedList>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}

	/**
	 * Consulta un domini d&#x27;un tipus d&#x27;expedient Obté el domini de
	 * tipus d&#x27;expedient donat el tipus d&#x27;expedient al que pertany, i
	 * el seu codi. En cas d&#x27;informar el tipus d&#x27;expedient pare es
	 * tindrà en compte la herència
	 * <p>
	 * <b>200</b> - Ok
	 * <p>
	 * <b>204</b> - No content
	 * <p>
	 * <b>400</b> - Bad input parameter
	 * 
	 * @param expedientTipusId
	 *            Identificador del tipus d&#x27;expedient
	 * @param codi
	 *            Codi del domini
	 * @param expedientTipusPareId
	 *            Identificador del tipus d&#x27;expedient pare al que pertany
	 *            el domini (en cas d\\&#x27;herència)
	 * @return Domini
	 * @throws RestClientException
	 *             if an error occurs while attempting to invoke the API
	 */
	public Domini getDominiByExpedientTipusAndCodi(Long expedientTipusId, String codi, Long expedientTipusPareId)
			throws RestClientException {
		Object postBody = null;
		// verify the required parameter 'expedientTipusId' is set
		if (expedientTipusId == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'expedientTipusId' when calling getDominiByExpedientTipusAndCodi");
		}
		// verify the required parameter 'codi' is set
		if (codi == null) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
					"Missing the required parameter 'codi' when calling getDominiByExpedientTipusAndCodi");
		}
		// create path and map variables
		final Map<String, Object> uriVariables = new HashMap<String, Object>();
		uriVariables.put("expedientTipusId", expedientTipusId);
		uriVariables.put("codi", codi);
		String path = UriComponentsBuilder.fromPath("/api/v1/expedientTipus/{expedientTipusId}/dominis/{codi}")
				.buildAndExpand(uriVariables).toUriString();

		final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
		final HttpHeaders headerParams = new HttpHeaders();
		final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
		queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusPareId", expedientTipusPareId));

		final String[] accepts = { "application/json" };
		final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
		final String[] contentTypes = {};
		final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

		String[] authNames = new String[] {};

		ParameterizedTypeReference<Domini> returnType = new ParameterizedTypeReference<Domini>() {
		};
		return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept,
				contentType, authNames, returnType);
	}
}
