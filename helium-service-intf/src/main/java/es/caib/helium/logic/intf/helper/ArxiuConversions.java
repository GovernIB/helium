package es.caib.helium.logic.intf.helper;

import es.caib.helium.commons.dto.NtiTipoFirmaEnumDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Mètodes comuns per convertir tipus i enumeracions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuConversions {

	/** Map amb el mapeig dels perfils de firma cap als perfils admesos per l'Arxiu. */
	private static Map<String, String> mapPerfilsFirma = new HashMap<String, String>();
	static {
		mapPerfilsFirma.put("AdES-BES", "BES");
		mapPerfilsFirma.put("AdES-EPES", "EPES");
		mapPerfilsFirma.put("AdES-T", "T");
		mapPerfilsFirma.put("AdES-C", "C");
		mapPerfilsFirma.put("AdES-X", "X");
		mapPerfilsFirma.put("AdES-X1", "X");
		mapPerfilsFirma.put("AdES-X2", "X");
		mapPerfilsFirma.put("AdES-XL", "XL");
		mapPerfilsFirma.put("AdES-XL1", "XL");
		mapPerfilsFirma.put("AdES-XL2", "XL");
		mapPerfilsFirma.put("AdES-A", "A");
		mapPerfilsFirma.put("PAdES-LTV", "LTV");
		mapPerfilsFirma.put("PAdES-Basic", "BES");
	}

	/** Mapeja els diferents perfils de firma que pot retornar el plugin de firma simple o de validació cap
	 * als perfils admesos per l'Arxiu.
	 *
	 * @param perfil
	 * @return
	 */
	public static String toPerfilFirmaArxiu(String perfil) {
		if (mapPerfilsFirma.containsKey(perfil))
			perfil = mapPerfilsFirma.get(perfil);
		return perfil;
	}

	/** Mètode per obrtenir el tipus de firma a partir del tipus i format retornat pel plugin de validació de firmes. */
	public static String toFirmaTipus(String tipus, String format) {

		String tipusFirma = null;
		if ("XAdES".equals(tipus) && "explicit/detached".equals(format)) {
			tipusFirma = "TF02"; // NtiTipoFirmaEnumDto.XADES_DET
		} else if ("XAdES".equals(tipus) && "implicit_enveloping/attached".equals(format)) {
			tipusFirma = "TF03"; // NtiTipoFirmaEnumDto.XADES_ENV;
		} else if ("CAdES".equals(tipus) && "explicit/detached".equals(format)) {
			tipusFirma = "TF04"; // NtiTipoFirmaEnumDto.CADES_DET;
		} else if ("CAdES".equals(tipus) && "implicit_enveloping/attached".equals(format)) {
			tipusFirma = "TF05"; // NtiTipoFirmaEnumDto.CADES_ATT;
		} else if ("PAdES".equals(tipus) || "implicit_enveloped/attached".equals(format)) {
				tipusFirma = "TF06"; // NtiTipoFirmaEnumDto.PADES
		} else {
			tipusFirma = tipus;
		}
		return tipusFirma;
	}

	/** Mètode per obrtenir el tipus de firma a partir del tipus i mode retornat pel plugin de validació de firmes. */
	public static String toFirmaTipusPortafib(String tipus, Integer mode) {

		String tipusFirma = null;
		if ("XAdES".equals(tipus) && mode.equals(1)) {
			tipusFirma = "TF02"; // NtiTipoFirmaEnumDto.XADES_DET
		} else if ("XAdES".equals(tipus) && mode.equals(3)) {
			tipusFirma = "TF03"; // NtiTipoFirmaEnumDto.XADES_ENV;
		} else if ("CAdES".equals(tipus) && mode.equals(1)) {
			tipusFirma = "TF04"; // NtiTipoFirmaEnumDto.CADES_DET;
		} else if ("CAdES".equals(tipus) && mode.equals(3)) {
			tipusFirma = "TF05"; // NtiTipoFirmaEnumDto.CADES_ATT;
		} else if ("PAdES".equals(tipus) || mode.equals(0)) {
				tipusFirma = "TF06"; // NtiTipoFirmaEnumDto.PADES
		} else {
			tipusFirma = tipus;
		}
		return tipusFirma;
	}

	public static NtiTipoFirmaEnumDto toArxiuFirmaTipus(String tipusFirmaEni) {

		switch (tipusFirmaEni) {
			case "TF01":
				return NtiTipoFirmaEnumDto.CSV;
			case "TF02":
				return NtiTipoFirmaEnumDto.XADES_DET;
			case "TF03":
				return NtiTipoFirmaEnumDto.XADES_ENV;
			case "TF04":
				return NtiTipoFirmaEnumDto.CADES_DET;
			case "TF05":
				return NtiTipoFirmaEnumDto.CADES_ATT;
			case "TF06":
				return NtiTipoFirmaEnumDto.PADES;
			case "TF07":
				return NtiTipoFirmaEnumDto.SMIME;
			case "TF08":
				return NtiTipoFirmaEnumDto.ODT;
			case "TF09":
				return NtiTipoFirmaEnumDto.OOXML;
			default:
				return null;
		}
	}

	public static String toNtiTipoFirmaEnumDto(NtiTipoFirmaEnumDto tipusFirmaEni) {

		switch (tipusFirmaEni.name()) {
			case "CSV":
				return "TF01";
			case "XADES_DET":
				return"TF02";
			case "XADES_ENV":
				return "TF03";
			case "CADES_DET":
				return "TF04";
			case "CADES_ATT":
				return "TF05";
			case "PADES":
				return "TF06";
			case "SMIME":
				return "TF07";
			case "ODT":
				return "TF08";
			case "OOXML":
				return "TF09";
			default:
				return null;
		}
	}

}
