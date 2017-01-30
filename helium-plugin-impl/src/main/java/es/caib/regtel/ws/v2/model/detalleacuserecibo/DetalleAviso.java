
package es.caib.regtel.ws.v2.model.detalleacuserecibo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DetalleAviso complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DetalleAviso">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipo" type="{urn:es:caib:regtel:ws:v2:model:DetalleAcuseRecibo}TipoAviso"/>
 *         &lt;element name="destinatario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enviado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="fechaEnvio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="confirmarEnvio" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="confirmadoEnvio" type="{urn:es:caib:regtel:ws:v2:model:DetalleAcuseRecibo}TipoConfirmacionAviso" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetalleAviso", propOrder = {
    "tipo",
    "destinatario",
    "enviado",
    "fechaEnvio",
    "confirmarEnvio",
    "confirmadoEnvio"
})
public class DetalleAviso {

    @XmlElement(required = true)
    protected TipoAviso tipo;
    @XmlElement(required = true)
    protected String destinatario;
    protected boolean enviado;
    @XmlElementRef(name = "fechaEnvio", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> fechaEnvio;
    protected boolean confirmarEnvio;
    @XmlElementRef(name = "confirmadoEnvio", type = JAXBElement.class)
    protected JAXBElement<TipoConfirmacionAviso> confirmadoEnvio;

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link TipoAviso }
     *     
     */
    public TipoAviso getTipo() {
        return tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoAviso }
     *     
     */
    public void setTipo(TipoAviso value) {
        this.tipo = value;
    }

    /**
     * Gets the value of the destinatario property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * Sets the value of the destinatario property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinatario(String value) {
        this.destinatario = value;
    }

    /**
     * Gets the value of the enviado property.
     * 
     */
    public boolean isEnviado() {
        return enviado;
    }

    /**
     * Sets the value of the enviado property.
     * 
     */
    public void setEnviado(boolean value) {
        this.enviado = value;
    }

    /**
     * Gets the value of the fechaEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaEnvio() {
        return fechaEnvio;
    }

    /**
     * Sets the value of the fechaEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaEnvio(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaEnvio = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the confirmarEnvio property.
     * 
     */
    public boolean isConfirmarEnvio() {
        return confirmarEnvio;
    }

    /**
     * Sets the value of the confirmarEnvio property.
     * 
     */
    public void setConfirmarEnvio(boolean value) {
        this.confirmarEnvio = value;
    }

    /**
     * Gets the value of the confirmadoEnvio property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TipoConfirmacionAviso }{@code >}
     *     
     */
    public JAXBElement<TipoConfirmacionAviso> getConfirmadoEnvio() {
        return confirmadoEnvio;
    }

    /**
     * Sets the value of the confirmadoEnvio property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TipoConfirmacionAviso }{@code >}
     *     
     */
    public void setConfirmadoEnvio(JAXBElement<TipoConfirmacionAviso> value) {
        this.confirmadoEnvio = ((JAXBElement<TipoConfirmacionAviso> ) value);
    }

}
