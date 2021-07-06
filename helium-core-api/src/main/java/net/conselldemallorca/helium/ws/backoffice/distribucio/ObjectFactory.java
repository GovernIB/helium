
package net.conselldemallorca.helium.ws.backoffice.distribucio;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import es.caib.helium.logic.intf.service.ws.backoffice.distribucio.AnotacioRegistreId;
import es.caib.helium.logic.intf.service.ws.backoffice.distribucio.ComunicarAnotacionsPendents;
import es.caib.helium.logic.intf.service.ws.backoffice.distribucio.ComunicarAnotacionsPendentsResponse;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.conselldemallorca.helium.ws.backoffice.distribucio package. 
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

    private final static QName _ComunicarAnotacionsPendents_QNAME = new QName("http://www.caib.es/distribucio/ws/backoffice", "comunicarAnotacionsPendents");
    private final static QName _ComunicarAnotacionsPendentsResponse_QNAME = new QName("http://www.caib.es/distribucio/ws/backoffice", "comunicarAnotacionsPendentsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.conselldemallorca.helium.ws.backoffice.distribucio
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ComunicarAnotacionsPendentsResponse }
     * 
     */
    public ComunicarAnotacionsPendentsResponse createComunicarAnotacionsPendentsResponse() {
        return new ComunicarAnotacionsPendentsResponse();
    }

    /**
     * Create an instance of {@link ComunicarAnotacionsPendents }
     * 
     */
    public ComunicarAnotacionsPendents createComunicarAnotacionsPendents() {
        return new ComunicarAnotacionsPendents();
    }

    /**
     * Create an instance of {@link AnotacioRegistreId }
     * 
     */
    public AnotacioRegistreId createAnotacioRegistreId() {
        return new AnotacioRegistreId();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComunicarAnotacionsPendents }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/distribucio/ws/backoffice", name = "comunicarAnotacionsPendents")
    public JAXBElement<ComunicarAnotacionsPendents> createComunicarAnotacionsPendents(ComunicarAnotacionsPendents value) {
        return new JAXBElement<ComunicarAnotacionsPendents>(_ComunicarAnotacionsPendents_QNAME, ComunicarAnotacionsPendents.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComunicarAnotacionsPendentsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caib.es/distribucio/ws/backoffice", name = "comunicarAnotacionsPendentsResponse")
    public JAXBElement<ComunicarAnotacionsPendentsResponse> createComunicarAnotacionsPendentsResponse(ComunicarAnotacionsPendentsResponse value) {
        return new JAXBElement<ComunicarAnotacionsPendentsResponse>(_ComunicarAnotacionsPendentsResponse_QNAME, ComunicarAnotacionsPendentsResponse.class, null, value);
    }

}
