
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatosDocumentoPresencial complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatosDocumentoPresencial">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroInstancia" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="compulsarDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fotocopia" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosDocumentoPresencial", namespace = "urn:es:caib:bantel:ws:v1:model:DatosDocumentoPresencial", propOrder = {
    "identificador",
    "numeroInstancia",
    "tipoDocumento",
    "compulsarDocumento",
    "fotocopia",
    "firma"
})
public class DatosDocumentoPresencial {

    @XmlElement(required = true)
    protected String identificador;
    protected int numeroInstancia;
    @XmlElement(required = true)
    protected String tipoDocumento;
    @XmlElement(required = true)
    protected String compulsarDocumento;
    @XmlElement(required = true)
    protected String fotocopia;
    @XmlElement(required = true)
    protected String firma;

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
     * Gets the value of the compulsarDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompulsarDocumento() {
        return compulsarDocumento;
    }

    /**
     * Sets the value of the compulsarDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompulsarDocumento(String value) {
        this.compulsarDocumento = value;
    }

    /**
     * Gets the value of the fotocopia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotocopia() {
        return fotocopia;
    }

    /**
     * Sets the value of the fotocopia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotocopia(String value) {
        this.fotocopia = value;
    }

    /**
     * Gets the value of the firma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirma() {
        return firma;
    }

    /**
     * Sets the value of the firma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirma(String value) {
        this.firma = value;
    }

}
