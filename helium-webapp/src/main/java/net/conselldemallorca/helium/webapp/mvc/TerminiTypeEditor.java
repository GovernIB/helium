/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc;

import java.beans.PropertyEditorSupport;

import net.conselldemallorca.helium.jbpm3.integracio.Termini;

/**
 * TypeEditor per als terminis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TerminiTypeEditor extends PropertyEditorSupport {

	public void setAsText(String text) {
		if (text == null || text.length() == 0)
			setValue(null);
		else
			setValue(valueFromString(text));
	}
	public String getAsText() {
		if (getValue() == null) return null;
		return stringFromValue();
	}

	private Object valueFromString(String text) {
		String[] parts = text.split("/");
		Termini termini = new Termini();
		if (parts.length == 3) {
			termini.setAnys(new Integer(parts[0]));
			termini.setMesos(new Integer(parts[1]));
			termini.setDies(new Integer(parts[2]));
		}
		return termini;
	}

	public String stringFromValue() {
		Termini value = (Termini)getValue();
		return value.getAnys() + "/" + value.getMesos() + "/" + value.getDies();
	}

}
