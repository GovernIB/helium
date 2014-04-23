
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para File complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="File">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="type" type="{http://www.indra.es/portafirmasws/cws}TypeEnum"/>
 *         &lt;element name="reference" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profile" type="{http://www.indra.es/portafirmasws/cws}ProfileEnum"/>
 *         &lt;element name="file-format" type="{http://www.indra.es/portafirmasws/cws}CodificationTypeEnum"/>
 *         &lt;element name="extension" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mime-type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="base64-data" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="number-signatures" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signers-id" type="{http://www.indra.es/portafirmasws/cws}SignersID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "File", propOrder = {
    "index",
    "type",
    "reference",
    "profile",
    "fileFormat",
    "extension",
    "mimeType",
    "base64Data",
    "numberSignatures",
    "signersId"
})
public class File {

    protected int index;
    @XmlElement(required = true)
    protected TypeEnum type;
    protected String reference;
    @XmlElement(required = true)
    protected ProfileEnum profile;
    @XmlElement(name = "file-format", required = true, nillable = true)
    protected CodificationTypeEnum fileFormat;
    protected String extension;
    @XmlElementRef(name = "mime-type", type = JAXBElement.class)
    protected JAXBElement<String> mimeType;
    @XmlElementRef(name = "base64-data", type = JAXBElement.class)
    protected JAXBElement<String> base64Data;
    @XmlElementRef(name = "number-signatures", type = JAXBElement.class)
    protected JAXBElement<String> numberSignatures;
    @XmlElementRef(name = "signers-id", type = JAXBElement.class)
    protected JAXBElement<SignersID> signersId;

    /**
     * Obtiene el valor de la propiedad index.
     * 
     */
    public int getIndex() {
        return index;
    }

    /**
     * Define el valor de la propiedad index.
     * 
     */
    public void setIndex(int value) {
        this.index = value;
    }

    /**
     * Obtiene el valor de la propiedad type.
     * 
     * @return
     *     possible object is
     *     {@link TypeEnum }
     *     
     */
    public TypeEnum getType() {
        return type;
    }

    /**
     * Define el valor de la propiedad type.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeEnum }
     *     
     */
    public void setType(TypeEnum value) {
        this.type = value;
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
     * Obtiene el valor de la propiedad profile.
     * 
     * @return
     *     possible object is
     *     {@link ProfileEnum }
     *     
     */
    public ProfileEnum getProfile() {
        return profile;
    }

    /**
     * Define el valor de la propiedad profile.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileEnum }
     *     
     */
    public void setProfile(ProfileEnum value) {
        this.profile = value;
    }

    /**
     * Obtiene el valor de la propiedad fileFormat.
     * 
     * @return
     *     possible object is
     *     {@link CodificationTypeEnum }
     *     
     */
    public CodificationTypeEnum getFileFormat() {
        return fileFormat;
    }

    /**
     * Define el valor de la propiedad fileFormat.
     * 
     * @param value
     *     allowed object is
     *     {@link CodificationTypeEnum }
     *     
     */
    public void setFileFormat(CodificationTypeEnum value) {
        this.fileFormat = value;
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
     * Obtiene el valor de la propiedad mimeType.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getMimeType() {
        return mimeType;
    }

    /**
     * Define el valor de la propiedad mimeType.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setMimeType(JAXBElement<String> value) {
        this.mimeType = value;
    }

    /**
     * Obtiene el valor de la propiedad base64Data.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBase64Data() {
        return base64Data;
    }

    /**
     * Define el valor de la propiedad base64Data.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBase64Data(JAXBElement<String> value) {
        this.base64Data = value;
    }

    /**
     * Obtiene el valor de la propiedad numberSignatures.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumberSignatures() {
        return numberSignatures;
    }

    /**
     * Define el valor de la propiedad numberSignatures.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumberSignatures(JAXBElement<String> value) {
        this.numberSignatures = value;
    }

    /**
     * Obtiene el valor de la propiedad signersId.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignersID }{@code >}
     *     
     */
    public JAXBElement<SignersID> getSignersId() {
        return signersId;
    }

    /**
     * Define el valor de la propiedad signersId.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignersID }{@code >}
     *     
     */
    public void setSignersId(JAXBElement<SignersID> value) {
        this.signersId = value;
    }

}
