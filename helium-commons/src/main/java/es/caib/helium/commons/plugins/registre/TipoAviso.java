
package es.caib.helium.commons.plugins.registre;

/**
 * Java class for TipoAviso.
 */
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
