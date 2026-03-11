package es.caib.helium.commons.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una avis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AvisDto {
	private Long id;
	private String assumpte;
	private String missatge;
	private Date dataInici;
	private Date dataFinal;
	private String horaInici;
	private String horaFi;
	private Boolean actiu;
	private AvisNivellEnumDto avisNivell;
}
