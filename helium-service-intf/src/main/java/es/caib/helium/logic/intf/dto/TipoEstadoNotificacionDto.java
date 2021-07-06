package es.caib.helium.logic.intf.dto;

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
