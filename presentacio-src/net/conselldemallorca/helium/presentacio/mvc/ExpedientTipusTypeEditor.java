/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.service.DissenyService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per als tipus d'expedient
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class ExpedientTipusTypeEditor extends ModelTypeEditor<ExpedientTipus> {

	private DissenyService dissenyService;



	public ExpedientTipusTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		ExpedientTipus e = (ExpedientTipus)getValue();
		return e.getId().toString();
	}
	@Override
	public ExpedientTipus valueFromString(String text) {
		return dissenyService.getExpedientTipusById(new Long(text));
	}

}
