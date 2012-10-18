package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les consultes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaTypeEditor extends ModelTypeEditor<Consulta> {

	private DissenyService dissenyService;



	public ConsultaTypeEditor(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}

	@Override
	public String stringFromValue() {
		Consulta c = (Consulta)getValue();
		return c.getId().toString();
	}
	@Override
	public Consulta valueFromString(String text) {
		return dissenyService.getConsultaById(new Long(text));
	}

}
