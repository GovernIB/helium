/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Carrec;
import net.conselldemallorca.helium.model.service.OrganitzacioService;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per als càrrecs
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class CarrecTypeEditor extends ModelTypeEditor<Carrec> {

	private OrganitzacioService organitzacioService;



	public CarrecTypeEditor(OrganitzacioService organitzacioService) {
		this.organitzacioService = organitzacioService;
	}

	@Override
	public String stringFromValue() {
		Carrec at = (Carrec)getValue();
		return at.getId().toString();
	}
	@Override
	public Carrec valueFromString(String text) {
		return organitzacioService.getCarrecById(new Long(text));
	}

}
