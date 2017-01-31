
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.zonaper.ws.v2.model.estadopago.EstadoPagos;


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
 *         &lt;element name="obtenerEstadoPagosTramiteReturn" type="{urn:es:caib:zonaper:ws:v2:model:EstadoPago}EstadoPagos"/>
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
    "obtenerEstadoPagosTramiteReturn"
})
@XmlRootElement(name = "obtenerEstadoPagosTramiteResponse")
public class ObtenerEstadoPagosTramiteResponse {

    @XmlElement(required = true)
    protected EstadoPagos obtenerEstadoPagosTramiteReturn;

    /**
     * Gets the value of the obtenerEstadoPagosTramiteReturn property.
     * 
     * @return
     *     possible object is
     *     {@link EstadoPagos }
     *     
     */
    public EstadoPagos getObtenerEstadoPagosTramiteReturn() {
        return obtenerEstadoPagosTramiteReturn;
    }

    /**
     * Sets the value of the obtenerEstadoPagosTramiteReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoPagos }
     *     
     */
    public void setObtenerEstadoPagosTramiteReturn(EstadoPagos value) {
        this.obtenerEstadoPagosTramiteReturn = value;
    }

}
