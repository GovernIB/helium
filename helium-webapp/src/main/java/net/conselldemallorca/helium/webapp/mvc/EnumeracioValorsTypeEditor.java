/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per els valors de les enumeracions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EnumeracioValorsTypeEditor extends ModelTypeEditor<EnumeracioValors> {

	private DissenyService dissenyService;



	public EnumeracioValorsTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		EnumeracioValors e = (EnumeracioValors)getValue();
		return e.getId().toString();
	}
	@Override
	public EnumeracioValors valueFromString(String text) {
		return dissenyService.getEnumeracioValorsById(new Long(text));
	}

}
