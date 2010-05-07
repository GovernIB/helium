
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

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
 *         &lt;element name="obtenerEntradaReturn" type="{urn:es:caib:bantel:ws:v1:model:TramiteBTE}TramiteBTE"/>
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
    "obtenerEntradaReturn"
})
@XmlRootElement(name = "obtenerEntradaResponse")
public class ObtenerEntradaResponse {

    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected TramiteBTE obtenerEntradaReturn;

    /**
     * Gets the value of the obtenerEntradaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link TramiteBTE }
     *     
     */
    public TramiteBTE getObtenerEntradaReturn() {
        return obtenerEntradaReturn;
    }

    /**
     * Sets the value of the obtenerEntradaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link TramiteBTE }
     *     
     */
    public void setObtenerEntradaReturn(TramiteBTE value) {
        this.obtenerEntradaReturn = value;
    }

}
