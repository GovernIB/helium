package es.caib.helium.client.expedient.proces;

public class ProcesApiPath {

	public static final String API_PATH = "/api/v1/processos";
	
	public static final String FIND_PROCESSOS_AMB_FILTRE_PAGINAT = API_PATH + "";

	public static final String FIND_PROCESSOS_IDS_AMB_FILTRE_PAGINAT = API_PATH + "/ids";

	public static final String CREATE_PROCES = API_PATH + "";

	public static final String UPDATE_PROCES = API_PATH + "/{procesId}";
	
	public static final String PUT_PROCES = API_PATH + "/{procesId}";

	public static final String PATCH_PROCES = API_PATH + "/{procesId}";

	public static final String DELETE_PROCES = API_PATH + "/{procesId}";
	
	public static final String GET_PROCES = API_PATH + "/{procesId}";
	
	public static final String GET_PROCES_LLISTAT = API_PATH + "/{procesId}/llistat";

	public static final String GET_PROCES_EXPEDIENT_ID = API_PATH + "/{procesId}/expedientId";
}
