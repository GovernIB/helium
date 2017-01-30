
package es.caib.regtel.ws.v2.model.referenciards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciaRDSAsientoRegistral complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciaRDSAsientoRegistral">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="asientoRegistral" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDS"/>
 *         &lt;element name="anexos" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}AnexosMap"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenciaRDSAsientoRegistral", propOrder = {
    "asientoRegistral",
    "anexos"
})
public class ReferenciaRDSAsientoRegistral {

    @XmlElement(required = true)
    protected ReferenciaRDS asientoRegistral;
    @XmlElement(required = true)
    protected AnexosMap anexos;

    /**
     * Gets the value of the asientoRegistral property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaRDS }
     *     
     */
    public ReferenciaRDS getAsientoRegistral() {
        return asientoRegistral;
    }

    /**
     * Sets the value of the asientoRegistral property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaRDS }
     *     
     */
    public void setAsientoRegistral(ReferenciaRDS value) {
        this.asientoRegistral = value;
    }

    /**
     * Gets the value of the anexos property.
     * 
     * @return
     *     possible object is
     *     {@link AnexosMap }
     *     
     */
    public AnexosMap getAnexos() {
        return anexos;
    }

    /**
     * Sets the value of the anexos property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnexosMap }
     *     
     */
    public void setAnexos(AnexosMap value) {
        this.anexos = value;
    }

}
