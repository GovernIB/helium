
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ProfileEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ProfileEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="source"/>
 *     &lt;enumeration value="signature"/>
 *     &lt;enumeration value="visual"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ProfileEnum")
@XmlEnum
public enum ProfileEnum {

    @XmlEnumValue("source")
    SOURCE("source"),
    @XmlEnumValue("signature")
    SIGNATURE("signature"),
    @XmlEnumValue("visual")
    VISUAL("visual");
    private final String value;

    ProfileEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProfileEnum fromValue(String v) {
        for (ProfileEnum c: ProfileEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
