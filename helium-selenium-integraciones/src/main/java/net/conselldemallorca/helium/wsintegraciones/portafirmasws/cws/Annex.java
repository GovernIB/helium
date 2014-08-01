
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Annex complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Annex">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sign-annex" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="sender" type="{http://www.indra.es/portafirmasws/cws}Sender" minOccurs="0"/>
 *         &lt;element name="external-ids" type="{http://www.indra.es/portafirmasws/cws}ExternalIDs" minOccurs="0"/>
 *         &lt;element name="archive-options" type="{http://www.indra.es/portafirmasws/cws}ArchiveOptions" minOccurs="0"/>
 *         &lt;element name="type-sign" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="is-file-sign" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Annex", propOrder = {
    "description",
    "extension",
    "reference",
    "url",
    "signAnnex",
    "sender",
    "externalIds",
    "archiveOptions",
    "typeSign",
    "isFileSign"
})
public class Annex {

    protected String description;
    protected String extension;
    protected String reference;
    protected String url;
    @XmlElementRef(name = "sign-annex", type = JAXBElement.class)
    protected JAXBElement<Boolean> signAnnex;
    protected Sender sender;
    @XmlElementRef(name = "external-ids", type = JAXBElement.class)
    protected JAXBElement<ExternalIDs> externalIds;
    @XmlElementRef(name = "archive-options", type = JAXBElement.class)
    protected JAXBElement<ArchiveOptions> archiveOptions;
    @XmlElementRef(name = "type-sign", type = JAXBElement.class)
    protected JAXBElement<Integer> typeSign;
    @XmlElementRef(name = "is-file-sign", type = JAXBElement.class)
    protected JAXBElement<Boolean> isFileSign;

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad extension.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Define el valor de la propiedad extension.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtension(String value) {
        this.extension = value;
    }

    /**
     * Obtiene el valor de la propiedad reference.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReference() {
        return reference;
    }

    /**
     * Define el valor de la propiedad reference.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReference(String value) {
        this.reference = value;
    }

    /**
     * Obtiene el valor de la propiedad url.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Define el valor de la propiedad url.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Obtiene el valor de la propiedad signAnnex.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSignAnnex() {
        return signAnnex;
    }

    /**
     * Define el valor de la propiedad signAnnex.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSignAnnex(JAXBElement<Boolean> value) {
        this.signAnnex = value;
    }

    /**
     * Obtiene el valor de la propiedad sender.
     * 
     * @return
     *     possible object is
     *     {@link Sender }
     *     
     */
    public Sender getSender() {
        return sender;
    }

    /**
     * Define el valor de la propiedad sender.
     * 
     * @param value
     *     allowed object is
     *     {@link Sender }
     *     
     */
    public void setSender(Sender value) {
        this.sender = value;
    }

    /**
     * Obtiene el valor de la propiedad externalIds.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public JAXBElement<ExternalIDs> getExternalIds() {
        return externalIds;
    }

    /**
     * Define el valor de la propiedad externalIds.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}
     *     
     */
    public void setExternalIds(JAXBElement<ExternalIDs> value) {
        this.externalIds = value;
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
     * Obtiene el valor de la propiedad typeSign.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getTypeSign() {
        return typeSign;
    }

    /**
     * Define el valor de la propiedad typeSign.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setTypeSign(JAXBElement<Integer> value) {
        this.typeSign = value;
    }

    /**
     * Obtiene el valor de la propiedad isFileSign.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getIsFileSign() {
        return isFileSign;
    }

    /**
     * Define el valor de la propiedad isFileSign.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setIsFileSign(JAXBElement<Boolean> value) {
        this.isFileSign = value;
    }

}
