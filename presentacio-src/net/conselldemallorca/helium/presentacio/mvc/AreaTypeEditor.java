/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Area;
import net.conselldemallorca.helium.model.service.OrganitzacioService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per les àrees
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class AreaTypeEditor extends ModelTypeEditor<Area> {

	private OrganitzacioService organitzacioService;



	public AreaTypeEditor(OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
	}

	@Override
	public String stringFromValue() {
		Area at = (Area)getValue();
		return at.getId().toString();
	}
	@Override
	public Area valueFromString(String text) {
		return organitzacioService.getAreaById(new Long(text));
	}

}
