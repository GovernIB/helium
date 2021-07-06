package es.caib.helium.client.integracio.persones;

public class PersonesPath {

	public static final String API_PATH = "/api/v1/persones";
	
	public static final String GET_PERSONES = API_PATH + "";

	public static final String GET_PERSONA_BY_CODI = API_PATH + "{codi}";

	public static final String GET_PERSONA_ROLS_BY_CODI = API_PATH + "{codi}/rols";
}
