
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.detalleacuserecibo.DetalleAcuseRecibo;


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
 *         &lt;element name="obtenerDetalleAcuseReciboReturn" type="{urn:es:caib:regtel:ws:v2:model:DetalleAcuseRecibo}DetalleAcuseRecibo"/>
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
    "obtenerDetalleAcuseReciboReturn"
})
@XmlRootElement(name = "obtenerDetalleAcuseReciboResponse")
public class ObtenerDetalleAcuseReciboResponse {

    @XmlElement(required = true)
    protected DetalleAcuseRecibo obtenerDetalleAcuseReciboReturn;

    /**
     * Gets the value of the obtenerDetalleAcuseReciboReturn property.
     * 
     * @return
     *     possible object is
     *     {@link DetalleAcuseRecibo }
     *     
     */
    public DetalleAcuseRecibo getObtenerDetalleAcuseReciboReturn() {
        return obtenerDetalleAcuseReciboReturn;
    }

    /**
     * Sets the value of the obtenerDetalleAcuseReciboReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link DetalleAcuseRecibo }
     *     
     */
    public void setObtenerDetalleAcuseReciboReturn(DetalleAcuseRecibo value) {
        this.obtenerDetalleAcuseReciboReturn = value;
    }

}
