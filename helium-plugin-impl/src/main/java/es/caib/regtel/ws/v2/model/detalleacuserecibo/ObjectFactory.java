
package es.caib.regtel.ws.v2.model.detalleacuserecibo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import es.caib.regtel.ws.v2.model.referenciards.ReferenciaRDS;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.detalleacuserecibo package. 
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

    private final static QName _DetalleAcuseReciboFicheroAcuseRecibo_QNAME = new QName("", "ficheroAcuseRecibo");
    private final static QName _DetalleAcuseReciboFechaAcuseRecibo_QNAME = new QName("", "fechaAcuseRecibo");
    private final static QName _DetalleAcuseReciboAvisos_QNAME = new QName("", "avisos");
    private final static QName _DetalleAvisoFechaEnvio_QNAME = new QName("", "fechaEnvio");
    private final static QName _DetalleAvisoConfirmadoEnvio_QNAME = new QName("", "confirmadoEnvio");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.detalleacuserecibo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DetalleAvisos }
     * 
     */
    public DetalleAvisos createDetalleAvisos() {
        return new DetalleAvisos();
    }

    /**
     * Create an instance of {@link DetalleAviso }
     * 
     */
    public DetalleAviso createDetalleAviso() {
        return new DetalleAviso();
    }

    /**
     * Create an instance of {@link DetalleAcuseRecibo }
     * 
     */
    public DetalleAcuseRecibo createDetalleAcuseRecibo() {
        return new DetalleAcuseRecibo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenciaRDS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ficheroAcuseRecibo", scope = DetalleAcuseRecibo.class)
    public JAXBElement<ReferenciaRDS> createDetalleAcuseReciboFicheroAcuseRecibo(ReferenciaRDS value) {
        return new JAXBElement<ReferenciaRDS>(_DetalleAcuseReciboFicheroAcuseRecibo_QNAME, ReferenciaRDS.class, DetalleAcuseRecibo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaAcuseRecibo", scope = DetalleAcuseRecibo.class)
    public JAXBElement<XMLGregorianCalendar> createDetalleAcuseReciboFechaAcuseRecibo(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DetalleAcuseReciboFechaAcuseRecibo_QNAME, XMLGregorianCalendar.class, DetalleAcuseRecibo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DetalleAvisos }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "avisos", scope = DetalleAcuseRecibo.class)
    public JAXBElement<DetalleAvisos> createDetalleAcuseReciboAvisos(DetalleAvisos value) {
        return new JAXBElement<DetalleAvisos>(_DetalleAcuseReciboAvisos_QNAME, DetalleAvisos.class, DetalleAcuseRecibo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fechaEnvio", scope = DetalleAviso.class)
    public JAXBElement<XMLGregorianCalendar> createDetalleAvisoFechaEnvio(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DetalleAvisoFechaEnvio_QNAME, XMLGregorianCalendar.class, DetalleAviso.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TipoConfirmacionAviso }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "confirmadoEnvio", scope = DetalleAviso.class)
    public JAXBElement<TipoConfirmacionAviso> createDetalleAvisoConfirmadoEnvio(TipoConfirmacionAviso value) {
        return new JAXBElement<TipoConfirmacionAviso>(_DetalleAvisoConfirmadoEnvio_QNAME, TipoConfirmacionAviso.class, DetalleAviso.class, value);
    }

}
