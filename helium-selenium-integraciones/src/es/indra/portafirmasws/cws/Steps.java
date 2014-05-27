
package es.indra.portafirmasws.cws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Steps complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Steps">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sign-mode" type="{http://www.indra.es/portafirmasws/cws}SignModeEnum" minOccurs="0"/>
 *         &lt;element name="step" type="{http://www.indra.es/portafirmasws/cws}Step" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Steps", propOrder = {
    "signMode",
    "step"
})
public class Steps {

    @XmlElementRef(name = "sign-mode", type = JAXBElement.class)
    protected JAXBElement<SignModeEnum> signMode;
    @XmlElement(required = true)
    protected List<Step> step;

    /**
     * Obtiene el valor de la propiedad signMode.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SignModeEnum }{@code >}
     *     
     */
    public JAXBElement<SignModeEnum> getSignMode() {
        return signMode;
    }

    /**
     * Define el valor de la propiedad signMode.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SignModeEnum }{@code >}
     *     
     */
    public void setSignMode(JAXBElement<SignModeEnum> value) {
        this.signMode = value;
    }

    /**
     * Gets the value of the step property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the step property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Step }
     * 
     * 
     */
    public List<Step> getStep() {
        if (step == null) {
            step = new ArrayList<Step>();
        }
        return this.step;
    }

}
