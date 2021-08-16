package es.caib.helium.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


/**
 * DTO amb informació d'un camp d'una tasca de la
 * definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

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

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((camp == null) ? 0 : camp.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		CampTascaDto other = (CampTascaDto) obj;
//		if (camp == null) {
//			if (other.camp != null)
//				return false;
//		} else if (!camp.equals(other.camp))
//			return false;
//		return true;
//	}

	private static final long serialVersionUID = 1L;

}
