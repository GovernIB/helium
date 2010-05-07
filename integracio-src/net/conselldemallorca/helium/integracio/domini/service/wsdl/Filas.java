
package net.conselldemallorca.helium.integracio.domini.service.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filas complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fila" type="{urn:es:caib:sistra:ws:v1:model:ValoresDominio}fila" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filas", namespace = "urn:es:caib:sistra:ws:v1:model:ValoresDominio", propOrder = {
    "fila"
})
public class Filas {

    protected List<Fila> fila;

    /**
     * Gets the value of the fila property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fila property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFila().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Fila }
     * 
     * 
     */
    public List<Fila> getFila() {
        if (fila == null) {
            fila = new ArrayList<Fila>();
        }
        return this.fila;
    }

}
