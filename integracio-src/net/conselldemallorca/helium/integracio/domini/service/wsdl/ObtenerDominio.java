
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
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parametros" type="{urn:es:caib:sistra:ws:v1:model:SistraFacade}parametrosDominio"/>
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
    "id",
    "parametros"
})
@XmlRootElement(name = "obtenerDominio")
public class ObtenerDominio {

    @XmlElement(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", required = true)
    protected String id;
    @XmlElement(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", required = true)
    protected ParametrosDominio parametros;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the parametros property.
     * 
     * @return
     *     possible object is
     *     {@link ParametrosDominio }
     *     
     */
    public ParametrosDominio getParametros() {
        return parametros;
    }

    /**
     * Sets the value of the parametros property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParametrosDominio }
     *     
     */
    public void setParametros(ParametrosDominio value) {
        this.parametros = value;
    }

}
