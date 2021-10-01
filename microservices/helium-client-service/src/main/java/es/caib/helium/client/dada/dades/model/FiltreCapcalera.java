package es.caib.helium.client.dada.dades.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


/** 
 * Classe per especificar els filtres que s'aplicaran a les dades de capçalera {@link es.caib.helium.dada.domain.Expedient}
 */
@Getter
@Setter
@ToString
@JsonTypeName(FiltreCapcalera.JSON_TYPE_NAME)
public class FiltreCapcalera extends Filtre {

	public static final String JSON_TYPE_NAME = "filtreCapcalera";

	private Long expedientId;
	private String numero;
	private String titol;
	private Long procesPrincipalId;
	private Integer estatId;
	private Date dataIniciInicial;
	private Date dataIniciFinal;
	private Date dataFiInicial;
	private Date dataFiFinal;
}
