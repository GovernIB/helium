package net.conselldemallorca.helium.core.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtilsHelium {

	public static String retallaString(String in, int midaMax) {
		if (in!=null && in.length()>midaMax) {
			return in.substring(0, midaMax);
		} else {
			return in;
		}
	}
	
	public static String abreuja(String text, int maxim) {
		if (text != null && (text.length() > maxim) && (maxim - 3 > 0)) {
			text = text.substring(0, maxim - 3) + "...";
		}
		return text;
	}
	
	public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
	
	private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public static String formatDateTime(Date date) {
		return date != null ? df.format(date) : "";
	}
	
	/** Passa de bytes a String
	 * 
	 * @param bytes
	 * @return
	 */
	private static String[] tamanyUnitats = {"b", "Kb", "Mb", "Gb", "Tb", "Pb"};
	
	public static String formatBytes(long bytes) {
		long valor = bytes;
		int i = 0;
		while (bytes > Math.pow(1024, i + 1) 
				&& i < tamanyUnitats.length - 1) {
			valor = valor / 1024;
			i++;
		}
		DecimalFormat df = new DecimalFormat("#,###.##");
		return df.format(valor) + " " + tamanyUnitats[i];
	}

}
