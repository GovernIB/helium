/**
 * 
 */
package net.conselldemallorca.helium.api.dto.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus de document
 * d'identitat d'un interessat del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreInteressatDocumentTipusEnum {

	NIF("N"),
	CIF("C"),
	PASSAPORT("P"),
	ESTRANGER("E"),
	ALTRES("X"),
	CODI_ORIGEN("O");

	private final String valor;
	private RegistreInteressatDocumentTipusEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreInteressatDocumentTipusEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreInteressatDocumentTipusEnum>();
		for (RegistreInteressatDocumentTipusEnum s: EnumSet.allOf(RegistreInteressatDocumentTipusEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreInteressatDocumentTipusEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
