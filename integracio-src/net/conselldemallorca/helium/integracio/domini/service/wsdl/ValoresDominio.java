
package net.conselldemallorca.helium.integracio.domini.service.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValoresDominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValoresDominio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="descripcionError" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="filas" type="{urn:es:caib:sistra:ws:v1:model:ValoresDominio}filas"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValoresDominio", namespace = "urn:es:caib:sistra:ws:v1:model:ValoresDominio", propOrder = {
    "descripcionError",
    "error",
    "filas"
})
public class ValoresDominio {

    @XmlElementRef(name = "descripcionError", type = JAXBElement.class)
    protected JAXBElement<String> descripcionError;
    @XmlElement(defaultValue = "false")
    protected boolean error;
    @XmlElement(required = true)
    protected Filas filas;

    /**
     * Gets the value of the descripcionError property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDescripcionError() {
        return descripcionError;
    }

    /**
     * Sets the value of the descripcionError property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDescripcionError(JAXBElement<String> value) {
        this.descripcionError = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the error property.
     * 
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     */
    public void setError(boolean value) {
        this.error = value;
    }

    /**
     * Gets the value of the filas property.
     * 
     * @return
     *     possible object is
     *     {@link Filas }
     *     
     */
    public Filas getFilas() {
        return filas;
    }

    /**
     * Sets the value of the filas property.
     * 
     * @param value
     *     allowed object is
     *     {@link Filas }
     *     
     */
    public void setFilas(Filas value) {
        this.filas = value;
    }

}
