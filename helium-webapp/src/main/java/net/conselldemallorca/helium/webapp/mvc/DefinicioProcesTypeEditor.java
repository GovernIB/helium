/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les definicions de procés
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DefinicioProcesTypeEditor extends ModelTypeEditor<DefinicioProces> {

	private DissenyService dissenyService;



	public DefinicioProcesTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		DefinicioProces dp = (DefinicioProces)getValue();
		return dp.getId().toString();
	}
	@Override
	public DefinicioProces valueFromString(String text) {
		return dissenyService.getById(new Long(text));
	}

}
