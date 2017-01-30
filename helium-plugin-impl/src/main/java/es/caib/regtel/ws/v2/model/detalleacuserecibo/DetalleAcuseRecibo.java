
package es.caib.regtel.ws.v2.model.detalleacuserecibo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import es.caib.regtel.ws.v2.model.referenciards.ReferenciaRDS;


/**
 * <p>Java class for DetalleAcuseRecibo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DetalleAcuseRecibo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="estado" type="{urn:es:caib:regtel:ws:v2:model:DetalleAcuseRecibo}TipoEstadoNotificacion"/>
 *         &lt;element name="fechaAcuseRecibo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ficheroAcuseRecibo" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDS" minOccurs="0"/>
 *         &lt;element name="avisos" type="{urn:es:caib:regtel:ws:v2:model:DetalleAcuseRecibo}DetalleAvisos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetalleAcuseRecibo", propOrder = {
    "estado",
    "fechaAcuseRecibo",
    "ficheroAcuseRecibo",
    "avisos"
})
public class DetalleAcuseRecibo {

    @XmlElement(required = true)
    protected TipoEstadoNotificacion estado;
    @XmlElementRef(name = "fechaAcuseRecibo", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> fechaAcuseRecibo;
    @XmlElementRef(name = "ficheroAcuseRecibo", type = JAXBElement.class)
    protected JAXBElement<ReferenciaRDS> ficheroAcuseRecibo;
    @XmlElementRef(name = "avisos", type = JAXBElement.class)
    protected JAXBElement<DetalleAvisos> avisos;

    /**
     * Gets the value of the estado property.
     * 
     * @return
     *     possible object is
     *     {@link TipoEstadoNotificacion }
     *     
     */
    public TipoEstadoNotificacion getEstado() {
        return estado;
    }

    /**
     * Sets the value of the estado property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoEstadoNotificacion }
     *     
     */
    public void setEstado(TipoEstadoNotificacion value) {
        this.estado = value;
    }

    /**
     * Gets the value of the fechaAcuseRecibo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaAcuseRecibo() {
        return fechaAcuseRecibo;
    }

    /**
     * Sets the value of the fechaAcuseRecibo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaAcuseRecibo(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaAcuseRecibo = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the ficheroAcuseRecibo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}
     *     
     */
    public JAXBElement<ReferenciaRDS> getFicheroAcuseRecibo() {
        return ficheroAcuseRecibo;
    }

    /**
     * Sets the value of the ficheroAcuseRecibo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}
     *     
     */
    public void setFicheroAcuseRecibo(JAXBElement<ReferenciaRDS> value) {
        this.ficheroAcuseRecibo = ((JAXBElement<ReferenciaRDS> ) value);
    }

    /**
     * Gets the value of the avisos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DetalleAvisos }{@code >}
     *     
     */
    public JAXBElement<DetalleAvisos> getAvisos() {
        return avisos;
    }

    /**
     * Sets the value of the avisos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DetalleAvisos }{@code >}
     *     
     */
    public void setAvisos(JAXBElement<DetalleAvisos> value) {
        this.avisos = ((JAXBElement<DetalleAvisos> ) value);
    }

}
