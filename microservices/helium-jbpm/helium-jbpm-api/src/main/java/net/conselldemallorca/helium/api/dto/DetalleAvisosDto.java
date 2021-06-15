package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Java class for DetalleAvisos complex type.
 */
@Getter @Setter
public class DetalleAvisosDto {

	protected List<DetalleAvisoDto> aviso;

	public List<DetalleAvisoDto> getAviso() {
		if (aviso == null) {
			aviso = new ArrayList<DetalleAvisoDto>();
		}
		return this.aviso;
	}
}
