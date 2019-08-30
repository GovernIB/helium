/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.core.model.service.OrganitzacioService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per als tipus d'àrea
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AreaTipusTypeEditor extends ModelTypeEditor<AreaTipus> {

	private OrganitzacioService organitzacioService;



	public AreaTipusTypeEditor(OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
	}

	@Override
	public String stringFromValue() {
		AreaTipus at = (AreaTipus)getValue();
		return at.getId().toString();
	}
	@Override
	public AreaTipus valueFromString(String text) {
		return organitzacioService.getAreaTipusById(new Long(text));
	}

}
