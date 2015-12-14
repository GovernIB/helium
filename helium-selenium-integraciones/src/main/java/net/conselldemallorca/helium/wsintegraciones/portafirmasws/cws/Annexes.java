
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Annexes complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Annexes">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="annex" type="{http://www.indra.es/portafirmasws/cws}Annex" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Annexes", propOrder = {
    "annex"
})
public class Annexes {

    protected List<Annex> annex;

    /**
     * Gets the value of the annex property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annex property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnex().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Annex }
     * 
     * 
     */
    public List<Annex> getAnnex() {
        if (annex == null) {
            annex = new ArrayList<Annex>();
        }
        return this.annex;
    }

}
