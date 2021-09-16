/**
 * 
 */
package es.caib.helium.back.command;

import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTascaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Command per afegir camps a les tasques de la definició de procés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefinicioProcesTascaVariableCommand {
	
	@NotNull(groups = {Creacio.class})
	private Long TascaId;
	@NotEmpty(groups = {Creacio.class})
	private List<Long> campId;
	private boolean readFrom;
	private boolean writeTo;
	private boolean required;
	private boolean readOnly;
	private int ampleCols;
	private int buitCols;
	private Long expedientTipusId;
	
	public static List<CampTascaDto> asCampTascaDto(DefinicioProcesTascaVariableCommand command) {
		List<CampTascaDto> campsTasques = new ArrayList<>();
		command.getCampId().forEach(c -> campsTasques.add(
				CampTascaDto.builder()
						.camp(CampDto.builder().id(c).build())
						.readFrom(command.isReadFrom())
						.writeTo(command.isWriteTo())
						.required(command.isRequired())
						.readOnly(command.isReadOnly())
						.ampleCols(command.getAmpleCols())
						.buitCols(command.getBuitCols())
						.expedientTipusId(command.getExpedientTipusId())
						.build()
		));
//		CampTascaDto dto = new CampTascaDto();
//		CampDto camp = new CampDto();
//		camp.setId(command.getCampId());
//		dto.setCamp(camp);
//		dto.setReadFrom(command.isReadFrom());
//		dto.setWriteTo(command.isWriteTo());
//		dto.setRequired(command.isRequired());
//		dto.setReadOnly(command.isReadOnly());
//		dto.setAmpleCols(command.getAmpleCols());
//		dto.setBuitCols(command.getBuitCols());
//		dto.setExpedientTipusId(command.getExpedientTipusId());
//		return dto;
		return campsTasques;
	}	
	public interface Creacio {}
}
