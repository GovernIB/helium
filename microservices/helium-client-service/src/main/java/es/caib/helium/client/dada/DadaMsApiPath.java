package es.caib.helium.client.dada;

public class DadaMsApiPath {

	public static final String DADA_PATH = "/api/v1/dades/expedients/";
	
	// Path per gestionar les dades de la capçalera de l'expedient
	
	public static final String CONSULTA_RESULTATS = DADA_PATH + "consulta/resultats";

	public static final String CONSULTA_RESULTATS_LLISTAT = DADA_PATH + "consulta/resultats/llistat";

	public static final String GET_BY_EXPEDIENT_ID = DADA_PATH + "{expedientId}";

	public static final String POST_DADES_CAPCALERA = DADA_PATH +  "";

	public static final String POST_CREAR_EXPEDIENTS_DADES_CAPCALERA = DADA_PATH + "crear/expedients";

	public static final String DELETE_BY_EXPEDIENT_ID = DADA_PATH + "{expedientId}";

	public static final String DELETE_EXPEDIENTS = DADA_PATH + "borrar/expedients";

	public static final String PUT_BY_EXPEDIENT_ID = DADA_PATH + "{expedientId}";

	public static final String PUT_EXPEDIENTS = DADA_PATH + "put/expedients";

	public static final String PATCH_BY_EXPEDIENT_ID = DADA_PATH + "{expedientId}";
	
	public static final String PATCH_EXPEDIENTS = DADA_PATH + "patch/expedients";
	
	// PATH per gestionar 

	public static final String GET_DADES = DADA_PATH + "{expedientId}/dades";

	public static final String GET_DADA_BY_EXPEDIENT_ID_AND_CODI = DADA_PATH + "{expedientId}/dades/{codi}";

	public static final String GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID = DADA_PATH + "{expedientId}/proces/{procesId}/dades";

	public static final String GET_DADA_BY_EXPEDIENT_ID_AND_PROCES_ID_AND_CODI = DADA_PATH + "{expedientId}/proces/{procesId}/dades/{codi}";

	public static final String GET_DADA_BY_PROCES_ID_AND_CODI = DADA_PATH + "proces/{procesId}/dades/{codi}";
	
	public static final String GET_DADA_EXPEDIENT_ID_BY_PROCES_ID = DADA_PATH + "proces/{procesId}/dades/expedient/id";

	public static final String GET_EXPEDIENT_ID_BY_PROCES_ID = DADA_PATH + "proces/{procesId}/dades/expedient/id";
	
	public static final String POST_DADES_BY_EXPEDIENT_ID = DADA_PATH + "{expedientId}/dades";

	public static final String PUT_DADES_BY_EXPEDIENT_ID_AND_CODI = DADA_PATH + "{expedientId}/dades/codi";

	public static final String DELETE_DADES_BY_EXPEDIENT_ID_AND_CODI = DADA_PATH + "{expedientId}/dades/codi";

	public static final String POST_DADES_BY_EXPEDIENT_ID_AND_PROCES_ID = DADA_PATH + "{expedientId}/proces/{procesId}/dades";
	
	
}


