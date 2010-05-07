
package net.conselldemallorca.helium.integracio.domini.service.wsdl;

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
 *         &lt;element name="obtenerDominioReturn" type="{urn:es:caib:sistra:ws:v1:model:ValoresDominio}ValoresDominio"/>
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
    "obtenerDominioReturn"
})
@XmlRootElement(name = "obtenerDominioResponse")
public class ObtenerDominioResponse {

    @XmlElement(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", required = true)
    protected ValoresDominio obtenerDominioReturn;

    /**
     * Gets the value of the obtenerDominioReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ValoresDominio }
     *     
     */
    public ValoresDominio getObtenerDominioReturn() {
        return obtenerDominioReturn;
    }

    /**
     * Sets the value of the obtenerDominioReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValoresDominio }
     *     
     */
    public void setObtenerDominioReturn(ValoresDominio value) {
        this.obtenerDominioReturn = value;
    }

}
