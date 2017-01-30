
package es.caib.regtel.ws.v2.model.detalleacuserecibo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TipoEstadoNotificacion.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TipoEstadoNotificacion">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PENDIENTE"/>
 *     &lt;enumeration value="ENTREGADA"/>
 *     &lt;enumeration value="RECHAZADA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TipoEstadoNotificacion")
@XmlEnum
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
