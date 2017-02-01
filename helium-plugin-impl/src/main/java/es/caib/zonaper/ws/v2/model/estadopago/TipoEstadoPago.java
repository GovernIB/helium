
package es.caib.zonaper.ws.v2.model.estadopago;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoEstadoPago.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoEstadoPago">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAGADO"/>
 *     &lt;enumeration value="PENDIENTE_CONFIRMAR"/>
 *     &lt;enumeration value="NO_PAGADO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoEstadoPago")
@XmlEnum
public enum TipoEstadoPago {

    PAGADO,
    PENDIENTE_CONFIRMAR,
    NO_PAGADO;

    public String value() {
        return name();
    }

    public static TipoEstadoPago fromValue(String v) {
        return valueOf(v);
    }

}
