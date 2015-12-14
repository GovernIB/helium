
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para PendingDocuments complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PendingDocuments">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="owned" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="delegated" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PendingDocuments", propOrder = {
    "owned",
    "delegated"
})
public class PendingDocuments {

    @XmlElement(required = true)
    protected String owned;
    @XmlElement(required = true)
    protected String delegated;

    /**
     * Obtiene el valor de la propiedad owned.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwned() {
        return owned;
    }

    /**
     * Define el valor de la propiedad owned.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwned(String value) {
        this.owned = value;
    }

    /**
     * Obtiene el valor de la propiedad delegated.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelegated() {
        return delegated;
    }

    /**
     * Define el valor de la propiedad delegated.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelegated(String value) {
        this.delegated = value;
    }

}
