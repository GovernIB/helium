
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArchiveLocator complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArchiveLocator">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repository-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="archive-uri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="archive-version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="repository-base" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="folder-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="file-path-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature-files-path-pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visual-file-path-name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="retention-policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signature-custody" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchiveLocator", propOrder = {
    "repositoryId",
    "archiveUri",
    "archiveVersion",
    "repositoryBase",
    "folderName",
    "filePathName",
    "signatureFilesPathPattern",
    "visualFilePathName",
    "retentionPolicy",
    "signatureCustody"
})
public class ArchiveLocator {

    @XmlElement(name = "repository-id", required = true, nillable = true)
    protected String repositoryId;
    @XmlElementRef(name = "archive-uri", type = JAXBElement.class)
    protected JAXBElement<String> archiveUri;
    @XmlElementRef(name = "archive-version", type = JAXBElement.class)
    protected JAXBElement<String> archiveVersion;
    @XmlElementRef(name = "repository-base", type = JAXBElement.class)
    protected JAXBElement<String> repositoryBase;
    @XmlElementRef(name = "folder-name", type = JAXBElement.class)
    protected JAXBElement<String> folderName;
    @XmlElementRef(name = "file-path-name", type = JAXBElement.class)
    protected JAXBElement<String> filePathName;
    @XmlElementRef(name = "signature-files-path-pattern", type = JAXBElement.class)
    protected JAXBElement<String> signatureFilesPathPattern;
    @XmlElementRef(name = "visual-file-path-name", type = JAXBElement.class)
    protected JAXBElement<String> visualFilePathName;
    @XmlElementRef(name = "retention-policy", type = JAXBElement.class)
    protected JAXBElement<String> retentionPolicy;
    @XmlElementRef(name = "signature-custody", type = JAXBElement.class)
    protected JAXBElement<Boolean> signatureCustody;

    /**
     * Obtiene el valor de la propiedad repositoryId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepositoryId() {
        return repositoryId;
    }

    /**
     * Define el valor de la propiedad repositoryId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepositoryId(String value) {
        this.repositoryId = value;
    }

    /**
     * Obtiene el valor de la propiedad archiveUri.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getArchiveUri() {
        return archiveUri;
    }

    /**
     * Define el valor de la propiedad archiveUri.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setArchiveUri(JAXBElement<String> value) {
        this.archiveUri = value;
    }

    /**
     * Obtiene el valor de la propiedad archiveVersion.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getArchiveVersion() {
        return archiveVersion;
    }

    /**
     * Define el valor de la propiedad archiveVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setArchiveVersion(JAXBElement<String> value) {
        this.archiveVersion = value;
    }

    /**
     * Obtiene el valor de la propiedad repositoryBase.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepositoryBase() {
        return repositoryBase;
    }

    /**
     * Define el valor de la propiedad repositoryBase.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepositoryBase(JAXBElement<String> value) {
        this.repositoryBase = value;
    }

    /**
     * Obtiene el valor de la propiedad folderName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFolderName() {
        return folderName;
    }

    /**
     * Define el valor de la propiedad folderName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFolderName(JAXBElement<String> value) {
        this.folderName = value;
    }

    /**
     * Obtiene el valor de la propiedad filePathName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFilePathName() {
        return filePathName;
    }

    /**
     * Define el valor de la propiedad filePathName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFilePathName(JAXBElement<String> value) {
        this.filePathName = value;
    }

    /**
     * Obtiene el valor de la propiedad signatureFilesPathPattern.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getSignatureFilesPathPattern() {
        return signatureFilesPathPattern;
    }

    /**
     * Define el valor de la propiedad signatureFilesPathPattern.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setSignatureFilesPathPattern(JAXBElement<String> value) {
        this.signatureFilesPathPattern = value;
    }

    /**
     * Obtiene el valor de la propiedad visualFilePathName.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getVisualFilePathName() {
        return visualFilePathName;
    }

    /**
     * Define el valor de la propiedad visualFilePathName.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setVisualFilePathName(JAXBElement<String> value) {
        this.visualFilePathName = value;
    }

    /**
     * Obtiene el valor de la propiedad retentionPolicy.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * Define el valor de la propiedad retentionPolicy.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRetentionPolicy(JAXBElement<String> value) {
        this.retentionPolicy = value;
    }

    /**
     * Obtiene el valor de la propiedad signatureCustody.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSignatureCustody() {
        return signatureCustody;
    }

    /**
     * Define el valor de la propiedad signatureCustody.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSignatureCustody(JAXBElement<Boolean> value) {
        this.signatureCustody = value;
    }

}
