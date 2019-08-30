/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les agrupacions de camps
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CampAgrupacioTypeEditor extends ModelTypeEditor<CampAgrupacio> {

	private DissenyService dissenyService;



	public CampAgrupacioTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		CampAgrupacio ca = (CampAgrupacio)getValue();
		return ca.getId().toString();
	}
	@Override
	public CampAgrupacio valueFromString(String text) {
		return dissenyService.getCampAgrupacioById(new Long(text));
	}

}
