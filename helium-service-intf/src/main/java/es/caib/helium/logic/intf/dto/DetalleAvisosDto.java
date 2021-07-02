package es.caib.helium.logic.intf.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Java class for DetalleAvisos complex type.
 */
public class DetalleAvisosDto {

	protected List<DetalleAvisoDto> aviso;

	public List<DetalleAvisoDto> getAviso() {
		if (aviso == null) {
			aviso = new ArrayList<DetalleAvisoDto>();
		}
		return this.aviso;
	}
}
