//package net.conselldemallorca.helium.ms.domini.client.api;
//
//import net.conselldemallorca.helium.ms.domini.client.invoker.ApiClient;
//
//import net.conselldemallorca.helium.ms.domini.client.model.Domini;
//import net.conselldemallorca.helium.ms.domini.client.model.DominiPagedList;
//import net.conselldemallorca.helium.ms.domini.client.model.PatchDocument;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.util.UriComponentsBuilder;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
////@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-04-15T16:51:41.639+02:00[Europe/Paris]")@Component("net.conselldemallorca.helium.ms.domini.client.api.DissenyApi")
//public class DissenyApi {
//    private ApiClient apiClient;
//
//    public DissenyApi() {
//        this(new ApiClient());
//    }
//
//    @Autowired
//    public DissenyApi(ApiClient apiClient) {
//        this.apiClient = apiClient;
//    }
//
//    public ApiClient getApiClient() {
//        return apiClient;
//    }
//
//    public void setApiClient(ApiClient apiClient) {
//        this.apiClient = apiClient;
//    }
//
//    /**
//     * Crea un nou domini
//     * Afegiex un nou domini al sistema
//     * <p><b>201</b> - Domini created
//     * <p><b>400</b> - Bad request
//     * <p><b>409</b> - Conflicte. Ja existeix el domini
//     * @param body Domini a crear
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public void createDominiV1(Domini body) throws RestClientException {
//        Object postBody = body;
//        // verify the required parameter 'body' is set
//        if (body == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling createDominiV1");
//        }
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis").build().toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//        final String[] accepts = {  };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = { 
//            "application/json"
//         };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
//        apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//    /**
//     * Elimina un domini donat el seu identificador
//     * Elimina el domini amb l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;
//     * <p><b>204</b> - Ok. No content
//     * <p><b>404</b> - Not found
//     * @param dominiId Identificador del domini
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public void deleteDominiV1(Long dominiId) throws RestClientException {
//        Object postBody = null;
//        // verify the required parameter 'dominiId' is set
//        if (dominiId == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'dominiId' when calling deleteDominiV1");
//        }
//        // create path and map variables
//        final Map<String, Object> uriVariables = new HashMap<String, Object>();
//        uriVariables.put("dominiId", dominiId);
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables).toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//        final String[] accepts = {  };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = {  };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
//        apiClient.invokeAPI(path, HttpMethod.DELETE, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//    /**
//     * Consulta un domini donat el seu identificador
//     * Retorna les dades del domini amb l&#x27;identificador informat al paràmetre &#x60;dominiId&#x60;
//     * <p><b>200</b> - Ok
//     * <p><b>404</b> - Not found
//     * @param dominiId Identificador del domini
//     * @return Domini
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public Domini getDominiV1(Long dominiId) throws RestClientException {
//        Object postBody = null;
//        // verify the required parameter 'dominiId' is set
//        if (dominiId == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'dominiId' when calling getDominiV1");
//        }
//        // create path and map variables
//        final Map<String, Object> uriVariables = new HashMap<String, Object>();
//        uriVariables.put("dominiId", dominiId);
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables).toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//        final String[] accepts = { 
//            "application/json"
//         };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = {  };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<Domini> returnType = new ParameterizedTypeReference<Domini>() {};
//        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//    /**
//     * Cerca de dominis
//     * Obté una página de la cerca dels **dominis** del sistema.  La cerca pot rebre paràmetres per - ordenar - paginar - filtrar (utilitzant sintaxi rsql) 
//     * <p><b>200</b> - Ok
//     * <p><b>204</b> - No content
//     * <p><b>400</b> - Bad input parameter
//     * @param entornId Identificador de l&#x27;entorn
//     * @param filtre cadena amb format rsql per a definir el filtre a aplicar a la consulta
//     * @param expedientTipusId Identificador del tipus d&#x27;expedient al que pertany el domini
//     * @param expedientTipusPareId Identificador del tipus d&#x27;expedient pare al que pertany el domini (en cas d\\&#x27;herència)
//     * @param page número de pagina a retornar en cas de desitjar paginació
//     * @param size mida de la pàgina a retornat en cas de desitjar paginació
//     * @param sort ordre a aplicar a la consulta
//     * @return DominiPagedList
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public DominiPagedList listDominisV1(Long entornId, String filtre, Long expedientTipusId, Long expedientTipusPareId, Integer page, Integer size, String sort) throws RestClientException {
//        Object postBody = null;
//        // verify the required parameter 'entornId' is set
//        if (entornId == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'entornId' when calling listDominisV1");
//        }
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis").build().toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "entornId", entornId));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "filtre", filtre));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusId", expedientTipusId));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "expedientTipusPareId", expedientTipusPareId));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "page", page));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "size", size));
//        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "sort", sort));
//
//        final String[] accepts = { 
//            "application/json"
//         };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = {  };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<DominiPagedList> returnType = new ParameterizedTypeReference<DominiPagedList>() {};
//        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//    /**
//     * Actualitza camps concrets d&#x27;un domini donat el seu identificador
//     * Modifica els camps del domini amb l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;, amb els camps de l&#x27;objecte Domini passat al body de la petició.   Només modifica els camps informats en l&#x27;objecte. Els camps no inclosos en l&#x27;objecte no seran modificats. 
//     * <p><b>204</b> - Domini actualitzat
//     * <p><b>404</b> - Not found
//     * <p><b>409</b> - Conflicte
//     * @param body The body parameter
//     * @param dominiId Identificador del domini
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public void patchDominiV1(List<PatchDocument> body, Long dominiId) throws RestClientException {
//        Object postBody = body;
//        // verify the required parameter 'body' is set
//        if (body == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling patchDominiV1");
//        }
//        // verify the required parameter 'dominiId' is set
//        if (dominiId == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'dominiId' when calling patchDominiV1");
//        }
//        // create path and map variables
//        final Map<String, Object> uriVariables = new HashMap<String, Object>();
//        uriVariables.put("dominiId", dominiId);
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables).toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//        final String[] accepts = {  };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = { 
//            "application/json"
//         };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
//        apiClient.invokeAPI(path, HttpMethod.PATCH, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//    /**
//     * Modifica un domini donat el seu identificador
//     * Modifica el domini amb l&#x27;identificador passat al paràmetre &#x60;dominiId&#x60;, amb les totes dades de l&#x27;objecte Domini passat al body de la petició
//     * <p><b>204</b> - Domini actualitzat
//     * <p><b>404</b> - Not found
//     * <p><b>409</b> - Conflicte
//     * @param body The body parameter
//     * @param dominiId Identificador del domini
//     * @throws RestClientException if an error occurs while attempting to invoke the API
//     */
//    public void updateDominiV1(Domini body, Long dominiId) throws RestClientException {
//        Object postBody = body;
//        // verify the required parameter 'body' is set
//        if (body == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling updateDominiV1");
//        }
//        // verify the required parameter 'dominiId' is set
//        if (dominiId == null) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'dominiId' when calling updateDominiV1");
//        }
//        // create path and map variables
//        final Map<String, Object> uriVariables = new HashMap<String, Object>();
//        uriVariables.put("dominiId", dominiId);
//        String path = UriComponentsBuilder.fromPath("/api/v1/dominis/{dominiId}").buildAndExpand(uriVariables).toUriString();
//        
//        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
//        final HttpHeaders headerParams = new HttpHeaders();
//        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
//
//        final String[] accepts = {  };
//        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
//        final String[] contentTypes = { 
//            "application/json"
//         };
//        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);
//
//        String[] authNames = new String[] {  };
//
//        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
//        apiClient.invokeAPI(path, HttpMethod.PUT, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
//    }
//}
