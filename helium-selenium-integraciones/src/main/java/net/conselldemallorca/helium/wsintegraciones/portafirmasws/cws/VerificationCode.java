
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para VerificationCode complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="VerificationCode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="provider-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdf-position" type="{http://www.indra.es/portafirmasws/cws}PdfPosition" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VerificationCode", propOrder = {
    "providerId",
    "value",
    "pdfPosition"
})
public class VerificationCode {

    @XmlElement(name = "provider-id", required = true, nillable = true)
    protected String providerId;
    protected String value;
    @XmlElementRef(name = "pdf-position", type = JAXBElement.class)
    protected JAXBElement<PdfPosition> pdfPosition;

    /**
     * Obtiene el valor de la propiedad providerId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * Define el valor de la propiedad providerId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderId(String value) {
        this.providerId = value;
    }

    /**
     * Obtiene el valor de la propiedad value.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Define el valor de la propiedad value.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Obtiene el valor de la propiedad pdfPosition.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}
     *     
     */
    public JAXBElement<PdfPosition> getPdfPosition() {
        return pdfPosition;
    }

    /**
     * Define el valor de la propiedad pdfPosition.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}
     *     
     */
    public void setPdfPosition(JAXBElement<PdfPosition> value) {
        this.pdfPosition = value;
    }

}
