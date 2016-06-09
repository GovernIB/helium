
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for expedientInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="expedientInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="autenticat" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="avisosEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="avisosHabilitats" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="avisosMobil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comentari" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataFi" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="dataInici" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="entornCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estatCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estatNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientTipusCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="expedientTipusNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="geoPosX" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="geoPosY" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="geoReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="infoAturat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iniciadorCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iniciadorTipus" type="{http://tramitacio.integracio.helium.conselldemallorca.net/}iniciadorTipus" minOccurs="0"/>
 *         &lt;element name="interessatNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interessatNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="notificacioTelematicaHabilitada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroDefault" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processInstanceId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="registreData" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="registreNumero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representantNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="representantNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsableCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tramitExpedientClau" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tramitExpedientIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tramitadorNif" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tramitadorNom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unitatAdministrativa" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "expedientInfo", propOrder = {
    "autenticat",
    "avisosEmail",
    "avisosHabilitats",
    "avisosMobil",
    "comentari",
    "dataFi",
    "dataInici",
    "entornCodi",
    "estatCodi",
    "estatNom",
    "expedientTipusCodi",
    "expedientTipusNom",
    "geoPosX",
    "geoPosY",
    "geoReferencia",
    "identificador",
    "idioma",
    "infoAturat",
    "iniciadorCodi",
    "iniciadorTipus",
    "interessatNif",
    "interessatNom",
    "notificacioTelematicaHabilitada",
    "numero",
    "numeroDefault",
    "processInstanceId",
    "registreData",
    "registreNumero",
    "representantNif",
    "representantNom",
    "responsableCodi",
    "titol",
    "tramitExpedientClau",
    "tramitExpedientIdentificador",
    "tramitadorNif",
    "tramitadorNom",
    "unitatAdministrativa"
})
public class ExpedientInfo {

    protected boolean autenticat;
    protected String avisosEmail;
    protected boolean avisosHabilitats;
    protected String avisosMobil;
    protected String comentari;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataFi;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataInici;
    protected String entornCodi;
    protected String estatCodi;
    protected String estatNom;
    protected String expedientTipusCodi;
    protected String expedientTipusNom;
    protected Double geoPosX;
    protected Double geoPosY;
    protected String geoReferencia;
    protected String identificador;
    protected String idioma;
    protected String infoAturat;
    protected String iniciadorCodi;
    protected IniciadorTipus iniciadorTipus;
    protected String interessatNif;
    protected String interessatNom;
    protected boolean notificacioTelematicaHabilitada;
    protected String numero;
    protected String numeroDefault;
    protected long processInstanceId;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar registreData;
    protected String registreNumero;
    protected String representantNif;
    protected String representantNom;
    protected String responsableCodi;
    protected String titol;
    protected String tramitExpedientClau;
    protected String tramitExpedientIdentificador;
    protected String tramitadorNif;
    protected String tramitadorNom;
    protected Long unitatAdministrativa;

    /**
     * Gets the value of the autenticat property.
     * 
     */
    public boolean isAutenticat() {
        return autenticat;
    }

    /**
     * Sets the value of the autenticat property.
     * 
     */
    public void setAutenticat(boolean value) {
        this.autenticat = value;
    }

    /**
     * Gets the value of the avisosEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvisosEmail() {
        return avisosEmail;
    }

    /**
     * Sets the value of the avisosEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvisosEmail(String value) {
        this.avisosEmail = value;
    }

    /**
     * Gets the value of the avisosHabilitats property.
     * 
     */
    public boolean isAvisosHabilitats() {
        return avisosHabilitats;
    }

    /**
     * Sets the value of the avisosHabilitats property.
     * 
     */
    public void setAvisosHabilitats(boolean value) {
        this.avisosHabilitats = value;
    }

    /**
     * Gets the value of the avisosMobil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvisosMobil() {
        return avisosMobil;
    }

    /**
     * Sets the value of the avisosMobil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvisosMobil(String value) {
        this.avisosMobil = value;
    }

    /**
     * Gets the value of the comentari property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComentari() {
        return comentari;
    }

    /**
     * Sets the value of the comentari property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComentari(String value) {
        this.comentari = value;
    }

    /**
     * Gets the value of the dataFi property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFi() {
        return dataFi;
    }

    /**
     * Sets the value of the dataFi property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFi(XMLGregorianCalendar value) {
        this.dataFi = value;
    }

    /**
     * Gets the value of the dataInici property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInici() {
        return dataInici;
    }

    /**
     * Sets the value of the dataInici property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInici(XMLGregorianCalendar value) {
        this.dataInici = value;
    }

    /**
     * Gets the value of the entornCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntornCodi() {
        return entornCodi;
    }

    /**
     * Sets the value of the entornCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntornCodi(String value) {
        this.entornCodi = value;
    }

    /**
     * Gets the value of the estatCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstatCodi() {
        return estatCodi;
    }

    /**
     * Sets the value of the estatCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstatCodi(String value) {
        this.estatCodi = value;
    }

    /**
     * Gets the value of the estatNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstatNom() {
        return estatNom;
    }

    /**
     * Sets the value of the estatNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstatNom(String value) {
        this.estatNom = value;
    }

    /**
     * Gets the value of the expedientTipusCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientTipusCodi() {
        return expedientTipusCodi;
    }

    /**
     * Sets the value of the expedientTipusCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientTipusCodi(String value) {
        this.expedientTipusCodi = value;
    }

    /**
     * Gets the value of the expedientTipusNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpedientTipusNom() {
        return expedientTipusNom;
    }

    /**
     * Sets the value of the expedientTipusNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpedientTipusNom(String value) {
        this.expedientTipusNom = value;
    }

    /**
     * Gets the value of the geoPosX property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGeoPosX() {
        return geoPosX;
    }

    /**
     * Sets the value of the geoPosX property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGeoPosX(Double value) {
        this.geoPosX = value;
    }

    /**
     * Gets the value of the geoPosY property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGeoPosY() {
        return geoPosY;
    }

    /**
     * Sets the value of the geoPosY property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGeoPosY(Double value) {
        this.geoPosY = value;
    }

    /**
     * Gets the value of the geoReferencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeoReferencia() {
        return geoReferencia;
    }

    /**
     * Sets the value of the geoReferencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeoReferencia(String value) {
        this.geoReferencia = value;
    }

    /**
     * Gets the value of the identificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Sets the value of the identificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificador(String value) {
        this.identificador = value;
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
     * Gets the value of the infoAturat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoAturat() {
        return infoAturat;
    }

    /**
     * Sets the value of the infoAturat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoAturat(String value) {
        this.infoAturat = value;
    }

    /**
     * Gets the value of the iniciadorCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIniciadorCodi() {
        return iniciadorCodi;
    }

    /**
     * Sets the value of the iniciadorCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIniciadorCodi(String value) {
        this.iniciadorCodi = value;
    }

    /**
     * Gets the value of the iniciadorTipus property.
     * 
     * @return
     *     possible object is
     *     {@link IniciadorTipus }
     *     
     */
    public IniciadorTipus getIniciadorTipus() {
        return iniciadorTipus;
    }

