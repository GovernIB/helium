
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
 *         &lt;element name="identificadorPersistenciaTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "identificadorPersistenciaTramite"
})
@XmlRootElement(name = "obtenerEstadoPagosTramite")
public class ObtenerEstadoPagosTramite {

    @XmlElement(required = true)
    protected String identificadorPersistenciaTramite;

    /**
     * Gets the value of the identificadorPersistenciaTramite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorPersistenciaTramite() {
        return identificadorPersistenciaTramite;
    }

    /**
     * Sets the value of the identificadorPersistenciaTramite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorPersistenciaTramite(String value) {
        this.identificadorPersistenciaTramite = value;
    }

}
