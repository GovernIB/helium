package es.caib.helium.client.integracio.arxiu;

import es.caib.helium.client.integracio.IntegracioMsApiPath;

public class ArxiuPath extends IntegracioMsApiPath {
	
	public static final String API_PATH = "/api/v1/arxiu/";

	public static final String GET_EXPEDIENT_BY_UUID = API_PATH + "expedient/{uuId}";

	public static final String POST_EXPEDIENT = API_PATH + "expedients";

	public static final String PUT_EXPEDIENT = API_PATH + "expedients";

	public static final String DELETE_EXPEDIENT = API_PATH + "expedients/{uuId}";

	public static final String TANCAR_EXPEDIENT = API_PATH + "expedients/{arxiuUuId}/tancar";

	public static final String OBRIR_EXPEDIENT = API_PATH + "expedients/{arxiuUuId}/obrir";
	
	public static final String GET_DOCUMENT = API_PATH + "documents/{uuId}";

	public static final String POST_DOCUMENT = API_PATH + "documents";

	public static final String PUT_DOCUMENT = API_PATH + "documents";

	public static final String DELETE_DOCUMENT = API_PATH + "documents/{uuId}";
}
