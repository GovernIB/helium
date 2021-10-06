package es.caib.helium.client.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/** Implementació del conversor de String a Date amb el mateix format de data del JSON. És necessari
 * perquè en cas contrari el patch de dates no funciona.
 */
public class StringToDateConverter implements Converter<String, Date> {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	@Override
	public Date convert(String source) {
		Date date = null;
		if (source != null ) {
			try {
				date = sdf.parse(source);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return date;
	}
}
