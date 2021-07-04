package es.caib.helium.logic.intf.dto;

import java.util.List;


/**
 * DTO con informaciÃ³n del nÃºmero de tareas totales y un subconjunto de ellas
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistatIds {

	private int count;
	private List<Long> ids;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
}
