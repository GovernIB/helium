/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les tasques
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TascaTypeEditor extends ModelTypeEditor<Tasca> {

	private DissenyService dissenyService;



	public TascaTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		Tasca t = (Tasca)getValue();
		return t.getId().toString();
	}
	@Override
	public Tasca valueFromString(String text) {
		return dissenyService.getTascaById(new Long(text));
	}

}
