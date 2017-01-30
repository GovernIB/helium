
package es.caib.regtel.ws.v2.model.documento;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.firmaws.FirmasWS;
import es.caib.regtel.ws.v2.model.referenciards.ReferenciaRDS;


/**
 * <p>Java class for Documento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Documento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="referenciaRDS" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDS" minOccurs="0"/>
 *         &lt;element name="modelo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datosFichero" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="plantilla" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firmas" type="{urn:es:caib:regtel:ws:v2:model:FirmaWS}FirmasWS" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Documento", propOrder = {
    "referenciaRDS",
    "modelo",
    "version",
    "nombre",
    "extension",
    "datosFichero",
    "plantilla",
    "firmas"
})
public class Documento {

    @XmlElementRef(name = "referenciaRDS", type = JAXBElement.class)
    protected JAXBElement<ReferenciaRDS> referenciaRDS;
    @XmlElementRef(name = "modelo", type = JAXBElement.class)
    protected JAXBElement<String> modelo;
    @XmlElementRef(name = "version", type = JAXBElement.class)
    protected JAXBElement<Integer> version;
    @XmlElementRef(name = "nombre", type = JAXBElement.class)
    protected JAXBElement<String> nombre;
    @XmlElementRef(name = "extension", type = JAXBElement.class)
    protected JAXBElement<String> extension;
    @XmlElementRef(name = "datosFichero", type = JAXBElement.class)
    protected JAXBElement<byte[]> datosFichero;
    @XmlElementRef(name = "plantilla", type = JAXBElement.class)
    protected JAXBElement<String> plantilla;
    @XmlElementRef(name = "firmas", type = JAXBElement.class)
    protected JAXBElement<FirmasWS> firmas;

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
        this.nombre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExtension(JAXBElement<String> value) {
        this.extension = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the datosFichero property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getDatosFichero() {
        return datosFichero;
    }

    /**
     * Sets the value of the datosFichero property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setDatosFichero(JAXBElement<byte[]> value) {
        this.datosFichero = ((JAXBElement<byte[]> ) value);
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
