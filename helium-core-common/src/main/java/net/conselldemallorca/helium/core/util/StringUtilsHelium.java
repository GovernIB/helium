package net.conselldemallorca.helium.core.util;

public class StringUtilsHelium {

	public static String retallaString(String in, int midaMax) {
		if (in!=null && in.length()>midaMax) {
			return in.substring(0, midaMax);
		} else {
			return in;
		}
	}
}
