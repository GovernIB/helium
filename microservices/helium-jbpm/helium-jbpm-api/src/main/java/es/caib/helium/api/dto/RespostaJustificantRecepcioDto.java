package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Resposta a una petició per obtenir justificant de recepció
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RespostaJustificantRecepcioDto {

	private Date data;

}
