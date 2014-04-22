
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para campTasca complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
     * Obtiene el valor de la propiedad dominiCampText.
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
     * Define el valor de la propiedad dominiCampText.
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
     * Obtiene el valor de la propiedad dominiCampValor.
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
     * Define el valor de la propiedad dominiCampValor.
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
     * Obtiene el valor de la propiedad dominiId.
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
     * Define el valor de la propiedad dominiId.
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
     * Obtiene el valor de la propiedad dominiParams.
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
     * Define el valor de la propiedad dominiParams.
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
     * Obtiene el valor de la propiedad etiqueta.
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
     * Define el valor de la propiedad etiqueta.
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
     * Obtiene el valor de la propiedad jbpmAction.
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
     * Define el valor de la propiedad jbpmAction.
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
     * Obtiene el valor de la propiedad multiple.
     * 
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * Define el valor de la propiedad multiple.
     * 
     */
    public void setMultiple(boolean value) {
        this.multiple = value;
    }

    /**
     * Obtiene el valor de la propiedad observacions.
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
     * Define el valor de la propiedad observacions.
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
     * Obtiene el valor de la propiedad ocult.
     * 
     */
    public boolean isOcult() {
        return ocult;
    }

    /**
     * Define el valor de la propiedad ocult.
     * 
     */
    public void setOcult(boolean value) {
        this.ocult = value;
    }

    /**
     * Obtiene el valor de la propiedad readFrom.
     * 
     */
    public boolean isReadFrom() {
        return readFrom;
    }

    /**
     * Define el valor de la propiedad readFrom.
     * 
     */
    public void setReadFrom(boolean value) {
        this.readFrom = value;
    }

    /**
     * Obtiene el valor de la propiedad readOnly.
     * 
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Define el valor de la propiedad readOnly.
     * 
     */
    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    /**
     * Obtiene el valor de la propiedad required.
     * 
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Define el valor de la propiedad required.
     * 
     */
    public void setRequired(boolean value) {
        this.required = value;
    }

    /**
     * Obtiene el valor de la propiedad tipus.
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
     * Define el valor de la propiedad tipus.
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
     * Obtiene el valor de la propiedad valor.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getValor() {
        return valor;
    }

    /**
     * Define el valor de la propiedad valor.
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
     * Obtiene el valor de la propiedad writeTo.
     * 
     */
    public boolean isWriteTo() {
        return writeTo;
    }

    /**
     * Define el valor de la propiedad writeTo.
     * 
     */
    public void setWriteTo(boolean value) {
        this.writeTo = value;
    }

}
