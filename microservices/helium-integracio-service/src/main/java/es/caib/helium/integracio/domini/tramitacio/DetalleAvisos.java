package es.caib.helium.integracio.domini.tramitacio;

import java.util.ArrayList;
import java.util.List;

public class DetalleAvisos {

	protected List<DetalleAviso> aviso;
	    
	    public List<DetalleAviso> getAviso() {
	        if (aviso == null) {
	            aviso = new ArrayList<DetalleAviso>();
        }
        return this.aviso;
    }
}
