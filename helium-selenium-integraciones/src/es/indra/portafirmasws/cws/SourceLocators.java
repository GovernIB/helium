
package es.indra.portafirmasws.cws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SourceLocators complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SourceLocators">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="archive-locator" type="{http://www.indra.es/portafirmasws/cws}ArchiveLocator" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SourceLocators", propOrder = {
    "archiveLocator"
})
public class SourceLocators {

    @XmlElement(name = "archive-locator", nillable = true)
    protected List<ArchiveLocator> archiveLocator;

    /**
     * Gets the value of the archiveLocator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archiveLocator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchiveLocator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchiveLocator }
     * 
     * 
     */
    public List<ArchiveLocator> getArchiveLocator() {
        if (archiveLocator == null) {
            archiveLocator = new ArrayList<ArchiveLocator>();
        }
        return this.archiveLocator;
    }

}
