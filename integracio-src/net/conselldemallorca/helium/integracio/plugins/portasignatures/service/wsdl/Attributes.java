
package net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Attributes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attributes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="state" type="{http://www.indra.es/portafirmasmcgdws/mcgdws}stateEnum"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="date-last-update" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="sign-annexes" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="external-data" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attributes", propOrder = {
    "state",
    "title",
    "dateLastUpdate",
    "signAnnexes",
    "externalData"
})
public class Attributes {

    protected int state;
    @XmlElement(required = true)
    protected String title;
    @XmlElement(name = "date-last-update", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateLastUpdate;
    @XmlElement(name = "sign-annexes")
    protected boolean signAnnexes;
    @XmlElement(name = "external-data", required = true, nillable = true)
    protected String externalData;

    /**
     * Gets the value of the state property.
     * 
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     */
    public void setState(int value) {
        this.state = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the dateLastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLastUpdate() {
        return dateLastUpdate;
    }

    /**
     * Sets the value of the dateLastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLastUpdate(XMLGregorianCalendar value) {
        this.dateLastUpdate = value;
    }

    /**
     * Gets the value of the signAnnexes property.
     * 
     */
    public boolean isSignAnnexes() {
        return signAnnexes;
    }

    /**
     * Sets the value of the signAnnexes property.
     * 
     */
    public void setSignAnnexes(boolean value) {
        this.signAnnexes = value;
    }

    /**
     * Gets the value of the externalData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalData() {
        return externalData;
    }

    /**
     * Sets the value of the externalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalData(String value) {
        this.externalData = value;
    }

}
