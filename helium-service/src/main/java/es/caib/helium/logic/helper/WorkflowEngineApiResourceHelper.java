package es.caib.helium.logic.helper;

import es.caib.helium.disseny.handler.HeliumActionHandler;
import es.caib.helium.logic.classloader.WorkflowEngineApiClassLoader;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Mètodes per a gestionar là càrrega dinàmica de les classes dels handlers.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class WorkflowEngineApiResourceHelper {

	private final WorkflowEngineApi workflowEngineApi;

	/**
	 * Carrega la classe amb el nom especificat des del classloader.
	 *
	 * @param deploymentId
	 *            l'id del desplegament.
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
		String deploymentId,
		String name,
		Class<T> type) throws ClassNotFoundException {
		Class<?> loadedClass = getRepositoryClassLoader(deploymentId).loadClass(name);
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
	 * @param deploymentId
	 *            l'id del desplegament.
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
		String deploymentId,
		String className,
		Class<T> type) throws ClassNotFoundException, ReflectiveOperationException {
		Class<? extends T> loadedClass = loadClass(deploymentId, className, type);
		return loadedClass.getDeclaredConstructor().newInstance();
	}

	/**
	 * Obté el contingut d'un recurs.
	 *
	 * @param deploymentId
	 *            l'id del desplegament.
	 * @param name
	 *            el nom del recurs.
	 * @return el contingut del recurs o null si el recurs no s'ha trobat.
	 */
	public byte[] loadResource(
		String deploymentId,
		String name) throws IOException {
		try (InputStream is = getRepositoryClassLoader(deploymentId).getResourceAsStream(name)) {
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
	 * @param deploymentId
	 *            l'id del desplegament.
	 * @param className
	 *            el nom de la classe.
	 * @return la llista de paràmetres.
	 * @throws ClassNotFoundException
	 *            si no es troba la classe amb el nom especificat.
	 * @throws IntrospectionException
	 *            si es produeix algun error obtenint els paràmetres.
	 */
	public List<ExpedientTipusRecursHelper.HandlerParameter> getHandlerParameters(
		String deploymentId,
		String className) throws ClassNotFoundException, IntrospectionException {
		List<ExpedientTipusRecursHelper.HandlerParameter> params = new ArrayList<>();
		Class<? extends HeliumActionHandler> handlerClass = loadClass(
			deploymentId,
			className,
			HeliumActionHandler.class);
		BeanInfo info = Introspector.getBeanInfo(handlerClass);
		for (PropertyDescriptor pd: info.getPropertyDescriptors()) {
			if (pd.getWriteMethod() != null) {
				params.add(new ExpedientTipusRecursHelper.HandlerParameter(pd.getName(), pd.getPropertyType()));
			}
		}
		return params;
	}

	/**
	 * Retorna una nova instància del handler creada a partir del recurs que correspon a la classe especificada.
	 *
	 * @param deploymentId
	 *            l'id del desplegament.
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
		String deploymentId,
		String className,
		Map<String, String> values) throws ClassNotFoundException, ReflectiveOperationException {
		HeliumActionHandler actionHandler = loadClassAndCreateInstance(
			deploymentId,
			className,
			HeliumActionHandler.class);
		new BeanWrapperImpl(actionHandler).setPropertyValues(values);
		return actionHandler;
	}

	private ClassLoader getRepositoryClassLoader(String deploymentId) {
		return new WorkflowEngineApiClassLoader(workflowEngineApi, deploymentId);
	}

}
