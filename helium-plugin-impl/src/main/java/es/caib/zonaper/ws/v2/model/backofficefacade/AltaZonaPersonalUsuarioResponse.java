
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
 *         &lt;element name="altaZonaPersonalUsuarioReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "altaZonaPersonalUsuarioReturn"
})
@XmlRootElement(name = "altaZonaPersonalUsuarioResponse")
public class AltaZonaPersonalUsuarioResponse {

    @XmlElement(required = true)
    protected String altaZonaPersonalUsuarioReturn;

    /**
     * Gets the value of the altaZonaPersonalUsuarioReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAltaZonaPersonalUsuarioReturn() {
        return altaZonaPersonalUsuarioReturn;
    }

    /**
     * Sets the value of the altaZonaPersonalUsuarioReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAltaZonaPersonalUsuarioReturn(String value) {
        this.altaZonaPersonalUsuarioReturn = value;
    }

}
