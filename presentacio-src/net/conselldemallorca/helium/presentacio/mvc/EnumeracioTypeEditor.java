/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les enumeracions
 * 
 * @author Josep Gayà <josepg@limit.es>
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
