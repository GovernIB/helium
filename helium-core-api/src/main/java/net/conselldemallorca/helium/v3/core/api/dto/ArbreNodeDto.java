package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Node per a l'estructura de dades en forma d'arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArbreNodeDto<T> {

	public ArbreNodeDto<T> pare;
	public T dades;
	public List<ArbreNodeDto<T>> fills;
	public long count = 0;
	public boolean mostrarCount = false;

	public ArbreNodeDto(
			ArbreNodeDto<T> pare) {
		super();
		this.pare = pare;
	}
	public ArbreNodeDto(
			ArbreNodeDto<T> pare,
			T dades) {
		this(pare);
		setDades(dades);
	}

	/**
     * Retorna el pare del node actual.
     * 
     * @return El pare del node actual.
     */
	public ArbreNodeDto<T> getPare() {
		return pare;
	}
	/**
     * Retorna els fills del node actual.
     * 
     * @return Els fills del node actual.
     */
	public List<ArbreNodeDto<T>> getFills() {
		if (this.fills == null) {
			return new ArrayList<ArbreNodeDto<T>>();
		}
		return fills;
	}
	/**
	 * Estableix els fills del node actual.
	 * 
	 * @param fills La llista de fills a establir.
	 */
	public void setFills(List<ArbreNodeDto<T>> fills) {
		this.fills = fills;
	}
	/**
	 * Retorna el nombre de fills per al node actual.
	 * 
	 * @return El nombre de fills.
	 */
	public int countFills() {
		if (fills == null)
			return 0;
		return fills.size();
	}
	/**
	 * Afegeix un fill al node actual.
	 * 
	 * @param fill El fill per afegir.
	 */
	public void addFill(ArbreNodeDto<T> fill) {
		if (fills == null) {
			fills = new ArrayList<ArbreNodeDto<T>>();
		}
		fills.add(fill);
	}
	/**
	 * Esborra un fill del node actual.
	 * 
	 * @param fill El fill a esborrar.
	 */
	public void removeFill(ArbreNodeDto<T> fill) {
		if (fills != null) {
			fills.remove(fill);
		}
	}
	/**
	 * Insereix un fill a un punt determinat.
	 * 
	 * @param index El punt a on s'afegeix el fill.
	 * @param fill El fill a afegir.
	 * @throws IndexOutOfBoundsException Si el punt excedeix
	 *        el tamany de la llista.
	 */
	public void insertFillAt(int index, ArbreNodeDto<T> fill)
			throws IndexOutOfBoundsException {
		if (index == countFills()) {
			// this is really an append
			addFill(fill);
			return;
		} else {
			fills.get(index); // just to throw the exception, and stop here
			fills.add(index, fill);
		}
	}
	/**
	 * Esborra un fill d'un punt determinat.
	 * 
	 * @param index El punt d'on eliminar el fill.
	 * @throws IndexOutOfBoundsException Si el punt excedeix
	 *        el tamany de la llista.
	 */
	public void removeFillAt(int index) throws IndexOutOfBoundsException {
		fills.remove(index);
	}
	/**
	 * ObtÃ© les dades associades amb el node.
	 * 
	 * @return Les dades.
	 */
	public T getDades() {
		return this.dades;
	}
	/**
	 * Estableix les dades per al node.
	 * 
	 * @param dades Les dades.
	 */
	public void setDades(T dades) {
		this.dades = dades;
	}

	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
		mostrarCount = true;
	}

	public void addCount(long count) {
		this.count += count;
		mostrarCount = true;
	}

	public int getNivell() {
		int nivell = 0;
		ArbreNodeDto<T> nodeActual = this;
		while (nodeActual.getPare() != null) {
			nodeActual = nodeActual.getPare();
			nivell ++;
		}
		return nivell;
	}

	public boolean isMostrarCount() {
		return mostrarCount;
	}

	public ArbreNodeDto<T> clone(
			ArbreNodeDto<T> pare) {
		ArbreNodeDto<T> clon = new ArbreNodeDto<T>(
				pare,
				getDades());
		for (ArbreNodeDto<T> fill: getFills()) {
			clon.addFill(
					fill.clone(clon));
		}
		return clon;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(getDades().toString()).append(",[");
		int i = 0;
		for (ArbreNodeDto<T> e : getFills()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(e.getDades().toString());
			i++;
		}
		sb.append("]").append("}");
		return sb.toString();
	}

	private static final long serialVersionUID = -139254994389509932L;
}
