package es.caib.helium.client.engine.areaCarrec;

public class AreaCarrecApiPath {

    public static final String API_PATH = "/api/v1";

    public static final String FIND_AREES_BY_FILTRE = "/arees/byFiltre/{filtre}";

    public static final String FIND_AREES_BY_PERSONA = "/arees/{personaCodi}";

    public static final String FIND_ROLS_BY_PERSONA = "/rols/{personaCodi}";

    public static final String FIND_CARRECS_BY_FILTRE = "/carrecs/byFiltre/{filtre}";

    public static final String FIND_PERSONES_BY_GRUP_AND_CARREC = "/persones/{grupCodi}/{carrecCodi)";

    public static final String FIND_CARRECS_BY_PERSONA_AND_GRUP = "/carrecs/{grupCodi}/{personaCodi}";

    public static final String FIND_PERSONES_BY_CARREC = "/persones/byCarrec/{carrecCodi}";

    public static final String FIND_PERSONES_BY_GRUP = "/persones/byGrup/{grupCodi}";
}
