/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per als camps
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampTypeEditor extends ModelTypeEditor<Camp> {

	private DissenyService dissenyService;



	public CampTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		Camp c = (Camp)getValue();
		return c.getId().toString();
	}
	@Override
	public Camp valueFromString(String text) {
		return dissenyService.getCampById(new Long(text));
	}

}
