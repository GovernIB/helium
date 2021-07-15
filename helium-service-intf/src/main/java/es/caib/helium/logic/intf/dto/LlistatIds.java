package es.caib.helium.logic.intf.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * DTO con informaciÃ³n del nÃºmero de tareas totales y un subconjunto de ellas
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
public class LlistatIds {

	private int count;
	private List<Long> ids;

}
