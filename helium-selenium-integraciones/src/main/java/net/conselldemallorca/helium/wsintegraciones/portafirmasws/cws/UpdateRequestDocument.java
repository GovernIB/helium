
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para UpdateRequestDocument complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="UpdateRequestDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributes" type="{http://www.indra.es/portafirmasws/cws}DocumentAttributes" minOccurs="0"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *         &lt;element name="annexes" type="{http://www.indra.es/portafirmasws/cws}Annexes" minOccurs="0"/>
 *         &lt;element name="steps" type="{http://www.indra.es/portafirmasws/cws}Steps" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateRequestDocument", propOrder = {
    "id",
    "attributes",
    "archiveOptions",
    "annexes",
    "steps"
})
public class UpdateRequestDocument {

    protected int id;
    protected DocumentAttributes attributes;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class)
    protected JAXBElement<ArchiveOptions> archiveOptions;
    protected Annexes annexes;
    protected Steps steps;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad attributes.
     * 
     * @return
     *     possible object is
     *     {@link DocumentAttributes }
     *     
     */
    public DocumentAttributes getAttributes() {
        return attributes;
    }

    /**
     * Define el valor de la propiedad attributes.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentAttributes }
     *     
     */
    public void setAttributes(DocumentAttributes value) {
        this.attributes = value;
    }

    /**
     * Obtiene el valor de la propiedad archiveOptions.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public JAXBElement<ArchiveOptions> getArchiveOptions() {
        return archiveOptions;
    }

    /**
     * Define el valor de la propiedad archiveOptions.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}
     *     
     */
    public void setArchiveOptions(JAXBElement<ArchiveOptions> value) {
        this.archiveOptions = value;
    }

    /**
     * Obtiene el valor de la propiedad annexes.
     * 
     * @return
     *     possible object is
     *     {@link Annexes }
     *     
     */
    public Annexes getAnnexes() {
        return annexes;
    }

    /**
     * Define el valor de la propiedad annexes.
     * 
     * @param value
     *     allowed object is
     *     {@link Annexes }
     *     
     */
    public void setAnnexes(Annexes value) {
        this.annexes = value;
    }

    /**
     * Obtiene el valor de la propiedad steps.
     * 
     * @return
     *     possible object is
     *     {@link Steps }
     *     
     */
    public Steps getSteps() {
        return steps;
    }

    /**
     * Define el valor de la propiedad steps.
     * 
     * @param value
     *     allowed object is
     *     {@link Steps }
     *     
     */
    public void setSteps(Steps value) {
        this.steps = value;
    }

}
