
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.firmaws.FirmaWS;
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
 *         &lt;element name="referenciaRDS" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDSAsientoRegistral"/>
 *         &lt;element name="firma" type="{urn:es:caib:regtel:ws:v2:model:FirmaWS}FirmaWS"/>
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
    "referenciaRDS",
    "firma"
})
@XmlRootElement(name = "registroEntradaConFirma")
public class RegistroEntradaConFirma {

    @XmlElement(required = true)
    protected ReferenciaRDSAsientoRegistral referenciaRDS;
    @XmlElement(required = true)
    protected FirmaWS firma;

    /**
     * Gets the value of the referenciaRDS property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaRDSAsientoRegistral }
     *     
     */
    public ReferenciaRDSAsientoRegistral getReferenciaRDS() {
        return referenciaRDS;
    }

    /**
     * Sets the value of the referenciaRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaRDSAsientoRegistral }
     *     
     */
    public void setReferenciaRDS(ReferenciaRDSAsientoRegistral value) {
        this.referenciaRDS = value;
    }

    /**
     * Gets the value of the firma property.
     * 
     * @return
     *     possible object is
     *     {@link FirmaWS }
     *     
     */
    public FirmaWS getFirma() {
        return firma;
    }

    /**
     * Sets the value of the firma property.
     * 
     * @param value
     *     allowed object is
     *     {@link FirmaWS }
     *     
     */
    public void setFirma(FirmaWS value) {
        this.firma = value;
    }

}
