
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ConditionEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="ConditionEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="in"/>
 *     &lt;enumeration value="between"/>
 *     &lt;enumeration value="equal"/>
 *     &lt;enumeration value="notEqual"/>
 *     &lt;enumeration value="greater"/>
 *     &lt;enumeration value="greaterEqual"/>
 *     &lt;enumeration value="less"/>
 *     &lt;enumeration value="lessEqual"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConditionEnum")
@XmlEnum
public enum ConditionEnum {

    @XmlEnumValue("in")
    IN("in"),
    @XmlEnumValue("between")
    BETWEEN("between"),
    @XmlEnumValue("equal")
    EQUAL("equal"),
    @XmlEnumValue("notEqual")
    NOT_EQUAL("notEqual"),
    @XmlEnumValue("greater")
    GREATER("greater"),
    @XmlEnumValue("greaterEqual")
    GREATER_EQUAL("greaterEqual"),
    @XmlEnumValue("less")
    LESS("less"),
    @XmlEnumValue("lessEqual")
    LESS_EQUAL("lessEqual");
    private final String value;

    ConditionEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConditionEnum fromValue(String v) {
        for (ConditionEnum c: ConditionEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
