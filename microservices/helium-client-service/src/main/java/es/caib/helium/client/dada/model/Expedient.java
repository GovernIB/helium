package es.caib.helium.client.dada.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Representació de la col·leccio {@link es.caib.helium.enums.Collections#EXPEDIENT}
 * S'utilitza tant com a model per Mongo com per Dto.
 */
@Getter
@Setter
@ToString
public class Expedient {

	private String id;
	private Long expedientId;
	private Long entornId;
	private Long tipusId;
	private String numero;
	private String titol;
	private Long procesPrincipalId;
	private Integer estatId;
	private Date dataInici;
	private Date dataFi;

	private List<Dada> dades;
}
