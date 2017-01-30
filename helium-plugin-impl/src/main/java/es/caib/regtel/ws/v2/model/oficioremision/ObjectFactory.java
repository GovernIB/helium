
package es.caib.regtel.ws.v2.model.oficioremision;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.oficioremision package. 
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

    private final static QName _OficioRemisionTramiteSubsanacion_QNAME = new QName("", "tramiteSubsanacion");
    private final static QName _OficioRemisionTramiteSubsanacionParametrosTramite_QNAME = new QName("", "parametrosTramite");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.oficioremision
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParametroTramite }
     * 
     */
    public ParametroTramite createParametroTramite() {
        return new ParametroTramite();
    }

    /**
     * Create an instance of {@link OficioRemision }
     * 
     */
    public OficioRemision createOficioRemision() {
        return new OficioRemision();
    }

    /**
     * Create an instance of {@link OficioRemision.TramiteSubsanacion }
     * 
     */
    public OficioRemision.TramiteSubsanacion createOficioRemisionTramiteSubsanacion() {
        return new OficioRemision.TramiteSubsanacion();
    }

    /**
     * Create an instance of {@link OficioRemision.TramiteSubsanacion.ParametrosTramite }
     * 
     */
    public OficioRemision.TramiteSubsanacion.ParametrosTramite createOficioRemisionTramiteSubsanacionParametrosTramite() {
        return new OficioRemision.TramiteSubsanacion.ParametrosTramite();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tramiteSubsanacion", scope = OficioRemision.class)
    public JAXBElement<OficioRemision.TramiteSubsanacion> createOficioRemisionTramiteSubsanacion(OficioRemision.TramiteSubsanacion value) {
        return new JAXBElement<OficioRemision.TramiteSubsanacion>(_OficioRemisionTramiteSubsanacion_QNAME, OficioRemision.TramiteSubsanacion.class, OficioRemision.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OficioRemision.TramiteSubsanacion.ParametrosTramite }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "parametrosTramite", scope = OficioRemision.TramiteSubsanacion.class)
    public JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite> createOficioRemisionTramiteSubsanacionParametrosTramite(OficioRemision.TramiteSubsanacion.ParametrosTramite value) {
        return new JAXBElement<OficioRemision.TramiteSubsanacion.ParametrosTramite>(_OficioRemisionTramiteSubsanacionParametrosTramite_QNAME, OficioRemision.TramiteSubsanacion.ParametrosTramite.class, OficioRemision.TramiteSubsanacion.class, value);
    }

}
