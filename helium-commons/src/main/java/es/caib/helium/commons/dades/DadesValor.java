package es.caib.helium.commons.dades;

/**
 * Objecte per mapejar el JSON del valor d'una dada del servei de dades.
 * 
 */
public class DadesValor {

	/** Valor contingut de l'objecte, pot ser un valor simple o una llista. */
	private Object v;
	/** Tipus de la dada. */
	private String t;
	/** Valor de caché opcional pel cas dels dominis. */
	private Object cache;

	public DadesValor() {
	}
	
	public DadesValor(Object v, String t) {
		this.v = v;
		this.t = t;
	}

	public DadesValor(Object v, String t, Object cache) {
		this.v = v;
		this.t = t;
		this.cache = cache;
	}

	public Object getV() {
		return v;
	}

	public void setV(Object v) {
		this.v = v;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public Object getCache() {
		return cache;
	}

	public void setCache(Object cache) {
		this.cache = cache;
	}
}
