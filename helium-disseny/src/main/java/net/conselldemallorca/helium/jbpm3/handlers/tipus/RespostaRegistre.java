/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.handlers.tipus;

import java.util.Date;



/**
 * Classe amb informació sobre un seient registral de sortida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RespostaRegistre {

	private String numero;
	private Date data;
	private ReferenciaRDSJustificante referenciaRDSJustificante;

	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public ReferenciaRDSJustificante getReferenciaRDSJustificante() {
		return referenciaRDSJustificante;
	}
	public void setReferenciaRDSJustificante(ReferenciaRDSJustificante referenciaRDSJustificante) {
		this.referenciaRDSJustificante = referenciaRDSJustificante;
	}
}
