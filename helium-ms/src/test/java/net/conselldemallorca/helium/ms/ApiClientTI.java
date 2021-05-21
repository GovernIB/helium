package net.conselldemallorca.helium.ms;

import java.util.Date;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import net.conselldemallorca.helium.ms.domini.client.model.Domini;
import net.conselldemallorca.helium.ms.domini.client.model.Domini.TipusEnum;
import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;

/** Proves amb l'ApiClent.
 * 
 *
 */
public class ApiClientTI {
	
	/** prova la reació d'un domini */
//	@Test
//	public void create_domini() {
	public static void main( String args[]) {
		
		System.out.println("- Inici test ApiClientTI -");
		ApiClient apiClient = new ApiClient();
		apiClient.setDebugging(true);
		apiClient.setBasePath("http://localhost:8082");
		
		ApiClientTI.llistar_dominis(apiClient);
		ApiClientTI.crear_domini(apiClient);
		

		System.out.println("- Fi test ApiClientTI -");
	}

	private static void llistar_dominis(ApiClient apiClient) {

		System.out.println("\t- Cerar domini");

	    Long entornId = 2L;
	    String filtre = null;
	    Long expedientTipusId = null;
	    Long expedientTipusPareId = null;
	    Integer page = null;
	    Integer size = null;
	    String sort = null;

		Object postBody = null;
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis").build().toUriString();

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
		DominiPagedList resultat = apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams,
				formParams, accept, contentType, authNames, returnType);
		System.out.println("resultat llistat: " + resultat);
	}

	private static void crear_domini(ApiClient apiClient) {

		System.out.println("\t- Cerar domini");

		Domini body = new Domini();
		body.setEntornId(2L);
		body.setCodi(String.valueOf(new Date().getTime()));
		body.setNom(body.getCodi());
		body.setTipus(TipusEnum.WS);
		body.setUrl("url");

		Object postBody = body;
		String path = UriComponentsBuilder.fromPath("/api/v1/dominis").build().toUriString();

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
	}

}