/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus SICRES d'un
 * document annex a una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreAnnexSicresTipusDocumentEnum {

	FORM("01"),
	FORM_ADJUNT("02"),
	INTERN("03");

	private final String valor;
	private RegistreAnnexSicresTipusDocumentEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreAnnexSicresTipusDocumentEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreAnnexSicresTipusDocumentEnum>();
		for (RegistreAnnexSicresTipusDocumentEnum s: EnumSet.allOf(RegistreAnnexSicresTipusDocumentEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreAnnexSicresTipusDocumentEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
