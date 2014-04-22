
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
 * <p>Clase Java para tascaTramitacio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
     * Obtiene el valor de la propiedad cancelled.
     * 
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Define el valor de la propiedad cancelled.
     * 
     */
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    /**
     * Obtiene el valor de la propiedad codi.
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
     * Define el valor de la propiedad codi.
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
     * Obtiene el valor de la propiedad completed.
     * 
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Define el valor de la propiedad completed.
     * 
     */
    public void setCompleted(boolean value) {
        this.completed = value;
    }

    /**
     * Obtiene el valor de la propiedad dataCreacio.
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
     * Define el valor de la propiedad dataCreacio.
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
     * Obtiene el valor de la propiedad dataFi.
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
     * Define el valor de la propiedad dataFi.
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
     * Obtiene el valor de la propiedad dataInici.
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
     * Define el valor de la propiedad dataInici.
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
     * Obtiene el valor de la propiedad dataLimit.
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
     * Define el valor de la propiedad dataLimit.
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
     * Obtiene el valor de la propiedad expedient.
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
     * Define el valor de la propiedad expedient.
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
     * Obtiene el valor de la propiedad id.
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
     * Define el valor de la propiedad id.
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
     * Obtiene el valor de la propiedad missatgeInfo.
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
     * Define el valor de la propiedad missatgeInfo.
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
     * Obtiene el valor de la propiedad missatgeWarn.
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
     * Define el valor de la propiedad missatgeWarn.
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
     * Obtiene el valor de la propiedad open.
     * 
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Define el valor de la propiedad open.
     * 
     */
    public void setOpen(boolean value) {
        this.open = value;
    }

    /**
     * Obtiene el valor de la propiedad prioritat.
     * 
     */
    public int getPrioritat() {
        return prioritat;
    }

    /**
     * Define el valor de la propiedad prioritat.
     * 
     */
    public void setPrioritat(int value) {
        this.prioritat = value;
    }

    /**
     * Obtiene el valor de la propiedad processInstanceId.
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
     * Define el valor de la propiedad processInstanceId.
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
     * Obtiene el valor de la propiedad responsable.
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
     * Define el valor de la propiedad responsable.
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
     * Obtiene el valor de la propiedad suspended.
     * 
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Define el valor de la propiedad suspended.
     * 
     */
    public void setSuspended(boolean value) {
        this.suspended = value;
    }

    /**
     * Obtiene el valor de la propiedad titol.
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
     * Define el valor de la propiedad titol.
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
