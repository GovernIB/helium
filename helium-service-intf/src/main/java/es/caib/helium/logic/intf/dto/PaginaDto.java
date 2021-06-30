/**
 * 
 */
package es.caib.helium.logic.intf.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació resultant d'executar una consulta paginada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginaDto<T> implements Iterable<T>, Serializable {

	private int numero; // De la pàgina actual
	private int tamany; // De la pàgina actual
	private int total; // De pàgines
	private long elementsTotal;
	private boolean anteriors;
	private boolean primera;
	private boolean posteriors;
	private boolean darrera;
	private List<T> contingut = new ArrayList<T>();



	public PaginaDto() {
	}

	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public int getTamany() {
		return tamany;
	}
	public void setTamany(int tamany) {
		this.tamany = tamany;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public long getElementsTotal() {
		return elementsTotal;
	}
	public void setElementsTotal(long elementsTotal) {
		this.elementsTotal = elementsTotal;
	}
	public boolean isAnteriors() {
		return anteriors;
	}
	public void setAnteriors(boolean anteriors) {
		this.anteriors = anteriors;
	}
	public boolean isPrimera() {
		return primera;
	}
	public void setPrimera(boolean primera) {
		this.primera = primera;
	}
	public boolean isPosteriors() {
		return posteriors;
	}
	public void setPosteriors(boolean posteriors) {
		this.posteriors = posteriors;
	}
	public boolean isDarrera() {
		return darrera;
	}
	public void setDarrera(boolean darrera) {
		this.darrera = darrera;
	}
	public List<T> getContingut() {
		return contingut;
	}
	public void setContingut(List<T> contingut) {
		this.contingut = contingut;
	}

	public int getElementsNombre() {
		if (isBuida())
			return 0;
		else
			return contingut.size();
	}
	public boolean isBuida() {
		return contingut == null || contingut.size() == 0;
	}

	public Iterator<T> iterator() {
		if (contingut != null)
			return getContingut().iterator();
		else
			return new ArrayList<T>().iterator();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
