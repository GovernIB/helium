
package net.conselldemallorca.helium.integracio.redose.client.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.redose.wsdl package. 
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

    private final static QName _Fault_QNAME = new QName("urn:es:caib:redose:ws:v1:model:BackofficeFacade", "fault");
    private final static QName _FirmaWSFormato_QNAME = new QName("", "formato");
    private final static QName _DocumentoRDSReferenciaRDS_QNAME = new QName("", "referenciaRDS");
    private final static QName _DocumentoRDSFirmas_QNAME = new QName("", "firmas");
    private final static QName _DocumentoRDSHashFichero_QNAME = new QName("", "hashFichero");
    private final static QName _DocumentoRDSNif_QNAME = new QName("", "nif");
    private final static QName _DocumentoRDSPlantilla_QNAME = new QName("", "plantilla");
    private final static QName _DocumentoRDSEstructurado_QNAME = new QName("", "estructurado");
    private final static QName _DocumentoRDSUrlVerificacion_QNAME = new QName("", "urlVerificacion");
    private final static QName _DocumentoRDSUsuarioSeycon_QNAME = new QName("", "usuarioSeycon");
    private final static QName _DocumentoRDSFechaRDS_QNAME = new QName("", "fechaRDS");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.redose.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConsultarDocumento }
     * 
     */
    public ConsultarDocumento createConsultarDocumento() {
        return new ConsultarDocumento();
    }

    /**
     * Create an instance of {@link ReferenciaRDS }
     * 
     */
    public ReferenciaRDS createReferenciaRDS() {
        return new ReferenciaRDS();
    }

    /**
     * Create an instance of {@link ConsultarDocumentoResponse }
     * 
     */
    public ConsultarDocumentoResponse createConsultarDocumentoResponse() {
        return new ConsultarDocumentoResponse();
    }

    /**
     * Create an instance of {@link FirmasWS }
     * 
     */
    public FirmasWS createFirmasWS() {
        return new FirmasWS();
    }

    /**
     * Create an instance of {@link InsertarDocumentoResponse }
     * 
     */
    public InsertarDocumentoResponse createInsertarDocumentoResponse() {
        return new InsertarDocumentoResponse();
    }

    /**
     * Create an instance of {@link FirmaWS }
     * 
     */
    public FirmaWS createFirmaWS() {
        return new FirmaWS();
    }

    /**
     * Create an instance of {@link InsertarDocumento }
     * 
     */
    public InsertarDocumento createInsertarDocumento() {
        return new InsertarDocumento();
    }

    /**
     * Create an instance of {@link BackofficeFacadeException }
     * 
     */
    public BackofficeFacadeException createBackofficeFacadeException() {
        return new BackofficeFacadeException();
    }

    /**
     * Create an instance of {@link DocumentoRDS }
     * 
     */
    public DocumentoRDS createDocumentoRDS() {
        return new DocumentoRDS();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BackofficeFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:redose:ws:v1:model:BackofficeFacade", name = "fault")
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
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "referenciaRDS", scope = DocumentoRDS.class)
    public JAXBElement<ReferenciaRDS> createDocumentoRDSReferenciaRDS(ReferenciaRDS value) {
        return new JAXBElement<ReferenciaRDS>(_DocumentoRDSReferenciaRDS_QNAME, ReferenciaRDS.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmasWS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "firmas", scope = DocumentoRDS.class)
    public JAXBElement<FirmasWS> createDocumentoRDSFirmas(FirmasWS value) {
        return new JAXBElement<FirmasWS>(_DocumentoRDSFirmas_QNAME, FirmasWS.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "hashFichero", scope = DocumentoRDS.class)
    public JAXBElement<String> createDocumentoRDSHashFichero(String value) {
        return new JAXBElement<String>(_DocumentoRDSHashFichero_QNAME, String.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nif", scope = DocumentoRDS.class)
    public JAXBElement<String> createDocumentoRDSNif(String value) {
        return new JAXBElement<String>(_DocumentoRDSNif_QNAME, String.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "plantilla", scope = DocumentoRDS.class)
    public JAXBElement<String> createDocumentoRDSPlantilla(String value) {
        return new JAXBElement<String>(_DocumentoRDSPlantilla_QNAME, String.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "estructurado", scope = DocumentoRDS.class)
    public JAXBElement<Boolean> createDocumentoRDSEstructurado(Boolean value) {
        return new JAXBElement<Boolean>(_DocumentoRDSEstructurado_QNAME, Boolean.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "urlVerificacion", scope = DocumentoRDS.class)
    public JAXBElement<String> createDocumentoRDSUrlVerificacion(String value) {
        return new JAXBElement<String>(_DocumentoRDSUrlVerificacion_QNAME, String.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "usuarioSeycon", scope = DocumentoRDS.class)
    public JAXBElement<String> createDocumentoRDSUsuarioSeycon(String value) {
        return new JAXBElement<String>(_DocumentoRDSUsuarioSeycon_QNAME, String.class, DocumentoRDS.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaRDS", scope = DocumentoRDS.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentoRDSFechaRDS(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentoRDSFechaRDS_QNAME, XMLGregorianCalendar.class, DocumentoRDS.class, value);
    }

}
