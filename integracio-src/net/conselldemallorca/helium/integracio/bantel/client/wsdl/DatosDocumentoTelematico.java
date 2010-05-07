
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatosDocumentoTelematico complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatosDocumentoTelematico">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroInstancia" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="codigoReferenciaRds" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="firmas" type="{urn:es:caib:bantel:ws:v1:model:FirmaWS}FirmasWS"/>
 *         &lt;element name="estructurado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosDocumentoTelematico", namespace = "urn:es:caib:bantel:ws:v1:model:DatosDocumentoTelematico", propOrder = {
    "identificador",
    "numeroInstancia",
    "codigoReferenciaRds",
    "claveReferenciaRds",
    "nombre",
    "extension",
    "content",
    "firmas",
    "estructurado"
})
public class DatosDocumentoTelematico {

    @XmlElement(required = true)
    protected String identificador;
    protected int numeroInstancia;
    protected long codigoReferenciaRds;
    @XmlElement(required = true)
    protected String claveReferenciaRds;
    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    protected String extension;
    @XmlElement(required = true)
    protected byte[] content;
    @XmlElement(required = true)
    protected FirmasWS firmas;
    protected boolean estructurado;

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
    }

    /**
     * Gets the value of the numeroInstancia property.
     * 
     */
    public int getNumeroInstancia() {
        return numeroInstancia;
    }

    /**
     * Sets the value of the numeroInstancia property.
     * 
     */
    public void setNumeroInstancia(int value) {
        this.numeroInstancia = value;
    }

    /**
     * Gets the value of the codigoReferenciaRds property.
     * 
     */
    public long getCodigoReferenciaRds() {
        return codigoReferenciaRds;
    }

    /**
     * Sets the value of the codigoReferenciaRds property.
     * 
     */
    public void setCodigoReferenciaRds(long value) {
        this.codigoReferenciaRds = value;
    }

    /**
     * Gets the value of the claveReferenciaRds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRds() {
        return claveReferenciaRds;
    }

    /**
     * Sets the value of the claveReferenciaRds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRds(String value) {
        this.claveReferenciaRds = value;
    }

    /**
     * Gets the value of the nombre property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets the value of the nombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setContent(byte[] value) {
        this.content = ((byte[]) value);
    }

    /**
     * Gets the value of the firmas property.
     * 
     * @return
     *     possible object is
     *     {@link FirmasWS }
     *     
     */
    public FirmasWS getFirmas() {
        return firmas;
    }

    /**
     * Sets the value of the firmas property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmasWS }
     *     
     */
    public void setFirmas(FirmasWS value) {
        this.firmas = value;
    }

    /**
     * Gets the value of the estructurado property.
     * 
     */
    public boolean isEstructurado() {
        return estructurado;
    }

    /**
     * Sets the value of the estructurado property.
     * 
     */
    public void setEstructurado(boolean value) {
        this.estructurado = value;
    }

}
