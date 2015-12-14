
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CriteriaEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="CriteriaEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="state"/>
 *     &lt;enumeration value="importance"/>
 *     &lt;enumeration value="dateentry"/>
 *     &lt;enumeration value="searchtype"/>
 *     &lt;enumeration value="usercode"/>
 *     &lt;enumeration value="applicationname"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CriteriaEnum")
@XmlEnum
public enum CriteriaEnum {

    @XmlEnumValue("state")
    STATE("state"),
    @XmlEnumValue("importance")
    IMPORTANCE("importance"),
    @XmlEnumValue("dateentry")
    DATEENTRY("dateentry"),
    @XmlEnumValue("searchtype")
    SEARCHTYPE("searchtype"),
    @XmlEnumValue("usercode")
    USERCODE("usercode"),
    @XmlEnumValue("applicationname")
    APPLICATIONNAME("applicationname");
    private final String value;

    CriteriaEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CriteriaEnum fromValue(String v) {
        for (CriteriaEnum c: CriteriaEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
