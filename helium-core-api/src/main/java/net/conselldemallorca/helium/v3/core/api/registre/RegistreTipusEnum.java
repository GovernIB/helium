/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus d'una anotació
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreTipusEnum {

	ENTRADA("0"),
	SORTIDA("1");

	private final String valor;
	private RegistreTipusEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreTipusEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreTipusEnum>();
		for (RegistreTipusEnum s: EnumSet.allOf(RegistreTipusEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreTipusEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
