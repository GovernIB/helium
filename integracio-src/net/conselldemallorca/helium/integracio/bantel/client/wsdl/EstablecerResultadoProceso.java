
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="numeroEntrada" type="{urn:es:caib:bantel:ws:v1:model:ReferenciaEntrada}ReferenciaEntrada"/>
 *         &lt;element name="resultado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultadoProcesamiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "numeroEntrada",
    "resultado",
    "resultadoProcesamiento"
})
@XmlRootElement(name = "establecerResultadoProceso")
public class EstablecerResultadoProceso {

    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected ReferenciaEntrada numeroEntrada;
    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected String resultado;
    @XmlElement(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", required = true)
    protected String resultadoProcesamiento;

    /**
     * Gets the value of the numeroEntrada property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciaEntrada }
     *     
     */
    public ReferenciaEntrada getNumeroEntrada() {
        return numeroEntrada;
    }

    /**
     * Sets the value of the numeroEntrada property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciaEntrada }
     *     
     */
    public void setNumeroEntrada(ReferenciaEntrada value) {
        this.numeroEntrada = value;
    }

    /**
     * Gets the value of the resultado property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultado() {
        return resultado;
    }

    /**
     * Sets the value of the resultado property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultado(String value) {
        this.resultado = value;
    }

    /**
     * Gets the value of the resultadoProcesamiento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultadoProcesamiento() {
        return resultadoProcesamiento;
    }

    /**
     * Sets the value of the resultadoProcesamiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultadoProcesamiento(String value) {
        this.resultadoProcesamiento = value;
    }

}
