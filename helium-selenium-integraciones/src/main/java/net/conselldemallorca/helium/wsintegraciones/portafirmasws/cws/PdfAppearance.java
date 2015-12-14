
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PdfAppearance complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PdfAppearance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="signature-image" type="{http://www.indra.es/portafirmasws/cws}SignatureImage" minOccurs="0"/>
 *         &lt;element name="positions" type="{http://www.indra.es/portafirmasws/cws}Positions" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PdfAppearance", propOrder = {
    "signatureImage",
    "positions"
})
public class PdfAppearance {

    @XmlElementRef(name = "signature-image", type = JAXBElement.class)
    protected JAXBElement<SignatureImage> signatureImage;
    protected Positions positions;

    /**
     * Obtiene el valor de la propiedad signatureImage.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignatureImage }{@code >}
     *     
     */
    public JAXBElement<SignatureImage> getSignatureImage() {
        return signatureImage;
    }

    /**
     * Define el valor de la propiedad signatureImage.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignatureImage }{@code >}
     *     
     */
    public void setSignatureImage(JAXBElement<SignatureImage> value) {
        this.signatureImage = value;
    }

    /**
     * Obtiene el valor de la propiedad positions.
     * 
     * @return
     *     possible object is
     *     {@link Positions }
     *     
     */
    public Positions getPositions() {
        return positions;
    }

    /**
     * Define el valor de la propiedad positions.
     * 
     * @param value
     *     allowed object is
     *     {@link Positions }
     *     
     */
    public void setPositions(Positions value) {
        this.positions = value;
    }

}
