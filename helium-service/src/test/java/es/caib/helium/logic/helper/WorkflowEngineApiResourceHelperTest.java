package es.caib.helium.logic.helper;

import es.caib.helium.disseny.handler.HeliumActionHandler;
import es.caib.helium.logic.intf.service.WorkflowEngineApi;
import net.bytebuddy.ByteBuddy;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaris per a RecursHelper.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(MockitoExtension.class)
public class WorkflowEngineApiResourceHelperTest {

	private static final String HANDLER_RESOURCE_CLASS = "es.caib.helium.test.handler.TestHandler";

	@Mock
	private WorkflowEngineApi workflowEngineApi;

	private WorkflowEngineApiResourceHelper workflowEngineApiResourceHelper;

	private final String deploymentId = "1234567890";

	@BeforeEach
	public void setUp() {
		workflowEngineApiResourceHelper = new WorkflowEngineApiResourceHelper(workflowEngineApi);
	}

	// --------------------------------------------------
	// loadClass
	// --------------------------------------------------

	@Test
	public void shouldLoadClassSuccessfully() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(workflowEngineApi.getResourceBytes(
			deploymentId,
			className.replace('.', '/') + ".class")).
			thenReturn(bytes);
		Class<?> clazz = workflowEngineApiResourceHelper.loadClass(deploymentId, className, Object.class);
		assertNotNull(clazz);
		assertEquals(className, clazz.getName());
	}

	@Test
	public void shouldFailWhenTypeNotAssignable() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(workflowEngineApi.getResourceBytes(
			deploymentId,
			className.replace('.', '/') + ".class")).
			thenReturn(bytes);
		// String.class no és assignable
		assertThrows(ClassCastException.class, () ->
			workflowEngineApiResourceHelper.loadClass(deploymentId, className, String.class)
		);
	}

	// --------------------------------------------------
	// loadClassAndCreateInstance
	// --------------------------------------------------

	@Test
	public void shouldCreateInstance() throws Exception {
		String className = "test.dynamic.MyClass";
		byte[] bytes = DummyClassGenerator.generateSimpleClassBytes(className);
		when(workflowEngineApi.getResourceBytes(
			deploymentId,
			className.replace('.', '/') + ".class")).
			thenReturn(bytes);
		Object instance = workflowEngineApiResourceHelper.loadClassAndCreateInstance(deploymentId, className, Object.class);
		assertNotNull(instance);
		assertEquals(className, instance.getClass().getName());
	}

	// --------------------------------------------------
	// loadResource
	// --------------------------------------------------

	@Test
	public void shouldLoadResource() throws Exception {
		byte[] data = "hola".getBytes();
		when(workflowEngineApi.getResourceBytes(
			eq(deploymentId),
			eq("test.txt"))).
			thenReturn(data);
		byte[] result = workflowEngineApiResourceHelper.loadResource(deploymentId, "test.txt");
		assertArrayEquals(data, result);
	}

	@Test
	public void shouldReturnNullWhenResourceNotFound() throws Exception {
		when(workflowEngineApi.getResourceBytes(any(), any())).
			thenThrow(new FlowableObjectNotFoundException(deploymentId));
		byte[] result = workflowEngineApiResourceHelper.loadResource(deploymentId, "missing.txt");
		assertNull(result);
	}

	// --------------------------------------------------
	// getHandlerParameters
	// --------------------------------------------------

	@Test
	void shouldGetHandlerParameters() throws Exception {
		String resourceName = HANDLER_RESOURCE_CLASS.replace('.', '/') + ".class";
		byte[] bytes = getResourceBytesFromJarFile(resourceName);
		when(workflowEngineApi.getResourceBytes(
			deploymentId,
			resourceName)).
			thenReturn(bytes);
		List<ExpedientTipusRecursHelper.HandlerParameter> params = workflowEngineApiResourceHelper.getHandlerParameters(
			deploymentId, HANDLER_RESOURCE_CLASS);
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
		when(workflowEngineApi.getResourceBytes(
			deploymentId,
			resourceName)).
			thenReturn(bytes);
		Map<String, String> values = new HashMap<>();
		values.put("variable1", "valor-prova");
		HeliumActionHandler handler = workflowEngineApiResourceHelper.createHandlerInstance(
			deploymentId,
			HANDLER_RESOURCE_CLASS,
			values);
		assertNotNull(handler);
		assertEquals(HANDLER_RESOURCE_CLASS, handler.getClass().getName());
		Field variable1Field = handler.getClass().getDeclaredField("variable1");
		variable1Field.setAccessible(true);
		assertEquals("valor-prova", variable1Field.get(handler));
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
