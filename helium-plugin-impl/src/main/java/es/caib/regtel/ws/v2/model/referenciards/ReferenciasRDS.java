
package es.caib.regtel.ws.v2.model.referenciards;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciasRDS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciasRDS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="referenciasRDS" type="{urn:es:caib:regtel:ws:v2:model:ReferenciaRDS}ReferenciaRDS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenciasRDS", propOrder = {
    "referenciasRDS"
})
public class ReferenciasRDS {

    protected List<ReferenciaRDS> referenciasRDS;

    /**
     * Gets the value of the referenciasRDS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referenciasRDS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferenciasRDS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenciaRDS }
     * 
     * 
     */
    public List<ReferenciaRDS> getReferenciasRDS() {
        if (referenciasRDS == null) {
            referenciasRDS = new ArrayList<ReferenciaRDS>();
        }
        return this.referenciasRDS;
    }

}
