/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Objecte de domini que representa una sequencia anual d'un tipus d'expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"any", "expedientTipus"})
public class SequenciaAnyDto implements Serializable {

	private Long id;
	private ExpedientTipusDto expedientTipus;
	private Integer any;
	private Long sequencia;


	public SequenciaAnyDto(ExpedientTipusDto expedientTipus, Integer any, Long sequencia) {
		this.expedientTipus = expedientTipus;
		this.any = any;
		this.sequencia = sequencia;
	}

	private static final long serialVersionUID = 1L;
}
