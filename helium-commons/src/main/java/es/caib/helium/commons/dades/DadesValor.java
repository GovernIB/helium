package es.caib.helium.commons.dades;

import lombok.Getter;
import lombok.Setter;

/**
 * Objecte per mapejar el JSON del valor d'una dada del servei de dades.
 *
 */


@Getter
@Setter
public class DadesValor {

	/** Valor contingut de l'objecte, pot ser un valor simple o una llista. */
	private Object v;
	/** Tipus de la dada. */
	private DadaTipusEnum t;
	/** Valor de caché opcional pel cas dels dominis. */
	private Object cache;

	public DadesValor() {
	}

	public DadesValor(Object v, DadaTipusEnum t) {
		this.v = v;
		this.t = t;
	}

	public DadesValor(Object v, DadaTipusEnum t, Object cache) {
		this.v = v;
		this.t = t;
		this.cache = cache;
	}
}
