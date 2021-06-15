
package net.conselldemallorca.helium.api.dto;

/**
 * Java class for TipoAviso.
 */
public enum TipoAvisoDto {
    EMAIL,
    SMS;

    public String value() {
        return name();
    }

    public static TipoAvisoDto fromValue(String v) {
        return valueOf(v);
    }

}
