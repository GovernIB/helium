
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.referenciards.ReferenciaRDSAsientoRegistral;


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
 *         &lt;element name="prepararRegistroEntradaReturn" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDSAsientoRegistral"/>
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
    "prepararRegistroEntradaReturn"
})
@XmlRootElement(name = "prepararRegistroEntradaResponse")
public class PrepararRegistroEntradaResponse {

    @XmlElement(required = true)
    protected ReferenciaRDSAsientoRegistral prepararRegistroEntradaReturn;

    /**
     * Gets the value of the prepararRegistroEntradaReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaRDSAsientoRegistral }
     *     
     */
    public ReferenciaRDSAsientoRegistral getPrepararRegistroEntradaReturn() {
        return prepararRegistroEntradaReturn;
    }

    /**
     * Sets the value of the prepararRegistroEntradaReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaRDSAsientoRegistral }
     *     
     */
    public void setPrepararRegistroEntradaReturn(ReferenciaRDSAsientoRegistral value) {
        this.prepararRegistroEntradaReturn = value;
    }

}
