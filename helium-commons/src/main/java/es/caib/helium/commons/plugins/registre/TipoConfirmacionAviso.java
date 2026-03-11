package es.caib.helium.commons.plugins.registre;

/**
 * Java class for TipoConfirmacionAviso.
 */
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
