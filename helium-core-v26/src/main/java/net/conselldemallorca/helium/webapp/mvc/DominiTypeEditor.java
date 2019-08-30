/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor pels dominis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DominiTypeEditor extends ModelTypeEditor<Domini> {

	private DissenyService dissenyService;



	public DominiTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		Domini dp = (Domini)getValue();
		return dp.getId().toString();
	}
	@Override
	public Domini valueFromString(String text) {
		return dissenyService.getDominiById(new Long(text));
	}

}
