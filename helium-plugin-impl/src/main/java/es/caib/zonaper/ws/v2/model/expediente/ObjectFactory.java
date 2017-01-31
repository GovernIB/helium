
package es.caib.zonaper.ws.v2.model.expediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import es.caib.zonaper.ws.v2.model.configuracionavisosexpediente.ConfiguracionAvisosExpediente;
import es.caib.zonaper.ws.v2.model.eventoexpediente.EventosExpediente;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.zonaper.ws.v2.model.expediente package. 
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

    private final static QName _ExpedienteNifRepresentado_QNAME = new QName("", "nifRepresentado");
    private final static QName _ExpedienteConfiguracionAvisos_QNAME = new QName("", "configuracionAvisos");
    private final static QName _ExpedienteNombreRepresentado_QNAME = new QName("", "nombreRepresentado");
    private final static QName _ExpedienteEventos_QNAME = new QName("", "eventos");
    private final static QName _ExpedienteIdentificadorProcedimiento_QNAME = new QName("", "identificadorProcedimiento");
    private final static QName _ExpedienteNifRepresentante_QNAME = new QName("", "nifRepresentante");
    private final static QName _ExpedienteNumeroEntradaBTE_QNAME = new QName("", "numeroEntradaBTE");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.zonaper.ws.v2.model.expediente
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Expediente }
     * 
     */
    public Expediente createExpediente() {
        return new Expediente();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nifRepresentado", scope = Expediente.class)
    public JAXBElement<String> createExpedienteNifRepresentado(String value) {
        return new JAXBElement<String>(_ExpedienteNifRepresentado_QNAME, String.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfiguracionAvisosExpediente }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "configuracionAvisos", scope = Expediente.class)
    public JAXBElement<ConfiguracionAvisosExpediente> createExpedienteConfiguracionAvisos(ConfiguracionAvisosExpediente value) {
        return new JAXBElement<ConfiguracionAvisosExpediente>(_ExpedienteConfiguracionAvisos_QNAME, ConfiguracionAvisosExpediente.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombreRepresentado", scope = Expediente.class)
    public JAXBElement<String> createExpedienteNombreRepresentado(String value) {
        return new JAXBElement<String>(_ExpedienteNombreRepresentado_QNAME, String.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EventosExpediente }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "eventos", scope = Expediente.class)
    public JAXBElement<EventosExpediente> createExpedienteEventos(EventosExpediente value) {
        return new JAXBElement<EventosExpediente>(_ExpedienteEventos_QNAME, EventosExpediente.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "identificadorProcedimiento", scope = Expediente.class)
    public JAXBElement<String> createExpedienteIdentificadorProcedimiento(String value) {
        return new JAXBElement<String>(_ExpedienteIdentificadorProcedimiento_QNAME, String.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nifRepresentante", scope = Expediente.class)
    public JAXBElement<String> createExpedienteNifRepresentante(String value) {
        return new JAXBElement<String>(_ExpedienteNifRepresentante_QNAME, String.class, Expediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "numeroEntradaBTE", scope = Expediente.class)
    public JAXBElement<String> createExpedienteNumeroEntradaBTE(String value) {
        return new JAXBElement<String>(_ExpedienteNumeroEntradaBTE_QNAME, String.class, Expediente.class, value);
    }

}
