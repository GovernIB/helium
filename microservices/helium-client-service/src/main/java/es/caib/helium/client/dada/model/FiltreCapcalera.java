package es.caib.helium.client.dada.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/** 
 * Classe per especificar els filtres que s'aplicaran a les dades de capçalera {@link es.caib.helium.dada.domain.Expedient}
 */
@Getter
@Setter
@ToString
@JsonTypeName("filtreCapcalera")
public class FiltreCapcalera extends Filtre {
	
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
