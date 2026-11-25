package es.caib.helium.bpmn.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Resultat d'una anotació de registre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RespostaRegistre {

	private String numero;
	private Date data;

}
