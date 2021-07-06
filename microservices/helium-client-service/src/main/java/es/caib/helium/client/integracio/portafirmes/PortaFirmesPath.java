package es.caib.helium.client.integracio.portafirmes;

public class PortaFirmesPath {
	
	public static final String API_PATH = "/api/v1/persones";
	
	public static final String GET_BY_DOCUMENT_ID = API_PATH + "{documentId}";
	
	public static final String GET_BY_PROCESS_INSTANCE_ID = API_PATH +"proces/{processInstanceId}";
	
	public static final String GET_BY_PROCESS_INSTANCE_ID_AND_DOCUMENT_STORE_ID = API_PATH + "proces/{processInstanceId}/ds/{documentStoreId}";
	
	public static final String GET_PENDENTS_FIRMAR = API_PATH + "";

	public static final String GET_BY_EXPEDIENT_ID_AND_ESTAT = API_PATH + "expedient/{expedientId}/estat/{estat}";

	public static final String GET_BY_PROCESS_INSTANCE_ID_AND_ESTAT_NOT_IN = API_PATH + "proces/{processInstanceId}/not/estat";

	public static final String GET_PENDENTS_FIRMAR_BY_PROCESINSTANCE = API_PATH + "{processInstanceId}/pendents";

	public static final String ENVIAR_PORTA_FIRMES = API_PATH + "";

	public static final String POST_PORTA_FIRMA = API_PATH + "portafirma";

	public static final String CANCELAR_ENVIAMENTS = API_PATH + "";

	public static final String PROCESSAR_DOCUMENT_CALL_BACK = API_PATH + "processar/{documentId}/callback";
	
}
