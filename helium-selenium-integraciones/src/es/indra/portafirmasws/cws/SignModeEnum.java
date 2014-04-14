
package es.indra.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SignModeEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="SignModeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="attached"/>
 *     &lt;enumeration value="detached"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SignModeEnum")
@XmlEnum
public enum SignModeEnum {

    @XmlEnumValue("attached")
    ATTACHED("attached"),
    @XmlEnumValue("detached")
    DETACHED("detached");
    private final String value;

    SignModeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SignModeEnum fromValue(String v) {
        for (SignModeEnum c: SignModeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
