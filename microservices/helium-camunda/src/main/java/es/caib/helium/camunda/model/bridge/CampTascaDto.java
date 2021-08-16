package es.caib.helium.camunda.model.bridge;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


/**
 * DTO amb informació d'un camp d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"camp"})
public class CampTascaDto extends HeretableDto implements Serializable {

	private Long id;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int order;
	private int ampleCols;
	private int buitCols;

	private CampDto camp;
	
	/** Quan es crea una relació entre un camp i la definició de procés pot ser que el camp sigui 
	 * del tipus d'expedient i la tasca sigui de la definició de procés del tipus expedient pare heretat. Aquest
	 * camp fa referència al tipus d'expedient que posseix la relació camp-tasca.
	 */
	private Long expedientTipusId;

	public CampTascaDto(
			CampDto camp,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly,
			int order,
			int ampleCols,
			int buitCols) {
		this.camp = camp;
		this.readFrom = readFrom;
		this.writeTo = writeTo;
		this.required = required;
		this.readOnly = readOnly;
		this.order = order;
		this.ampleCols = ampleCols;
		this.buitCols = buitCols;
	}

	private static final long serialVersionUID = 1L;

}
