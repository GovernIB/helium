
package net.conselldemallorca.helium.integracio.forms;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.forms package. 
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

    private final static QName _GuardarResponse_QNAME = new QName("http://forms.integracio.helium.conselldemallorca.net/", "guardarResponse");
    private final static QName _Guardar_QNAME = new QName("http://forms.integracio.helium.conselldemallorca.net/", "guardar");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.forms
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Guardar }
     * 
     */
    public Guardar createGuardar() {
        return new Guardar();
    }

    /**
     * Create an instance of {@link GuardarResponse }
     * 
     */
    public GuardarResponse createGuardarResponse() {
        return new GuardarResponse();
    }

    /**
     * Create an instance of {@link ParellaCodiValor }
     * 
     */
    public ParellaCodiValor createParellaCodiValor() {
        return new ParellaCodiValor();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuardarResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://forms.integracio.helium.conselldemallorca.net/", name = "guardarResponse")
    public JAXBElement<GuardarResponse> createGuardarResponse(GuardarResponse value) {
        return new JAXBElement<GuardarResponse>(_GuardarResponse_QNAME, GuardarResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Guardar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://forms.integracio.helium.conselldemallorca.net/", name = "guardar")
    public JAXBElement<Guardar> createGuardar(Guardar value) {
        return new JAXBElement<Guardar>(_Guardar_QNAME, Guardar.class, null, value);
    }

}
