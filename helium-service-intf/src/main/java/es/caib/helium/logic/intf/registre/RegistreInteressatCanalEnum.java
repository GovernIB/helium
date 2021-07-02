/**
 * 
 */
package es.caib.helium.logic.intf.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del canal de notificació
 * preferent d'un interessat del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreInteressatCanalEnum {

	POSTAL("01"),
	ADRECA_ELEC_HAB("02"),
	COMPAREIXENCA_ELEC("03");

	private final String valor;
	private RegistreInteressatCanalEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreInteressatCanalEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreInteressatCanalEnum>();
		for (RegistreInteressatCanalEnum s: EnumSet.allOf(RegistreInteressatCanalEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreInteressatCanalEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
