/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Tasca;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les tasques
 * 
 * @author Josep Gayà <josepg@limit.es>
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
