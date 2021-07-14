package es.caib.helium.client.integracio.tramitacio;

public class TramitacioPath {

	public static final String API_PATH = "/api/v1/tramitacio";
	
	public static final String GET_JUSTIFICANT = "{numRegistre}/justificant";

	public static final String GET_JUSTIFICANT_DETALL = "{numRegistre}/justificant/detall";

	public static final String GET_DADES_TRAMIT = "dades/tramit";

	public static final String OBTENIR_VISTA_DOCUMENT = "vista/document";

	public static final String TRAMITACIO_COMUNICAR_RESULTAT_PROCES = "comunicar/resultat/proces";

	public static final String CREAR_EXPEDIENT_ZONA_PERSONAL = "zonaper/expedient/crear";

	public static final String CREAR_EVENT_ZONA_PERSONAL = "zonaper/event/crear";

	public static final String REGISTRAR_NOTIFICACIO = "zonaper/notificacio/registrar";

	public static final String EXISTEIX_EXPEDIENT = "expedient/{identificadorExpediente}/unitat/{unidadAdministrativa}";

}
