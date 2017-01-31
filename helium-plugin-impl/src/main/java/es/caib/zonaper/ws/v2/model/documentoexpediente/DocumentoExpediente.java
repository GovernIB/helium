
package es.caib.zonaper.ws.v2.model.documentoexpediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentoExpediente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoExpediente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="titulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contenidoFichero" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="modeloRDS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionRDS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="codigoRDS" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="claveRDS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estructurado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoExpediente", propOrder = {
    "titulo",
    "nombre",
    "contenidoFichero",
    "modeloRDS",
    "versionRDS",
    "codigoRDS",
    "claveRDS",
    "estructurado"
})
public class DocumentoExpediente {

    @XmlElementRef(name = "titulo", type = JAXBElement.class)
    protected JAXBElement<String> titulo;
    @XmlElementRef(name = "nombre", type = JAXBElement.class)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "contenidoFichero", type = JAXBElement.class)
    protected JAXBElement<byte[]> contenidoFichero;
    @XmlElementRef(name = "modeloRDS", type = JAXBElement.class)
    protected JAXBElement<String> modeloRDS;
    @XmlElementRef(name = "versionRDS", type = JAXBElement.class)
    protected JAXBElement<Integer> versionRDS;
    @XmlElementRef(name = "codigoRDS", type = JAXBElement.class)
    protected JAXBElement<Long> codigoRDS;
    @XmlElementRef(name = "claveRDS", type = JAXBElement.class)
    protected JAXBElement<String> claveRDS;
    @XmlElementRef(name = "estructurado", type = JAXBElement.class)
    protected JAXBElement<Boolean> estructurado;

    /**
     * Gets the value of the titulo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTitulo() {
        return titulo;
    }

    /**
     * Sets the value of the titulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTitulo(JAXBElement<String> value) {
        this.titulo = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombre(JAXBElement<String> value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the contenidoFichero property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getContenidoFichero() {
        return contenidoFichero;
    }

    /**
     * Sets the value of the contenidoFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setContenidoFichero(JAXBElement<byte[]> value) {
        this.contenidoFichero = value;
    }

    /**
     * Gets the value of the modeloRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getModeloRDS() {
        return modeloRDS;
    }

    /**
     * Sets the value of the modeloRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setModeloRDS(JAXBElement<String> value) {
        this.modeloRDS = value;
    }

    /**
     * Gets the value of the versionRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVersionRDS() {
        return versionRDS;
    }

    /**
     * Sets the value of the versionRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVersionRDS(JAXBElement<Integer> value) {
        this.versionRDS = value;
    }

    /**
     * Gets the value of the codigoRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getCodigoRDS() {
        return codigoRDS;
    }

    /**
     * Sets the value of the codigoRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setCodigoRDS(JAXBElement<Long> value) {
        this.codigoRDS = value;
    }

    /**
     * Gets the value of the claveRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getClaveRDS() {
        return claveRDS;
    }

    /**
     * Sets the value of the claveRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setClaveRDS(JAXBElement<String> value) {
        this.claveRDS = value;
    }

    /**
     * Gets the value of the estructurado property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getEstructurado() {
        return estructurado;
    }

    /**
     * Sets the value of the estructurado property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setEstructurado(JAXBElement<Boolean> value) {
        this.estructurado = value;
    }

}
