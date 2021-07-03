/**
 * 
 */
package es.caib.helium.logic.intf.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus NTI d'un
 * document annex a una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreAnnexNtiTipusDocumentEnum {

	RESOLUCIO("TD01"),
	ACORD("TD02"),
	CONTRACTE("TD03"),
	CONVENI("TD04"),
	DECLARACIO("TD05"),
	COMUNICACIO("TD06"),
	NOTIFICACIO("TD07"),
	PUBLICACIO("TD08"),
	ACUS_REBUT("TD09"),
	ACTE("TD10"),
	CERTIFICAT("TD11"),
	DILIGENCIA("TD12"),
	INFORME("TD13"),
	SOLICITUD("TD14"),
	DENUNCIA("TD15"),
	ALEGACIONS("TD16"),
	RECURSOS("TD17"),
	COMUNICACIO_CIUTADA("TD18"),
	FACTURA("TD19"),
	ALTRES_INCAUTATS("TD20"),
	ALTRES("TD99");

	private final String valor;
	private RegistreAnnexNtiTipusDocumentEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreAnnexNtiTipusDocumentEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreAnnexNtiTipusDocumentEnum>();
		for (RegistreAnnexNtiTipusDocumentEnum s: EnumSet.allOf(RegistreAnnexNtiTipusDocumentEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreAnnexNtiTipusDocumentEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
