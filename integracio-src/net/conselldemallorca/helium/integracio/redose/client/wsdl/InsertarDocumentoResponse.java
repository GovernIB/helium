
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
 *         &lt;element name="insertarDocumentoReturn" type="{urn:es:caib:redose:ws:v1:model:ReferenciaRDS}ReferenciaRDS"/>
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
    "insertarDocumentoReturn"
})
@XmlRootElement(name = "insertarDocumentoResponse")
public class InsertarDocumentoResponse {

    @XmlElement(namespace = "urn:es:caib:redose:ws:v1:model:BackofficeFacade", required = true)
    protected ReferenciaRDS insertarDocumentoReturn;

    /**
     * Gets the value of the insertarDocumentoReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaRDS }
     *     
     */
    public ReferenciaRDS getInsertarDocumentoReturn() {
        return insertarDocumentoReturn;
    }

    /**
     * Sets the value of the insertarDocumentoReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaRDS }
     *     
     */
    public void setInsertarDocumentoReturn(ReferenciaRDS value) {
        this.insertarDocumentoReturn = value;
    }

}
