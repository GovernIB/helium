
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ArchiveOptions complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ArchiveOptions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source-locators" type="{http://www.indra.es/portafirmasws/cws}SourceLocators" minOccurs="0"/>
 *         &lt;element name="destination-locators" type="{http://www.indra.es/portafirmasws/cws}DestinationLocators" minOccurs="0"/>
 *         &lt;element name="archive-metadatas" type="{http://www.indra.es/portafirmasws/cws}ArchiveMetadatas" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchiveOptions", propOrder = {
    "sourceLocators",
    "destinationLocators",
    "archiveMetadatas"
})
public class ArchiveOptions {

    @XmlElementRef(name = "source-locators", type = JAXBElement.class, required = false)
    protected JAXBElement<SourceLocators> sourceLocators;
    @XmlElementRef(name = "destination-locators", type = JAXBElement.class, required = false)
    protected JAXBElement<DestinationLocators> destinationLocators;
    @XmlElementRef(name = "archive-metadatas", type = JAXBElement.class, required = false)
    protected JAXBElement<ArchiveMetadatas> archiveMetadatas;

    /**
     * Obtiene el valor de la propiedad sourceLocators.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SourceLocators }{@code >}
     *     
     */
    public JAXBElement<SourceLocators> getSourceLocators() {
        return sourceLocators;
    }

    /**
     * Define el valor de la propiedad sourceLocators.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SourceLocators }{@code >}
     *     
     */
    public void setSourceLocators(JAXBElement<SourceLocators> value) {
        this.sourceLocators = value;
    }

    /**
     * Obtiene el valor de la propiedad destinationLocators.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DestinationLocators }{@code >}
     *     
     */
    public JAXBElement<DestinationLocators> getDestinationLocators() {
        return destinationLocators;
    }

    /**
     * Define el valor de la propiedad destinationLocators.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DestinationLocators }{@code >}
     *     
     */
    public void setDestinationLocators(JAXBElement<DestinationLocators> value) {
        this.destinationLocators = value;
    }

    /**
     * Obtiene el valor de la propiedad archiveMetadatas.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArchiveMetadatas }{@code >}
     *     
     */
    public JAXBElement<ArchiveMetadatas> getArchiveMetadatas() {
        return archiveMetadatas;
    }

    /**
     * Define el valor de la propiedad archiveMetadatas.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArchiveMetadatas }{@code >}
     *     
     */
    public void setArchiveMetadatas(JAXBElement<ArchiveMetadatas> value) {
        this.archiveMetadatas = value;
    }

}
