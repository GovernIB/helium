package es.caib.helium.client.expedient.expedient.model;

import es.caib.helium.client.expedient.expedient.enums.ExpedientEstatTipusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Informació de l'expedient amb les propietats necessàries pel llistat filtrat i paginat
 * d'expedients.
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedientDto {

	private Long id;
	private Long entornId;
	private Long expedientTipusId;
	private String processInstanceId;
	private String numero;
	private String numeroDefault;
	private String titol;
	private Date dataInici;
	private Date dataFi;
	private ExpedientEstatTipusEnum estatTipus;
	private Long estatId;
	private boolean aturat = false;
	private String infoAturat;
	private boolean anulat = false;
	private String comentariAnulat;
	private Long alertesTotals = 0L;
	private Long alertesPendents = 0L;
	private boolean ambErrors = false;
}
