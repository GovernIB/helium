package es.caib.helium.api.dto;

/**
 * Java class for TipoEstadoNotificacion.
 */
public enum TipoEstadoNotificacionDto {

    PENDIENTE,
    ENTREGADA,
    RECHAZADA;

    public String value() {
        return name();
    }

    public static TipoEstadoNotificacionDto fromValue(String v) {
        return valueOf(v);
    }
}
