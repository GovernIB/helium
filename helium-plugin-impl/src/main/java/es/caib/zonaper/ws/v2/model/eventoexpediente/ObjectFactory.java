
package es.caib.zonaper.ws.v2.model.eventoexpediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import es.caib.zonaper.ws.v2.model.documentoexpediente.DocumentosExpediente;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.zonaper.ws.v2.model.eventoexpediente package. 
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

    private final static QName _EventoExpedienteAccesiblePorClave_QNAME = new QName("", "accesiblePorClave");
    private final static QName _EventoExpedienteDocumentos_QNAME = new QName("", "documentos");
    private final static QName _EventoExpedienteFecha_QNAME = new QName("", "fecha");
    private final static QName _EventoExpedienteTextoSMS_QNAME = new QName("", "textoSMS");
    private final static QName _EventoExpedienteEnlaceConsulta_QNAME = new QName("", "enlaceConsulta");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.zonaper.ws.v2.model.eventoexpediente
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventoExpediente }
     * 
     */
    public EventoExpediente createEventoExpediente() {
        return new EventoExpediente();
    }

    /**
     * Create an instance of {@link EventosExpediente }
     * 
     */
    public EventosExpediente createEventosExpediente() {
        return new EventosExpediente();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "accesiblePorClave", scope = EventoExpediente.class)
    public JAXBElement<Boolean> createEventoExpedienteAccesiblePorClave(Boolean value) {
        return new JAXBElement<Boolean>(_EventoExpedienteAccesiblePorClave_QNAME, Boolean.class, EventoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentosExpediente }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "documentos", scope = EventoExpediente.class)
    public JAXBElement<DocumentosExpediente> createEventoExpedienteDocumentos(DocumentosExpediente value) {
        return new JAXBElement<DocumentosExpediente>(_EventoExpedienteDocumentos_QNAME, DocumentosExpediente.class, EventoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fecha", scope = EventoExpediente.class)
    public JAXBElement<String> createEventoExpedienteFecha(String value) {
        return new JAXBElement<String>(_EventoExpedienteFecha_QNAME, String.class, EventoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "textoSMS", scope = EventoExpediente.class)
    public JAXBElement<String> createEventoExpedienteTextoSMS(String value) {
        return new JAXBElement<String>(_EventoExpedienteTextoSMS_QNAME, String.class, EventoExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "enlaceConsulta", scope = EventoExpediente.class)
    public JAXBElement<String> createEventoExpedienteEnlaceConsulta(String value) {
        return new JAXBElement<String>(_EventoExpedienteEnlaceConsulta_QNAME, String.class, EventoExpediente.class, value);
    }

}
