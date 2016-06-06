package net.conselldemallorca.helium.jbpm3.handlers.tipus;

public enum TipoAviso {

	EMAIL,
    SMS;

    public String value() {
        return name();
    }

    public static TipoAviso fromValue(String v) {
        return valueOf(v);
    }
    
}
