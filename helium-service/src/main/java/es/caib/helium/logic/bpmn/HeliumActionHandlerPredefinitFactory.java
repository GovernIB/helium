package es.caib.helium.logic.bpmn;

import es.caib.helium.disseny.handler.HeliumActionHandler;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Factoria per a la creació de les instàncies dels handlers BPMN predefinits.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HeliumActionHandlerPredefinitFactory {

	public HeliumActionHandler createInstance(
		String handlerClassName,
		Map<String, String> params) throws ReflectiveOperationException {
		Class<?> rawClass = Class.forName(handlerClassName);
		if (!HeliumActionHandler.class.isAssignableFrom(rawClass)) {
			throw new IllegalArgumentException(
				"La classe " + handlerClassName + " no implementa HeliumActionHandler");
		}
		return createInstance(rawClass.asSubclass(HeliumActionHandler.class), params);
	}

	public HeliumActionHandler createInstance(
		Class<? extends HeliumActionHandler> handlerClass,
		Map<String, String> params) throws ReflectiveOperationException {
		Class<? extends HeliumActionHandler> implementationClass = findImplementationClass(handlerClass);
			HeliumActionHandler handler = implementationClass.getDeclaredConstructor().newInstance();
			new BeanWrapperImpl(handler).setPropertyValues(params);
			return handler;
	}

	private Class<? extends HeliumActionHandler> findImplementationClass(
		Class<? extends HeliumActionHandler> handlerClass) {
		String implementationsPackage = HeliumActionHandlerPredefinitFactory.class.getPackageName();
		ClassPathScanningCandidateComponentProvider scanner =
			new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(handlerClass));
		List<Class<? extends HeliumActionHandler>> candidates = new ArrayList<>();
		for (var candidate: scanner.findCandidateComponents(implementationsPackage)) {
			try {
				Class<?> clazz = Class.forName(candidate.getBeanClassName());
				if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
					continue;
				}
				if (handlerClass.isAssignableFrom(clazz)) {
					candidates.add(clazz.asSubclass(HeliumActionHandler.class));
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(
					"No s'ha pogut carregar la classe " + candidate.getBeanClassName(),
					e);
			}
		}
		if (candidates.isEmpty()) {
			throw new IllegalArgumentException(
				"No s'ha trobat cap implementacio de " + handlerClass.getName()
					+ " dins el paquet " + implementationsPackage);
		}
		if (candidates.size() > 1) {
			throw new IllegalArgumentException(
				"S'han trobat multiples implementacions de " + handlerClass.getName()
					+ " dins el paquet " + implementationsPackage
					+ ": " + candidates);
		}
		return candidates.get(0);
	}

}
