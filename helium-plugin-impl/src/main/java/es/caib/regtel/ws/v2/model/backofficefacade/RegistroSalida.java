
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import es.caib.regtel.ws.v2.model.datosregistrosalida.DatosRegistroSalida;


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
 *         &lt;element name="notificacion" type="{urn:es:caib:regtel:ws:v2:model:DatosRegistroSalida}DatosRegistroSalida"/>
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
    "notificacion"
})
@XmlRootElement(name = "registroSalida")
public class RegistroSalida {

    @XmlElement(required = true)
    protected DatosRegistroSalida notificacion;

    /**
     * Gets the value of the notificacion property.
     * 
     * @return
     *     possible object is
     *     {@link DatosRegistroSalida }
     *     
     */
    public DatosRegistroSalida getNotificacion() {
        return notificacion;
    }

    /**
     * Sets the value of the notificacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosRegistroSalida }
     *     
     */
    public void setNotificacion(DatosRegistroSalida value) {
        this.notificacion = value;
    }

}
