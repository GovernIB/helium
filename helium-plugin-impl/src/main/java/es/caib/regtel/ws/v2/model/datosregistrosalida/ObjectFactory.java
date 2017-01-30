
package es.caib.regtel.ws.v2.model.datosregistrosalida;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import es.caib.regtel.ws.v2.model.documento.Documentos;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.datosregistrosalida package. 
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

    private final static QName _DatosRegistroSalidaDocumentos_QNAME = new QName("", "documentos");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.datosregistrosalida
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatosRegistroSalida }
     * 
     */
    public DatosRegistroSalida createDatosRegistroSalida() {
        return new DatosRegistroSalida();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Documentos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "documentos", scope = DatosRegistroSalida.class)
    public JAXBElement<Documentos> createDatosRegistroSalidaDocumentos(Documentos value) {
        return new JAXBElement<Documentos>(_DatosRegistroSalidaDocumentos_QNAME, Documentos.class, DatosRegistroSalida.class, value);
    }

}
