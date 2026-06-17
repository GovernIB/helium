package es.caib.helium.logic.helper;

import es.caib.helium.disseny.handler.HeliumActionHandler;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.entity.Recurs;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.ExpedientTipusRepository;
import es.caib.helium.persistence.repository.RecursRepository;
import net.bytebuddy.ByteBuddy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per a RecursHelper.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(MockitoExtension.class)
public class ExpedientTipusRecursHelperTest {

	private static final String HANDLER_RESOURCE_CLASS = "es.caib.helium.test.handler.TestHandler";
	private static final String LOGO_RESOURCE_PATH = "es/caib/logo.svg";

	@Mock
	private ExpedientTipusRepository expedientTipusRepository;
	@Mock
	private DefinicioProcesRepository definicioProcesRepository;
	@Mock
	private RecursRepository recursRepository;

	private ExpedientTipusRecursHelper expedientTipusRecursHelper;

	private final Long expedientTipusId = 1L;
	private final Long definicioProcesId = 2L;

	@BeforeEach
	public void setUp() {
		expedientTipusRecursHelper = new ExpedientTipusRecursHelper(expedientTipusRepository, definicioProcesRepository, recursRepository);
	}

	// --------------------------------------------------
	// deploy
	// --------------------------------------------------

	@Test
	void shouldDeployJarResources() throws Exception {
		Long expedientTipusId = 1L;
		Long definicioProcesId = 2L;
		when(expedientTipusRepository.findById(expedientTipusId)).
			thenReturn(Optional.of(new ExpedientTipus()));
		when(definicioProcesRepository.findById(definicioProcesId)).
			thenReturn(Optional.of(new DefinicioProces()));
		when(recursRepository.save(any())).
			thenAnswer(invocation -> invocation.getArgument(0));
		byte[] jar = createFakeJar("file1.txt", "file2.txt");
		expedientTipusRecursHelper.deploy(expedientTipusId, definicioProcesId, jar);
		verify(recursRepository, times(2)).save(any(Recurs.class));
	}

	@Test
	void shouldThrowWhenExpedientTipusNotFound() {
		Long expedientTipusId = 1L;
		when(expedientTipusRepository.findById(expedientTipusId)).
			thenReturn(Optional.empty());
		byte[] jar = new byte[] { 1, 2, 3 };
		assertThrows(EntityNotFoundException.class, () ->
			expedientTipusRecursHelper.deploy(expedientTipusId, 2L, jar)
		);
		verifyNoInteractions(recursRepository);
	}

	@Test
	void shouldDeployEvenIfDefinicioProcesMissing() throws Exception {
		Long expedientTipusId = 1L;
		Long definicioProcesId = 2L;
		when(expedientTipusRepository.findById(expedientTipusId)).
			thenReturn(Optional.of(new ExpedientTipus()));
		when(definicioProcesRepository.findById(definicioProcesId)).
			thenReturn(Optional.empty());
		when(recursRepository.save(any())).
			thenAnswer(invocation -> invocation.getArgument(0));
		byte[] jar = createFakeJar("file.txt");
		expedientTipusRecursHelper.deploy(expedientTipusId, definicioProcesId, jar);
		verify(recursRepository).save(any(Recurs.class));
	}

	@Test
	public void shouldLoadRealJarSuccessfully() throws IOException {
		String handlerResource = HANDLER_RESOURCE_CLASS.replace(".", "/") + ".class";
		when(expedientTipusRepository.findById(any())).
			thenReturn(Optional.of(new ExpedientTipus()));
		when(recursRepository.save(any())).
			thenAnswer(invocation -> invocation.getArgument(0));
		expedientTipusRecursHelper.deploy(
			expedientTipusId,
			definicioProcesId,
			loadRecursosJarFile());
		ArgumentCaptor<Recurs> captor = ArgumentCaptor.forClass(Recurs.class);
		verify(recursRepository, times(2)).save(captor.capture());
		List<Recurs> savedRecursos = captor.getAllValues();
		assertEquals(2, savedRecursos.size());
		assertEquals(LOGO_RESOURCE_PATH, savedRecursos.get(0).getNom());
		assertFalse(savedRecursos.get(0).isClasse());
		assertEquals(handlerResource, savedRecursos.get(1).getNom());
		assertTrue(savedRecursos.get(1).isClasse());
		assertTrue(savedRecursos.get(1).isHandler());
	}

	// --------------------------------------------------
	// loadClass
	// --------------------------------------------------

