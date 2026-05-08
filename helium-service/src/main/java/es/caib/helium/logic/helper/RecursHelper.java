package es.caib.helium.logic.helper;

import es.caib.helium.bpmn.handler.HeliumActionHandler;
import es.caib.helium.logic.classloader.RecursListClassLoader;
import es.caib.helium.logic.classloader.RecursRepositoryClassLoader;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Recurs;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.persistence.repository.RecursRepository;
import liquibase.pro.packaged.T;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Mètodes per a gestionar là càrrega dinàmica de les classes dels handlers.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class RecursHelper {

	private final ExpedientTipusRepository expedientTipusRepository;
	private final DefinicioProcesRepository definicioProcesRepository;
	private final RecursRepository recursRepository;

	/**
	 * Desplega els recursos d'un .jar a dins un tipus d'expedient / definició de procés.
	 * <p>
	 * Si el recurs és una classe l'emmagatzema substituint els '/' del nom per '.' i llevant el '.class' final. Si no
	 * és una classe emmagatzema el recurs tal i com apareix al .jar (amb '/').
	 *
	 * @param expedientTipusId
	 *            l'id del tipus d'expedient.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (si és null es crearà el recurs lligat només al tipus d'expedient).
	 * @param jarContent
	 *            el contingut del fitxer .jar.
	 * @throws IOException
	 *            si no s'ha pogut llegit el contingut del .jar.
	 */
	public List<Recurs> deploy(
		Long expedientTipusId,
		Long definicioProcesId,
		byte[] jarContent) throws IOException {
		Optional<ExpedientTipus> expedientTipus = expedientTipusRepository.findById(expedientTipusId);
		if (expedientTipus.isPresent()) {
			Optional<DefinicioProces> definicioProces = (definicioProcesId != null) ?
				definicioProcesRepository.findById(definicioProcesId) :
				Optional.empty();
			try (InputStream is = new ByteArrayInputStream(jarContent); JarInputStream jis = new JarInputStream(is)) {
				List<Recurs> recursosCreats = new ArrayList<>();
				JarEntry entry;
				while ((entry = jis.getNextJarEntry()) != null) {
					if (!entry.isDirectory()) {
						boolean isClass = (entry.getName().endsWith(".class"));
						byte[] contingut = readJarEntryBytes(jis);
						Recurs saved;
						Optional<Recurs> existent = recursRepository.findByExpedientTipusAndDefinicioProcesAndNom(
							expedientTipus.get(),
							definicioProces.orElse(null),
							entry.getName());
						if (existent.isPresent()) {
							saved = existent.get();
							saved.setClasse(isClass);
							saved.setDataCreacio(new Date());
							saved.setContingut(contingut);
						} else {
							saved = recursRepository.save(
								Recurs.builder().
									nom(entry.getName()).
									classe(isClass).
									dataCreacio(new Date()).
									contingut(contingut).
									expedientTipus(expedientTipus.get()).
									definicioProces(definicioProces.orElse(null)).
									build());
						}
						recursosCreats.add(saved);
					}
				}
				updateRecursHandlerField(recursosCreats);
				return recursosCreats;
			}
		} else {
			throw new EntityNotFoundException("Couldn't find ExpedientTipus with id " + expedientTipusId);
		}
	}

	/**
	 * Carrega la classe amb el nom especificat des del classloader.
	 *
	 * @param expedientTipusId
	 *            l'id del tipus d'expedient.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (pot ser null i es cercarà un recurs lligat al tipus d'expedient).
	 * @param name
	 *            el nom de la classe.
	 * @param type
	 *            el tipus esperat.
	 * @return la classe carregada.
	 * @param <T> el tipus esperat.
	 * @throws ClassNotFoundException
	 *            si no es troba la classe amb el nom especificat.
	 */
	public <T> Class<? extends T> loadClass(
		Long expedientTipusId,
		Long definicioProcesId,
		String name,
		Class<T> type) throws ClassNotFoundException {
		Class<?> loadedClass = getRepositoryClassLoader(expedientTipusId, definicioProcesId).loadClass(name);
		if (!type.isAssignableFrom(loadedClass)) {
			throw new ClassCastException(
				"Class " + name + " is not of type " + type.getName()
			);
		}
		return loadedClass.asSubclass(type);
	}

	/**
	 * Carrega la classe amb el nom especificat i en retorna una instància.
	 *
	 * @param expedientTipusId
	 *            l'id del tipus d'expedient.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (pot ser null i es cercarà un recurs lligat al tipus d'expedient).
	 * @param className
	 *            el nom de la classe.
	 * @param type
	 *            el tipus esperat.
	 * @return la instància creada.
	 * @param <T> el tipus esperat.
	 * @throws ClassNotFoundException
	 *            si no es troba la classe amb el nom especificat.
	 * @throws ReflectiveOperationException
	 *            si es produeix algun altre error creant la instància.
	 */
	public <T> T loadClassAndCreateInstance(
		Long expedientTipusId,
		Long definicioProcesId,
		String className,
		Class<T> type) throws ClassNotFoundException, ReflectiveOperationException {
		Class<? extends T> loadedClass = loadClass(expedientTipusId, definicioProcesId, className, type);
		return loadedClass.getDeclaredConstructor().newInstance();
	}

	/**
	 * Obté el contingut d'un recurs.
	 *
	 * @param expedientTipusId
	 *            l'id del tipus d'expedient.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (pot ser null i es cercarà un recurs lligat al tipus d'expedient).
	 * @param name
	 *            el nom del recurs.
	 * @return el contingut del recurs o null si el recurs no s'ha trobat.
	 */
	public byte[] loadResource(
		Long expedientTipusId,
		Long definicioProcesId,
		String name) throws IOException {
		try (InputStream is = getRepositoryClassLoader(expedientTipusId, definicioProcesId).getResourceAsStream(name)) {
			if (is != null) {
				return is.readAllBytes();
			} else {
				return null;
			}
		}
	}

	/**
	 * Obté els paràmetres (a partir dels mètodes get) de la classe del handler.
	 *
	 * @param expedientTipusId
	 *            l'id del tipus d'expedient.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (pot ser null i es cercarà un recurs lligat al tipus d'expedient).
	 * @param className
	 *            el nom de la classe.
	 * @return la llista de paràmetres.
	 * @throws ClassNotFoundException
	 *            si no es troba la classe amb el nom especificat.
	 * @throws IntrospectionException
	 *            si es produeix algun error obtenint els paràmetres.
	 */
	public List<HandlerParameter> getHandlerParameters(
		Long expedientTipusId,
		Long definicioProcesId,
		String className) throws ClassNotFoundException, IntrospectionException {
		List<HandlerParameter> params = new ArrayList<>();
		Class<? extends HeliumActionHandler> handlerClass = loadClass(
			expedientTipusId,
			definicioProcesId,
			className,
			HeliumActionHandler.class);
		BeanInfo info = Introspector.getBeanInfo(handlerClass);
		for (PropertyDescriptor pd: info.getPropertyDescriptors()) {
			if (pd.getWriteMethod() != null) {
				params.add(new HandlerParameter(pd.getName(), pd.getPropertyType()));
			}
		}
		return params;
	}

	/**
	 * Retorna una nova instància del handler creada a partir del recurs que correspon a la classe especificada.
	 *
	 * @param expedient
	 *            l'expedient a dins el qual s'executa el handler.
	 * @param definicioProcesId
	 *            l'id de la definició de procés (pot ser null i es cercarà un recurs lligat al tipus d'expedient).
	 * @param className
	 *            el nom de la classe del handler.
	 * @param values
	 *            els valors dels paràmetres del handler.
	 * @return la instància del handler.
	 * @throws ClassNotFoundException
	 *            si no es troba el handler amb el nom especificat.
	 * @throws ReflectiveOperationException
	 *            si es produeix algun altre error creant la instància del handler.
	 */
	public HeliumActionHandler createHandlerInstance(
		Expedient expedient,
		Long definicioProcesId,
		String className,
		Map<String, String> values) throws ClassNotFoundException, ReflectiveOperationException {
		HeliumActionHandler actionHandler = loadClassAndCreateInstance(
			expedient.getTipus().getId(),
			definicioProcesId,
			className,
			HeliumActionHandler.class);
		new BeanWrapperImpl(actionHandler).setPropertyValues(values);
		return actionHandler;
	}

	/*
	 * Llegeix els bytes d'una entrada del .jar.
	 */
	private byte[] readJarEntryBytes(JarInputStream jis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read;
		while ((read = jis.read(buffer)) != -1) {
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}

	/*
	 * Actualitza el camp handler del recurs en funció de si el recurs és una classe i aquesta implementa la
	 * interfície HeliumActionHandler.
	 * Per a saber si la classe implementa la interfície carregam totes les classes del .jar a dins un classloader i
	 * comprovam si implementa la interfície amb Class.isAssignableFrom.
	 */
	private void updateRecursHandlerField(List<Recurs> recursos) {
		ClassLoader classLoader = new RecursListClassLoader(recursos);
		recursos.stream().
			filter(Recurs::isClasse).
			forEach(r -> {
				try {
					String className = r.getNom().
						replace("/", ".").
						replace(".class", "");
					Class<?> clazz = classLoader.loadClass(className);
					r.setHandler(HeliumActionHandler.class.isAssignableFrom(clazz));
				} catch (ClassNotFoundException ignored) { }
			});
	}

	private ClassLoader getRepositoryClassLoader(
		Long expedientTipusId,
		Long definicioProcesId) {
		return new RecursRepositoryClassLoader(expedientTipusId, definicioProcesId, recursRepository);
	}

	@Getter
	@RequiredArgsConstructor
	public static class HandlerParameter {
		private final String name;
		private final Class<?> type;
	}

}
