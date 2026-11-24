package es.caib.helium.bpmn.handler;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.integracio.Termini;

/**
 * Injecció dels paràmetres d'un handler, independent del motor de workflow.
 *
 * <p>Cada paràmetre s'assigna al setter JavaBean amb el mateix nom ({@code setNom}) o, si no
 * n'hi ha, al camp amb aquest nom. El valor es converteix al tipus del setter o del camp:
 * {@code String}, booleans, números i {@code Date} (format {@value AbstractHeliumActionHandler#FORMAT_DATA}).
 * Cap a {@code String}, les dates es formaten amb aquest mateix format i els terminis com a {@code anys/mesos/dies},
 * de manera que una expressió que retorna una dada de l'expedient ({@code ${dataInici}}) es pot passar a un
 * paràmetre {@code String}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class HandlerParametres {

	private HandlerParametres() {}

	/**
	 * Assigna els valors dels paràmetres a la instància del handler.
	 *
	 * @param handler la instància del handler.
	 * @param valors els valors dels paràmetres, indexats pel nom del paràmetre.
	 * @throws HeliumHandlerException si el handler no té algun dels paràmetres o si un valor
	 *         no es pot convertir al tipus del paràmetre.
	 */
	public static void injectar(Object handler, Map<String, ?> valors) {
		if (valors == null || valors.isEmpty())
			return;
		Class<?> handlerClass = handler.getClass();
		for (Map.Entry<String, ?> entry : valors.entrySet()) {
			String nom = entry.getKey();
			if (nom == null || nom.trim().isEmpty())
				continue;
			try {
				Method setter = findSetter(handlerClass, nom);
				if (setter != null) {
					setter.invoke(handler, convertir(entry.getValue(), setter.getParameterTypes()[0], nom));
					continue;
				}
				Field field = findField(handlerClass, nom);
				if (field != null) {
					field.setAccessible(true);
					field.set(handler, convertir(entry.getValue(), field.getType(), nom));
					continue;
				}
			} catch (InvocationTargetException ex) {
				throw new HeliumHandlerException(
					"Error assignant el paràmetre '" + nom + "' al handler " + handlerClass.getName(),
					ex.getCause());
			} catch (IllegalAccessException | IntrospectionException ex) {
				throw new HeliumHandlerException(
					"Error assignant el paràmetre '" + nom + "' al handler " + handlerClass.getName(),
					ex);
			}
			throw new HeliumHandlerException(
				"El handler " + handlerClass.getName() + " no té el paràmetre '" + nom + "'");
		}
	}

	/** Indica si el handler té el paràmetre (un setter JavaBean o un camp amb aquest nom). */
	public static boolean teParametre(Class<?> handlerClass, String nom) {
		if (handlerClass == null || nom == null || nom.isEmpty())
			return false;
		try {
			return findSetter(handlerClass, nom) != null || findField(handlerClass, nom) != null;
		} catch (IntrospectionException ex) {
			return false;
		}
	}

	private static Method findSetter(Class<?> handlerClass, String nom) throws IntrospectionException {
		for (PropertyDescriptor pd : Introspector.getBeanInfo(handlerClass).getPropertyDescriptors()) {
			if (pd.getName().equals(nom) && pd.getWriteMethod() != null)
				return pd.getWriteMethod();
		}
		return null;
	}

	private static Field findField(Class<?> handlerClass, String nom) {
		Class<?> c = handlerClass;
		while (c != null && c != Object.class) {
			for (Field f : c.getDeclaredFields()) {
				if (f.getName().equals(nom) && !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
					return f;
			}
			c = c.getSuperclass();
		}
		return null;
	}

	static Object convertir(Object valor, Class<?> tipus, String nom) {
		if (valor == null)
			return tipus.isPrimitive() ? valorPerDefecte(tipus) : null;
		if (tipus.isPrimitive() ? wrapper(tipus).isInstance(valor) : tipus.isInstance(valor))
			return valor;
		if (tipus == String.class) {
			if (valor instanceof Date)
				return new SimpleDateFormat(AbstractHeliumActionHandler.FORMAT_DATA).format((Date) valor);
			if (valor instanceof Termini) {
				Termini termini = (Termini) valor;
				return termini.getAnys() + "/" + termini.getMesos() + "/" + termini.getDies();
			}
			return String.valueOf(valor);
		}
		String text = String.valueOf(valor).trim();
		if (text.isEmpty())
			return tipus.isPrimitive() ? valorPerDefecte(tipus) : null;
		try {
			Class<?> t = tipus.isPrimitive() ? wrapper(tipus) : tipus;
			if (t == Boolean.class)
				return Boolean.valueOf(text);
			if (t == Integer.class)
				return valor instanceof Number ? ((Number) valor).intValue() : Integer.valueOf(text);
			if (t == Long.class)
				return valor instanceof Number ? ((Number) valor).longValue() : Long.valueOf(text);
			if (t == Double.class)
				return valor instanceof Number ? ((Number) valor).doubleValue() : Double.valueOf(text);
			if (t == BigDecimal.class)
				return new BigDecimal(text);
			if (t == Date.class)
				return new SimpleDateFormat(AbstractHeliumActionHandler.FORMAT_DATA).parse(text);
		} catch (NumberFormatException | ParseException ex) {
			throw new HeliumHandlerException(
				"No s'ha pogut convertir el valor '" + valor + "' del paràmetre '" + nom + "' a " + tipus.getSimpleName(),
				ex);
		}
		throw new HeliumHandlerException(
			"El paràmetre '" + nom + "' és de tipus " + tipus.getName() + ", que no està suportat (valor de tipus " +
				valor.getClass().getName() + ")");
	}

	private static Class<?> wrapper(Class<?> primitive) {
		if (primitive == boolean.class) return Boolean.class;
		if (primitive == int.class) return Integer.class;
		if (primitive == long.class) return Long.class;
		if (primitive == double.class) return Double.class;
		if (primitive == float.class) return Float.class;
		if (primitive == short.class) return Short.class;
		if (primitive == byte.class) return Byte.class;
		if (primitive == char.class) return Character.class;
		return primitive;
	}

	private static Object valorPerDefecte(Class<?> primitive) {
		if (primitive == boolean.class) return false;
		if (primitive == char.class) return '\0';
		if (primitive == long.class) return 0L;
		if (primitive == double.class) return 0d;
		if (primitive == float.class) return 0f;
		if (primitive == short.class) return (short) 0;
		if (primitive == byte.class) return (byte) 0;
		return 0;
	}

}
