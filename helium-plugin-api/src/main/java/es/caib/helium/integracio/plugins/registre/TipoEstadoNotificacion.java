package es.caib.helium.integracio.plugins.registre;

/**
 * Java class for TipoEstadoNotificacion.
 */
public enum TipoEstadoNotificacion {

    PENDIENTE,
    ENTREGADA,
    RECHAZADA;

    public String value() {
        return name();
    }

    public static TipoEstadoNotificacion fromValue(String v) {
        return valueOf(v);
    }
}
