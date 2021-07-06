package es.caib.helium.client.integracio.custodia;

import es.caib.helium.client.integracio.IntegracioMsApiPath;

public class CustodiaPath extends IntegracioMsApiPath {

	public static final String API_PATH = "/api/v1/custodia";
	
	public static final String AFEGIR_SIGNATURA = API_PATH + "";
	
	public static final String GET_SIGNATURES = API_PATH + "{documentId}";

	public static final String DELETE_SIGNATURES = API_PATH + "{documentId}";

	public static final String GET_SIGNATURES_AMB_ARXIU = API_PATH + "{documentId}/arxiu";

	public static final String GET_DADES_VALIDACIO_SIGNATURA = API_PATH + "{documentId}/validacio";

	public static final String GET_URL_COMPROVACIO_SIGNATURES = API_PATH + "{documentId}/url/comprovacio";
	
}
