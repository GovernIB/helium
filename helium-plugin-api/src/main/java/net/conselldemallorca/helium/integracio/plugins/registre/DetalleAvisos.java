
package net.conselldemallorca.helium.integracio.plugins.registre;

import java.util.ArrayList;
import java.util.List;


/**
 * Java class for DetalleAvisos complex type.
 */
public class DetalleAvisos {

    protected List<DetalleAviso> aviso;
    
    public List<DetalleAviso> getAviso() {
        if (aviso == null) {
            aviso = new ArrayList<DetalleAviso>();
        }
        return this.aviso;
    }
}
