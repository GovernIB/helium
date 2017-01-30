
package es.caib.regtel.ws.v2.model.datosrepresentado;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DatosRepresentado complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatosRepresentado">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nif" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nombreApellidos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreApellidosDesglosado" type="{urn:es:caib:regtel:ws:v2:model:DatosRepresentado}IdentificacionRepresentadoDesglosada" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosRepresentado", propOrder = {
    "nif",
    "nombreApellidos",
    "nombreApellidosDesglosado"
})
public class DatosRepresentado {

    @XmlElement(required = true)
    protected String nif;
    protected String nombreApellidos;
    protected IdentificacionRepresentadoDesglosada nombreApellidosDesglosado;

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
     *     {@link IdentificacionRepresentadoDesglosada }
     *     
     */
    public IdentificacionRepresentadoDesglosada getNombreApellidosDesglosado() {
        return nombreApellidosDesglosado;
    }

    /**
     * Sets the value of the nombreApellidosDesglosado property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificacionRepresentadoDesglosada }
     *     
     */
    public void setNombreApellidosDesglosado(IdentificacionRepresentadoDesglosada value) {
        this.nombreApellidosDesglosado = value;
    }

}
