package net.conselldemallorca.helium.v3.core.api.dto;

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
