
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Referencia RDS de un justificante
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ReferenciaRDSJustificanteDto {
	private String clave;
	private Long codigo;

	@Override
	public String toString() {
		return "ReferenciaRDSJustificanteDto [clave=" + clave + ", codigo=" + codigo + "]";
	}
}
