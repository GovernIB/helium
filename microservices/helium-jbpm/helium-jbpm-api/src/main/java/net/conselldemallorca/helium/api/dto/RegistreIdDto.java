/**
 * 
 */
package net.conselldemallorca.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * DTO amb informació d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreIdDto {

	private String numero;
	private Date data;
	private ReferenciaRDSJustificanteDto referenciaRDSJustificante;

}
