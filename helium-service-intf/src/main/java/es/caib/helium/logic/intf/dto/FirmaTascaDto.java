/**
 * 
 */
package es.caib.helium.logic.intf.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Objecte de domini que representa la firma d'un document a una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FirmaTascaDto extends HeretableDto {

	private static final long serialVersionUID = 1831839772913769166L;

	private Long id;
	private boolean required;
	private int order;
	private DocumentDto document;
	private ExpedientTascaDto tasca;

	
	/** Quan es crea una relació entre un camp i la definició de procés pot ser que el camp sigui 
	 * del tipus d'expedient i la tasca sigui de la definició de procés del tipus expedient pare heretat. Aquest
	 * camp fa referència al tipus d'expedient que posseix la relació camp-tasca.
	 */
	private Long expedientTipusId;

	public FirmaTascaDto(
			DocumentDto document,
			ExpedientTascaDto tasca,
			boolean required,
			int order) {
		this.document = document;
		this.tasca = tasca;
		this.required = required;
		this.order = order;
	}

//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((document == null) ? 0 : document.hashCode());
//		result = prime * result + ((tasca == null) ? 0 : tasca.hashCode());
//		return result;
//	}
//
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		FirmaTascaDto other = (FirmaTascaDto) obj;
//		if (document == null) {
//			if (other.document != null)
//				return false;
//		} else if (!document.equals(other.document))
//			return false;
//		if (tasca == null) {
//			if (other.tasca != null)
//				return false;
//		} else if (!tasca.equals(other.tasca))
//			return false;
//		return true;
//	}
}
