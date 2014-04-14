
package es.indra.portafirmasws.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PendingResult complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PendingResult">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}SearchResult">
 *       &lt;sequence>
 *         &lt;element name="pending-documents" type="{http://www.indra.es/portafirmasws/cws}PendingDocuments"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PendingResult", propOrder = {
    "pendingDocuments"
})
public class PendingResult
    extends SearchResult
{

    @XmlElement(name = "pending-documents", required = true, nillable = true)
    protected PendingDocuments pendingDocuments;

    /**
     * Obtiene el valor de la propiedad pendingDocuments.
     * 
     * @return
     *     possible object is
     *     {@link PendingDocuments }
     *     
     */
    public PendingDocuments getPendingDocuments() {
        return pendingDocuments;
    }

    /**
     * Define el valor de la propiedad pendingDocuments.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingDocuments }
     *     
     */
    public void setPendingDocuments(PendingDocuments value) {
        this.pendingDocuments = value;
    }

}
