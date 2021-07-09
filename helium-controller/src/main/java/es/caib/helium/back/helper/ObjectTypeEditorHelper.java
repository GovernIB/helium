/**
 * 
 */
package es.caib.helium.back.helper;

import java.beans.PropertyEditorSupport;

/**
 * TypeEditor per als terminis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ObjectTypeEditorHelper extends PropertyEditorSupport {

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
		return text;
	}

	public String stringFromValue() {
		Object value = getValue();
		return String.valueOf(value);
	}

}
