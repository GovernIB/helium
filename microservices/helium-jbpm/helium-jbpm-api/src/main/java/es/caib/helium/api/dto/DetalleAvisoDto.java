package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for DetalleAviso complex type.
 */
@Getter @Setter
public class DetalleAvisoDto implements Comparable<DetalleAvisoDto> {

	protected TipoAvisoDto tipo;
	protected String destinatario;
	protected boolean enviado;
	protected XMLGregorianCalendar fechaEnvio;
	protected boolean confirmarEnvio;
	protected TipoConfirmacionAvisoDto confirmadoEnvio;

	@Override
	public int compareTo(DetalleAvisoDto o) {
		if (getFechaEnvio() == null || o.getFechaEnvio() == null)
			return 0;
		return getFechaEnvio().compare(o.getFechaEnvio());
	}
}
