/**
 * 
 */
package net.conselldemallorca.helium.webapp.v3.helper;

import net.conselldemallorca.helium.core.model.hibernate.Permis;
import net.conselldemallorca.helium.webapp.mvc.util.ModelTypeEditor;

/**
 * TypeEditor pels permisos
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisTypeEditorHelper extends ModelTypeEditor<Permis> {

	@Override
	public String stringFromValue() {
		Permis p = (Permis)getValue();
		return p.getCodi();
	}
	@Override
	public Permis valueFromString(String text) {
		return new Permis(text);
	}

}
