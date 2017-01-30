
package es.caib.regtel.ws.v2.model.datosnotificacion;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.datosnotificacion package. 
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

    private final static QName _DatosNotificacionAccesiblePorClave_QNAME = new QName("", "accesiblePorClave");
    private final static QName _DatosNotificacionPlazo_QNAME = new QName("", "plazo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.datosnotificacion
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatosNotificacion }
     * 
     */
    public DatosNotificacion createDatosNotificacion() {
        return new DatosNotificacion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "accesiblePorClave", scope = DatosNotificacion.class)
    public JAXBElement<Boolean> createDatosNotificacionAccesiblePorClave(Boolean value) {
        return new JAXBElement<Boolean>(_DatosNotificacionAccesiblePorClave_QNAME, Boolean.class, DatosNotificacion.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "plazo", scope = DatosNotificacion.class)
    public JAXBElement<Integer> createDatosNotificacionPlazo(Integer value) {
        return new JAXBElement<Integer>(_DatosNotificacionPlazo_QNAME, Integer.class, DatosNotificacion.class, value);
    }

}
