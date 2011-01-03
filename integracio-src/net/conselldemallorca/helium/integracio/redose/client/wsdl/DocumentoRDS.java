
package net.conselldemallorca.helium.integracio.redose.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DocumentoRDS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentoRDS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modelo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="titulo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuarioSeycon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="nombreFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extensionFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="datosFichero" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="fechaRDS" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="estructurado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="hashFichero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plantilla" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="urlVerificacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="referenciaRDS" type="{urn:es:caib:redose:ws:v1:model:ReferenciaRDS}ReferenciaRDS" minOccurs="0"/>
 *         &lt;element name="firmas" type="{urn:es:caib:redose:ws:v1:model:FirmaWS}FirmasWS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentoRDS", namespace = "urn:es:caib:redose:ws:v1:model:DocumentoRDS", propOrder = {
    "modelo",
    "version",
    "titulo",
    "nif",
    "usuarioSeycon",
    "unidadAdministrativa",
    "nombreFichero",
    "extensionFichero",
    "datosFichero",
    "fechaRDS",
    "estructurado",
    "hashFichero",
    "plantilla",
    "urlVerificacion",
    "referenciaRDS",
    "firmas"
})
public class DocumentoRDS {

    @XmlElement(required = true)
    protected String modelo;
    protected int version;
    @XmlElement(required = true)
    protected String titulo;
    @XmlElementRef(name = "nif", type = JAXBElement.class)
    protected JAXBElement<String> nif;
    @XmlElementRef(name = "usuarioSeycon", type = JAXBElement.class)
    protected JAXBElement<String> usuarioSeycon;
    protected long unidadAdministrativa;
    @XmlElement(required = true)
    protected String nombreFichero;
    @XmlElement(required = true)
    protected String extensionFichero;
    @XmlElement(required = true)
    protected byte[] datosFichero;
    @XmlElementRef(name = "fechaRDS", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> fechaRDS;
    @XmlElementRef(name = "estructurado", type = JAXBElement.class)
    protected JAXBElement<Boolean> estructurado;
    @XmlElementRef(name = "hashFichero", type = JAXBElement.class)
    protected JAXBElement<String> hashFichero;
    @XmlElementRef(name = "plantilla", type = JAXBElement.class)
    protected JAXBElement<String> plantilla;
    @XmlElementRef(name = "urlVerificacion", type = JAXBElement.class)
    protected JAXBElement<String> urlVerificacion;
    @XmlElementRef(name = "referenciaRDS", type = JAXBElement.class)
    protected JAXBElement<ReferenciaRDS> referenciaRDS;
    @XmlElementRef(name = "firmas", type = JAXBElement.class)
    protected JAXBElement<FirmasWS> firmas;

    /**
     * Gets the value of the modelo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Sets the value of the modelo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelo(String value) {
        this.modelo = value;
    }

    /**
     * Gets the value of the version property.
     * 
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     */
    public void setVersion(int value) {
        this.version = value;
    }

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
     * Gets the value of the nif property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNif() {
        return nif;
    }

    /**
     * Sets the value of the nif property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNif(JAXBElement<String> value) {
        this.nif = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the usuarioSeycon property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioSeycon() {
        return usuarioSeycon;
    }

    /**
     * Sets the value of the usuarioSeycon property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioSeycon(JAXBElement<String> value) {
        this.usuarioSeycon = ((JAXBElement<String> ) value);
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
     * Gets the value of the nombreFichero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreFichero() {
        return nombreFichero;
    }

    /**
     * Sets the value of the nombreFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreFichero(String value) {
        this.nombreFichero = value;
    }

    /**
     * Gets the value of the extensionFichero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionFichero() {
        return extensionFichero;
    }

    /**
     * Sets the value of the extensionFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionFichero(String value) {
        this.extensionFichero = value;
    }

    /**
     * Gets the value of the datosFichero property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDatosFichero() {
        return datosFichero;
    }

    /**
     * Sets the value of the datosFichero property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDatosFichero(byte[] value) {
        this.datosFichero = ((byte[]) value);
    }

    /**
     * Gets the value of the fechaRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaRDS() {
        return fechaRDS;
    }

    /**
     * Sets the value of the fechaRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaRDS(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaRDS = ((JAXBElement<XMLGregorianCalendar> ) value);
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
        this.estructurado = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the hashFichero property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHashFichero() {
        return hashFichero;
    }

    /**
     * Sets the value of the hashFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHashFichero(JAXBElement<String> value) {
        this.hashFichero = ((JAXBElement<String> ) value);
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
     * Gets the value of the urlVerificacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUrlVerificacion() {
        return urlVerificacion;
    }

    /**
     * Sets the value of the urlVerificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUrlVerificacion(JAXBElement<String> value) {
        this.urlVerificacion = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the referenciaRDS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}
     *     
     */
    public JAXBElement<ReferenciaRDS> getReferenciaRDS() {
        return referenciaRDS;
    }

    /**
     * Sets the value of the referenciaRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}
     *     
     */
    public void setReferenciaRDS(JAXBElement<ReferenciaRDS> value) {
        this.referenciaRDS = ((JAXBElement<ReferenciaRDS> ) value);
    }

    /**
     * Gets the value of the firmas property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FirmasWS }{@code >}
     *     
     */
    public JAXBElement<FirmasWS> getFirmas() {
        return firmas;
    }

    /**
     * Sets the value of the firmas property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FirmasWS }{@code >}
     *     
     */
    public void setFirmas(JAXBElement<FirmasWS> value) {
        this.firmas = ((JAXBElement<FirmasWS> ) value);
    }

}