    /**
     * Sets the value of the iniciadorTipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link IniciadorTipus }
     *     
     */
    public void setIniciadorTipus(IniciadorTipus value) {
        this.iniciadorTipus = value;
    }

    /**
     * Gets the value of the interessatNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteressatNif() {
        return interessatNif;
    }

    /**
     * Sets the value of the interessatNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteressatNif(String value) {
        this.interessatNif = value;
    }

    /**
     * Gets the value of the interessatNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteressatNom() {
        return interessatNom;
    }

    /**
     * Sets the value of the interessatNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteressatNom(String value) {
        this.interessatNom = value;
    }

    /**
     * Gets the value of the notificacioTelematicaHabilitada property.
     * 
     */
    public boolean isNotificacioTelematicaHabilitada() {
        return notificacioTelematicaHabilitada;
    }

    /**
     * Sets the value of the notificacioTelematicaHabilitada property.
     * 
     */
    public void setNotificacioTelematicaHabilitada(boolean value) {
        this.notificacioTelematicaHabilitada = value;
    }

    /**
     * Gets the value of the numero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Sets the value of the numero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Gets the value of the numeroDefault property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDefault() {
        return numeroDefault;
    }

    /**
     * Sets the value of the numeroDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDefault(String value) {
        this.numeroDefault = value;
    }

    /**
     * Gets the value of the processInstanceId property.
     * 
     */
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Sets the value of the processInstanceId property.
     * 
     */
    public void setProcessInstanceId(long value) {
        this.processInstanceId = value;
    }

    /**
     * Gets the value of the registreData property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegistreData() {
        return registreData;
    }

    /**
     * Sets the value of the registreData property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegistreData(XMLGregorianCalendar value) {
        this.registreData = value;
    }

    /**
     * Gets the value of the registreNumero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistreNumero() {
        return registreNumero;
    }

    /**
     * Sets the value of the registreNumero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistreNumero(String value) {
        this.registreNumero = value;
    }

    /**
     * Gets the value of the representantNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepresentantNif() {
        return representantNif;
    }

    /**
     * Sets the value of the representantNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepresentantNif(String value) {
        this.representantNif = value;
    }

    /**
     * Gets the value of the representantNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRepresentantNom() {
        return representantNom;
    }

    /**
     * Sets the value of the representantNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRepresentantNom(String value) {
        this.representantNom = value;
    }

    /**
     * Gets the value of the responsableCodi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsableCodi() {
        return responsableCodi;
    }

    /**
     * Sets the value of the responsableCodi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsableCodi(String value) {
        this.responsableCodi = value;
    }

    /**
     * Gets the value of the titol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitol() {
        return titol;
    }

    /**
     * Sets the value of the titol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitol(String value) {
        this.titol = value;
    }

    /**
     * Gets the value of the tramitExpedientClau property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTramitExpedientClau() {
        return tramitExpedientClau;
    }

    /**
     * Sets the value of the tramitExpedientClau property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTramitExpedientClau(String value) {
        this.tramitExpedientClau = value;
    }

    /**
     * Gets the value of the tramitExpedientIdentificador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTramitExpedientIdentificador() {
        return tramitExpedientIdentificador;
    }

    /**
     * Sets the value of the tramitExpedientIdentificador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTramitExpedientIdentificador(String value) {
        this.tramitExpedientIdentificador = value;
    }

    /**
     * Gets the value of the tramitadorNif property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTramitadorNif() {
        return tramitadorNif;
    }

    /**
     * Sets the value of the tramitadorNif property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTramitadorNif(String value) {
        this.tramitadorNif = value;
    }

    /**
     * Gets the value of the tramitadorNom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTramitadorNom() {
        return tramitadorNom;
    }

    /**
     * Sets the value of the tramitadorNom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTramitadorNom(String value) {
        this.tramitadorNom = value;
    }

    /**
     * Gets the value of the unitatAdministrativa property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUnitatAdministrativa() {
        return unitatAdministrativa;
    }

    /**
     * Sets the value of the unitatAdministrativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUnitatAdministrativa(Long value) {
        this.unitatAdministrativa = value;
    }

}
