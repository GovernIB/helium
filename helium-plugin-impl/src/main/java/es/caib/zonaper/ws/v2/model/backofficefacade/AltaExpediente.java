
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.zonaper.ws.v2.model.expediente.Expediente;


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
 *         &lt;element name="expediente" type="{urn:es:caib:zonaper:ws:v2:model:Expediente}Expediente"/>
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
    "expediente"
})
@XmlRootElement(name = "altaExpediente")
public class AltaExpediente {

    @XmlElement(required = true)
    protected Expediente expediente;

    /**
     * Gets the value of the expediente property.
     * 
     * @return
     *     possible object is
     *     {@link Expediente }
     *     
     */
    public Expediente getExpediente() {
        return expediente;
    }

    /**
     * Sets the value of the expediente property.
     * 
     * @param value
     *     allowed object is
     *     {@link Expediente }
     *     
     */
    public void setExpediente(Expediente value) {
        this.expediente = value;
    }

}
