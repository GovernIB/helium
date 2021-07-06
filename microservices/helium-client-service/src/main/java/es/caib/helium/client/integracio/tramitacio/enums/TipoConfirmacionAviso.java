package es.caib.helium.client.integracio.tramitacio.enums;

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
