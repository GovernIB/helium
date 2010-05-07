
package net.conselldemallorca.helium.integracio.domini.service.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentoConsulta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoConsulta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoRDS" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="claveRDS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="xml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="plantilla" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contenidoFichero" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="nombreFichero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urlAcceso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urlNuevaVentana" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoConsulta", namespace = "urn:es:caib:sistra:ws:v1:model:DocumentoConsulta", propOrder = {
    "tipoDocumento",
    "nombreDocumento",
    "codigoRDS",
    "claveRDS",
    "xml",
    "modelo",
    "version",
    "plantilla",
    "contenidoFichero",
    "nombreFichero",
    "urlAcceso",
    "urlNuevaVentana"
})
public class DocumentoConsulta {

    @XmlElement(required = true)
    protected String tipoDocumento;
    @XmlElement(required = true)
    protected String nombreDocumento;
    @XmlElementRef(name = "codigoRDS", type = JAXBElement.class)
    protected JAXBElement<Long> codigoRDS;
    @XmlElementRef(name = "claveRDS", type = JAXBElement.class)
    protected JAXBElement<String> claveRDS;
    @XmlElementRef(name = "xml", type = JAXBElement.class)
    protected JAXBElement<String> xml;
    @XmlElementRef(name = "modelo", type = JAXBElement.class)
    protected JAXBElement<String> modelo;
    @XmlElementRef(name = "version", type = JAXBElement.class)
    protected JAXBElement<Integer> version;
    @XmlElementRef(name = "plantilla", type = JAXBElement.class)
    protected JAXBElement<String> plantilla;
    @XmlElementRef(name = "contenidoFichero", type = JAXBElement.class)
    protected JAXBElement<byte[]> contenidoFichero;
    @XmlElementRef(name = "nombreFichero", type = JAXBElement.class)
    protected JAXBElement<String> nombreFichero;
    @XmlElementRef(name = "urlAcceso", type = JAXBElement.class)
    protected JAXBElement<String> urlAcceso;
    @XmlElementRef(name = "urlNuevaVentana", type = JAXBElement.class)
    protected JAXBElement<Boolean> urlNuevaVentana;

    /**
     * Gets the value of the tipoDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Sets the value of the tipoDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(String value) {
        this.tipoDocumento = value;
    }

    /**
     * Gets the value of the nombreDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreDocumento() {
        return nombreDocumento;
    }

    /**
     * Sets the value of the nombreDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreDocumento(String value) {
        this.nombreDocumento = value;
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
        this.codigoRDS = ((JAXBElement<Long> ) value);
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
        this.claveRDS = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setXml(JAXBElement<String> value) {
        this.xml = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the modelo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getModelo() {
        return modelo;
    }

    /**
     * Sets the value of the modelo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setModelo(JAXBElement<String> value) {
        this.modelo = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setVersion(JAXBElement<Integer> value) {
        this.version = ((JAXBElement<Integer> ) value);
    }

    /**
     * Gets the value of the plantilla property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getPlantilla() {
        return plantilla;
    }

    /**
     * Sets the value of the plantilla property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setPlantilla(JAXBElement<String> value) {
        this.plantilla = ((JAXBElement<String> ) value);
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
        this.contenidoFichero = ((JAXBElement<byte[]> ) value);
    }

    /**
     * Gets the value of the nombreFichero property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreFichero() {
        return nombreFichero;
    }

    /**
     * Sets the value of the nombreFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreFichero(JAXBElement<String> value) {
        this.nombreFichero = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the urlAcceso property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUrlAcceso() {
        return urlAcceso;
    }

    /**
     * Sets the value of the urlAcceso property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUrlAcceso(JAXBElement<String> value) {
        this.urlAcceso = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the urlNuevaVentana property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getUrlNuevaVentana() {
        return urlNuevaVentana;
    }

    /**
     * Sets the value of the urlNuevaVentana property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setUrlNuevaVentana(JAXBElement<Boolean> value) {
        this.urlNuevaVentana = ((JAXBElement<Boolean> ) value);
    }

}
