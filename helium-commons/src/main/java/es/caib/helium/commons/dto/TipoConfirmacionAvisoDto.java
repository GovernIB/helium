package es.caib.helium.commons.dto;

/**
 * Java class for TipoConfirmacionAviso.
 */
public enum TipoConfirmacionAvisoDto {
    DESCONOCIDO,
    ENVIADO,
    NO_ENVIADO;

    public String value() {
        return name();
    }

    public static TipoConfirmacionAvisoDto fromValue(String v) {
        return valueOf(v);
    }
}
