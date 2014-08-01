
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="application" type="{http://www.indra.es/portafirmasws/cws}Application"/>
 *         &lt;element name="download-documents" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="additional-info" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="archive-info" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="document" type="{http://www.indra.es/portafirmasws/cws}DownloadRequestDocument"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "application",
    "downloadDocuments",
    "additionalInfo",
    "archiveInfo",
    "document"
})
@XmlRootElement(name = "download-request")
public class DownloadRequest {

    @XmlElement(required = true)
    protected Application application;
    @XmlElementRef(name = "download-documents", type = JAXBElement.class)
    protected JAXBElement<Boolean> downloadDocuments;
    @XmlElementRef(name = "additional-info", type = JAXBElement.class)
    protected JAXBElement<Boolean> additionalInfo;
    @XmlElementRef(name = "archive-info", type = JAXBElement.class)
    protected JAXBElement<Boolean> archiveInfo;
    @XmlElement(required = true)
    protected DownloadRequestDocument document;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Obtiene el valor de la propiedad application.
     * 
     * @return
     *     possible object is
     *     {@link Application }
     *     
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Define el valor de la propiedad application.
     * 
     * @param value
     *     allowed object is
     *     {@link Application }
     *     
     */
    public void setApplication(Application value) {
        this.application = value;
    }

    /**
     * Obtiene el valor de la propiedad downloadDocuments.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getDownloadDocuments() {
        return downloadDocuments;
    }

    /**
     * Define el valor de la propiedad downloadDocuments.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setDownloadDocuments(JAXBElement<Boolean> value) {
        this.downloadDocuments = value;
    }

    /**
     * Obtiene el valor de la propiedad additionalInfo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Define el valor de la propiedad additionalInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setAdditionalInfo(JAXBElement<Boolean> value) {
        this.additionalInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad archiveInfo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getArchiveInfo() {
        return archiveInfo;
    }

    /**
     * Define el valor de la propiedad archiveInfo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setArchiveInfo(JAXBElement<Boolean> value) {
        this.archiveInfo = value;
    }

    /**
     * Obtiene el valor de la propiedad document.
     * 
     * @return
     *     possible object is
     *     {@link DownloadRequestDocument }
     *     
     */
    public DownloadRequestDocument getDocument() {
        return document;
    }

    /**
     * Define el valor de la propiedad document.
     * 
     * @param value
     *     allowed object is
     *     {@link DownloadRequestDocument }
     *     
     */
    public void setDocument(DownloadRequestDocument value) {
        this.document = value;
    }

    /**
     * Obtiene el valor de la propiedad version.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Define el valor de la propiedad version.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
