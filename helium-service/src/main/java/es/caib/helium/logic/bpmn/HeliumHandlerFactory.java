package es.caib.helium.logic.bpmn;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HandlerParametres;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;
import es.caib.helium.logic.helper.ExpedientTipusRecursHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Crea les instàncies dels handlers de Helium (predefinits i propis) independentment del
 * motor de workflow.
 *
 * <p>La classe es cerca primer a les classes de l'aplicació (handlers predefinits) i, si no
 * hi és, als recursos de la definició de procés i, si tampoc hi és, als del tipus d'expedient (handlers propis).
 * Si la classe és una interfície o una classe abstracta (per exemple les interfícies de
 * {@code helium-disseny}), se'n cerca la implementació dins aquest paquet.</p>
 *
 * <p>Cada crida crea una instància nova, de manera que els handlers poden guardar
 * l'estat (paràmetres) a camps sense problemes de concurrència.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HeliumHandlerFactory {

	private final ExpedientTipusRecursHelper expedientTipusRecursHelper;
	private final Map<Class<?>, Class<? extends HeliumBpmnHandler>> implementacions = new ConcurrentHashMap<>();

	@Autowired
	public HeliumHandlerFactory(@Lazy ExpedientTipusRecursHelper expedientTipusRecursHelper) {
		this.expedientTipusRecursHelper = expedientTipusRecursHelper;
	}

	/**
	 * Crea una instància del handler i li injecta els paràmetres.
	 *
	 * @param classe el nom de la classe del handler (o d'una interfície de {@code helium-disseny}).
	 * @param parametres els valors dels paràmetres, ja resolts.
	 * @param expedientTipusId el tipus d'expedient on cercar els handlers propis.
	 * @param definicioProcesId la definició de procés on cercar els handlers propis (pot ser null).
	 */
	public HeliumBpmnHandler crearHandler(
			String classe,
			Map<String, ?> parametres,
			Long expedientTipusId,
			Long definicioProcesId) {
		return crearHandler(classe, parametres, () -> new ContextRecursos(expedientTipusId, definicioProcesId));
	}

	/**
	 * Crea una instància del handler i li injecta els paràmetres.
	 *
	 * @param classe el nom de la classe del handler (o d'una interfície de {@code helium-disseny}).
	 * @param parametres els valors dels paràmetres, ja resolts.
	 * @param contextRecursos proveeix on cercar els handlers propis. Només s'invoca si la classe
	 *            no és a l'aplicació.
	 */
	public HeliumBpmnHandler crearHandler(
			String classe,
			Map<String, ?> parametres,
			Supplier<ContextRecursos> contextRecursos) {
		return crearHandler(resoldreClasse(classe, contextRecursos), parametres);
	}

	/** Crea una instància nova de la classe del handler i li injecta els paràmetres. */
	public HeliumBpmnHandler crearHandler(
			Class<? extends HeliumBpmnHandler> handlerClass,
			Map<String, ?> parametres) {
		HeliumBpmnHandler handler;
		try {
			handler = handlerClass.getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException ex) {
			throw new HeliumHandlerException("No s'ha pogut crear la instància del handler " + handlerClass.getName(), ex);
		}
		HandlerParametres.injectar(handler, parametres);
		return handler;
	}

	/**
	 * Resol la classe del handler: primer a l'aplicació i després als recursos. Si és una interfície de
	 * {@code helium-disseny}, retorna la seva implementació.
	 */
	public Class<? extends HeliumBpmnHandler> resoldreClasse(
			String classe,
			Supplier<ContextRecursos> contextRecursos) {
		if (classe == null || classe.trim().isEmpty()) {
			throw new HeliumHandlerException("No s'ha especificat la classe del handler");
		}
		classe = classe.trim();
		Class<?> clazz;
		try {
			clazz = Class.forName(classe, false, HeliumHandlerFactory.class.getClassLoader());
		} catch (ClassNotFoundException ex) {
			clazz = carregarDeRecursos(classe, contextRecursos);
		}
		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			return implementacions.computeIfAbsent(clazz, this::cercarImplementacio);
		}
		if (!HeliumBpmnHandler.class.isAssignableFrom(clazz)) {
			throw new HeliumHandlerException(
				"La classe " + classe + " no implementa " + HeliumBpmnHandler.class.getName());
		}
		return clazz.asSubclass(HeliumBpmnHandler.class);
	}

	private Class<?> carregarDeRecursos(String classe, Supplier<ContextRecursos> contextRecursos) {
		ContextRecursos context = contextRecursos != null ? contextRecursos.get() : null;
		if (context == null || context.getExpedientTipusId() == null) {
			throw new HeliumHandlerException("No s'ha trobat la classe del handler " + classe);
		}
		try {
			if (context.getDefinicioProcesId() != null) {
				try {
					// Primer els recursos de la definició de procés
					return expedientTipusRecursHelper.loadClass(
						context.getExpedientTipusId(),
						context.getDefinicioProcesId(),
						classe,
						HeliumBpmnHandler.class);
				} catch (ClassNotFoundException ex) {
					// Si no hi és, es cerca als recursos del tipus d'expedient
				}
			}
			return expedientTipusRecursHelper.loadClass(
				context.getExpedientTipusId(),
				null,
				classe,
				HeliumBpmnHandler.class);
		} catch (ClassNotFoundException | ClassCastException ex) {
			throw new HeliumHandlerException(
				"No s'ha trobat el handler " + classe + " als recursos (" +
					"expedientTipusId=" + context.getExpedientTipusId() + ", " +
					"definicioProcesId=" + context.getDefinicioProcesId() + ")",
				ex);
		}
	}

	private Class<? extends HeliumBpmnHandler> cercarImplementacio(Class<?> tipus) {
		String paquet = HeliumHandlerFactory.class.getPackageName();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(tipus));
		List<Class<? extends HeliumBpmnHandler>> candidats = new ArrayList<>();
		for (var candidat : scanner.findCandidateComponents(paquet)) {
			try {
				Class<?> c = Class.forName(candidat.getBeanClassName(), false, HeliumHandlerFactory.class.getClassLoader());
				if (!c.isInterface()
						&& !Modifier.isAbstract(c.getModifiers())
						&& tipus.isAssignableFrom(c)
						&& HeliumBpmnHandler.class.isAssignableFrom(c)) {
					candidats.add(c.asSubclass(HeliumBpmnHandler.class));
				}
			} catch (ClassNotFoundException ex) {
				throw new HeliumHandlerException("No s'ha pogut carregar la classe " + candidat.getBeanClassName(), ex);
			}
		}
		if (candidats.isEmpty()) {
			throw new HeliumHandlerException(
				"No s'ha trobat cap implementació de " + tipus.getName() + " dins el paquet " + paquet);
		}
		if (candidats.size() > 1) {
			throw new HeliumHandlerException(
				"S'han trobat múltiples implementacions de " + tipus.getName() + " dins el paquet " + paquet +
					": " + candidats);
		}
		return candidats.get(0);
	}

	/** On s'han de cercar els handlers propis. */
	@Getter
	@AllArgsConstructor
	public static class ContextRecursos {
		private final Long expedientTipusId;
		private final Long definicioProcesId;
	}

}
