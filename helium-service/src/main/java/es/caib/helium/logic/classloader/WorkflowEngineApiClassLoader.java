package es.caib.helium.logic.classloader;

import es.caib.helium.disseny.api.HeliumApi;
import es.caib.helium.disseny.exception.HeliumHandlerException;
import es.caib.helium.disseny.handler.HeliumActionHandler;
import es.caib.helium.logic.bpmn.HeliumApiImpl;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació de ClassLoader que carrega els recursos del WorkflowEngineApi.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class WorkflowEngineApiClassLoader extends RecursClassLoader {

	private static final ThreadLocal<String> deploymentId = new ThreadLocal<>();

	private final WorkflowEngineApi workflowEngineApi;
	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi, ClassLoader parent) {
		super(parent);
		this.workflowEngineApi = workflowEngineApi;
	}

	public WorkflowEngineApiClassLoader(WorkflowEngineApi workflowEngineApi) {
		super();
		this.workflowEngineApi = workflowEngineApi;
	}

	public static void setDeploymentId(String value) {
		deploymentId.set(value);
	}

	public static String getCurrentDeploymentId() {
		return deploymentId.get();
	}

	public static void clearCurrentDeploymentId() {
		deploymentId.remove();
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = loadedClasses.get(name);
			if (c == null) {
				try {
					c = findClass(name);
					if (HeliumActionHandler.class.isAssignableFrom(c)) {
						@SuppressWarnings("unchecked")
						Class<? extends HeliumActionHandler> handlerClass =
							(Class<? extends HeliumActionHandler>) c;
						c = generateJavaDelegateProxy(handlerClass);
					}
				} catch (ClassNotFoundException e) {
					c = super.loadClass(name, false);
				}
				loadedClasses.put(name, c);
			}
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}

	@Override
	protected byte[] loadResourceBytes(String name, boolean isClass) throws IOException {
		if (deploymentId.get() == null) {
			throw new IOException("Deployment " + (isClass ? "class" : "resource") + " " + name + " not found: empty deploymentId");
		}
		try {
			// Si el recurs no es troba ja es llença una exception des del mètode getResourceBytes
			return workflowEngineApi.getResourceBytes(deploymentId.get(), name);
		} catch (Exception ex) {
			throw new IOException("Deployment " + (isClass ? "class" : "resource") + " " + name + " not found (" +
				"deploymentId=" + deploymentId.get() + ")");
		}
	}

	@SneakyThrows
	private Class<?> generateJavaDelegateProxy(Class<? extends HeliumActionHandler> handlerClass) {
		DynamicType.Builder<Object> builder = new ByteBuddy()
			.subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
			.name(handlerClass.getName() + "$JavaDelegateProxy")
			.implement(JavaDelegate.class)
			.defineField("interceptor", JavaDelegateInterceptor.class, Visibility.PRIVATE)
			.defineConstructor(Visibility.PUBLIC)
			.intercept(MethodCall.invoke(Object.class.getDeclaredConstructor())
				.andThen(FieldAccessor.ofField("interceptor")
					.setsValue(new JavaDelegateInterceptor(handlerClass))))
			.method(ElementMatchers.named("execute")
				.and(ElementMatchers.takesArguments(DelegateExecution.class)))
			.intercept(MethodDelegation.toField("interceptor"));
		for (Method setter: findSetterMethods(handlerClass)) {
			builder = builder
				.defineMethod(setter.getName(), void.class, Visibility.PUBLIC)
				.withParameters(Expression.class)
				.intercept(MethodCall
					.invoke(JavaDelegateInterceptor.class.getMethod("setField", String.class, Object.class))
					.onField("interceptor")
					.with(setter.getName())
					.withArgument(0));
		}
		return builder
			.make()
			.load(this, ClassLoadingStrategy.Default.INJECTION)
			.getLoaded();
	}

	private List<Method> findSetterMethods(Class<?> clazz) {
		List<Method> setters = new ArrayList<>();
		for (Method m: clazz.getMethods()) {
			if (m.getName().startsWith("set")
				&& m.getParameterCount() == 1
				&& m.getReturnType() == void.class
				&& !m.getDeclaringClass().equals(Object.class)) {
				setters.add(m);
			}
		}
		return setters;
	}

	public static class JavaDelegateInterceptor {
		private final Class<? extends HeliumActionHandler> handlerClass;
		private final Map<String, Object> pendingFieldValues = new ConcurrentHashMap<>();
		public JavaDelegateInterceptor(Class<? extends HeliumActionHandler> handlerClass) {
			this.handlerClass = handlerClass;
		}
		// Cridat pels setters generats a la classe proxy (un per cada setXxx detectat)
		public void setField(String setterName, Object value) {
			pendingFieldValues.put(setterName, value);
		}
		// Aquest mètode s'invocarà quan es cridi execute(DelegateExecution) sobre la classe generada
		public void execute(DelegateExecution execution) {
			try {
				HeliumActionHandler handlerInstance = handlerClass.getDeclaredConstructor().newInstance();
				applyPendingFieldValues(handlerInstance, execution);
				HeliumApi heliumApi = buildHeliumApi(execution);
				handlerInstance.execute(heliumApi);
			} catch (HeliumHandlerException ex) {
				throw new RuntimeException(ex);
			} catch (Exception ex) {
				throw new RuntimeException("Couldn't create instance of " + handlerClass.getName(), ex);
			}
		}
		private void applyPendingFieldValues(
			HeliumActionHandler handlerInstance,
			DelegateExecution execution) throws ReflectiveOperationException {
			for (Map.Entry<String, Object> entry : pendingFieldValues.entrySet()) {
				String setterName = entry.getKey();
				Object rawValue = entry.getValue();
				Object resolvedValue = (rawValue instanceof Expression)
					? ((Expression) rawValue).getValue(execution)
					: rawValue;
				Method setter = findSetterByName(setterName);
				setter.invoke(handlerInstance, resolvedValue);
			}
		}
		private Method findSetterByName(String setterName) throws NoSuchMethodException {
			for (Method m : handlerClass.getMethods()) {
				if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
					return m;
				}
			}
			throw new NoSuchMethodException("No s'ha trobat el setter " + setterName + " a " + handlerClass.getName());
		}
		private HeliumApi buildHeliumApi(DelegateExecution execution) {
			return new HeliumApiImpl(
				null,
				execution.getProcessInstanceId(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null);
		}
	}

}
