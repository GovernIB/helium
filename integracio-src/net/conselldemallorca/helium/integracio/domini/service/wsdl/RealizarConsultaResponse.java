
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
 *         &lt;element name="realizarConsultaReturn" type="{urn:es:caib:sistra:ws:v1:model:DocumentoConsulta}DocumentosConsulta"/>
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
    "realizarConsultaReturn"
})
@XmlRootElement(name = "realizarConsultaResponse")
public class RealizarConsultaResponse {

    @XmlElement(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", required = true)
    protected DocumentosConsulta realizarConsultaReturn;

    /**
     * Gets the value of the realizarConsultaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentosConsulta }
     *     
     */
    public DocumentosConsulta getRealizarConsultaReturn() {
        return realizarConsultaReturn;
    }

    /**
     * Sets the value of the realizarConsultaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentosConsulta }
     *     
     */
    public void setRealizarConsultaReturn(DocumentosConsulta value) {
        this.realizarConsultaReturn = value;
    }

}
