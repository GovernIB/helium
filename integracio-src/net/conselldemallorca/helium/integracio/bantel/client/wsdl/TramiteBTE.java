
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TramiteBTE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TramiteBTE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroEntrada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoEntrada" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="unidadAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="firmadaDigitalmente" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="procesada" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="identificadorTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="versionTramite" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nivelAutenticacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usuarioSeycon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="descripcionTramite" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoReferenciaRDSAsiento" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRDSAsiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="codigoReferenciaRDSJustificante" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="claveReferenciaRDSJustificante" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroRegistro" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaRegistro" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="numeroPreregistro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaPreregistro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="usuarioNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usuarioNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representadoNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representadoNombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoConfirmacionPreregistro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="habilitarAvisos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisoSMS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisoEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="habilitarNotificacionTelematica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentos" type="{urn:es:caib:bantel:ws:v1:model:DocumentoBTE}DocumentosBTE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TramiteBTE", namespace = "urn:es:caib:bantel:ws:v1:model:TramiteBTE", propOrder = {
    "numeroEntrada",
    "codigoEntrada",
    "unidadAdministrativa",
    "fecha",
    "tipo",
    "firmadaDigitalmente",
    "procesada",
    "identificadorTramite",
    "versionTramite",
    "nivelAutenticacion",
    "usuarioSeycon",
    "descripcionTramite",
    "codigoReferenciaRDSAsiento",
    "claveReferenciaRDSAsiento",
    "codigoReferenciaRDSJustificante",
    "claveReferenciaRDSJustificante",
    "numeroRegistro",
    "fechaRegistro",
    "numeroPreregistro",
    "fechaPreregistro",
    "usuarioNif",
    "usuarioNombre",
    "representadoNif",
    "representadoNombre",
    "idioma",
    "tipoConfirmacionPreregistro",
    "habilitarAvisos",
    "avisoSMS",
    "avisoEmail",
    "habilitarNotificacionTelematica",
    "documentos"
})
public class TramiteBTE {

