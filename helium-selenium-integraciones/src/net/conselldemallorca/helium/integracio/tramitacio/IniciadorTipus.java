
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para iniciadorTipus.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
