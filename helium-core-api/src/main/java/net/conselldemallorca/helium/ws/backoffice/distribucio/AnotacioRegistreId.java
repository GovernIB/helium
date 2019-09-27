
package net.conselldemallorca.helium.ws.backoffice.distribucio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anotacioRegistreId complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="anotacioRegistreId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clauAcces" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="indetificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "anotacioRegistreId", propOrder = {
    "clauAcces",
    "indetificador"
})
public class AnotacioRegistreId {

    protected String clauAcces;
    protected String indetificador;

    /**
     * Obtiene el valor de la propiedad clauAcces.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClauAcces() {
        return clauAcces;
    }

    /**
     * Define el valor de la propiedad clauAcces.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClauAcces(String value) {
        this.clauAcces = value;
    }

    /**
     * Obtiene el valor de la propiedad indetificador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndetificador() {
        return indetificador;
    }

    /**
     * Define el valor de la propiedad indetificador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndetificador(String value) {
        this.indetificador = value;
    }

}
