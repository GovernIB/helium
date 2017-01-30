
package es.caib.regtel.ws.v2.model.datosinteresado;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatosInteresado complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatosInteresado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="autenticado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreApellidos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreApellidosDesglosado" type="{urn:es:caib:regtel:ws:v2:model:DatosInteresado}IdentificacionInteresadoDesglosada" minOccurs="0"/>
 *         &lt;element name="codigoPais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombrePais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoProvincia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreProvincia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoLocalidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreLocalidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosInteresado", propOrder = {
    "autenticado",
    "nif",
    "nombreApellidos",
    "nombreApellidosDesglosado",
    "codigoPais",
    "nombrePais",
    "codigoProvincia",
    "nombreProvincia",
    "codigoLocalidad",
    "nombreLocalidad"
})
public class DatosInteresado {

    @XmlElementRef(name = "autenticado", type = JAXBElement.class)
    protected JAXBElement<Boolean> autenticado;
    @XmlElement(required = true)
    protected String nif;
    protected String nombreApellidos;
    protected IdentificacionInteresadoDesglosada nombreApellidosDesglosado;
    @XmlElementRef(name = "codigoPais", type = JAXBElement.class)
    protected JAXBElement<String> codigoPais;
    @XmlElementRef(name = "nombrePais", type = JAXBElement.class)
    protected JAXBElement<String> nombrePais;
    @XmlElementRef(name = "codigoProvincia", type = JAXBElement.class)
    protected JAXBElement<String> codigoProvincia;
    @XmlElementRef(name = "nombreProvincia", type = JAXBElement.class)
    protected JAXBElement<String> nombreProvincia;
    @XmlElementRef(name = "codigoLocalidad", type = JAXBElement.class)
    protected JAXBElement<String> codigoLocalidad;
    @XmlElementRef(name = "nombreLocalidad", type = JAXBElement.class)
    protected JAXBElement<String> nombreLocalidad;

    /**
     * Gets the value of the autenticado property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getAutenticado() {
        return autenticado;
    }

    /**
     * Sets the value of the autenticado property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setAutenticado(JAXBElement<Boolean> value) {
        this.autenticado = ((JAXBElement<Boolean> ) value);
    }

    /**
     * Gets the value of the nif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNif() {
        return nif;
    }

    /**
     * Sets the value of the nif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNif(String value) {
        this.nif = value;
    }

    /**
     * Gets the value of the nombreApellidos property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreApellidos() {
        return nombreApellidos;
    }

    /**
     * Sets the value of the nombreApellidos property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreApellidos(String value) {
        this.nombreApellidos = value;
    }

    /**
     * Gets the value of the nombreApellidosDesglosado property.
     * 
     * @return
     *     possible object is
     *     {@link IdentificacionInteresadoDesglosada }
     *     
     */
    public IdentificacionInteresadoDesglosada getNombreApellidosDesglosado() {
        return nombreApellidosDesglosado;
    }

    /**
     * Sets the value of the nombreApellidosDesglosado property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificacionInteresadoDesglosada }
     *     
     */
    public void setNombreApellidosDesglosado(IdentificacionInteresadoDesglosada value) {
        this.nombreApellidosDesglosado = value;
    }

    /**
     * Gets the value of the codigoPais property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoPais() {
        return codigoPais;
    }

    /**
     * Sets the value of the codigoPais property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoPais(JAXBElement<String> value) {
        this.codigoPais = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the nombrePais property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombrePais() {
        return nombrePais;
    }

    /**
     * Sets the value of the nombrePais property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombrePais(JAXBElement<String> value) {
        this.nombrePais = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the codigoProvincia property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoProvincia() {
        return codigoProvincia;
    }

    /**
     * Sets the value of the codigoProvincia property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoProvincia(JAXBElement<String> value) {
        this.codigoProvincia = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the nombreProvincia property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreProvincia() {
        return nombreProvincia;
    }

    /**
     * Sets the value of the nombreProvincia property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreProvincia(JAXBElement<String> value) {
        this.nombreProvincia = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the codigoLocalidad property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoLocalidad() {
        return codigoLocalidad;
    }

    /**
     * Sets the value of the codigoLocalidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoLocalidad(JAXBElement<String> value) {
        this.codigoLocalidad = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the nombreLocalidad property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNombreLocalidad() {
        return nombreLocalidad;
    }

    /**
     * Sets the value of the nombreLocalidad property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNombreLocalidad(JAXBElement<String> value) {
        this.nombreLocalidad = ((JAXBElement<String> ) value);
    }

}
