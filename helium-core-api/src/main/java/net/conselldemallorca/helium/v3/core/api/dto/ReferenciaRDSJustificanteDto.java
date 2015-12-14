
package net.conselldemallorca.helium.v3.core.api.dto;

/**
 * Referencia RDS de un justificante
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReferenciaRDSJustificanteDto {
	private String clave;
	private Long codigo;
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	@Override
	public String toString() {
		return "ReferenciaRDSJustificanteDto [clave=" + clave + ", codigo=" + codigo + "]";
	}
}
