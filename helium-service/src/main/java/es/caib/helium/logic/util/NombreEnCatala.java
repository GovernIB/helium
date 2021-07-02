/**
 * 
 */
package es.caib.helium.logic.util;

import java.math.BigDecimal;


/**
 * Transforma quantitas numèriques en text en català
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NombreEnCatala {

	public static final int MONEDA_PESETA = 0;
	public static final int MONEDA_EURO = 1;
	public static final int MONEDA_DOLAR = 2;

	public static final int SEXE_MASCULI = 0;
	public static final int SEXE_FEMENI = 1;

	private static final String[][] TEXT_MONEDA = new String[][] {
		{"pesseta", "pessetes", "cèntim", "cèntims"},
		{"euro", "euros", "cèntim", "cèntims"},
		{"dòlar", "dòlars", "centau", "centaus"}
	};
	private static final int[][] SEXE_MONEDA = new int[][] {
		{SEXE_FEMENI, SEXE_MASCULI},
		{SEXE_MASCULI, SEXE_MASCULI},
		{SEXE_MASCULI, SEXE_MASCULI}
	};

	private static final String TEXT_ZERO = "zero";

	private static final String[] TEXT_CENT = {"cent", "cents", "centes"};

	private static final String[][] TEXT_FINS_A_VINT = new String[][] {
			{"", ""},
			{"un", "una"},
			{"dos", "dues"},
			{"tres", null},
			{"quatre", null},
			{"cinc", null},
			{"sis", null},
			{"set", null},
			{"vuit", null},
			{"nou", null},
			{"deu", null},
			{"onze", null},
			{"dotze", null},
			{"tretze", null},
			{"catorze", null},
			{"quinze", null},
			{"setze", null},
			{"desset", null},
			{"devuit", null},
			{"denou", null}};
	private static final String[] TEXT_DECENES = new String[] {
		"",
		"deu",
		"vint",
		"trenta",
		"quaranta",
		"cinquanta",
		"seixanta",
		"setanta",
		"vuitanta",
		"noranta"};

	private static final String[][] TEXT_GRUPS = new String[][] {
		{"", ""},
		{"milió", "milions"},
		{"bilió", "bilions"},
		{"trilió", "trilions"},
		{"cuatrilió", "cuatrilions"},
		{"quintilió", "quintilions"},
		{"sexilió", "sexilions"},
		{"septilió", "septilions"},
		{"octilió", "octilions"},
		{"nonilió", "nonilions"}
	};



	public static String escriureNombre(
			long nombre,
			int sexe) {
		StringBuffer sb = new StringBuffer();
		long2text(nombre, sb, 0, sexe, true);
		if (nombre < 0)
			sb.insert(0, "menys ");
		return sb.toString().trim();
	}

	public static String escriurePreu(
			BigDecimal preu,
			int numDecimals,
			int moneda) {
		long partSencera = preu.longValue();
		long partDecimal = 0;
		if (numDecimals != -1) {
			preu.setScale(numDecimals, BigDecimal.ROUND_HALF_UP);
			partDecimal = preu.subtract(new BigDecimal(partSencera)).multiply(new BigDecimal(Math.pow(10, numDecimals))).longValue();
		}
		StringBuffer sbSencer = new StringBuffer();
		long2text(partSencera, sbSencer, 0, SEXE_MONEDA[moneda][0], true);
		StringBuffer sbDecimal = new StringBuffer();
		long2text(partDecimal, sbDecimal, 0, SEXE_MONEDA[moneda][1], true);
		StringBuffer sbPreu = new StringBuffer();
		if (partSencera < 0)
			sbPreu.insert(0, "menys ");
		sbPreu.append(sbSencer.toString().trim());
		sbPreu.append(" ");
		if (partSencera == 1)
			sbPreu.append(TEXT_MONEDA[moneda][0]);
		else
			sbPreu.append(TEXT_MONEDA[moneda][1]);
		if (partDecimal != 0) {
			sbPreu.append(" amb ");
			sbPreu.append(sbDecimal);
			sbPreu.append(" ");
			if (partDecimal == 1)
				sbPreu.append(TEXT_MONEDA[moneda][2]);
			else
				sbPreu.append(TEXT_MONEDA[moneda][3]);
		}
		return sbPreu.toString().trim();
	}



	private static void long2text(
			long nombre,
			StringBuffer sb,
			int grupIndex,
			int sexe,
			boolean grupAnteriorZero) {
		if (nombre < 0)
			nombre = -nombre;
		if (nombre == 0 && grupIndex == 0)
			sb.append(TEXT_ZERO);
		String nombreStr = Long.toString(nombre);
		if (nombreStr.length() > 3 * grupIndex) {
			String grupString = grupAmbIndex(nombreStr, grupIndex);
			int grup = Integer.parseInt(grupString);
			int centenes = Integer.parseInt(grupString.substring(0, 1));
			int decenes = Integer.parseInt(grupString.substring(1, 2));
			int unitats = Integer.parseInt(grupString.substring(2, 3));
			int decenesAmbUnitats = decenes * 10 + unitats;
			if (grup > 0) {
				if (grupIndex > 0)
					sb.insert(0, " ");
				if (grupIndex % 2 == 1) {
					if (grupAnteriorZero) {
						sb.insert(0, textMilions(grupIndex, false));
						sb.insert(0, " ");
					}
					sb.insert(0, "mil");
				} else {
					sb.insert(0, textMilions(grupIndex, grup == 1));
				}
				if (grupIndex > 0)
					sb.insert(0, " ");
				if (grupIndex % 2 != 1 || grup != 1) { // perque no surti "1100: un mil cent"
					if (decenesAmbUnitats < 20) {
						sb.insert(0, textMenorVint(decenesAmbUnitats, sexe));
					} else {
						if (unitats > 0) {
							sb.insert(0, textMenorVint(unitats, sexe));
							if (decenes == 2)
								sb.insert(0, "i-");
							sb.insert(0, "-");
						}
						sb.insert(0, TEXT_DECENES[decenes]);
					}
					if (centenes > 0) {
						if (decenesAmbUnitats > 0)
							sb.insert(0, " ");
						if (centenes > 1) {
							sb.insert(0, TEXT_CENT[(sexe == SEXE_FEMENI) ? 2 : 1]);
							sb.insert(0, "-");
							sb.insert(0, textMenorVint(centenes, sexe));
						} else {
							sb.insert(0, TEXT_CENT[0]);
						}
					}
				}
			}
			long2text(nombre, sb, grupIndex + 1, SEXE_MASCULI, grup == 0);
		}
	}

	private static String grupAmbIndex(String nombreStr, int grupIndex) {
		int endIndex = nombreStr.length() - (3 * grupIndex);
		int beginIndex = endIndex - 3;
		if (beginIndex < 0) beginIndex = 0;

		long grup = Long.parseLong(nombreStr.substring(beginIndex, endIndex));
		java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("000");
		return decimalFormat.format(grup);
	}

	private static String textMenorVint(int index, int sexe) {
		if (TEXT_FINS_A_VINT[index][sexe] == null)
			return TEXT_FINS_A_VINT[index][0];
		else
			return TEXT_FINS_A_VINT[index][sexe];
	}

	private static String textMilions(int grupIndex, boolean singular) {
		if (grupIndex / 2 < TEXT_GRUPS.length)
			return TEXT_GRUPS[grupIndex / 2][singular ? 0 : 1];
		else
			return (grupIndex / 2) + (singular ? "lió" : "lions");
	}

}
