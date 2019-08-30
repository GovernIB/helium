/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.util;

import java.beans.PropertyEditorSupport;

/**
 * Editor per qualsevol classe del model
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ModelTypeEditor<T> extends PropertyEditorSupport {

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

	public abstract String stringFromValue();
	public abstract T valueFromString(String text);

}
