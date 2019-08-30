/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les enumeracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioTypeEditor extends ModelTypeEditor<Enumeracio> {

	private DissenyService dissenyService;



	public EnumeracioTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		Enumeracio e = (Enumeracio)getValue();
		return e.getId().toString();
	}
	@Override
	public Enumeracio valueFromString(String text) {
		return dissenyService.getEnumeracioById(new Long(text));
	}

}
