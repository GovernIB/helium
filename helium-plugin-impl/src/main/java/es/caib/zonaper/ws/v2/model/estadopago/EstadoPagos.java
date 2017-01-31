
package es.caib.zonaper.ws.v2.model.estadopago;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EstadoPagos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EstadoPagos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="estadoTramite" type="{urn:es:caib:zonaper:ws:v2:model:EstadoPago}TipoEstadoTramite"/>
 *         &lt;element name="estadoPago" type="{urn:es:caib:zonaper:ws:v2:model:EstadoPago}EstadoPago" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EstadoPagos", propOrder = {
    "estadoTramite",
    "estadoPago"
})
public class EstadoPagos {

    @XmlElement(required = true)
    protected TipoEstadoTramite estadoTramite;
    protected List<EstadoPago> estadoPago;

    /**
     * Gets the value of the estadoTramite property.
     * 
     * @return
     *     possible object is
     *     {@link TipoEstadoTramite }
     *     
     */
    public TipoEstadoTramite getEstadoTramite() {
        return estadoTramite;
    }

    /**
     * Sets the value of the estadoTramite property.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoEstadoTramite }
     *     
     */
    public void setEstadoTramite(TipoEstadoTramite value) {
        this.estadoTramite = value;
    }

    /**
     * Gets the value of the estadoPago property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the estadoPago property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEstadoPago().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EstadoPago }
     * 
     * 
     */
    public List<EstadoPago> getEstadoPago() {
        if (estadoPago == null) {
            estadoPago = new ArrayList<EstadoPago>();
        }
        return this.estadoPago;
    }

}
