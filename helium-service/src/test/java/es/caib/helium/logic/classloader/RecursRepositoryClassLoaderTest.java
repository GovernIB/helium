package es.caib.helium.logic.classloader;

import es.caib.helium.persistence.repository.RecursRepository;
import net.bytebuddy.ByteBuddy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per a RecursRepositoryClassLoader.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(MockitoExtension.class)
public class RecursRepositoryClassLoaderTest {

	@Mock
	private RecursRepository recursRepository;

	private RecursRepositoryClassLoader classLoader;

	private final Long expedientTipusId = 1L;
	private final Long definicioProcesId = 2L;

	@BeforeEach
	void setUp() {
		classLoader = new RecursRepositoryClassLoader(
			expedientTipusId,
			definicioProcesId,
			recursRepository
		);
	}

	@Test
	public void shouldLoadClassFromRepository() throws Exception {
		String className = "com.example.TestClass";
		String resourceName = "com/example/TestClass.class";
		byte[] fakeClassBytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			resourceName,
			true)).
			thenReturn(Optional.of(fakeClassBytes));
		Class<?> clazz = classLoader.loadClass(className);
		assertNotNull(clazz);
		assertEquals(className, clazz.getName());
		verify(recursRepository, times(1))
			.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
				expedientTipusId,
				definicioProcesId,
				resourceName,
				true);
	}

	@Test
	public void shouldCacheLoadedClass() throws Exception {
		String className = "com.example.TestClass";
		String resourceName = "com/example/TestClass.class";
		byte[] fakeClassBytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			resourceName,
			true)).
			thenReturn(Optional.of(fakeClassBytes));
		Class<?> firstLoad = classLoader.loadClass(className);
		Class<?> secondLoad = classLoader.loadClass(className);
		assertSame(firstLoad, secondLoad);
		// Només es crida una vegada gràcies a la cache
		verify(recursRepository, times(1)).
			findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
				expedientTipusId,
				definicioProcesId,
				resourceName,
				true);
	}

	@Test
	public void shouldFallbackToParentClassLoader() throws Exception {
		String className = "java.lang.String";
		Class<?> clazz = classLoader.loadClass(className);
		assertEquals(String.class, clazz);
	}

	@Test
	public void shouldThrowClassNotFoundWhenNotFoundAnywhere() {
		String className = "com.unknown.Class";
		String resourceName = "com/unknown/Class.class";
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
				any(), any(), eq(resourceName), any())).
			thenReturn(Optional.empty());
		assertThrows(ClassNotFoundException.class, () -> classLoader.loadClass(className));
	}

	@Test
	public void shouldReturnResourceAsStream() throws Exception {
		String resourceName = "test.txt";
		byte[] data = "hello".getBytes();
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
				any(), any(), eq(resourceName), any())).
			thenReturn(Optional.of(data));
		InputStream is = classLoader.getResourceAsStream(resourceName);
		assertNotNull(is);
		assertArrayEquals(data, is.readAllBytes());
	}

	@Test
	public void shouldReturnNullStreamWhenResourceFails() throws Exception {
		String resourceName = "missing.txt";
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			any(), any(), eq(resourceName), any())).
			thenReturn(Optional.empty());
		InputStream is = classLoader.getResourceAsStream(resourceName);
		assertNull(is);
	}

	private static class DummyClassGenerator {
		public static byte[] generateSimpleClassBytes(String className) throws Exception {
			return new ByteBuddy()
				.subclass(Object.class)
				.name(className)
				.make()
				.getBytes();
		}
	}

}
