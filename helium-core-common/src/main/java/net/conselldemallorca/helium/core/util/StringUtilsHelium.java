package net.conselldemallorca.helium.core.util;

public class StringUtilsHelium {

	public static String retallaString(String in, int midaMax) {
		if (in!=null && in.length()>midaMax) {
			return in.substring(0, midaMax);
		} else {
			return in;
		}
	}
	
	public static String abreuja(String text, int maxim) {
		if ((text.length() > maxim) && (maxim - 3 > 0)) {
			text = text.substring(0, maxim - 3) + "...";
		}
		return text;
	}
	
	public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
