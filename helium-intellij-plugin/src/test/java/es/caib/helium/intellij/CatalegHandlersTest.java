package es.caib.helium.intellij;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class CatalegHandlersTest extends LightJavaCodeInsightFixtureTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		myFixture.addClass("package es.caib.helium.bpmn.handler; public interface HeliumBpmnHandler { }");
	}

	public void testElsHandlersPropisSonLesClassesQueImplementenLaInterficie() {
		myFixture.addClass("package es.limit.prova;"
				+ " import es.caib.helium.bpmn.handler.HeliumBpmnHandler;"
				+ " public class ProvaHandler implements HeliumBpmnHandler {"
				+ "   private static final long serialVersionUID = 1L;"
				+ "   private String nom; private String valor;"
				+ " }");
		myFixture.addClass("package es.limit.prova;"
				+ " import es.caib.helium.bpmn.handler.HeliumBpmnHandler;"
				+ " public abstract class BaseHandler implements HeliumBpmnHandler { }");
		myFixture.addClass("package es.limit.prova; public class FillHandler extends BaseHandler { private String codi; }");
		myFixture.addClass("package es.limit.prova; public class NoEsHandler { private String x; }");

		List<JsonObject> propis = CatalegHandlers.propis(getProject());

		assertEquals(List.of("es.limit.prova.FillHandler", "es.limit.prova.ProvaHandler"),
				propis.stream().map(h -> h.get("classe").getAsString()).toList());
		JsonObject prova = propis.get(1);
		assertEquals("ProvaHandler", prova.get("nom").getAsString());
		// Els camps estàtics no són paràmetres
		assertEquals(List.of("nom", "valor"), codis(prova.getAsJsonArray("parametres")));
	}

	/** El càlcul en segon pla que fa servir l'editor (el que envia el catàleg al diagrama). */
	public void testElCalculEnSegonPlaLliuraElCatalegAmbElsHandlersPropis() {
		myFixture.addClass("package es.limit.prova;"
				+ " import es.caib.helium.bpmn.handler.HeliumBpmnHandler;"
				+ " public class ProvaHandler implements HeliumBpmnHandler { private String nom; }");
		String[] resultat = new String[1];

		PlatformTestUtil.waitForPromise(
				CatalegHandlers.calcular(getProject(), getTestRootDisposable(), new Object(), json -> resultat[0] = json));

		assertNotNull(resultat[0]);
		assertTrue(classes(CatalegHandlers.llegirCataleg(resultat[0])).contains("es.limit.prova.ProvaHandler"));
	}

	public void testSenseHeliumDissenyNoHiHaHandlersPropis() throws Exception {
		myFixture.addClass("package es.limit.prova; public class ProvaHandler { private String nom; }");
		// La interfície del setUp és l'única referència a Helium: cap classe la implementa
		assertTrue(CatalegHandlers.propis(getProject()).isEmpty());
	}

	public void testSiElProjecteNoTeElCatalegEsFaServirElDelPlugin() {
		JsonArray handlers = CatalegHandlers.handlers(getProject());
		assertTrue(handlers.size() > 10);
		assertTrue(classes(handlers).contains("es.caib.helium.logic.bpmn.ExpedientComentariModificarHandler"));
	}

	public void testUnHandlerPropiAmbLaClasseDUnPredefinitNoEsDuplica() {
		myFixture.addClass("package es.caib.helium.logic.bpmn;"
				+ " import es.caib.helium.bpmn.handler.HeliumBpmnHandler;"
				+ " public class ExpedientComentariModificarHandler implements HeliumBpmnHandler { private String comentari; }");
		List<String> classes = classes(CatalegHandlers.handlers(getProject()));
		assertEquals(1, classes.stream().filter("es.caib.helium.logic.bpmn.ExpedientComentariModificarHandler"::equals).count());
	}

	public void testCatalegDelProjecte() {
		myFixture.addFileToProject("resources/handlersPredefinits.json",
				"[{\"classe\": \"es.caib.helium.logic.bpmn.UnicHandler\", \"nom\": \"Únic\", \"agrupacio\": \"GENERICS\"}]");
		assertEquals(List.of("es.caib.helium.logic.bpmn.UnicHandler"), classes(CatalegHandlers.predefinits(getProject())));
	}

	private static List<String> codis(JsonArray parametres) {
		return parametres.asList().stream().map(p -> p.getAsJsonObject().get("codi").getAsString()).toList();
	}

	private static List<String> classes(JsonArray handlers) {
		return handlers.asList().stream().map(JsonElement::getAsJsonObject).map(h -> h.get("classe").getAsString()).toList();
	}

}
