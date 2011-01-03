
package net.conselldemallorca.helium.integracio.redose.client.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FirmasWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FirmasWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firmas" type="{urn:es:caib:redose:ws:v1:model:FirmaWS}FirmaWS" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmasWS", namespace = "urn:es:caib:redose:ws:v1:model:FirmaWS", propOrder = {
    "firmas"
})
public class FirmasWS {

    protected List<FirmaWS> firmas;

    /**
     * Gets the value of the firmas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the firmas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFirmas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FirmaWS }
     * 
     * 
     */
    public List<FirmaWS> getFirmas() {
        if (firmas == null) {
            firmas = new ArrayList<FirmaWS>();
        }
        return this.firmas;
    }

}
