package es.caib.helium.client.integracio.tramitacio.enums;

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
