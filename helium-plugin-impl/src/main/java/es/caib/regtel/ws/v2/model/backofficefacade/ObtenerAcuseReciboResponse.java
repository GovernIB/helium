
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.acuserecibo.AcuseRecibo;


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
 *         &lt;element name="obtenerAcuseReciboReturn" type="{urn:es:caib:regtel:ws:v2:model:AcuseRecibo}AcuseRecibo"/>
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
    "obtenerAcuseReciboReturn"
})
@XmlRootElement(name = "obtenerAcuseReciboResponse")
public class ObtenerAcuseReciboResponse {

    @XmlElement(required = true)
    protected AcuseRecibo obtenerAcuseReciboReturn;

    /**
     * Gets the value of the obtenerAcuseReciboReturn property.
     * 
     * @return
     *     possible object is
     *     {@link AcuseRecibo }
     *     
     */
    public AcuseRecibo getObtenerAcuseReciboReturn() {
        return obtenerAcuseReciboReturn;
    }

    /**
     * Sets the value of the obtenerAcuseReciboReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link AcuseRecibo }
     *     
     */
    public void setObtenerAcuseReciboReturn(AcuseRecibo value) {
        this.obtenerAcuseReciboReturn = value;
    }

}
