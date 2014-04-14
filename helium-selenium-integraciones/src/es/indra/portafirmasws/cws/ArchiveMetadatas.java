
package es.indra.portafirmasws.cws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArchiveMetadatas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArchiveMetadatas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="archive-metadata" type="{http://www.indra.es/portafirmasws/cws}ArchiveMetadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchiveMetadatas", propOrder = {
    "archiveMetadata"
})
public class ArchiveMetadatas {

    @XmlElement(name = "archive-metadata", nillable = true)
    protected List<ArchiveMetadata> archiveMetadata;

    /**
     * Gets the value of the archiveMetadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archiveMetadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchiveMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchiveMetadata }
     * 
     * 
     */
    public List<ArchiveMetadata> getArchiveMetadata() {
        if (archiveMetadata == null) {
            archiveMetadata = new ArrayList<ArchiveMetadata>();
        }
        return this.archiveMetadata;
    }

}
