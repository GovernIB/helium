
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="identificadorExpediente" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "unidadAdministrativa",
    "identificadorExpediente"
})
@XmlRootElement(name = "existeExpediente")
public class ExisteExpediente {

    protected long unidadAdministrativa;
    @XmlElement(required = true)
    protected String identificadorExpediente;

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

}
