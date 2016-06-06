
package net.conselldemallorca.helium.integracio.tramitacio;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for tascaTramitacio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tascaTramitacio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cancelled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="completed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dataCreacio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataFi" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataInici" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataLimit" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="expedient" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="missatgeInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="missatgeWarn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="prioritat" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsables" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="suspended" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="titol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transicionsSortida" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tascaTramitacio", propOrder = {
    "cancelled",
    "codi",
    "completed",
    "dataCreacio",
    "dataFi",
    "dataInici",
    "dataLimit",
    "expedient",
    "id",
    "missatgeInfo",
    "missatgeWarn",
    "open",
    "prioritat",
    "processInstanceId",
    "responsable",
    "responsables",
    "suspended",
    "titol",
    "transicionsSortida"
})
public class TascaTramitacio {

    protected boolean cancelled;
    protected String codi;
    protected boolean completed;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataCreacio;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataFi;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataInici;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataLimit;
    protected String expedient;
    protected String id;
    protected String missatgeInfo;
    protected String missatgeWarn;
    protected boolean open;
    protected int prioritat;
    protected String processInstanceId;
    protected String responsable;
    @XmlElement(nillable = true)
    protected List<String> responsables;
    protected boolean suspended;
    protected String titol;
    @XmlElement(nillable = true)
    protected List<String> transicionsSortida;

    /**
     * Gets the value of the cancelled property.
     * 
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the value of the cancelled property.
     * 
     */
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    /**
     * Gets the value of the codi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodi() {
        return codi;
    }

    /**
     * Sets the value of the codi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodi(String value) {
        this.codi = value;
    }

    /**
     * Gets the value of the completed property.
     * 
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the value of the completed property.
     * 
     */
    public void setCompleted(boolean value) {
        this.completed = value;
    }

    /**
     * Gets the value of the dataCreacio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCreacio() {
        return dataCreacio;
    }

    /**
     * Sets the value of the dataCreacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCreacio(XMLGregorianCalendar value) {
        this.dataCreacio = value;
    }

    /**
     * Gets the value of the dataFi property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFi() {
        return dataFi;
    }

    /**
     * Sets the value of the dataFi property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFi(XMLGregorianCalendar value) {
        this.dataFi = value;
    }

    /**
     * Gets the value of the dataInici property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInici() {
        return dataInici;
    }

    /**
     * Sets the value of the dataInici property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInici(XMLGregorianCalendar value) {
        this.dataInici = value;
    }

    /**
     * Gets the value of the dataLimit property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataLimit() {
        return dataLimit;
    }

    /**
     * Sets the value of the dataLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataLimit(XMLGregorianCalendar value) {
        this.dataLimit = value;
    }

    /**
     * Gets the value of the expedient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedient() {
        return expedient;
    }

    /**
     * Sets the value of the expedient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedient(String value) {
        this.expedient = value;
    }

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
     * Gets the value of the missatgeInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMissatgeInfo() {
        return missatgeInfo;
    }

    /**
     * Sets the value of the missatgeInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMissatgeInfo(String value) {
        this.missatgeInfo = value;
    }

    /**
     * Gets the value of the missatgeWarn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMissatgeWarn() {
        return missatgeWarn;
    }

    /**
     * Sets the value of the missatgeWarn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMissatgeWarn(String value) {
        this.missatgeWarn = value;
    }

    /**
     * Gets the value of the open property.
     * 
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Sets the value of the open property.
     * 
     */
    public void setOpen(boolean value) {
        this.open = value;
    }

    /**
     * Gets the value of the prioritat property.
     * 
     */
    public int getPrioritat() {
        return prioritat;
    }

    /**
     * Sets the value of the prioritat property.
     * 
     */
    public void setPrioritat(int value) {
        this.prioritat = value;
    }

    /**
     * Gets the value of the processInstanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Sets the value of the processInstanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessInstanceId(String value) {
        this.processInstanceId = value;
    }

    /**
     * Gets the value of the responsable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * Sets the value of the responsable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsable(String value) {
        this.responsable = value;
    }

    /**
     * Gets the value of the responsables property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the responsables property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResponsables().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getResponsables() {
        if (responsables == null) {
            responsables = new ArrayList<String>();
        }
        return this.responsables;
    }

    /**
     * Gets the value of the suspended property.
     * 
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Sets the value of the suspended property.
     * 
     */
    public void setSuspended(boolean value) {
        this.suspended = value;
    }

    /**
     * Gets the value of the titol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitol() {
        return titol;
    }

    /**
     * Sets the value of the titol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitol(String value) {
        this.titol = value;
    }

    /**
     * Gets the value of the transicionsSortida property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transicionsSortida property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransicionsSortida().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTransicionsSortida() {
        if (transicionsSortida == null) {
            transicionsSortida = new ArrayList<String>();
        }
        return this.transicionsSortida;
    }

}
