
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.resultadoregistro.ResultadoRegistro;


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
 *         &lt;element name="registroSalidaReturn" type="{urn:es:caib:regtel:ws:v2:model:ResultadoRegistro}ResultadoRegistro"/>
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
    "registroSalidaReturn"
})
@XmlRootElement(name = "registroSalidaResponse")
public class RegistroSalidaResponse {

    @XmlElement(required = true)
    protected ResultadoRegistro registroSalidaReturn;

    /**
     * Gets the value of the registroSalidaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoRegistro }
     *     
     */
    public ResultadoRegistro getRegistroSalidaReturn() {
        return registroSalidaReturn;
    }

    /**
     * Sets the value of the registroSalidaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoRegistro }
     *     
     */
    public void setRegistroSalidaReturn(ResultadoRegistro value) {
        this.registroSalidaReturn = value;
    }

}
