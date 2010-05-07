
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="procesada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="desde" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="hasta" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identificadorTramite",
    "procesada",
    "desde",
    "hasta"
})
@XmlRootElement(name = "obtenerNumerosEntradas")
public class ObtenerNumerosEntradas {

    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected String identificadorTramite;
    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected String procesada;
    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar desde;
    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar hasta;

    /**
     * Gets the value of the identificadorTramite property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificadorTramite() {
        return identificadorTramite;
    }

    /**
     * Sets the value of the identificadorTramite property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificadorTramite(String value) {
        this.identificadorTramite = value;
    }

    /**
     * Gets the value of the procesada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcesada() {
        return procesada;
    }

    /**
     * Sets the value of the procesada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcesada(String value) {
        this.procesada = value;
    }

    /**
     * Gets the value of the desde property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDesde() {
        return desde;
    }

    /**
     * Sets the value of the desde property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDesde(XMLGregorianCalendar value) {
        this.desde = value;
    }

    /**
     * Gets the value of the hasta property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHasta() {
        return hasta;
    }

    /**
     * Sets the value of the hasta property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHasta(XMLGregorianCalendar value) {
        this.hasta = value;
    }

}
