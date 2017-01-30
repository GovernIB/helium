
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.datosregistroentrada.DatosRegistroEntrada;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entrada" type="{urn:es:caib:regtel:ws:v2:model:DatosRegistroEntrada}DatosRegistroEntrada"/>
 *         &lt;element name="diasPersistencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entrada",
    "diasPersistencia"
})
@XmlRootElement(name = "prepararRegistroEntrada")
public class PrepararRegistroEntrada {

    @XmlElement(required = true)
    protected DatosRegistroEntrada entrada;
    @XmlElementRef(name = "diasPersistencia", namespace = "urn:es:caib:regtel:ws:v2:model:BackofficeFacade", type = JAXBElement.class)
    protected JAXBElement<String> diasPersistencia;

    /**
     * Gets the value of the entrada property.
     * 
     * @return
     *     possible object is
     *     {@link DatosRegistroEntrada }
     *     
     */
    public DatosRegistroEntrada getEntrada() {
        return entrada;
    }

    /**
     * Sets the value of the entrada property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosRegistroEntrada }
     *     
     */
    public void setEntrada(DatosRegistroEntrada value) {
        this.entrada = value;
    }

    /**
     * Gets the value of the diasPersistencia property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDiasPersistencia() {
        return diasPersistencia;
    }

    /**
     * Sets the value of the diasPersistencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDiasPersistencia(JAXBElement<String> value) {
        this.diasPersistencia = ((JAXBElement<String> ) value);
    }

}
