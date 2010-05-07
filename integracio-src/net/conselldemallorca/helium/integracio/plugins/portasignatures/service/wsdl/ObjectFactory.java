
package net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.plugins.portasignatures.service.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CallbackRequest }
     * 
     */
    public CallbackRequest createCallbackRequest() {
        return new CallbackRequest();
    }

    /**
     * Create an instance of {@link ArrayOfLogMessage }
     * 
     */
    public ArrayOfLogMessage createArrayOfLogMessage() {
        return new ArrayOfLogMessage();
    }

    /**
     * Create an instance of {@link CallbackResponse }
     * 
     */
    public CallbackResponse createCallbackResponse() {
        return new CallbackResponse();
    }

    /**
     * Create an instance of {@link Signer }
     * 
     */
    public Signer createSigner() {
        return new Signer();
    }

    /**
     * Create an instance of {@link Attributes }
     * 
     */
    public Attributes createAttributes() {
        return new Attributes();
    }

    /**
     * Create an instance of {@link Document }
     * 
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link Certificate }
     * 
     */
    public Certificate createCertificate() {
        return new Certificate();
    }

    /**
     * Create an instance of {@link Delegate }
     * 
     */
    public Delegate createDelegate() {
        return new Delegate();
    }

    /**
     * Create an instance of {@link Application }
     * 
     */
    public Application createApplication() {
        return new Application();
    }

    /**
     * Create an instance of {@link LogMessage }
     * 
     */
    public LogMessage createLogMessage() {
        return new LogMessage();
    }

    /**
     * Create an instance of {@link Rejection }
     * 
     */
    public Rejection createRejection() {
        return new Rejection();
    }

}
