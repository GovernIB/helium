package net.conselldemallorca.helium.jbpm3.handlers.tipus;

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
