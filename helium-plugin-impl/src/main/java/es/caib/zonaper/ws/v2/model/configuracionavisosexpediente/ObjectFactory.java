
package es.caib.zonaper.ws.v2.model.configuracionavisosexpediente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.zonaper.ws.v2.model.configuracionavisosexpediente package. 
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

    private final static QName _ConfiguracionAvisosExpedienteAvisoSMS_QNAME = new QName("", "avisoSMS");
    private final static QName _ConfiguracionAvisosExpedienteHabilitarAvisos_QNAME = new QName("", "habilitarAvisos");
    private final static QName _ConfiguracionAvisosExpedienteAvisoEmail_QNAME = new QName("", "avisoEmail");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.zonaper.ws.v2.model.configuracionavisosexpediente
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConfiguracionAvisosExpediente }
     * 
     */
    public ConfiguracionAvisosExpediente createConfiguracionAvisosExpediente() {
        return new ConfiguracionAvisosExpediente();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoSMS", scope = ConfiguracionAvisosExpediente.class)
    public JAXBElement<String> createConfiguracionAvisosExpedienteAvisoSMS(String value) {
        return new JAXBElement<String>(_ConfiguracionAvisosExpedienteAvisoSMS_QNAME, String.class, ConfiguracionAvisosExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "habilitarAvisos", scope = ConfiguracionAvisosExpediente.class)
    public JAXBElement<Boolean> createConfiguracionAvisosExpedienteHabilitarAvisos(Boolean value) {
        return new JAXBElement<Boolean>(_ConfiguracionAvisosExpedienteHabilitarAvisos_QNAME, Boolean.class, ConfiguracionAvisosExpediente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisoEmail", scope = ConfiguracionAvisosExpediente.class)
    public JAXBElement<String> createConfiguracionAvisosExpedienteAvisoEmail(String value) {
        return new JAXBElement<String>(_ConfiguracionAvisosExpedienteAvisoEmail_QNAME, String.class, ConfiguracionAvisosExpediente.class, value);
    }

}
