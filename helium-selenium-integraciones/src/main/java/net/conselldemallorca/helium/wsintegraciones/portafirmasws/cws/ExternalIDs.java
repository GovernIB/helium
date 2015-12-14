
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ExternalIDs complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ExternalIDs">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logical-id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="verification-code" type="{http://www.indra.es/portafirmasws/cws}VerificationCode" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExternalIDs", propOrder = {
    "logicalId",
    "verificationCode"
})
public class ExternalIDs {

    @XmlElementRef(name = "logical-id", type = JAXBElement.class)
    protected JAXBElement<String> logicalId;
    @XmlElementRef(name = "verification-code", type = JAXBElement.class)
    protected JAXBElement<VerificationCode> verificationCode;

    /**
     * Obtiene el valor de la propiedad logicalId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLogicalId() {
        return logicalId;
    }

    /**
     * Define el valor de la propiedad logicalId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLogicalId(JAXBElement<String> value) {
        this.logicalId = value;
    }

    /**
     * Obtiene el valor de la propiedad verificationCode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link VerificationCode }{@code >}
     *     
     */
    public JAXBElement<VerificationCode> getVerificationCode() {
        return verificationCode;
    }

    /**
     * Define el valor de la propiedad verificationCode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link VerificationCode }{@code >}
     *     
     */
    public void setVerificationCode(JAXBElement<VerificationCode> value) {
        this.verificationCode = value;
    }

}
