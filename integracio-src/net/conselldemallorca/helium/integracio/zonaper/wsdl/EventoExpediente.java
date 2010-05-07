
package net.conselldemallorca.helium.integracio.zonaper.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventoExpediente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventoExpediente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="titulo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="texto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="textoSMS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="enlaceConsulta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentos" type="{urn:es:caib:zonaper:ws:v1:model:DocumentoExpediente}DocumentosExpediente" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventoExpediente", namespace = "urn:es:caib:zonaper:ws:v1:model:EventoExpediente", propOrder = {
    "titulo",
    "texto",
    "textoSMS",
    "enlaceConsulta",
    "fecha",
    "documentos"
})
public class EventoExpediente {

    @XmlElement(required = true)
    protected String titulo;
    @XmlElement(required = true)
    protected String texto;
    @XmlElementRef(name = "textoSMS", type = JAXBElement.class)
    protected JAXBElement<String> textoSMS;
    @XmlElementRef(name = "enlaceConsulta", type = JAXBElement.class)
    protected JAXBElement<String> enlaceConsulta;
    @XmlElementRef(name = "fecha", type = JAXBElement.class)
    protected JAXBElement<String> fecha;
    @XmlElementRef(name = "documentos", type = JAXBElement.class)
    protected JAXBElement<DocumentosExpediente> documentos;

    /**
     * Gets the value of the titulo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Sets the value of the titulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitulo(String value) {
        this.titulo = value;
    }

    /**
     * Gets the value of the texto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Sets the value of the texto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTexto(String value) {
        this.texto = value;
    }

    /**
     * Gets the value of the textoSMS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTextoSMS() {
        return textoSMS;
    }

    /**
     * Sets the value of the textoSMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTextoSMS(JAXBElement<String> value) {
        this.textoSMS = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the enlaceConsulta property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEnlaceConsulta() {
        return enlaceConsulta;
    }

    /**
     * Sets the value of the enlaceConsulta property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEnlaceConsulta(JAXBElement<String> value) {
        this.enlaceConsulta = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the fecha property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFecha() {
        return fecha;
    }

    /**
     * Sets the value of the fecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFecha(JAXBElement<String> value) {
        this.fecha = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the documentos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DocumentosExpediente }{@code >}
     *     
     */
    public JAXBElement<DocumentosExpediente> getDocumentos() {
        return documentos;
    }

    /**
     * Sets the value of the documentos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DocumentosExpediente }{@code >}
     *     
     */
    public void setDocumentos(JAXBElement<DocumentosExpediente> value) {
        this.documentos = ((JAXBElement<DocumentosExpediente> ) value);
    }

}
