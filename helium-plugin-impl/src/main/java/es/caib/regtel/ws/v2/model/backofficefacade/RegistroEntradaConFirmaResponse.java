
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
 *         &lt;element name="registroEntradaConFirmaReturn" type="{urn:es:caib:regtel:ws:v2:model:ResultadoRegistro}ResultadoRegistro"/>
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
    "registroEntradaConFirmaReturn"
})
@XmlRootElement(name = "registroEntradaConFirmaResponse")
public class RegistroEntradaConFirmaResponse {

    @XmlElement(required = true)
    protected ResultadoRegistro registroEntradaConFirmaReturn;

    /**
     * Gets the value of the registroEntradaConFirmaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoRegistro }
     *     
     */
    public ResultadoRegistro getRegistroEntradaConFirmaReturn() {
        return registroEntradaConFirmaReturn;
    }

    /**
     * Sets the value of the registroEntradaConFirmaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoRegistro }
     *     
     */
    public void setRegistroEntradaConFirmaReturn(ResultadoRegistro value) {
        this.registroEntradaConFirmaReturn = value;
    }

}
