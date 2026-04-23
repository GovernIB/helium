package es.caib.helium.logic.classloader;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementació de ClassLoader que carrega els recursos amb un mètode abstracte.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class RecursClassLoader extends ClassLoader {

	private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

	public RecursClassLoader(ClassLoader parent) {
		super(parent);
	}

	public RecursClassLoader() {
		super(RecursClassLoader.class.getClassLoader());
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = loadedClasses.get(name);
			if (c == null) {
				try {
					c = findClass(name);
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
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			byte[] bytes = loadClassBytes(name);
			if (bytes == null) {
				throw new ClassNotFoundException(name);
			}
			return defineClass(name, bytes, 0, bytes.length);
		} catch (IOException ex) {
			throw new ClassNotFoundException("Error carregant la classe " + name, ex);
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		Objects.requireNonNull(name);
		try {
			byte[] bytes = loadResourceBytes(name, null);
			return new ByteArrayInputStream(bytes);
		} catch (IOException ex) {
			return null;
		}
	}

	protected byte[] loadClassBytes(String className) throws IOException {
		return loadResourceBytes(className.replace('.', '/') + ".class", true);
	}

	protected abstract byte[] loadResourceBytes(String name, Boolean isClass) throws IOException;

}
