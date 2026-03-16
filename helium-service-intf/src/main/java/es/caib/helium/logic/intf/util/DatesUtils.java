package es.caib.helium.logic.intf.util;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

/** Classe amb mètodes d'utilitats comunes.
 * 
 */
public class DatesUtils {


    public static Date toLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? Date.from(offsetDateTime.toInstant()) : null;
    }

    public static OffsetDateTime toOffsetDateTime(Date data) {
        return data != null ? data.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null;
    }
}
