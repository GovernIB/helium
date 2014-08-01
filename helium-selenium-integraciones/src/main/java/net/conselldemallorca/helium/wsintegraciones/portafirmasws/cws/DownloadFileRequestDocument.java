
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DownloadFileRequestDocument complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DownloadFileRequestDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="download-options" type="{http://www.indra.es/portafirmasws/cws}DownloadOptions"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DownloadFileRequestDocument", propOrder = {
    "id",
    "downloadOptions"
})
public class DownloadFileRequestDocument {

    protected int id;
    @XmlElement(name = "download-options", required = true, nillable = true)
    protected DownloadOptions downloadOptions;

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
     * Obtiene el valor de la propiedad downloadOptions.
     * 
     * @return
     *     possible object is
     *     {@link DownloadOptions }
     *     
     */
    public DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }

    /**
     * Define el valor de la propiedad downloadOptions.
     * 
     * @param value
     *     allowed object is
     *     {@link DownloadOptions }
     *     
     */
    public void setDownloadOptions(DownloadOptions value) {
        this.downloadOptions = value;
    }

}
