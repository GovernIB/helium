
package es.caib.zonaper.ws.v2.model.documentoexpediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.zonaper.ws.v2.model.documentoexpediente package. 
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

    private final static QName _DocumentoExpedienteNombre_QNAME = new QName("", "nombre");
    private final static QName _DocumentoExpedienteTitulo_QNAME = new QName("", "titulo");
    private final static QName _DocumentoExpedienteClaveRDS_QNAME = new QName("", "claveRDS");
    private final static QName _DocumentoExpedienteCodigoRDS_QNAME = new QName("", "codigoRDS");
    private final static QName _DocumentoExpedienteVersionRDS_QNAME = new QName("", "versionRDS");
    private final static QName _DocumentoExpedienteModeloRDS_QNAME = new QName("", "modeloRDS");
    private final static QName _DocumentoExpedienteEstructurado_QNAME = new QName("", "estructurado");
    private final static QName _DocumentoExpedienteContenidoFichero_QNAME = new QName("", "contenidoFichero");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.zonaper.ws.v2.model.documentoexpediente
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DocumentoExpediente }
     * 
     */
    public DocumentoExpediente createDocumentoExpediente() {
        return new DocumentoExpediente();
    }

    /**
     * Create an instance of {@link DocumentosExpediente }
     * 
     */
    public DocumentosExpediente createDocumentosExpediente() {
        return new DocumentosExpediente();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombre", scope = DocumentoExpediente.class)
    public JAXBElement<String> createDocumentoExpedienteNombre(String value) {
        return new JAXBElement<String>(_DocumentoExpedienteNombre_QNAME, String.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "titulo", scope = DocumentoExpediente.class)
    public JAXBElement<String> createDocumentoExpedienteTitulo(String value) {
        return new JAXBElement<String>(_DocumentoExpedienteTitulo_QNAME, String.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "claveRDS", scope = DocumentoExpediente.class)
    public JAXBElement<String> createDocumentoExpedienteClaveRDS(String value) {
        return new JAXBElement<String>(_DocumentoExpedienteClaveRDS_QNAME, String.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoRDS", scope = DocumentoExpediente.class)
    public JAXBElement<Long> createDocumentoExpedienteCodigoRDS(Long value) {
        return new JAXBElement<Long>(_DocumentoExpedienteCodigoRDS_QNAME, Long.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "versionRDS", scope = DocumentoExpediente.class)
    public JAXBElement<Integer> createDocumentoExpedienteVersionRDS(Integer value) {
        return new JAXBElement<Integer>(_DocumentoExpedienteVersionRDS_QNAME, Integer.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "modeloRDS", scope = DocumentoExpediente.class)
    public JAXBElement<String> createDocumentoExpedienteModeloRDS(String value) {
        return new JAXBElement<String>(_DocumentoExpedienteModeloRDS_QNAME, String.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "estructurado", scope = DocumentoExpediente.class)
    public JAXBElement<Boolean> createDocumentoExpedienteEstructurado(Boolean value) {
        return new JAXBElement<Boolean>(_DocumentoExpedienteEstructurado_QNAME, Boolean.class, DocumentoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "contenidoFichero", scope = DocumentoExpediente.class)
    public JAXBElement<byte[]> createDocumentoExpedienteContenidoFichero(byte[] value) {
        return new JAXBElement<byte[]>(_DocumentoExpedienteContenidoFichero_QNAME, byte[].class, DocumentoExpediente.class, ((byte[]) value));
    }

}
