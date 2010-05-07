
package net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Signer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Signer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="delegate" type="{http://www.indra.es/portafirmasmcgdws/mcgdws}Delegate" minOccurs="0"/>
 *         &lt;element name="certificate" type="{http://www.indra.es/portafirmasmcgdws/mcgdws}Certificate" minOccurs="0"/>
 *         &lt;element name="rejection" type="{http://www.indra.es/portafirmasmcgdws/mcgdws}Rejection" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Signer", propOrder = {
    "id",
    "date",
    "delegate",
    "certificate",
    "rejection"
})
public class Signer {

    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected Delegate delegate;
    protected Certificate certificate;
    protected Rejection rejection;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the delegate property.
     * 
     * @return
     *     possible object is
     *     {@link Delegate }
     *     
     */
    public Delegate getDelegate() {
        return delegate;
    }

    /**
     * Sets the value of the delegate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Delegate }
     *     
     */
    public void setDelegate(Delegate value) {
        this.delegate = value;
    }

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link Certificate }
     *     
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificate }
     *     
     */
    public void setCertificate(Certificate value) {
        this.certificate = value;
    }

    /**
     * Gets the value of the rejection property.
     * 
     * @return
     *     possible object is
     *     {@link Rejection }
     *     
     */
    public Rejection getRejection() {
        return rejection;
    }

    /**
     * Sets the value of the rejection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rejection }
     *     
     */
    public void setRejection(Rejection value) {
        this.rejection = value;
    }

}
