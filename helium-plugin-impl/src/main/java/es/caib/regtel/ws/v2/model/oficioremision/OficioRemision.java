
package es.caib.regtel.ws.v2.model.oficioremision;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OficioRemision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OficioRemision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="titulo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="texto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tramiteSubsanacion" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="descripcionTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="versionTramite" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="parametrosTramite" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{urn:es:caib:regtel:ws:v2:model:OficioRemision}parametroTramite" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OficioRemision", propOrder = {
    "titulo",
    "texto",
    "tramiteSubsanacion"
})
public class OficioRemision {

    @XmlElement(required = true)
    protected String titulo;
    @XmlElement(required = true)
    protected String texto;
    @XmlElementRef(name = "tramiteSubsanacion", type = JAXBElement.class)
    protected JAXBElement<OficioRemision.TramiteSubsanacion> tramiteSubsanacion;

    /**
     * Gets the value of the titulo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Sets the value of the titulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitulo(String value) {
        this.titulo = value;
    }

    /**
     * Gets the value of the texto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Sets the value of the texto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTexto(String value) {
        this.texto = value;
    }

    /**
     * Gets the value of the tramiteSubsanacion property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion }{@code >}
     *     
     */
    public JAXBElement<OficioRemision.TramiteSubsanacion> getTramiteSubsanacion() {
        return tramiteSubsanacion;
    }

    /**
     * Sets the value of the tramiteSubsanacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion }{@code >}
     *     
     */
    public void setTramiteSubsanacion(JAXBElement<OficioRemision.TramiteSubsanacion> value) {
        this.tramiteSubsanacion = ((JAXBElement<OficioRemision.TramiteSubsanacion> ) value);
    }


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
     *         &lt;element name="descripcionTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="versionTramite" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="parametrosTramite" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{urn:es:caib:regtel:ws:v2:model:OficioRemision}parametroTramite" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
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
        "descripcionTramite",
        "identificadorTramite",
        "versionTramite",
        "parametrosTramite"
    })
    public static class TramiteSubsanacion {

        @XmlElement(required = true)
        protected String descripcionTramite;
        @XmlElement(required = true)
        protected String identificadorTramite;
        protected int versionTramite;
        @XmlElementRef(name = "parametrosTramite", type = JAXBElement.class)
        protected JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite> parametrosTramite;

        /**
         * Gets the value of the descripcionTramite property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescripcionTramite() {
            return descripcionTramite;
        }

        /**
         * Sets the value of the descripcionTramite property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescripcionTramite(String value) {
            this.descripcionTramite = value;
        }

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
         * Gets the value of the versionTramite property.
         * 
         */
        public int getVersionTramite() {
            return versionTramite;
        }

        /**
         * Sets the value of the versionTramite property.
         * 
         */
        public void setVersionTramite(int value) {
            this.versionTramite = value;
        }

        /**
         * Gets the value of the parametrosTramite property.
         * 
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion.ParametrosTramite }{@code >}
         *     
         */
        public JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite> getParametrosTramite() {
            return parametrosTramite;
        }

        /**
         * Sets the value of the parametrosTramite property.
         * 
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion.ParametrosTramite }{@code >}
         *     
         */
        public void setParametrosTramite(JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite> value) {
            this.parametrosTramite = ((JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite> ) value);
        }


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
         *         &lt;element ref="{urn:es:caib:regtel:ws:v2:model:OficioRemision}parametroTramite" maxOccurs="unbounded"/>
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
            "parametroTramite"
        })
        public static class ParametrosTramite {

            @XmlElement(namespace = "urn:es:caib:regtel:ws:v2:model:OficioRemision", required = true)
            protected List<ParametroTramite> parametroTramite;

            /**
             * Gets the value of the parametroTramite property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the parametroTramite property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getParametroTramite().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ParametroTramite }
             * 
             * 
             */
            public List<ParametroTramite> getParametroTramite() {
                if (parametroTramite == null) {
                    parametroTramite = new ArrayList<ParametroTramite>();
                }
                return this.parametroTramite;
            }

        }

    }

}
