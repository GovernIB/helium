/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO amb informació d'una dada de d'una tasca.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class TascaDto extends HeretableDto implements Serializable {
	
	private static final long serialVersionUID = 3685209683973264851L;

	public enum TipusTascaDto {
		ESTAT,
		FORM,
		SIGNATURA
	}
	private Long id;
	private String nom;
	private Long definicioProcesId;
	private TipusTascaDto tipus;
	private String missatgeInfo;
	private String missatgeWarn;
	private String nomScript;
	private String expressioDelegacio; // "" / "on"
	private String recursForm;
	private String formExtern;
	private boolean tramitacioMassiva = false;
	private boolean finalitzacioSegonPla = false;
	private boolean ambRepro = false;
	private boolean mostrarAgrupacions = false;

	private String jbpmName;
	private List<CampTascaDto> camps = new ArrayList<>();
	private List<DocumentTascaDto> documents = new ArrayList<>();
	private List<FirmaTascaDto> firmes = new ArrayList<>();
	
	
	/** Atribut que indica si la tasca és inicial. S'ha de fixar manualment aquest valor a les cerques. */
	private boolean inicial;
	
	public int getCampsCount() {
		return camps.size();
	}
	public int getDocumentsCount() {
		return documents.size();
	}
	public int getFirmesCount() {
		return firmes.size();
	}

}
