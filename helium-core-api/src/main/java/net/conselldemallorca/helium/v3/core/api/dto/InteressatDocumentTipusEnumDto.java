/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * Enumeració amb els possibles tipus de documentació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InteressatDocumentTipusEnumDto {
	NIF ("N"),
	CIF ("C"),
	PASSAPORT ("P"),
	DOCUMENT_IDENTIFICATIU_ESTRANGERS ("E"),
	ALTRES_DE_PERSONA_FISICA ("X"),
	CODI_ORIGEN ("O");
	
	private final String valor;
	private InteressatDocumentTipusEnumDto(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, InteressatDocumentTipusEnumDto> lookup;
	static {
		lookup = new HashMap<String, InteressatDocumentTipusEnumDto>();
		for (InteressatDocumentTipusEnumDto s: EnumSet.allOf(InteressatDocumentTipusEnumDto.class))
			lookup.put(s.getValor(), s);
	}
	public static InteressatDocumentTipusEnumDto valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }
}
