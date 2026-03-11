package es.caib.helium.commons.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació resultant d'executar una consulta paginada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
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
	
	@Override
	public Iterator<T> iterator() {
		if (contingut != null)
			return getContingut().iterator();
		else
			return new ArrayList<T>().iterator();
	}
	
	private static final long serialVersionUID = -139254994389509932L;
}
