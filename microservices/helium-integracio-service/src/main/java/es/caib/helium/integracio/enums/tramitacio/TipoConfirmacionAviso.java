package es.caib.helium.integracio.enums.tramitacio;

public enum TipoConfirmacionAviso {
	
    DESCONOCIDO,
    ENVIADO,
    NO_ENVIADO;

    public String value() {
        return name();
    }

    public static TipoConfirmacionAviso fromValue(String v) {
        return valueOf(v);
    }
}
