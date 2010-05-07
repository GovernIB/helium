
package net.conselldemallorca.helium.integracio.zonaper.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Expediente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Expediente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificadorExpediente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveExpediente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descripcion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="autenticado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="identificadorUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nifRepresentado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreRepresentado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroEntradaBTE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="configuracionAvisos" type="{urn:es:caib:zonaper:ws:v1:model:ConfiguracionAvisosExpediente}ConfiguracionAvisosExpediente" minOccurs="0"/>
 *         &lt;element name="eventos" type="{urn:es:caib:zonaper:ws:v1:model:EventoExpediente}EventosExpediente" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Expediente", namespace = "urn:es:caib:zonaper:ws:v1:model:Expediente", propOrder = {
    "identificadorExpediente",
    "unidadAdministrativa",
    "claveExpediente",
    "idioma",
    "descripcion",
    "autenticado",
    "identificadorUsuario",
    "nifRepresentado",
    "nombreRepresentado",
    "numeroEntradaBTE",
    "configuracionAvisos",
    "eventos"
})
public class Expediente {

    @XmlElement(required = true)
    protected String identificadorExpediente;
    protected long unidadAdministrativa;
    @XmlElement(required = true)
    protected String claveExpediente;
    @XmlElement(required = true)
    protected String idioma;
    @XmlElement(required = true)
    protected String descripcion;
    protected boolean autenticado;
    @XmlElementRef(name = "identificadorUsuario", type = JAXBElement.class)
    protected JAXBElement<String> identificadorUsuario;
    @XmlElementRef(name = "nifRepresentado", type = JAXBElement.class)
    protected JAXBElement<String> nifRepresentado;
    @XmlElementRef(name = "nombreRepresentado", type = JAXBElement.class)
    protected JAXBElement<String> nombreRepresentado;
    @XmlElementRef(name = "numeroEntradaBTE", type = JAXBElement.class)
    protected JAXBElement<String> numeroEntradaBTE;
    @XmlElementRef(name = "configuracionAvisos", type = JAXBElement.class)
    protected JAXBElement<ConfiguracionAvisosExpediente> configuracionAvisos;
    @XmlElementRef(name = "eventos", type = JAXBElement.class)
    protected JAXBElement<EventosExpediente> eventos;

    /**
     * Gets the value of the identificadorExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorExpediente() {
        return identificadorExpediente;
    }

    /**
     * Sets the value of the identificadorExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorExpediente(String value) {
        this.identificadorExpediente = value;
    }

    /**
     * Gets the value of the unidadAdministrativa property.
     * 
     */
    public long getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    /**
     * Sets the value of the unidadAdministrativa property.
     * 
     */
    public void setUnidadAdministrativa(long value) {
        this.unidadAdministrativa = value;
    }

    /**
     * Gets the value of the claveExpediente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveExpediente() {
        return claveExpediente;
    }

    /**
     * Sets the value of the claveExpediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveExpediente(String value) {
        this.claveExpediente = value;
    }

    /**
     * Gets the value of the idioma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Sets the value of the idioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Gets the value of the descripcion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Sets the value of the descripcion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcion(String value) {
        this.descripcion = value;
    }

    /**
     * Gets the value of the autenticado property.
     * 
     */
    public boolean isAutenticado() {
        return autenticado;
    }

    /**
     * Sets the value of the autenticado property.
     * 
     */
    public void setAutenticado(boolean value) {
        this.autenticado = value;
    }

    /**
     * Gets the value of the identificadorUsuario property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIdentificadorUsuario() {
        return identificadorUsuario;
    }

    /**
     * Sets the value of the identificadorUsuario property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIdentificadorUsuario(JAXBElement<String> value) {
        this.identificadorUsuario = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the nifRepresentado property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNifRepresentado() {
        return nifRepresentado;
    }

    /**
     * Sets the value of the nifRepresentado property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNifRepresentado(JAXBElement<String> value) {
        this.nifRepresentado = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the nombreRepresentado property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreRepresentado() {
        return nombreRepresentado;
    }

    /**
     * Sets the value of the nombreRepresentado property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreRepresentado(JAXBElement<String> value) {
        this.nombreRepresentado = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the numeroEntradaBTE property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumeroEntradaBTE() {
        return numeroEntradaBTE;
    }

    /**
     * Sets the value of the numeroEntradaBTE property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumeroEntradaBTE(JAXBElement<String> value) {
        this.numeroEntradaBTE = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the configuracionAvisos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ConfiguracionAvisosExpediente }{@code >}
     *     
     */
    public JAXBElement<ConfiguracionAvisosExpediente> getConfiguracionAvisos() {
        return configuracionAvisos;
    }

    /**
     * Sets the value of the configuracionAvisos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ConfiguracionAvisosExpediente }{@code >}
     *     
     */
    public void setConfiguracionAvisos(JAXBElement<ConfiguracionAvisosExpediente> value) {
        this.configuracionAvisos = ((JAXBElement<ConfiguracionAvisosExpediente> ) value);
    }

    /**
     * Gets the value of the eventos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EventosExpediente }{@code >}
     *     
     */
    public JAXBElement<EventosExpediente> getEventos() {
        return eventos;
    }

    /**
     * Sets the value of the eventos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EventosExpediente }{@code >}
     *     
     */
    public void setEventos(JAXBElement<EventosExpediente> value) {
        this.eventos = ((JAXBElement<EventosExpediente> ) value);
    }

}
