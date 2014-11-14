/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;


/**
 * Referencia RDS de un justificante
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReferenciaRDSJustificante {

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
}
