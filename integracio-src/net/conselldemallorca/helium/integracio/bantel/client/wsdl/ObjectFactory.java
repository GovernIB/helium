
package net.conselldemallorca.helium.integracio.bantel.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.sistra.wsdl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DocumentoBTEPresentacionTelematica_QNAME = new QName("", "presentacionTelematica");
    private final static QName _DocumentoBTEPresentacionPresencial_QNAME = new QName("", "presentacionPresencial");
    private final static QName _ReferenciaEntradaClaveAcceso_QNAME = new QName("", "claveAcceso");
    private final static QName _Fault_QNAME = new QName("urn:es:caib:bantel:ws:v1:model:BackofficeFacade", "fault");
    private final static QName _FirmaWSFormato_QNAME = new QName("", "formato");
    private final static QName _TramiteBTEHabilitarNotificacionTelematica_QNAME = new QName("", "habilitarNotificacionTelematica");
    private final static QName _TramiteBTEUsuarioSeycon_QNAME = new QName("", "usuarioSeycon");
    private final static QName _TramiteBTETipoConfirmacionPreregistro_QNAME = new QName("", "tipoConfirmacionPreregistro");
    private final static QName _TramiteBTEUsuarioNombre_QNAME = new QName("", "usuarioNombre");
    private final static QName _TramiteBTERepresentadoNombre_QNAME = new QName("", "representadoNombre");
    private final static QName _TramiteBTEAvisoEmail_QNAME = new QName("", "avisoEmail");
    private final static QName _TramiteBTENumeroPreregistro_QNAME = new QName("", "numeroPreregistro");
    private final static QName _TramiteBTEAvisoSMS_QNAME = new QName("", "avisoSMS");
    private final static QName _TramiteBTEHabilitarAvisos_QNAME = new QName("", "habilitarAvisos");
    private final static QName _TramiteBTEFechaPreregistro_QNAME = new QName("", "fechaPreregistro");
    private final static QName _TramiteBTEUsuarioNif_QNAME = new QName("", "usuarioNif");
    private final static QName _TramiteBTERepresentadoNif_QNAME = new QName("", "representadoNif");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.sistra.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FirmasWS }
     * 
     */
    public FirmasWS createFirmasWS() {
        return new FirmasWS();
    }

    /**
     * Create an instance of {@link DocumentosBTE }
     * 
     */
    public DocumentosBTE createDocumentosBTE() {
        return new DocumentosBTE();
    }

    /**
     * Create an instance of {@link DocumentoBTE }
     * 
     */
    public DocumentoBTE createDocumentoBTE() {
        return new DocumentoBTE();
    }

    /**
     * Create an instance of {@link ObtenerEntradaResponse }
     * 
     */
    public ObtenerEntradaResponse createObtenerEntradaResponse() {
        return new ObtenerEntradaResponse();
    }

    /**
     * Create an instance of {@link ObtenerEntrada }
     * 
     */
    public ObtenerEntrada createObtenerEntrada() {
        return new ObtenerEntrada();
    }

    /**
     * Create an instance of {@link DatosDocumentoPresencial }
     * 
     */
    public DatosDocumentoPresencial createDatosDocumentoPresencial() {
        return new DatosDocumentoPresencial();
    }

    /**
     * Create an instance of {@link ReferenciaEntrada }
     * 
     */
    public ReferenciaEntrada createReferenciaEntrada() {
        return new ReferenciaEntrada();
    }

    /**
     * Create an instance of {@link EstablecerResultadoProceso }
     * 
     */
    public EstablecerResultadoProceso createEstablecerResultadoProceso() {
        return new EstablecerResultadoProceso();
    }

    /**
     * Create an instance of {@link BackofficeFacadeException }
     * 
     */
    public BackofficeFacadeException createBackofficeFacadeException() {
        return new BackofficeFacadeException();
    }

    /**
     * Create an instance of {@link FirmaWS }
     * 
     */
    public FirmaWS createFirmaWS() {
        return new FirmaWS();
    }

    /**
     * Create an instance of {@link DatosDocumentoTelematico }
     * 
     */
    public DatosDocumentoTelematico createDatosDocumentoTelematico() {
        return new DatosDocumentoTelematico();
    }

    /**
     * Create an instance of {@link EstablecerResultadoProcesoResponse }
     * 
     */
    public EstablecerResultadoProcesoResponse createEstablecerResultadoProcesoResponse() {
        return new EstablecerResultadoProcesoResponse();
    }

    /**
     * Create an instance of {@link ReferenciasEntrada }
     * 
     */
    public ReferenciasEntrada createReferenciasEntrada() {
        return new ReferenciasEntrada();
    }

    /**
     * Create an instance of {@link ObtenerNumerosEntradasResponse }
     * 
     */
    public ObtenerNumerosEntradasResponse createObtenerNumerosEntradasResponse() {
        return new ObtenerNumerosEntradasResponse();
    }

    /**
     * Create an instance of {@link TramiteBTE }
     * 
     */
    public TramiteBTE createTramiteBTE() {
        return new TramiteBTE();
    }

    /**
     * Create an instance of {@link ObtenerNumerosEntradas }
     * 
     */
    public ObtenerNumerosEntradas createObtenerNumerosEntradas() {
        return new ObtenerNumerosEntradas();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatosDocumentoTelematico }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "presentacionTelematica", scope = DocumentoBTE.class)
    public JAXBElement<DatosDocumentoTelematico> createDocumentoBTEPresentacionTelematica(DatosDocumentoTelematico value) {
        return new JAXBElement<DatosDocumentoTelematico>(_DocumentoBTEPresentacionTelematica_QNAME, DatosDocumentoTelematico.class, DocumentoBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DatosDocumentoPresencial }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "presentacionPresencial", scope = DocumentoBTE.class)
    public JAXBElement<DatosDocumentoPresencial> createDocumentoBTEPresentacionPresencial(DatosDocumentoPresencial value) {
        return new JAXBElement<DatosDocumentoPresencial>(_DocumentoBTEPresentacionPresencial_QNAME, DatosDocumentoPresencial.class, DocumentoBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "claveAcceso", scope = ReferenciaEntrada.class)
    public JAXBElement<String> createReferenciaEntradaClaveAcceso(String value) {
        return new JAXBElement<String>(_ReferenciaEntradaClaveAcceso_QNAME, String.class, ReferenciaEntrada.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BackofficeFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:bantel:ws:v1:model:BackofficeFacade", name = "fault")
    public JAXBElement<BackofficeFacadeException> createFault(BackofficeFacadeException value) {
        return new JAXBElement<BackofficeFacadeException>(_Fault_QNAME, BackofficeFacadeException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "formato", scope = FirmaWS.class)
    public JAXBElement<String> createFirmaWSFormato(String value) {
        return new JAXBElement<String>(_FirmaWSFormato_QNAME, String.class, FirmaWS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "habilitarNotificacionTelematica", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEHabilitarNotificacionTelematica(String value) {
        return new JAXBElement<String>(_TramiteBTEHabilitarNotificacionTelematica_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioSeycon", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioSeycon(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioSeycon_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tipoConfirmacionPreregistro", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTETipoConfirmacionPreregistro(String value) {
        return new JAXBElement<String>(_TramiteBTETipoConfirmacionPreregistro_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioNombre", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioNombre(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioNombre_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "representadoNombre", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTERepresentadoNombre(String value) {
        return new JAXBElement<String>(_TramiteBTERepresentadoNombre_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoEmail", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEAvisoEmail(String value) {
        return new JAXBElement<String>(_TramiteBTEAvisoEmail_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "numeroPreregistro", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTENumeroPreregistro(String value) {
        return new JAXBElement<String>(_TramiteBTENumeroPreregistro_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoSMS", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEAvisoSMS(String value) {
        return new JAXBElement<String>(_TramiteBTEAvisoSMS_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "habilitarAvisos", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEHabilitarAvisos(String value) {
        return new JAXBElement<String>(_TramiteBTEHabilitarAvisos_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaPreregistro", scope = TramiteBTE.class)
    public JAXBElement<XMLGregorianCalendar> createTramiteBTEFechaPreregistro(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_TramiteBTEFechaPreregistro_QNAME, XMLGregorianCalendar.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioNif", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTEUsuarioNif(String value) {
        return new JAXBElement<String>(_TramiteBTEUsuarioNif_QNAME, String.class, TramiteBTE.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "representadoNif", scope = TramiteBTE.class)
    public JAXBElement<String> createTramiteBTERepresentadoNif(String value) {
        return new JAXBElement<String>(_TramiteBTERepresentadoNif_QNAME, String.class, TramiteBTE.class, value);
    }

}
