
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciaEntrada complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciaEntrada">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroEntrada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="claveAcceso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenciaEntrada", namespace = "urn:es:caib:bantel:ws:v1:model:ReferenciaEntrada", propOrder = {
    "numeroEntrada",
    "claveAcceso"
})
public class ReferenciaEntrada {

    @XmlElement(required = true)
    protected String numeroEntrada;
    @XmlElementRef(name = "claveAcceso", type = JAXBElement.class)
    protected JAXBElement<String> claveAcceso;

    /**
     * Gets the value of the numeroEntrada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroEntrada() {
        return numeroEntrada;
    }

    /**
     * Sets the value of the numeroEntrada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroEntrada(String value) {
        this.numeroEntrada = value;
    }

    /**
     * Gets the value of the claveAcceso property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaveAcceso() {
        return claveAcceso;
    }

    /**
     * Sets the value of the claveAcceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaveAcceso(JAXBElement<String> value) {
        this.claveAcceso = ((JAXBElement<String> ) value);
    }

}
