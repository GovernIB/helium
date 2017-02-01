
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="existeExpedienteReturn" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "existeExpedienteReturn"
})
@XmlRootElement(name = "existeExpedienteResponse")
public class ExisteExpedienteResponse {

    protected boolean existeExpedienteReturn;

    /**
     * Gets the value of the existeExpedienteReturn property.
     * 
     */
    public boolean isExisteExpedienteReturn() {
        return existeExpedienteReturn;
    }

    /**
     * Sets the value of the existeExpedienteReturn property.
     * 
     */
    public void setExisteExpedienteReturn(boolean value) {
        this.existeExpedienteReturn = value;
    }

}
