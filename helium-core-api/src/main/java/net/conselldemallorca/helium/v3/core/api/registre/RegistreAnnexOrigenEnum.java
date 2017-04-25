/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors de l'origen d'un
 * annex del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreAnnexOrigenEnum {

	CIUTADA("0"),
	ADMINISTRACIO("1");

	private final String valor;
	private RegistreAnnexOrigenEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreAnnexOrigenEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreAnnexOrigenEnum>();
		for (RegistreAnnexOrigenEnum s: EnumSet.allOf(RegistreAnnexOrigenEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreAnnexOrigenEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }
	
}
