
package es.caib.zonaper.ws.v2.model.estadopago;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoEstadoTramite.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoEstadoTramite">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDIENTE_ENVIAR"/>
 *     &lt;enumeration value="ENVIADO"/>
 *     &lt;enumeration value="NO_EXISTE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoEstadoTramite")
@XmlEnum
public enum TipoEstadoTramite {

    PENDIENTE_ENVIAR,
    ENVIADO,
    NO_EXISTE;

    public String value() {
        return name();
    }

    public static TipoEstadoTramite fromValue(String v) {
        return valueOf(v);
    }

}
