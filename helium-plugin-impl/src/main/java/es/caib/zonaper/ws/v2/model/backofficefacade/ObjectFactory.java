
package es.caib.zonaper.ws.v2.model.backofficefacade;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.zonaper.ws.v2.model.backofficefacade package. 
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

    private final static QName _Fault_QNAME = new QName("urn:es:caib:zonaper:ws:v2:model:BackofficeFacade", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.zonaper.ws.v2.model.backofficefacade
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExisteZonaPersonalUsuario }
     * 
     */
    public ExisteZonaPersonalUsuario createExisteZonaPersonalUsuario() {
        return new ExisteZonaPersonalUsuario();
    }

    /**
     * Create an instance of {@link AltaExpedienteResponse }
     * 
     */
    public AltaExpedienteResponse createAltaExpedienteResponse() {
        return new AltaExpedienteResponse();
    }

    /**
     * Create an instance of {@link ExisteExpediente }
     * 
     */
    public ExisteExpediente createExisteExpediente() {
        return new ExisteExpediente();
    }

    /**
     * Create an instance of {@link AltaZonaPersonalUsuarioResponse }
     * 
     */
    public AltaZonaPersonalUsuarioResponse createAltaZonaPersonalUsuarioResponse() {
        return new AltaZonaPersonalUsuarioResponse();
    }

    /**
     * Create an instance of {@link BackofficeFacadeException }
     * 
     */
    public BackofficeFacadeException createBackofficeFacadeException() {
        return new BackofficeFacadeException();
    }

    /**
     * Create an instance of {@link ExisteExpedienteResponse }
     * 
     */
    public ExisteExpedienteResponse createExisteExpedienteResponse() {
        return new ExisteExpedienteResponse();
    }

    /**
     * Create an instance of {@link ExisteZonaPersonalUsuarioResponse }
     * 
     */
    public ExisteZonaPersonalUsuarioResponse createExisteZonaPersonalUsuarioResponse() {
        return new ExisteZonaPersonalUsuarioResponse();
    }

    /**
     * Create an instance of {@link AltaEventoExpedienteResponse }
     * 
     */
    public AltaEventoExpedienteResponse createAltaEventoExpedienteResponse() {
        return new AltaEventoExpedienteResponse();
    }

    /**
     * Create an instance of {@link AltaZonaPersonalUsuario }
     * 
     */
    public AltaZonaPersonalUsuario createAltaZonaPersonalUsuario() {
        return new AltaZonaPersonalUsuario();
    }

    /**
     * Create an instance of {@link ObtenerEstadoPagosTramite }
     * 
     */
    public ObtenerEstadoPagosTramite createObtenerEstadoPagosTramite() {
        return new ObtenerEstadoPagosTramite();
    }

    /**
     * Create an instance of {@link ModificarAvisosExpediente }
     * 
     */
    public ModificarAvisosExpediente createModificarAvisosExpediente() {
        return new ModificarAvisosExpediente();
    }

    /**
     * Create an instance of {@link AltaExpediente }
     * 
     */
    public AltaExpediente createAltaExpediente() {
        return new AltaExpediente();
    }

    /**
     * Create an instance of {@link ModificarAvisosExpedienteResponse }
     * 
     */
    public ModificarAvisosExpedienteResponse createModificarAvisosExpedienteResponse() {
        return new ModificarAvisosExpedienteResponse();
    }

    /**
     * Create an instance of {@link ObtenerEstadoPagosTramiteResponse }
     * 
     */
    public ObtenerEstadoPagosTramiteResponse createObtenerEstadoPagosTramiteResponse() {
        return new ObtenerEstadoPagosTramiteResponse();
    }

    /**
     * Create an instance of {@link AltaEventoExpediente }
     * 
     */
    public AltaEventoExpediente createAltaEventoExpediente() {
        return new AltaEventoExpediente();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BackofficeFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:zonaper:ws:v2:model:BackofficeFacade", name = "fault")
    public JAXBElement<BackofficeFacadeException> createFault(BackofficeFacadeException value) {
        return new JAXBElement<BackofficeFacadeException>(_Fault_QNAME, BackofficeFacadeException.class, null, value);
    }

}
