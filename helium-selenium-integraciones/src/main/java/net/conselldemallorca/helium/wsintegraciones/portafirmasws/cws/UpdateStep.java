
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para UpdateStep complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="UpdateStep">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.indra.es/portafirmasws/cws}Step">
 *       &lt;sequence>
 *         &lt;element name="signers" type="{http://www.indra.es/portafirmasws/cws}Signers"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateStep", propOrder = {
    "signers"
})
public class UpdateStep
    extends Step
{

    @XmlElement(required = true)
    protected Signers signers;

    /**
     * Obtiene el valor de la propiedad signers.
     * 
     * @return
     *     possible object is
     *     {@link Signers }
     *     
     */
    public Signers getSigners() {
        return signers;
    }

    /**
     * Define el valor de la propiedad signers.
     * 
     * @param value
     *     allowed object is
     *     {@link Signers }
     *     
     */
    public void setSigners(Signers value) {
        this.signers = value;
    }

}
