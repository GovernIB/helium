
package net.conselldemallorca.helium.wsintegraciones.portafirmasws.cws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ServerSigners complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ServerSigners">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serverSigner" type="{http://www.indra.es/portafirmasws/cws}ServerSigner" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerSigners", propOrder = {
    "serverSigner"
})
public class ServerSigners {

    protected List<ServerSigner> serverSigner;

    /**
     * Gets the value of the serverSigner property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serverSigner property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServerSigner().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServerSigner }
     * 
     * 
     */
    public List<ServerSigner> getServerSigner() {
        if (serverSigner == null) {
            serverSigner = new ArrayList<ServerSigner>();
        }
        return this.serverSigner;
    }

}
