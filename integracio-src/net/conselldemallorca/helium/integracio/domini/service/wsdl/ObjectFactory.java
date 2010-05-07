
package net.conselldemallorca.helium.integracio.domini.service.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.domini.service.wsdl package. 
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

    private final static QName _DocumentoConsultaCodigoRDS_QNAME = new QName("", "codigoRDS");
    private final static QName _DocumentoConsultaUrlNuevaVentana_QNAME = new QName("", "urlNuevaVentana");
    private final static QName _DocumentoConsultaClaveRDS_QNAME = new QName("", "claveRDS");
    private final static QName _DocumentoConsultaNombreFichero_QNAME = new QName("", "nombreFichero");
    private final static QName _DocumentoConsultaContenidoFichero_QNAME = new QName("", "contenidoFichero");
    private final static QName _DocumentoConsultaUrlAcceso_QNAME = new QName("", "urlAcceso");
    private final static QName _DocumentoConsultaXml_QNAME = new QName("", "xml");
    private final static QName _DocumentoConsultaModelo_QNAME = new QName("", "modelo");
    private final static QName _DocumentoConsultaPlantilla_QNAME = new QName("", "plantilla");
    private final static QName _DocumentoConsultaVersion_QNAME = new QName("", "version");
    private final static QName _ValoresDominioDescripcionError_QNAME = new QName("", "descripcionError");
    private final static QName _Fault_QNAME = new QName("urn:es:caib:sistra:ws:v1:model:SistraFacade", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.domini.service.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Columna }
     * 
     */
    public Columna createColumna() {
        return new Columna();
    }

    /**
     * Create an instance of {@link ObtenerDominioResponse }
     * 
     */
    public ObtenerDominioResponse createObtenerDominioResponse() {
        return new ObtenerDominioResponse();
    }

    /**
     * Create an instance of {@link SistraFacadeException }
     * 
     */
    public SistraFacadeException createSistraFacadeException() {
        return new SistraFacadeException();
    }

    /**
     * Create an instance of {@link Fila }
     * 
     */
    public Fila createFila() {
        return new Fila();
    }

    /**
     * Create an instance of {@link FormularioConsulta }
     * 
     */
    public FormularioConsulta createFormularioConsulta() {
        return new FormularioConsulta();
    }

    /**
     * Create an instance of {@link RealizarConsulta }
     * 
     */
    public RealizarConsulta createRealizarConsulta() {
        return new RealizarConsulta();
    }

    /**
     * Create an instance of {@link DocumentoConsulta }
     * 
     */
    public DocumentoConsulta createDocumentoConsulta() {
        return new DocumentoConsulta();
    }

    /**
     * Create an instance of {@link ParametrosDominio }
     * 
     */
    public ParametrosDominio createParametrosDominio() {
        return new ParametrosDominio();
    }

    /**
     * Create an instance of {@link RealizarConsultaResponse }
     * 
     */
    public RealizarConsultaResponse createRealizarConsultaResponse() {
        return new RealizarConsultaResponse();
    }

    /**
     * Create an instance of {@link Filas }
     * 
     */
    public Filas createFilas() {
        return new Filas();
    }

    /**
     * Create an instance of {@link DocumentosConsulta }
     * 
     */
    public DocumentosConsulta createDocumentosConsulta() {
        return new DocumentosConsulta();
    }

    /**
     * Create an instance of {@link FormulariosConsulta }
     * 
     */
    public FormulariosConsulta createFormulariosConsulta() {
        return new FormulariosConsulta();
    }

    /**
     * Create an instance of {@link ObtenerDominio }
     * 
     */
    public ObtenerDominio createObtenerDominio() {
        return new ObtenerDominio();
    }

    /**
     * Create an instance of {@link ValoresDominio }
     * 
     */
    public ValoresDominio createValoresDominio() {
        return new ValoresDominio();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoRDS", scope = DocumentoConsulta.class)
    public JAXBElement<Long> createDocumentoConsultaCodigoRDS(Long value) {
        return new JAXBElement<Long>(_DocumentoConsultaCodigoRDS_QNAME, Long.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "urlNuevaVentana", scope = DocumentoConsulta.class)
    public JAXBElement<Boolean> createDocumentoConsultaUrlNuevaVentana(Boolean value) {
        return new JAXBElement<Boolean>(_DocumentoConsultaUrlNuevaVentana_QNAME, Boolean.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "claveRDS", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaClaveRDS(String value) {
        return new JAXBElement<String>(_DocumentoConsultaClaveRDS_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombreFichero", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaNombreFichero(String value) {
        return new JAXBElement<String>(_DocumentoConsultaNombreFichero_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "contenidoFichero", scope = DocumentoConsulta.class)
    public JAXBElement<byte[]> createDocumentoConsultaContenidoFichero(byte[] value) {
        return new JAXBElement<byte[]>(_DocumentoConsultaContenidoFichero_QNAME, byte[].class, DocumentoConsulta.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "urlAcceso", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaUrlAcceso(String value) {
        return new JAXBElement<String>(_DocumentoConsultaUrlAcceso_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "xml", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaXml(String value) {
        return new JAXBElement<String>(_DocumentoConsultaXml_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "modelo", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaModelo(String value) {
        return new JAXBElement<String>(_DocumentoConsultaModelo_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "plantilla", scope = DocumentoConsulta.class)
    public JAXBElement<String> createDocumentoConsultaPlantilla(String value) {
        return new JAXBElement<String>(_DocumentoConsultaPlantilla_QNAME, String.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "version", scope = DocumentoConsulta.class)
    public JAXBElement<Integer> createDocumentoConsultaVersion(Integer value) {
        return new JAXBElement<Integer>(_DocumentoConsultaVersion_QNAME, Integer.class, DocumentoConsulta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "descripcionError", scope = ValoresDominio.class)
    public JAXBElement<String> createValoresDominioDescripcionError(String value) {
        return new JAXBElement<String>(_ValoresDominioDescripcionError_QNAME, String.class, ValoresDominio.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SistraFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:sistra:ws:v1:model:SistraFacade", name = "fault")
    public JAXBElement<SistraFacadeException> createFault(SistraFacadeException value) {
        return new JAXBElement<SistraFacadeException>(_Fault_QNAME, SistraFacadeException.class, null, value);
    }

}
