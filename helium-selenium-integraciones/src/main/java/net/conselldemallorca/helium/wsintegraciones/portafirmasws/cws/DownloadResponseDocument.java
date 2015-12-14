
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DownloadResponseDocument complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DownloadResponseDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributes" type="{http://www.indra.es/portafirmasws/cws}DocumentAttributes"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *         &lt;element name="annexes" type="{http://www.indra.es/portafirmasws/cws}Annexes" minOccurs="0"/>
 *         &lt;element name="signature-files" type="{http://www.indra.es/portafirmasws/cws}SignatureFiles" minOccurs="0"/>
 *         &lt;element name="visual-files" type="{http://www.indra.es/portafirmasws/cws}VisualFiles" minOccurs="0"/>
 *         &lt;element name="steps" type="{http://www.indra.es/portafirmasws/cws}Steps"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownloadResponseDocument", propOrder = {
    "id",
    "attributes",
    "archiveOptions",
    "annexes",
    "signatureFiles",
    "visualFiles",
    "steps"
})
public class DownloadResponseDocument {

    protected int id;
    @XmlElement(required = true)
    protected DocumentAttributes attributes;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class)
    protected JAXBElement<ArchiveOptions> archiveOptions;
    protected Annexes annexes;
    @XmlElementRef(name = "signature-files", type = JAXBElement.class)
    protected JAXBElement<SignatureFiles> signatureFiles;
    @XmlElementRef(name = "visual-files", type = JAXBElement.class)
    protected JAXBElement<VisualFiles> visualFiles;
    @XmlElement(required = true)
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
     * Obtiene el valor de la propiedad signatureFiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignatureFiles }{@code >}
     *     
     */
    public JAXBElement<SignatureFiles> getSignatureFiles() {
        return signatureFiles;
    }

    /**
     * Define el valor de la propiedad signatureFiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignatureFiles }{@code >}
     *     
     */
    public void setSignatureFiles(JAXBElement<SignatureFiles> value) {
        this.signatureFiles = value;
    }

    /**
     * Obtiene el valor de la propiedad visualFiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link VisualFiles }{@code >}
     *     
     */
    public JAXBElement<VisualFiles> getVisualFiles() {
        return visualFiles;
    }

    /**
     * Define el valor de la propiedad visualFiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link VisualFiles }{@code >}
     *     
     */
    public void setVisualFiles(JAXBElement<VisualFiles> value) {
        this.visualFiles = value;
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
