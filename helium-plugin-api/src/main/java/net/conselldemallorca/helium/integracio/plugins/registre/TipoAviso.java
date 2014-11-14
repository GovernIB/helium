
package net.conselldemallorca.helium.integracio.plugins.registre;

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
