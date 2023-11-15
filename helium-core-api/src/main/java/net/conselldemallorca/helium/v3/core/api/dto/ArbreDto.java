package net.conselldemallorca.helium.v3.core.api.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * Estructura de dades en forma d'arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArbreDto<T> {

	private ArbreNodeDto<T> arrel;
	private boolean ingnorarArrel;

	public ArbreDto(boolean ingnorarArrel) {
		super();
		this.ingnorarArrel = ingnorarArrel;
	}

	/**
	 * Retorna el Node arrel de l'arbre.
	 * 
	 * @return el node arrel.
	 */
	public ArbreNodeDto<T> getArrel() {
		return this.arrel;
	}
	/**
	 * Estableix el Node arrel de l'arbre.
	 * 
	 * @param rootNode el node arrel a establir.
	 */
	public void setArrel(ArbreNodeDto<T> arrel) {
		this.arrel = arrel;
	}

	public boolean isIngnorarArrel() {
		return ingnorarArrel;
	}

	public void setIngnorarArrel(boolean ingnorarArrel) {
		this.ingnorarArrel = ingnorarArrel;
	}

	/**
	 * Retorna l'arbre com una llista de objectes NodeDto<T>. Els elements de la
	 * llista es generen recorreguent l'arbre en l'ordre pre-establert.
	 * 
	 * @return una llista List<Node<T>>.
	 */
	public List<ArbreNodeDto<T>> toList() {
		List<ArbreNodeDto<T>> list = new ArrayList<ArbreNodeDto<T>>();
		if (arrel != null)
			recorregut(arrel, list);
		if (ingnorarArrel && list.size() > 0)
			list.remove(0);
		return list;
	}

	/**
	 * Retorna l'arbre com una llista de objectes NodeDto<T>. Els elements de la
	 * llista es generen recorreguent l'arbre en l'ordre pre-establert.
	 * 
	 * @return una llista List<Node<T>>.
	 */
	public List<T> toDadesList() {
		List<T> list = new ArrayList<T>();
		if (arrel != null)
			recorregutDades(arrel, list);
		if (ingnorarArrel && list.size() > 0)
			list.remove(0);
		return list;
	}

	/**
	 * Retorna una representaciÃ³ textual de l'arbre. Els elements es generen
	 * recorreguent l'arbre en l'odre pre-establert.
	 * 
	 * @return la representaciÃ³ textual de l'arbre.
	 */
	public String toString() {
		return toList().toString();
	}

	/**
	 * Clona l'arbre.
	 * 
	 * @return un clon de l'arbre.
	 */
	public ArbreDto<T> clone() {
		ArbreDto<T> clon = new ArbreDto<T>(ingnorarArrel);
		clon.setArrel(arrel.clone(null));
		return clon;
	}



	private void recorregut(ArbreNodeDto<T> element, List<ArbreNodeDto<T>> list) {
		list.add(element);
		for (ArbreNodeDto<T> data : element.getFills())
			recorregut(data, list);
	}

	private void recorregutDades(ArbreNodeDto<T> element, List<T> list) {
		list.add(element.getDades());
		for (ArbreNodeDto<T> data : element.getFills())
			recorregutDades(data, list);
	}

	private static final long serialVersionUID = -139254994389509932L;
}
