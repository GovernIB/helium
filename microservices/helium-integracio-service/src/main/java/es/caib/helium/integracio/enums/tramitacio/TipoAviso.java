package es.caib.helium.integracio.enums.tramitacio;

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
