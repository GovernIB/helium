package es.caib.helium.disseny.model;

import es.caib.helium.commons.dto.ExpedientTipusTipusEnumDto;
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
	private String avisosMobil;
	private String avisosEmail;
	private String numeroIdentificador;
	private String tramitExpedientIdentificador;
	private String ntiIdentificador;
	private String processInstanceId;

	private Long entornId;
	private Long tipusId;
	private ExpedientTipusTipusEnumDto tipus;
	private String notibEmisor;
	private String notibCodiProcediment;
}
