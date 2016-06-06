package net.conselldemallorca.helium.jbpm3.handlers.tipus;

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
