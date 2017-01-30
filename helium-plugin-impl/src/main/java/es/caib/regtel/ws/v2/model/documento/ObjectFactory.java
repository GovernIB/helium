
package es.caib.regtel.ws.v2.model.documento;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import es.caib.regtel.ws.v2.model.firmaws.FirmasWS;
import es.caib.regtel.ws.v2.model.referenciards.ReferenciaRDS;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.documento package. 
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

    private final static QName _DocumentoExtension_QNAME = new QName("", "extension");
    private final static QName _DocumentoNombre_QNAME = new QName("", "nombre");
    private final static QName _DocumentoReferenciaRDS_QNAME = new QName("", "referenciaRDS");
    private final static QName _DocumentoFirmas_QNAME = new QName("", "firmas");
    private final static QName _DocumentoPlantilla_QNAME = new QName("", "plantilla");
    private final static QName _DocumentoModelo_QNAME = new QName("", "modelo");
    private final static QName _DocumentoDatosFichero_QNAME = new QName("", "datosFichero");
    private final static QName _DocumentoVersion_QNAME = new QName("", "version");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.documento
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Documento }
     * 
     */
    public Documento createDocumento() {
        return new Documento();
    }

    /**
     * Create an instance of {@link Documentos }
     * 
     */
    public Documentos createDocumentos() {
        return new Documentos();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "extension", scope = Documento.class)
    public JAXBElement<String> createDocumentoExtension(String value) {
        return new JAXBElement<String>(_DocumentoExtension_QNAME, String.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombre", scope = Documento.class)
    public JAXBElement<String> createDocumentoNombre(String value) {
        return new JAXBElement<String>(_DocumentoNombre_QNAME, String.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "referenciaRDS", scope = Documento.class)
    public JAXBElement<ReferenciaRDS> createDocumentoReferenciaRDS(ReferenciaRDS value) {
        return new JAXBElement<ReferenciaRDS>(_DocumentoReferenciaRDS_QNAME, ReferenciaRDS.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FirmasWS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "firmas", scope = Documento.class)
    public JAXBElement<FirmasWS> createDocumentoFirmas(FirmasWS value) {
        return new JAXBElement<FirmasWS>(_DocumentoFirmas_QNAME, FirmasWS.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "plantilla", scope = Documento.class)
    public JAXBElement<String> createDocumentoPlantilla(String value) {
        return new JAXBElement<String>(_DocumentoPlantilla_QNAME, String.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "modelo", scope = Documento.class)
    public JAXBElement<String> createDocumentoModelo(String value) {
        return new JAXBElement<String>(_DocumentoModelo_QNAME, String.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "datosFichero", scope = Documento.class)
    public JAXBElement<byte[]> createDocumentoDatosFichero(byte[] value) {
        return new JAXBElement<byte[]>(_DocumentoDatosFichero_QNAME, byte[].class, Documento.class, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "version", scope = Documento.class)
    public JAXBElement<Integer> createDocumentoVersion(Integer value) {
        return new JAXBElement<Integer>(_DocumentoVersion_QNAME, Integer.class, Documento.class, value);
    }

}
