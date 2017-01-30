
package es.caib.regtel.ws.v2.model.datosinteresado;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.caib.regtel.ws.v2.model.datosinteresado package. 
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

    private final static QName _DatosInteresadoNombrePais_QNAME = new QName("", "nombrePais");
    private final static QName _DatosInteresadoCodigoProvincia_QNAME = new QName("", "codigoProvincia");
    private final static QName _DatosInteresadoCodigoLocalidad_QNAME = new QName("", "codigoLocalidad");
    private final static QName _DatosInteresadoNombreLocalidad_QNAME = new QName("", "nombreLocalidad");
    private final static QName _DatosInteresadoNombreProvincia_QNAME = new QName("", "nombreProvincia");
    private final static QName _DatosInteresadoCodigoPais_QNAME = new QName("", "codigoPais");
    private final static QName _DatosInteresadoAutenticado_QNAME = new QName("", "autenticado");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.caib.regtel.ws.v2.model.datosinteresado
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DatosInteresado }
     * 
     */
    public DatosInteresado createDatosInteresado() {
        return new DatosInteresado();
    }

    /**
     * Create an instance of {@link IdentificacionInteresadoDesglosada }
     * 
     */
    public IdentificacionInteresadoDesglosada createIdentificacionInteresadoDesglosada() {
        return new IdentificacionInteresadoDesglosada();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombrePais", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoNombrePais(String value) {
        return new JAXBElement<String>(_DatosInteresadoNombrePais_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoProvincia", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoCodigoProvincia(String value) {
        return new JAXBElement<String>(_DatosInteresadoCodigoProvincia_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoLocalidad", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoCodigoLocalidad(String value) {
        return new JAXBElement<String>(_DatosInteresadoCodigoLocalidad_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombreLocalidad", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoNombreLocalidad(String value) {
        return new JAXBElement<String>(_DatosInteresadoNombreLocalidad_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nombreProvincia", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoNombreProvincia(String value) {
        return new JAXBElement<String>(_DatosInteresadoNombreProvincia_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codigoPais", scope = DatosInteresado.class)
    public JAXBElement<String> createDatosInteresadoCodigoPais(String value) {
        return new JAXBElement<String>(_DatosInteresadoCodigoPais_QNAME, String.class, DatosInteresado.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "autenticado", scope = DatosInteresado.class)
    public JAXBElement<Boolean> createDatosInteresadoAutenticado(Boolean value) {
        return new JAXBElement<Boolean>(_DatosInteresadoAutenticado_QNAME, Boolean.class, DatosInteresado.class, value);
    }

}
