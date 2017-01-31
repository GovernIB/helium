
package es.caib.zonaper.ws.v2.model.configuracionavisosexpediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConfiguracionAvisosExpediente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConfiguracionAvisosExpediente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="habilitarAvisos" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="avisoSMS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisoEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConfiguracionAvisosExpediente", propOrder = {
    "habilitarAvisos",
    "avisoSMS",
    "avisoEmail"
})
public class ConfiguracionAvisosExpediente {

    @XmlElementRef(name = "habilitarAvisos", type = JAXBElement.class)
    protected JAXBElement<Boolean> habilitarAvisos;
    @XmlElementRef(name = "avisoSMS", type = JAXBElement.class)
    protected JAXBElement<String> avisoSMS;
    @XmlElementRef(name = "avisoEmail", type = JAXBElement.class)
    protected JAXBElement<String> avisoEmail;

    /**
     * Gets the value of the habilitarAvisos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getHabilitarAvisos() {
        return habilitarAvisos;
    }

    /**
     * Sets the value of the habilitarAvisos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setHabilitarAvisos(JAXBElement<Boolean> value) {
        this.habilitarAvisos = value;
    }

    /**
     * Gets the value of the avisoSMS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoSMS() {
        return avisoSMS;
    }

    /**
     * Sets the value of the avisoSMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoSMS(JAXBElement<String> value) {
        this.avisoSMS = value;
    }

    /**
     * Gets the value of the avisoEmail property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoEmail() {
        return avisoEmail;
    }

    /**
     * Sets the value of the avisoEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoEmail(JAXBElement<String> value) {
        this.avisoEmail = value;
    }

}
