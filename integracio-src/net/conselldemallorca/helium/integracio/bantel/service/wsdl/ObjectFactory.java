
package net.conselldemallorca.helium.integracio.bantel.service.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.integracio.bantel.service.wsdl package. 
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

    private final static QName _Fault_QNAME = new QName("urn:es:caib:bantel:ws:v1:model:BantelFacade", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.integracio.bantel.service.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BantelFacadeException }
     * 
     */
    public BantelFacadeException createBantelFacadeException() {
        return new BantelFacadeException();
    }

    /**
     * Create an instance of {@link AvisoEntradasResponse }
     * 
     */
    public AvisoEntradasResponse createAvisoEntradasResponse() {
        return new AvisoEntradasResponse();
    }

    /**
     * Create an instance of {@link AvisoEntradas }
     * 
     */
    public AvisoEntradas createAvisoEntradas() {
        return new AvisoEntradas();
    }

    /**
     * Create an instance of {@link ReferenciaEntrada }
     * 
     */
    public ReferenciaEntrada createReferenciaEntrada() {
        return new ReferenciaEntrada();
    }

    /**
     * Create an instance of {@link ReferenciasEntrada }
     * 
     */
    public ReferenciasEntrada createReferenciasEntrada() {
        return new ReferenciasEntrada();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BantelFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:bantel:ws:v1:model:BantelFacade", name = "fault")
    public JAXBElement<BantelFacadeException> createFault(BantelFacadeException value) {
        return new JAXBElement<BantelFacadeException>(_Fault_QNAME, BantelFacadeException.class, null, value);
    }

}