    @XmlElement(required = true)
    protected String numeroEntrada;
    protected long codigoEntrada;
    protected long unidadAdministrativa;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fecha;
    @XmlElement(required = true)
    protected String tipo;
    protected boolean firmadaDigitalmente;
    @XmlElement(required = true)
    protected String procesada;
    @XmlElement(required = true)
    protected String identificadorTramite;
    protected int versionTramite;
    @XmlElement(required = true)
    protected String nivelAutenticacion;
    @XmlElementRef(name = "usuarioSeycon", type = JAXBElement.class)
    protected JAXBElement<String> usuarioSeycon;
    @XmlElement(required = true)
    protected String descripcionTramite;
    protected long codigoReferenciaRDSAsiento;
    @XmlElement(required = true)
    protected String claveReferenciaRDSAsiento;
    protected long codigoReferenciaRDSJustificante;
    @XmlElement(required = true)
    protected String claveReferenciaRDSJustificante;
    @XmlElement(required = true)
    protected String numeroRegistro;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaRegistro;
    @XmlElementRef(name = "numeroPreregistro", type = JAXBElement.class)
    protected JAXBElement<String> numeroPreregistro;
    @XmlElementRef(name = "fechaPreregistro", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> fechaPreregistro;
    @XmlElementRef(name = "usuarioNif", type = JAXBElement.class)
    protected JAXBElement<String> usuarioNif;
    @XmlElementRef(name = "usuarioNombre", type = JAXBElement.class)
    protected JAXBElement<String> usuarioNombre;
    @XmlElementRef(name = "representadoNif", type = JAXBElement.class)
    protected JAXBElement<String> representadoNif;
    @XmlElementRef(name = "representadoNombre", type = JAXBElement.class)
    protected JAXBElement<String> representadoNombre;
    @XmlElement(required = true)
    protected String idioma;
    @XmlElementRef(name = "tipoConfirmacionPreregistro", type = JAXBElement.class)
    protected JAXBElement<String> tipoConfirmacionPreregistro;
    @XmlElementRef(name = "habilitarAvisos", type = JAXBElement.class)
    protected JAXBElement<String> habilitarAvisos;
    @XmlElementRef(name = "avisoSMS", type = JAXBElement.class)
    protected JAXBElement<String> avisoSMS;
    @XmlElementRef(name = "avisoEmail", type = JAXBElement.class)
    protected JAXBElement<String> avisoEmail;
    @XmlElementRef(name = "habilitarNotificacionTelematica", type = JAXBElement.class)
    protected JAXBElement<String> habilitarNotificacionTelematica;
    @XmlElement(required = true)
    protected DocumentosBTE documentos;

    /**
     * Gets the value of the numeroEntrada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroEntrada() {
        return numeroEntrada;
    }

    /**
     * Sets the value of the numeroEntrada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroEntrada(String value) {
        this.numeroEntrada = value;
    }

    /**
     * Gets the value of the codigoEntrada property.
     * 
     */
    public long getCodigoEntrada() {
        return codigoEntrada;
    }

    /**
     * Sets the value of the codigoEntrada property.
     * 
     */
    public void setCodigoEntrada(long value) {
        this.codigoEntrada = value;
    }

    /**
     * Gets the value of the unidadAdministrativa property.
     * 
     */
    public long getUnidadAdministrativa() {
        return unidadAdministrativa;
    }

    /**
     * Sets the value of the unidadAdministrativa property.
     * 
     */
    public void setUnidadAdministrativa(long value) {
        this.unidadAdministrativa = value;
    }

    /**
     * Gets the value of the fecha property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFecha() {
        return fecha;
    }

    /**
     * Sets the value of the fecha property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFecha(XMLGregorianCalendar value) {
        this.fecha = value;
    }

    /**
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

    /**
     * Gets the value of the firmadaDigitalmente property.
     * 
     */
    public boolean isFirmadaDigitalmente() {
        return firmadaDigitalmente;
    }

    /**
     * Sets the value of the firmadaDigitalmente property.
     * 
     */
    public void setFirmadaDigitalmente(boolean value) {
        this.firmadaDigitalmente = value;
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
     * Gets the value of the nivelAutenticacion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNivelAutenticacion() {
        return nivelAutenticacion;
    }

    /**
     * Sets the value of the nivelAutenticacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNivelAutenticacion(String value) {
        this.nivelAutenticacion = value;
    }

    /**
     * Gets the value of the usuarioSeycon property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioSeycon() {
        return usuarioSeycon;
    }

    /**
     * Sets the value of the usuarioSeycon property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioSeycon(JAXBElement<String> value) {
        this.usuarioSeycon = ((JAXBElement<String> ) value);
    }

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
     * Gets the value of the codigoReferenciaRDSAsiento property.
     * 
     */
    public long getCodigoReferenciaRDSAsiento() {
        return codigoReferenciaRDSAsiento;
    }

    /**
     * Sets the value of the codigoReferenciaRDSAsiento property.
     * 
     */
    public void setCodigoReferenciaRDSAsiento(long value) {
        this.codigoReferenciaRDSAsiento = value;
    }

    /**
     * Gets the value of the claveReferenciaRDSAsiento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRDSAsiento() {
        return claveReferenciaRDSAsiento;
    }

    /**
     * Sets the value of the claveReferenciaRDSAsiento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRDSAsiento(String value) {
        this.claveReferenciaRDSAsiento = value;
    }

    /**
     * Gets the value of the codigoReferenciaRDSJustificante property.
     * 
     */
    public long getCodigoReferenciaRDSJustificante() {
        return codigoReferenciaRDSJustificante;
    }

    /**
     * Sets the value of the codigoReferenciaRDSJustificante property.
     * 
     */
    public void setCodigoReferenciaRDSJustificante(long value) {
        this.codigoReferenciaRDSJustificante = value;
    }

    /**
     * Gets the value of the claveReferenciaRDSJustificante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveReferenciaRDSJustificante() {
        return claveReferenciaRDSJustificante;
    }

    /**
     * Sets the value of the claveReferenciaRDSJustificante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveReferenciaRDSJustificante(String value) {
        this.claveReferenciaRDSJustificante = value;
    }

    /**
     * Gets the value of the numeroRegistro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    /**
     * Sets the value of the numeroRegistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroRegistro(String value) {
        this.numeroRegistro = value;
    }

    /**
     * Gets the value of the fechaRegistro property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Sets the value of the fechaRegistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaRegistro(XMLGregorianCalendar value) {
        this.fechaRegistro = value;
    }

    /**
     * Gets the value of the numeroPreregistro property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getNumeroPreregistro() {
        return numeroPreregistro;
    }

    /**
     * Sets the value of the numeroPreregistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setNumeroPreregistro(JAXBElement<String> value) {
        this.numeroPreregistro = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the fechaPreregistro property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getFechaPreregistro() {
        return fechaPreregistro;
    }

    /**
     * Sets the value of the fechaPreregistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setFechaPreregistro(JAXBElement<XMLGregorianCalendar> value) {
        this.fechaPreregistro = ((JAXBElement<XMLGregorianCalendar> ) value);
    }

    /**
     * Gets the value of the usuarioNif property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioNif() {
        return usuarioNif;
    }

    /**
     * Sets the value of the usuarioNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioNif(JAXBElement<String> value) {
        this.usuarioNif = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the usuarioNombre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getUsuarioNombre() {
        return usuarioNombre;
    }

    /**
     * Sets the value of the usuarioNombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setUsuarioNombre(JAXBElement<String> value) {
        this.usuarioNombre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the representadoNif property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepresentadoNif() {
        return representadoNif;
    }

    /**
     * Sets the value of the representadoNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepresentadoNif(JAXBElement<String> value) {
        this.representadoNif = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the representadoNombre property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRepresentadoNombre() {
        return representadoNombre;
    }

    /**
     * Sets the value of the representadoNombre property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRepresentadoNombre(JAXBElement<String> value) {
        this.representadoNombre = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the idioma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Sets the value of the idioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Gets the value of the tipoConfirmacionPreregistro property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getTipoConfirmacionPreregistro() {
        return tipoConfirmacionPreregistro;
    }

    /**
     * Sets the value of the tipoConfirmacionPreregistro property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setTipoConfirmacionPreregistro(JAXBElement<String> value) {
        this.tipoConfirmacionPreregistro = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the habilitarAvisos property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHabilitarAvisos() {
        return habilitarAvisos;
    }

    /**
     * Sets the value of the habilitarAvisos property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHabilitarAvisos(JAXBElement<String> value) {
        this.habilitarAvisos = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the avisoSMS property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoSMS() {
        return avisoSMS;
    }

    /**
     * Sets the value of the avisoSMS property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoSMS(JAXBElement<String> value) {
        this.avisoSMS = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the avisoEmail property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAvisoEmail() {
        return avisoEmail;
    }

    /**
     * Sets the value of the avisoEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAvisoEmail(JAXBElement<String> value) {
        this.avisoEmail = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the habilitarNotificacionTelematica property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getHabilitarNotificacionTelematica() {
        return habilitarNotificacionTelematica;
    }

    /**
     * Sets the value of the habilitarNotificacionTelematica property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setHabilitarNotificacionTelematica(JAXBElement<String> value) {
        this.habilitarNotificacionTelematica = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the documentos property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentosBTE }
     *     
     */
    public DocumentosBTE getDocumentos() {
        return documentos;
    }

    /**
     * Sets the value of the documentos property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentosBTE }
     *     
     */
    public void setDocumentos(DocumentosBTE value) {
        this.documentos = value;
    }

}