	@Test
	public void shouldLoadClassSuccessfully() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			className.replace('.', '/') + ".class",
			true)).
			thenReturn(Optional.of(bytes));
		Class<?> clazz = expedientTipusRecursHelper.loadClass(expedientTipusId, definicioProcesId, className, Object.class);
		assertNotNull(clazz);
		assertEquals(className, clazz.getName());
	}

	@Test
	public void shouldFailWhenTypeNotAssignable() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			className.replace('.', '/') + ".class",
			true)).
			thenReturn(Optional.of(bytes));
		// String.class no és assignable
		assertThrows(ClassCastException.class, () ->
			expedientTipusRecursHelper.loadClass(expedientTipusId, definicioProcesId, className, String.class)
		);
	}

	// --------------------------------------------------
	// loadClassAndCreateInstance
	// --------------------------------------------------

	@Test
	public void shouldCreateInstance() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			className.replace('.', '/') + ".class",
			true)).
			thenReturn(Optional.of(bytes));
		Object instance = expedientTipusRecursHelper.loadClassAndCreateInstance(expedientTipusId, definicioProcesId, className, Object.class);
		assertNotNull(instance);
		assertEquals(className, instance.getClass().getName());
	}

	// --------------------------------------------------
	// loadResource
	// --------------------------------------------------

	@Test
	public void shouldLoadResource() throws Exception {
		byte[] data = "hola".getBytes();
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			any(), any(), eq("test.txt"), any())).
			thenReturn(Optional.of(data));
		byte[] result = expedientTipusRecursHelper.loadResource(1L, 2L, "test.txt");
		assertArrayEquals(data, result);
	}

	@Test
	public void shouldReturnNullWhenResourceNotFound() throws Exception {
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			any(), any(), any(), any())).
			thenReturn(Optional.empty());
		byte[] result = expedientTipusRecursHelper.loadResource(1L, 2L, "missing.txt");
		assertNull(result);
	}

	// --------------------------------------------------
	// getHandlerParameters
	// --------------------------------------------------

	@Test
	void shouldGetHandlerParameters() throws Exception {
		String resourceName = HANDLER_RESOURCE_CLASS.replace('.', '/') + ".class";
		byte[] bytes = getResourceBytesFromJarFile(resourceName);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			HANDLER_RESOURCE_CLASS.replace('.', '/') + ".class",
			true)).
			thenReturn(Optional.of(bytes));
		List<ExpedientTipusRecursHelper.HandlerParameter> params = expedientTipusRecursHelper.getHandlerParameters(
			expedientTipusId, definicioProcesId, HANDLER_RESOURCE_CLASS);
		assertEquals(1, params.size());
		assertEquals("variable1", params.get(0).getName());
	}

	// --------------------------------------------------
	// createHandlerInstance
	// --------------------------------------------------

	@Test
	void shouldCreateHandlerInstanceAndSetValues() throws Exception {
		String resourceName = HANDLER_RESOURCE_CLASS.replace('.', '/') + ".class";
		byte[] bytes = getResourceBytesFromJarFile(resourceName);
		when(recursRepository.findContingutByExpedientTipusIdAndDefinicioProcesIdAndNameAndClasse(
			expedientTipusId,
			definicioProcesId,
			resourceName,
			true)).
			thenReturn(Optional.of(bytes));
		ExpedientTipus tipus = new ExpedientTipus();
		tipus.setId(expedientTipusId);
		Expedient expedient = new Expedient();
		expedient.setTipus(tipus);
		Map<String, String> values = new HashMap<>();
		values.put("variable1", "valor-prova");
		HeliumActionHandler handler = expedientTipusRecursHelper.createHandlerInstance(
			expedient,
			definicioProcesId,
			HANDLER_RESOURCE_CLASS,
			values);
		assertNotNull(handler);
		assertEquals(HANDLER_RESOURCE_CLASS, handler.getClass().getName());
		Field variable1Field = handler.getClass().getDeclaredField("variable1");
		variable1Field.setAccessible(true);
		assertEquals("valor-prova", variable1Field.get(handler));
	}

	private byte[] createFakeJar(String... entries) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (JarOutputStream jos = new JarOutputStream(baos)) {
			for (String entry: entries) {
				jos.putNextEntry(new JarEntry(entry));
				jos.write("content".getBytes());
				jos.closeEntry();
			}
		}
		return baos.toByteArray();
	}

	private byte[] loadRecursosJarFile() throws IOException {
		try (InputStream is = getClass().getResourceAsStream("/recursos.jar")) {
			assertNotNull(is);
			return is.readAllBytes();
		}
	}

	private byte[] getResourceBytesFromJarFile(String name) throws IOException, ClassNotFoundException {
		byte[] content = null;
		try (InputStream is = new ByteArrayInputStream(loadRecursosJarFile()); JarInputStream jis = new JarInputStream(is)) {
			JarEntry entry;
			while ((entry = jis.getNextJarEntry()) != null) {
				if (name.equals(entry.getName())) {
					content = readJarEntryBytes(jis);
				}
			}
		}
		return content;
	}

	private byte[] readJarEntryBytes(JarInputStream jis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read;
		while ((read = jis.read(buffer)) != -1) {
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}

	private static class DummyClassGenerator {
		public static byte[] generateSimpleClassBytes(String className) {
			return new ByteBuddy()
				.subclass(Object.class)
				.name(className)
				.make()
				.getBytes();
		}
	}

}
