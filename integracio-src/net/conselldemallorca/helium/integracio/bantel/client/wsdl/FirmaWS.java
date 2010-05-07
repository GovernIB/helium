
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FirmaWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FirmaWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firma" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="formato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmaWS", namespace = "urn:es:caib:bantel:ws:v1:model:FirmaWS", propOrder = {
    "firma",
    "formato"
})
public class FirmaWS {

    @XmlElement(required = true)
    protected byte[] firma;
    @XmlElementRef(name = "formato", type = JAXBElement.class)
    protected JAXBElement<String> formato;

    /**
     * Gets the value of the firma property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFirma() {
        return firma;
    }

    /**
     * Sets the value of the firma property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFirma(byte[] value) {
        this.firma = ((byte[]) value);
    }

    /**
     * Gets the value of the formato property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFormato() {
        return formato;
    }

    /**
     * Sets the value of the formato property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFormato(JAXBElement<String> value) {
        this.formato = ((JAXBElement<String> ) value);
    }

}
