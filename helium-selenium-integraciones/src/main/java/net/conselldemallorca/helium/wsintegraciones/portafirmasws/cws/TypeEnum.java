
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TypeEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="document"/>
 *     &lt;enumeration value="annex"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeEnum")
@XmlEnum
public enum TypeEnum {

    @XmlEnumValue("document")
    DOCUMENT("document"),
    @XmlEnumValue("annex")
    ANNEX("annex");
    private final String value;

    TypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TypeEnum fromValue(String v) {
        for (TypeEnum c: TypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
