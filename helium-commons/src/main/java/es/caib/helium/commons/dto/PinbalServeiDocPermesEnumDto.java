/**
 * 
 */
package es.caib.helium.commons.dto;

import java.io.Serializable;

/** Enumerat per tenir una llista dels serveis permesos per Pinbal quan restringeix per tipus 
 * de document. */
public enum PinbalServeiDocPermesEnumDto implements Serializable {
	DNI,
	NIF,
	CIF,
	NIE,
	PASSAPORT
}
