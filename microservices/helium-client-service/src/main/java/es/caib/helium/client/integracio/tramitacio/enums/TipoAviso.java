package es.caib.helium.client.integracio.tramitacio.enums;

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
