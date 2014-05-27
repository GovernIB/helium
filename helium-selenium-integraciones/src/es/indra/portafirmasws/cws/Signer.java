
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para Signer complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Signer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="check-cert" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="delegates" type="{http://www.indra.es/portafirmasws/cws}Delegates" minOccurs="0"/>
 *         &lt;element name="substitutes" type="{http://www.indra.es/portafirmasws/cws}Substitutes" minOccurs="0"/>
 *         &lt;element name="job" type="{http://www.indra.es/portafirmasws/cws}Job" minOccurs="0"/>
 *         &lt;element name="certificate" type="{http://www.indra.es/portafirmasws/cws}Certificate" minOccurs="0"/>
 *         &lt;element name="rejection" type="{http://www.indra.es/portafirmasws/cws}Rejection" minOccurs="0"/>
 *         &lt;element name="signature-files" type="{http://www.indra.es/portafirmasws/cws}SignerSignatureFiles" minOccurs="0"/>
 *         &lt;element name="id-update" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdf-appearance" type="{http://www.indra.es/portafirmasws/cws}PdfAppearance" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Signer", propOrder = {
    "id",
    "name",
    "email",
    "checkCert",
    "date",
    "delegates",
    "substitutes",
    "job",
    "certificate",
    "rejection",
    "signatureFiles",
    "idUpdate",
    "pdfAppearance"
})
public class Signer {

    @XmlElement(required = true)
    protected String id;
    protected String name;
    protected String email;
    @XmlElementRef(name = "check-cert", type = JAXBElement.class)
    protected JAXBElement<Boolean> checkCert;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected Delegates delegates;
    protected Substitutes substitutes;
    protected Job job;
    protected Certificate certificate;
    protected Rejection rejection;
    @XmlElementRef(name = "signature-files", type = JAXBElement.class)
    protected JAXBElement<SignerSignatureFiles> signatureFiles;
    @XmlElementRef(name = "id-update", type = JAXBElement.class)
    protected JAXBElement<String> idUpdate;
    @XmlElementRef(name = "pdf-appearance", type = JAXBElement.class)
    protected JAXBElement<PdfAppearance> pdfAppearance;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Obtiene el valor de la propiedad checkCert.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getCheckCert() {
        return checkCert;
    }

    /**
     * Define el valor de la propiedad checkCert.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setCheckCert(JAXBElement<Boolean> value) {
        this.checkCert = value;
    }

    /**
     * Obtiene el valor de la propiedad date.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Define el valor de la propiedad date.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Obtiene el valor de la propiedad delegates.
     * 
     * @return
     *     possible object is
     *     {@link Delegates }
     *     
     */
    public Delegates getDelegates() {
        return delegates;
    }

    /**
     * Define el valor de la propiedad delegates.
     * 
     * @param value
     *     allowed object is
     *     {@link Delegates }
     *     
     */
    public void setDelegates(Delegates value) {
        this.delegates = value;
    }

    /**
     * Obtiene el valor de la propiedad substitutes.
     * 
     * @return
     *     possible object is
     *     {@link Substitutes }
     *     
     */
    public Substitutes getSubstitutes() {
        return substitutes;
    }

    /**
     * Define el valor de la propiedad substitutes.
     * 
     * @param value
     *     allowed object is
     *     {@link Substitutes }
     *     
     */
    public void setSubstitutes(Substitutes value) {
        this.substitutes = value;
    }

    /**
     * Obtiene el valor de la propiedad job.
     * 
     * @return
     *     possible object is
     *     {@link Job }
     *     
     */
    public Job getJob() {
        return job;
    }

    /**
     * Define el valor de la propiedad job.
     * 
     * @param value
     *     allowed object is
     *     {@link Job }
     *     
     */
    public void setJob(Job value) {
        this.job = value;
    }

    /**
     * Obtiene el valor de la propiedad certificate.
     * 
     * @return
     *     possible object is
     *     {@link Certificate }
     *     
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * Define el valor de la propiedad certificate.
     * 
     * @param value
     *     allowed object is
     *     {@link Certificate }
     *     
     */
    public void setCertificate(Certificate value) {
        this.certificate = value;
    }

    /**
     * Obtiene el valor de la propiedad rejection.
     * 
     * @return
     *     possible object is
     *     {@link Rejection }
     *     
     */
    public Rejection getRejection() {
        return rejection;
    }

    /**
     * Define el valor de la propiedad rejection.
     * 
     * @param value
     *     allowed object is
     *     {@link Rejection }
     *     
     */
    public void setRejection(Rejection value) {
        this.rejection = value;
    }

    /**
     * Obtiene el valor de la propiedad signatureFiles.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignerSignatureFiles }{@code >}
     *     
     */
    public JAXBElement<SignerSignatureFiles> getSignatureFiles() {
        return signatureFiles;
    }

    /**
     * Define el valor de la propiedad signatureFiles.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignerSignatureFiles }{@code >}
     *     
     */
    public void setSignatureFiles(JAXBElement<SignerSignatureFiles> value) {
        this.signatureFiles = value;
    }

    /**
     * Obtiene el valor de la propiedad idUpdate.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIdUpdate() {
        return idUpdate;
    }

    /**
     * Define el valor de la propiedad idUpdate.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIdUpdate(JAXBElement<String> value) {
        this.idUpdate = value;
    }

    /**
     * Obtiene el valor de la propiedad pdfAppearance.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PdfAppearance }{@code >}
     *     
     */
    public JAXBElement<PdfAppearance> getPdfAppearance() {
        return pdfAppearance;
    }

    /**
     * Define el valor de la propiedad pdfAppearance.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PdfAppearance }{@code >}
     *     
     */
    public void setPdfAppearance(JAXBElement<PdfAppearance> value) {
        this.pdfAppearance = value;
    }

}
