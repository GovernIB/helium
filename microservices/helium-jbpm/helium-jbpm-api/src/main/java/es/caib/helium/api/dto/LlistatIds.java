package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * DTO con informaciÃ³n del nÃºmero de tareas totales y un subconjunto de ellas
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class LlistatIds {

	private int count;
	private List<Long> ids;
}
