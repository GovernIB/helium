package es.caib.helium.client.expedient.tasca;

public class TascaApiPath {

	public static final String API_PATH = "/api/v1/tasques";

	public static final String FIND_TASQUES_AMB_FILTRE_PAGINAT = API_PATH + "";

	public static final String FIND_TASQUES_IDS_AMB_FILTRE_PAGINAT = API_PATH + "/ids";

	public static final String CREATE_TASCA = API_PATH + "";

	public static final String UPDATE_TASCA = API_PATH + "/{tascaId}";

	public static final String PATCH_TASCA = API_PATH + "/{tascaId}";

	public static final String DELETE_TASCA = API_PATH + "/{tascaId}";
	
	public static final String GET_TASCA = API_PATH + "/{tascaId}";
	
	public static final String GET_RESPONSABLES = API_PATH + "/{tascaId}/responsables";

	public static final String SET_RESPONSABLES = API_PATH + "/{tascaId}/responsables";
	
	public static final String DELETE_RESPONSABLES = API_PATH + "/{tascaId}/responsables";
}
