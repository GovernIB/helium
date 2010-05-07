
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentoBTE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoBTE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="presentacionTelematica" type="{urn:es:caib:bantel:ws:v1:model:DatosDocumentoTelematico}DatosDocumentoTelematico" minOccurs="0"/>
 *         &lt;element name="presentacionPresencial" type="{urn:es:caib:bantel:ws:v1:model:DatosDocumentoPresencial}DatosDocumentoPresencial" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoBTE", namespace = "urn:es:caib:bantel:ws:v1:model:DocumentoBTE", propOrder = {
    "nombre",
    "presentacionTelematica",
    "presentacionPresencial"
})
public class DocumentoBTE {

    @XmlElement(required = true)
    protected String nombre;
    @XmlElementRef(name = "presentacionTelematica", type = JAXBElement.class)
    protected JAXBElement<DatosDocumentoTelematico> presentacionTelematica;
    @XmlElementRef(name = "presentacionPresencial", type = JAXBElement.class)
    protected JAXBElement<DatosDocumentoPresencial> presentacionPresencial;

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the presentacionTelematica property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}
     *     
     */
    public JAXBElement<DatosDocumentoTelematico> getPresentacionTelematica() {
        return presentacionTelematica;
    }

    /**
     * Sets the value of the presentacionTelematica property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}
     *     
     */
    public void setPresentacionTelematica(JAXBElement<DatosDocumentoTelematico> value) {
        this.presentacionTelematica = ((JAXBElement<DatosDocumentoTelematico> ) value);
    }

    /**
     * Gets the value of the presentacionPresencial property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}
     *     
     */
    public JAXBElement<DatosDocumentoPresencial> getPresentacionPresencial() {
        return presentacionPresencial;
    }

    /**
     * Sets the value of the presentacionPresencial property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}
     *     
     */
    public void setPresentacionPresencial(JAXBElement<DatosDocumentoPresencial> value) {
        this.presentacionPresencial = ((JAXBElement<DatosDocumentoPresencial> ) value);
    }

}
