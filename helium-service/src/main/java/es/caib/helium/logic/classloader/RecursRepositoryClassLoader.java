package es.caib.helium.logic.classloader;

import es.caib.helium.persistence.repository.RecursRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació de ClassLoader que carrega els recursos de la taula de base de dades.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecursRepositoryClassLoader extends RecursClassLoader {

	private final Long expedientTipusId;
	private final Long definicioProcesId;
	private final RecursRepository recursRepository;
	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public RecursRepositoryClassLoader(
		Long expedientTipusId,
		Long definicioProcesId,
		RecursRepository recursRepository,
		ClassLoader parent) {
		super(parent);
		this.expedientTipusId = expedientTipusId;
		this.definicioProcesId = definicioProcesId;
		this.recursRepository = recursRepository;
	}

	public RecursRepositoryClassLoader(
		Long expedientTipusId,
		Long definicioProcesId,
		RecursRepository recursRepository) {
		super();
		this.expedientTipusId = expedientTipusId;
		this.definicioProcesId = definicioProcesId;
		this.recursRepository = recursRepository;
	}

	@Override
	protected byte[] loadResourceBytes(String name, boolean isClass) throws IOException {
		Optional<byte[]> contingut = recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			name,
			isClass);
		if (contingut.isPresent()) {
			return contingut.get();
		} else {
			throw new IOException("Resource " + name + " not found (" +
				"expedientTipusId=" + expedientTipusId + ", " +
				"definicioProcesId=" + definicioProcesId + ")");
		}
	}

}
