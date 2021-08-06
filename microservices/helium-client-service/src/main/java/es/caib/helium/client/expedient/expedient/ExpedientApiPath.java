package es.caib.helium.client.expedient.expedient;

public class ExpedientApiPath {

	public static final String API_PATH = "/api/v1/expedients";
	
	public static final String FIND_EXPEDIENTS_AMB_FILTRE_PAGINAT = API_PATH + "";

	public static final String FIND_EXPEDIENTS_IDS_AMB_FILTRE_PAGINAT = API_PATH + "/ids";

	public static final String CREATE_EXPEDIENT = API_PATH + "";

	public static final String UPDATE_EXPEDIENT = API_PATH + "/{expedientId}";
	
	public static final String PUT_EXPEDIENT = API_PATH + "/{expedientId}";

	public static final String PATCH_EXPEDIENT = API_PATH + "/{expedientId}";

	public static final String DELETE_EXPEDIENT = API_PATH + "/{expedientId}";
	
	public static final String GET_EXPEDIENT = API_PATH + "/{expedientId}";
}
