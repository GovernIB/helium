/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.util.Date;

import es.caib.helium.logic.intf.dto.ReferenciaRDSJustificanteDto;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreIdDto {

	private String numero;
	private Date data;
	private ReferenciaRDSJustificanteDto referenciaRDSJustificante;

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
	public ReferenciaRDSJustificanteDto getReferenciaRDSJustificante() {
		return referenciaRDSJustificante;
	}
	public void setReferenciaRDSJustificante(ReferenciaRDSJustificanteDto referenciaRDSJustificante) {
		this.referenciaRDSJustificante = referenciaRDSJustificante;
	}
}
