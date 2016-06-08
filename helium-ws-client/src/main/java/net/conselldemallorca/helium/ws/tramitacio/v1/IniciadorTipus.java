
package net.conselldemallorca.helium.ws.tramitacio.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for iniciadorTipus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="iniciadorTipus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INTERN"/>
 *     &lt;enumeration value="SISTRA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "iniciadorTipus")
@XmlEnum
public enum IniciadorTipus {

    INTERN,
    SISTRA;

    public String value() {
        return name();
    }

    public static IniciadorTipus fromValue(String v) {
        return valueOf(v);
    }

}
