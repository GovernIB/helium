/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import net.conselldemallorca.helium.core.model.hibernate.Persona;
import net.conselldemallorca.helium.core.model.service.PersonaService;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor per a les persones
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonaTypeEditor extends ModelTypeEditor<Persona> {

	private PersonaService personaService;



	public PersonaTypeEditor(PersonaService personaService) {
		this.personaService = personaService;
	}

	@Override
	public String stringFromValue() {
		Persona p = (Persona)getValue();
		return p.getId().toString();
	}
	@Override
	public Persona valueFromString(String text) {
		return personaService.getPerfilInfo(new Long(text));
	}

}
