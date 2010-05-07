
package net.conselldemallorca.helium.integracio.bantel.service.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciasEntrada complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciasEntrada">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="referenciaEntrada" type="{urn:es:caib:bantel:ws:v1:model:ReferenciaEntrada}ReferenciaEntrada" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenciasEntrada", namespace = "urn:es:caib:bantel:ws:v1:model:ReferenciaEntrada", propOrder = {
    "referenciaEntrada"
})
public class ReferenciasEntrada {

    protected List<ReferenciaEntrada> referenciaEntrada;

    /**
     * Gets the value of the referenciaEntrada property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referenciaEntrada property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferenciaEntrada().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenciaEntrada }
     * 
     * 
     */
    public List<ReferenciaEntrada> getReferenciaEntrada() {
        if (referenciaEntrada == null) {
            referenciaEntrada = new ArrayList<ReferenciaEntrada>();
        }
        return this.referenciaEntrada;
    }

}
