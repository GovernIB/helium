package es.caib.helium.integracio.domini.tramitacio;

import javax.xml.datatype.XMLGregorianCalendar;

import es.caib.helium.integracio.enums.tramitacio.TipoAviso;
import es.caib.helium.integracio.enums.tramitacio.TipoConfirmacionAviso;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleAviso {

    protected TipoAviso tipo;
    protected String destinatario;
    protected boolean enviado;
    protected XMLGregorianCalendar fechaEnvio;
    protected boolean confirmarEnvio;
    protected TipoConfirmacionAviso confirmadoEnvio;
}
