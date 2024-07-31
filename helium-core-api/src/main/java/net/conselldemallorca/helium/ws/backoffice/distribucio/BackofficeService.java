
package net.conselldemallorca.helium.ws.backoffice.distribucio;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b14002
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "BackofficeService", targetNamespace = "http://www.caib.es/distribucio/ws/backoffice", wsdlLocation = "file:/C:/Users/andre/git/helium/helium-core-api/src/main/resources/net/conselldemallorca/helium/core/api/wsdl/backoffice-distribucio.wsdl")
public class BackofficeService
    extends Service
{

    private final static URL BACKOFFICESERVICE_WSDL_LOCATION;
    private final static WebServiceException BACKOFFICESERVICE_EXCEPTION;
    private final static QName BACKOFFICESERVICE_QNAME = new QName("http://www.caib.es/distribucio/ws/backoffice", "BackofficeService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/andre/git/helium/helium-core-api/src/main/resources/net/conselldemallorca/helium/core/api/wsdl/backoffice-distribucio.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        BACKOFFICESERVICE_WSDL_LOCATION = url;
        BACKOFFICESERVICE_EXCEPTION = e;
    }

    public BackofficeService() {
        super(__getWsdlLocation(), BACKOFFICESERVICE_QNAME);
    }

    public BackofficeService(WebServiceFeature... features) {
        super(__getWsdlLocation(), BACKOFFICESERVICE_QNAME, features);
    }

    public BackofficeService(URL wsdlLocation) {
        super(wsdlLocation, BACKOFFICESERVICE_QNAME);
    }

    public BackofficeService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, BACKOFFICESERVICE_QNAME, features);
    }

    public BackofficeService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BackofficeService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns Backoffice
     */
    @WebEndpoint(name = "BackofficeServicePort")
    public Backoffice getBackofficeServicePort() {
        return super.getPort(new QName("http://www.caib.es/distribucio/ws/backoffice", "BackofficeServicePort"), Backoffice.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Backoffice
     */
    @WebEndpoint(name = "BackofficeServicePort")
    public Backoffice getBackofficeServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.caib.es/distribucio/ws/backoffice", "BackofficeServicePort"), Backoffice.class, features);
    }

    private static URL __getWsdlLocation() {
        if (BACKOFFICESERVICE_EXCEPTION!= null) {
            throw BACKOFFICESERVICE_EXCEPTION;
        }
        return BACKOFFICESERVICE_WSDL_LOCATION;
    }

}
