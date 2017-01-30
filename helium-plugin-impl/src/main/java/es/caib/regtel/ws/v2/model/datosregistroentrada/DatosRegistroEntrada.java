
package es.caib.regtel.ws.v2.model.datosregistroentrada;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.datosasunto.DatosAsunto;
import es.caib.regtel.ws.v2.model.datosinteresado.DatosInteresado;
import es.caib.regtel.ws.v2.model.datosrepresentado.DatosRepresentado;
import es.caib.regtel.ws.v2.model.documento.Documentos;
import es.caib.regtel.ws.v2.model.oficinaregistral.OficinaRegistral;


/**
 * <p>Java class for DatosRegistroEntrada complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DatosRegistroEntrada">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oficinaRegistral" type="{urn:es:caib:regtel:ws:v2:model:OficinaRegistral}OficinaRegistral"/>
 *         &lt;element name="datosInteresado" type="{urn:es:caib:regtel:ws:v2:model:DatosInteresado}DatosInteresado"/>
 *         &lt;element name="datosRepresentado" type="{urn:es:caib:regtel:ws:v2:model:DatosRepresentado}DatosRepresentado"/>
 *         &lt;element name="datosAsunto" type="{urn:es:caib:regtel:ws:v2:model:DatosAsunto}DatosAsunto"/>
 *         &lt;element name="documentos" type="{urn:es:caib:regtel:ws:v2:model:Documento}Documentos" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosRegistroEntrada", propOrder = {
    "oficinaRegistral",
    "datosInteresado",
    "datosRepresentado",
    "datosAsunto",
    "documentos"
})
public class DatosRegistroEntrada {

    @XmlElement(required = true)
    protected OficinaRegistral oficinaRegistral;
    @XmlElement(required = true, nillable = true)
    protected DatosInteresado datosInteresado;
    @XmlElement(required = true, nillable = true)
    protected DatosRepresentado datosRepresentado;
    @XmlElement(required = true)
    protected DatosAsunto datosAsunto;
    @XmlElementRef(name = "documentos", type = JAXBElement.class)
    protected JAXBElement<Documentos> documentos;

    /**
     * Gets the value of the oficinaRegistral property.
     * 
     * @return
     *     possible object is
     *     {@link OficinaRegistral }
     *     
     */
    public OficinaRegistral getOficinaRegistral() {
        return oficinaRegistral;
    }

    /**
     * Sets the value of the oficinaRegistral property.
     * 
     * @param value
     *     allowed object is
     *     {@link OficinaRegistral }
     *     
     */
    public void setOficinaRegistral(OficinaRegistral value) {
        this.oficinaRegistral = value;
    }

    /**
     * Gets the value of the datosInteresado property.
     * 
     * @return
     *     possible object is
     *     {@link DatosInteresado }
     *     
     */
    public DatosInteresado getDatosInteresado() {
        return datosInteresado;
    }

    /**
     * Sets the value of the datosInteresado property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosInteresado }
     *     
     */
    public void setDatosInteresado(DatosInteresado value) {
        this.datosInteresado = value;
    }

    /**
     * Gets the value of the datosRepresentado property.
     * 
     * @return
     *     possible object is
     *     {@link DatosRepresentado }
     *     
     */
    public DatosRepresentado getDatosRepresentado() {
        return datosRepresentado;
    }

    /**
     * Sets the value of the datosRepresentado property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosRepresentado }
     *     
     */
    public void setDatosRepresentado(DatosRepresentado value) {
        this.datosRepresentado = value;
    }

    /**
     * Gets the value of the datosAsunto property.
     * 
     * @return
     *     possible object is
     *     {@link DatosAsunto }
     *     
     */
    public DatosAsunto getDatosAsunto() {
        return datosAsunto;
    }

    /**
     * Sets the value of the datosAsunto property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosAsunto }
     *     
     */
    public void setDatosAsunto(DatosAsunto value) {
        this.datosAsunto = value;
    }

    /**
     * Gets the value of the documentos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Documentos }{@code >}
     *     
     */
    public JAXBElement<Documentos> getDocumentos() {
        return documentos;
    }

    /**
     * Sets the value of the documentos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Documentos }{@code >}
     *     
     */
    public void setDocumentos(JAXBElement<Documentos> value) {
        this.documentos = ((JAXBElement<Documentos> ) value);
    }

}
