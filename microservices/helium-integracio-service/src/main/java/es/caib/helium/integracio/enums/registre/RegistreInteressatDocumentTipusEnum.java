package es.caib.helium.integracio.enums.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreInteressatDocumentTipusEnum {

	NIF("N"),
	CIF("C"),
	PASSAPORT("P"),
	DOCUMENT_IDENTIFICACIO_EXTRANGERS("E"),
	ALTRES_PERSONES_FISIQUES("X"),
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
