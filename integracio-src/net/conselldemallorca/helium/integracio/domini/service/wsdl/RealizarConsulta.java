
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
 *         &lt;element name="forms" type="{urn:es:caib:sistra:ws:v1:model:FormularioConsulta}FormulariosConsulta"/>
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
    "forms"
})
@XmlRootElement(name = "realizarConsulta")
public class RealizarConsulta {

    @XmlElement(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", required = true)
    protected FormulariosConsulta forms;

    /**
     * Gets the value of the forms property.
     * 
     * @return
     *     possible object is
     *     {@link FormulariosConsulta }
     *     
     */
    public FormulariosConsulta getForms() {
        return forms;
    }

    /**
     * Sets the value of the forms property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormulariosConsulta }
     *     
     */
    public void setForms(FormulariosConsulta value) {
        this.forms = value;
    }

}
