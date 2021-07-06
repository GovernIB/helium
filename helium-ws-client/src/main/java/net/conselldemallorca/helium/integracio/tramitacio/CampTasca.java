
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for campTasca complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="campTasca">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dominiCampText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dominiCampValor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dominiId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dominiParams" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="etiqueta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="jbpmAction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="multiple" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="observacions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ocult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="readFrom" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="readOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="required" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tipus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="writeTo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "campTasca", propOrder = {
    "codi",
    "dominiCampText",
    "dominiCampValor",
    "dominiId",
    "dominiParams",
    "etiqueta",
    "jbpmAction",
    "multiple",
    "observacions",
    "ocult",
    "readFrom",
    "readOnly",
    "required",
    "tipus",
    "valor",
    "writeTo"
})
@XmlSeeAlso({Object[].class, Object[][].class})
public class CampTasca {

    protected String codi;
    protected String dominiCampText;
    protected String dominiCampValor;
    protected String dominiId;
    protected String dominiParams;
    protected String etiqueta;
    protected String jbpmAction;
    protected boolean multiple;
    protected String observacions;
    protected boolean ocult;
    protected boolean readFrom;
    protected boolean readOnly;
    protected boolean required;
    protected String tipus;
    protected Object valor;
    protected boolean writeTo;

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
     * Gets the value of the dominiCampText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDominiCampText() {
        return dominiCampText;
    }

    /**
     * Sets the value of the dominiCampText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDominiCampText(String value) {
        this.dominiCampText = value;
    }

    /**
     * Gets the value of the dominiCampValor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDominiCampValor() {
        return dominiCampValor;
    }

    /**
     * Sets the value of the dominiCampValor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDominiCampValor(String value) {
        this.dominiCampValor = value;
    }

    /**
     * Gets the value of the dominiId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDominiId() {
        return dominiId;
    }

    /**
     * Sets the value of the dominiId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDominiId(String value) {
        this.dominiId = value;
    }

    /**
     * Gets the value of the dominiParams property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDominiParams() {
        return dominiParams;
    }

    /**
     * Sets the value of the dominiParams property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDominiParams(String value) {
        this.dominiParams = value;
    }

    /**
     * Gets the value of the etiqueta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Sets the value of the etiqueta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEtiqueta(String value) {
        this.etiqueta = value;
    }

    /**
     * Gets the value of the jbpmAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJbpmAction() {
        return jbpmAction;
    }

    /**
     * Sets the value of the jbpmAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJbpmAction(String value) {
        this.jbpmAction = value;
    }

    /**
     * Gets the value of the multiple property.
     * 
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * Sets the value of the multiple property.
     * 
     */
    public void setMultiple(boolean value) {
        this.multiple = value;
    }

    /**
     * Gets the value of the observacions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacions() {
        return observacions;
    }

    /**
     * Sets the value of the observacions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacions(String value) {
        this.observacions = value;
    }

    /**
     * Gets the value of the ocult property.
     * 
     */
    public boolean isOcult() {
        return ocult;
    }

    /**
     * Sets the value of the ocult property.
     * 
     */
    public void setOcult(boolean value) {
        this.ocult = value;
    }

    /**
     * Gets the value of the readFrom property.
     * 
     */
    public boolean isReadFrom() {
        return readFrom;
    }

    /**
     * Sets the value of the readFrom property.
     * 
     */
    public void setReadFrom(boolean value) {
        this.readFrom = value;
    }

    /**
     * Gets the value of the readOnly property.
     * 
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the value of the readOnly property.
     * 
     */
    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    /**
     * Gets the value of the required property.
     * 
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     */
    public void setRequired(boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the tipus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipus() {
        return tipus;
    }

    /**
     * Sets the value of the tipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipus(String value) {
        this.tipus = value;
    }

    /**
     * Gets the value of the valor property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getValor() {
    	if (valor != null && valor instanceof XMLGregorianCalendar) {
    		return ((XMLGregorianCalendar)valor).toGregorianCalendar().getTime();
    	}
        return valor;
    }

    /**
     * Sets the value of the valor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setValor(Object value) {
        this.valor = value;
    }

    /**
     * Gets the value of the writeTo property.
     * 
     */
    public boolean isWriteTo() {
        return writeTo;
    }

    /**
     * Sets the value of the writeTo property.
     * 
     */
    public void setWriteTo(boolean value) {
        this.writeTo = value;
    }

}
