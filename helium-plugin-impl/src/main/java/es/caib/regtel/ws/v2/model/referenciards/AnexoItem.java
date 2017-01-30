
package es.caib.regtel.ws.v2.model.referenciards;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AnexoItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AnexoItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificador_documento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="referenciaRDS" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDS"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnexoItem", propOrder = {
    "identificadorDocumento",
    "referenciaRDS"
})
public class AnexoItem {

    @XmlElement(name = "identificador_documento", required = true)
    protected String identificadorDocumento;
    @XmlElement(required = true)
    protected ReferenciaRDS referenciaRDS;

    /**
     * Gets the value of the identificadorDocumento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorDocumento() {
        return identificadorDocumento;
    }

    /**
     * Sets the value of the identificadorDocumento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorDocumento(String value) {
        this.identificadorDocumento = value;
    }

    /**
     * Gets the value of the referenciaRDS property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaRDS }
     *     
     */
    public ReferenciaRDS getReferenciaRDS() {
        return referenciaRDS;
    }

    /**
     * Sets the value of the referenciaRDS property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaRDS }
     *     
     */
    public void setReferenciaRDS(ReferenciaRDS value) {
        this.referenciaRDS = value;
    }

}
