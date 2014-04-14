
package es.indra.portafirmasws.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SignatureImageDimensions complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SignatureImageDimensions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="height" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="x_padding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="y_padding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureImageDimensions", propOrder = {
    "width",
    "height",
    "xPadding",
    "yPadding"
})
public class SignatureImageDimensions {

    @XmlElement(required = true)
    protected String width;
    @XmlElement(required = true)
    protected String height;
    @XmlElement(name = "x_padding", required = true)
    protected String xPadding;
    @XmlElement(name = "y_padding", required = true)
    protected String yPadding;

    /**
     * Obtiene el valor de la propiedad width.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Define el valor de la propiedad width.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Obtiene el valor de la propiedad height.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeight() {
        return height;
    }

    /**
     * Define el valor de la propiedad height.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeight(String value) {
        this.height = value;
    }

    /**
     * Obtiene el valor de la propiedad xPadding.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXPadding() {
        return xPadding;
    }

    /**
     * Define el valor de la propiedad xPadding.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXPadding(String value) {
        this.xPadding = value;
    }

    /**
     * Obtiene el valor de la propiedad yPadding.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYPadding() {
        return yPadding;
    }

    /**
     * Define el valor de la propiedad yPadding.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYPadding(String value) {
        this.yPadding = value;
    }

}
