/**
 * 
 */
package net.conselldemallorca.helium.presentacio.mvc;

import net.conselldemallorca.helium.model.hibernate.Permis;
import net.conselldemallorca.helium.presentacio.mvc.util.ModelTypeEditor;

/**
 * TypeEditor pels permisos
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PermisTypeEditor extends ModelTypeEditor<Permis> {

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
