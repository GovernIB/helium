package es.caib.helium.disseny.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExpedientInfo {

	private Long id;
	private String titol;
	private String numero;
	private Date dataInici;
	private Date dataFi;

}
