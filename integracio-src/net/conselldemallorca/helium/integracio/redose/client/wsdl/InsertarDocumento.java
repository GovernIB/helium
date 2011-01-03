
package net.conselldemallorca.helium.integracio.redose.client.wsdl;

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
 *         &lt;element name="documento" type="{urn:es:caib:redose:ws:v1:model:DocumentoRDS}DocumentoRDS"/>
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
    "documento"
})
@XmlRootElement(name = "insertarDocumento")
public class InsertarDocumento {

    @XmlElement(namespace = "urn:es:caib:redose:ws:v1:model:BackofficeFacade", required = true)
    protected DocumentoRDS documento;

    /**
     * Gets the value of the documento property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentoRDS }
     *     
     */
    public DocumentoRDS getDocumento() {
        return documento;
    }

    /**
     * Sets the value of the documento property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentoRDS }
     *     
     */
    public void setDocumento(DocumentoRDS value) {
        this.documento = value;
    }

}
