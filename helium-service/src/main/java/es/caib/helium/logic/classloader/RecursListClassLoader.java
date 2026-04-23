package es.caib.helium.logic.classloader;

import es.caib.helium.persistence.entity.Recurs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació de ClassLoader que carrega els recursos de la taula de base de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecursListClassLoader extends RecursClassLoader {

	private final List<Recurs> recursos;
	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public RecursListClassLoader(List<Recurs> recursos, ClassLoader parent) {
		super(parent);
		this.recursos = recursos;
	}

	public RecursListClassLoader(List<Recurs> recursos) {
		super();
		this.recursos = recursos;
	}

	@Override
	protected byte[] loadResourceBytes(String name, Boolean isClass) throws IOException {
		Optional<Recurs> recurs = recursos.stream().filter(r -> r.getNom().equals(name)).findFirst();
		if (recurs.isPresent()) {
			return recurs.get().getContingut();
		} else {
			throw new IOException("Resource " + name + " not found");
		}
	}

}
