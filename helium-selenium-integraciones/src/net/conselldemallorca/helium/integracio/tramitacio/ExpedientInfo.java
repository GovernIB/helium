
package net.conselldemallorca.helium.integracio.tramitacio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para expedientInfo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
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
 *         &lt;element name="expedientTipusCodi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="geoPosX" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="geoPosY" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="geoReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "expedientTipusCodi",
    "geoPosX",
    "geoPosY",
    "geoReferencia",
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
    protected String expedientTipusCodi;
    protected Double geoPosX;
    protected Double geoPosY;
    protected String geoReferencia;
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
     * Obtiene el valor de la propiedad autenticat.
     * 
     */
    public boolean isAutenticat() {
        return autenticat;
    }

    /**
     * Define el valor de la propiedad autenticat.
     * 
     */
    public void setAutenticat(boolean value) {
        this.autenticat = value;
    }

    /**
     * Obtiene el valor de la propiedad avisosEmail.
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
     * Define el valor de la propiedad avisosEmail.
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
     * Obtiene el valor de la propiedad avisosHabilitats.
     * 
     */
    public boolean isAvisosHabilitats() {
        return avisosHabilitats;
    }

    /**
     * Define el valor de la propiedad avisosHabilitats.
     * 
     */
    public void setAvisosHabilitats(boolean value) {
        this.avisosHabilitats = value;
    }

    /**
     * Obtiene el valor de la propiedad avisosMobil.
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
     * Define el valor de la propiedad avisosMobil.
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
     * Obtiene el valor de la propiedad comentari.
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
     * Define el valor de la propiedad comentari.
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
     * Obtiene el valor de la propiedad dataFi.
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
     * Define el valor de la propiedad dataFi.
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
     * Obtiene el valor de la propiedad dataInici.
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
     * Define el valor de la propiedad dataInici.
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
     * Obtiene el valor de la propiedad entornCodi.
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
     * Define el valor de la propiedad entornCodi.
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
     * Obtiene el valor de la propiedad estatCodi.
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
     * Define el valor de la propiedad estatCodi.
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
     * Obtiene el valor de la propiedad expedientTipusCodi.
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
     * Define el valor de la propiedad expedientTipusCodi.
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
     * Obtiene el valor de la propiedad geoPosX.
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
     * Define el valor de la propiedad geoPosX.
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
     * Obtiene el valor de la propiedad geoPosY.
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
     * Define el valor de la propiedad geoPosY.
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
     * Obtiene el valor de la propiedad geoReferencia.
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
     * Define el valor de la propiedad geoReferencia.
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
     * Obtiene el valor de la propiedad idioma.
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
     * Define el valor de la propiedad idioma.
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
     * Obtiene el valor de la propiedad infoAturat.
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
     * Define el valor de la propiedad infoAturat.
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
     * Obtiene el valor de la propiedad iniciadorCodi.
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
     * Define el valor de la propiedad iniciadorCodi.
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
     * Obtiene el valor de la propiedad iniciadorTipus.
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
     * Define el valor de la propiedad iniciadorTipus.
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
     * Obtiene el valor de la propiedad interessatNif.
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
     * Define el valor de la propiedad interessatNif.
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
     * Obtiene el valor de la propiedad interessatNom.
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
     * Define el valor de la propiedad interessatNom.
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
     * Obtiene el valor de la propiedad notificacioTelematicaHabilitada.
     * 
     */
    public boolean isNotificacioTelematicaHabilitada() {
        return notificacioTelematicaHabilitada;
    }

    /**
     * Define el valor de la propiedad notificacioTelematicaHabilitada.
     * 
     */
    public void setNotificacioTelematicaHabilitada(boolean value) {
        this.notificacioTelematicaHabilitada = value;
    }

    /**
     * Obtiene el valor de la propiedad numero.
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
     * Define el valor de la propiedad numero.
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
     * Obtiene el valor de la propiedad numeroDefault.
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
     * Define el valor de la propiedad numeroDefault.
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
     * Obtiene el valor de la propiedad processInstanceId.
     * 
     */
    public long getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * Define el valor de la propiedad processInstanceId.
     * 
     */
    public void setProcessInstanceId(long value) {
        this.processInstanceId = value;
    }

    /**
     * Obtiene el valor de la propiedad registreData.
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
     * Define el valor de la propiedad registreData.
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
     * Obtiene el valor de la propiedad registreNumero.
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
     * Define el valor de la propiedad registreNumero.
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
     * Obtiene el valor de la propiedad representantNif.
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
     * Define el valor de la propiedad representantNif.
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
     * Obtiene el valor de la propiedad representantNom.
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
     * Define el valor de la propiedad representantNom.
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
     * Obtiene el valor de la propiedad responsableCodi.
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
     * Define el valor de la propiedad responsableCodi.
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
     * Obtiene el valor de la propiedad titol.
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
     * Define el valor de la propiedad titol.
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
     * Obtiene el valor de la propiedad tramitExpedientClau.
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
     * Define el valor de la propiedad tramitExpedientClau.
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
     * Obtiene el valor de la propiedad tramitExpedientIdentificador.
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
     * Define el valor de la propiedad tramitExpedientIdentificador.
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
     * Obtiene el valor de la propiedad tramitadorNif.
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
     * Define el valor de la propiedad tramitadorNif.
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
     * Obtiene el valor de la propiedad tramitadorNom.
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
     * Define el valor de la propiedad tramitadorNom.
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
     * Obtiene el valor de la propiedad unitatAdministrativa.
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
     * Define el valor de la propiedad unitatAdministrativa.
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
