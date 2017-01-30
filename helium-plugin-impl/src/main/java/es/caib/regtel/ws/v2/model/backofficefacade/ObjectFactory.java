
package es.caib.regtel.ws.v2.model.backofficefacade;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.backofficefacade package. 
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

    private final static QName _Fault_QNAME = new QName("urn:es:caib:regtel:ws:v2:model:BackofficeFacade", "fault");
    private final static QName _PrepararRegistroEntradaDiasPersistencia_QNAME = new QName("urn:es:caib:regtel:ws:v2:model:BackofficeFacade", "diasPersistencia");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.backofficefacade
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ObtenerDetalleAcuseRecibo }
     * 
     */
    public ObtenerDetalleAcuseRecibo createObtenerDetalleAcuseRecibo() {
        return new ObtenerDetalleAcuseRecibo();
    }

    /**
     * Create an instance of {@link RegistroSalidaResponse }
     * 
     */
    public RegistroSalidaResponse createRegistroSalidaResponse() {
        return new RegistroSalidaResponse();
    }

    /**
     * Create an instance of {@link PrepararRegistroEntradaResponse }
     * 
     */
    public PrepararRegistroEntradaResponse createPrepararRegistroEntradaResponse() {
        return new PrepararRegistroEntradaResponse();
    }

    /**
     * Create an instance of {@link PrepararRegistroEntrada }
     * 
     */
    public PrepararRegistroEntrada createPrepararRegistroEntrada() {
        return new PrepararRegistroEntrada();
    }

    /**
     * Create an instance of {@link BackofficeFacadeException }
     * 
     */
    public BackofficeFacadeException createBackofficeFacadeException() {
        return new BackofficeFacadeException();
    }

    /**
     * Create an instance of {@link ObtenerAcuseReciboResponse }
     * 
     */
    public ObtenerAcuseReciboResponse createObtenerAcuseReciboResponse() {
        return new ObtenerAcuseReciboResponse();
    }

    /**
     * Create an instance of {@link RegistroEntradaConFirma }
     * 
     */
    public RegistroEntradaConFirma createRegistroEntradaConFirma() {
        return new RegistroEntradaConFirma();
    }

    /**
     * Create an instance of {@link RegistroSalida }
     * 
     */
    public RegistroSalida createRegistroSalida() {
        return new RegistroSalida();
    }

    /**
     * Create an instance of {@link RegistroEntradaConFirmaResponse }
     * 
     */
    public RegistroEntradaConFirmaResponse createRegistroEntradaConFirmaResponse() {
        return new RegistroEntradaConFirmaResponse();
    }

    /**
     * Create an instance of {@link RegistroEntradaResponse }
     * 
     */
    public RegistroEntradaResponse createRegistroEntradaResponse() {
        return new RegistroEntradaResponse();
    }

    /**
     * Create an instance of {@link ObtenerDetalleAcuseReciboResponse }
     * 
     */
    public ObtenerDetalleAcuseReciboResponse createObtenerDetalleAcuseReciboResponse() {
        return new ObtenerDetalleAcuseReciboResponse();
    }

    /**
     * Create an instance of {@link RegistroEntrada }
     * 
     */
    public RegistroEntrada createRegistroEntrada() {
        return new RegistroEntrada();
    }

    /**
     * Create an instance of {@link ObtenerAcuseRecibo }
     * 
     */
    public ObtenerAcuseRecibo createObtenerAcuseRecibo() {
        return new ObtenerAcuseRecibo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BackofficeFacadeException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:regtel:ws:v2:model:BackofficeFacade", name = "fault")
    public JAXBElement<BackofficeFacadeException> createFault(BackofficeFacadeException value) {
        return new JAXBElement<BackofficeFacadeException>(_Fault_QNAME, BackofficeFacadeException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:es:caib:regtel:ws:v2:model:BackofficeFacade", name = "diasPersistencia", scope = PrepararRegistroEntrada.class)
    public JAXBElement<String> createPrepararRegistroEntradaDiasPersistencia(String value) {
        return new JAXBElement<String>(_PrepararRegistroEntradaDiasPersistencia_QNAME, String.class, PrepararRegistroEntrada.class, value);
    }

}
