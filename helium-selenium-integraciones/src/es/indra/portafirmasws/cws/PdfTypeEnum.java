
package es.indra.portafirmasws.cws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PdfTypeEnum.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="PdfTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="invisible"/>
 *     &lt;enumeration value="page-of-signatures"/>
 *     &lt;enumeration value="defined"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PdfTypeEnum")
@XmlEnum
public enum PdfTypeEnum {

    @XmlEnumValue("invisible")
    INVISIBLE("invisible"),
    @XmlEnumValue("page-of-signatures")
    PAGE_OF_SIGNATURES("page-of-signatures"),
    @XmlEnumValue("defined")
    DEFINED("defined");
    private final String value;

    PdfTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PdfTypeEnum fromValue(String v) {
        for (PdfTypeEnum c: PdfTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
