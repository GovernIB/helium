
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
 *         &lt;element name="obtenerNumerosEntradasReturn" type="{urn:es:caib:bantel:ws:v1:model:ReferenciaEntrada}ReferenciasEntrada"/>
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
    "obtenerNumerosEntradasReturn"
})
@XmlRootElement(name = "obtenerNumerosEntradasResponse")
public class ObtenerNumerosEntradasResponse {

    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected ReferenciasEntrada obtenerNumerosEntradasReturn;

    /**
     * Gets the value of the obtenerNumerosEntradasReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciasEntrada }
     *     
     */
    public ReferenciasEntrada getObtenerNumerosEntradasReturn() {
        return obtenerNumerosEntradasReturn;
    }

    /**
     * Sets the value of the obtenerNumerosEntradasReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciasEntrada }
     *     
     */
    public void setObtenerNumerosEntradasReturn(ReferenciasEntrada value) {
        this.obtenerNumerosEntradasReturn = value;
    }

}
