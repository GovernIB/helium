/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.AreaTipus;
import net.conselldemallorca.helium.model.service.OrganitzacioService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per als tipus d'àrea
 * 
 * @author Josep Gayà <josepg@limit.es>
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
