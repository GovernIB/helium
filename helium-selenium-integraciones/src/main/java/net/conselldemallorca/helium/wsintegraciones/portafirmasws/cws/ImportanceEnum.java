
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ImportanceEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ImportanceEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="low"/>
 *     &lt;enumeration value="normal"/>
 *     &lt;enumeration value="high"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ImportanceEnum")
@XmlEnum
public enum ImportanceEnum {

    @XmlEnumValue("low")
    LOW("low"),
    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("high")
    HIGH("high");
    private final String value;

    ImportanceEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImportanceEnum fromValue(String v) {
        for (ImportanceEnum c: ImportanceEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
