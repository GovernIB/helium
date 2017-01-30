
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
 *         &lt;element name="registroEntradaReturn" type="{urn:es:caib:regtel:ws:v2:model:ResultadoRegistro}ResultadoRegistro"/>
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
    "registroEntradaReturn"
})
@XmlRootElement(name = "registroEntradaResponse")
public class RegistroEntradaResponse {

    @XmlElement(required = true)
    protected ResultadoRegistro registroEntradaReturn;

    /**
     * Gets the value of the registroEntradaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoRegistro }
     *     
     */
    public ResultadoRegistro getRegistroEntradaReturn() {
        return registroEntradaReturn;
    }

    /**
     * Sets the value of the registroEntradaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoRegistro }
     *     
     */
    public void setRegistroEntradaReturn(ResultadoRegistro value) {
        this.registroEntradaReturn = value;
    }

}
