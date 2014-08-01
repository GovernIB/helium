
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CodificationTypeEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="CodificationTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="binary"/>
 *     &lt;enumeration value="base64"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CodificationTypeEnum")
@XmlEnum
public enum CodificationTypeEnum {

    @XmlEnumValue("binary")
    BINARY("binary"),
    @XmlEnumValue("base64")
    BASE_64("base64");
    private final String value;

    CodificationTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CodificationTypeEnum fromValue(String v) {
        for (CodificationTypeEnum c: CodificationTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
