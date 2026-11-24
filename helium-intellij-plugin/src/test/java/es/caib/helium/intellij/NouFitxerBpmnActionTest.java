package es.caib.helium.intellij;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class NouFitxerBpmnActionTest extends BasePlatformTestCase {

	private static final String BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	private static final String BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI";

	public void testCreaUnProcesExecutableAmbLEsdevenimentDInici() throws Exception {
		PsiFile fitxer = crear("Alta d'expedient & tràmit");

		assertEquals("Alta d'expedient & tràmit.bpmn", fitxer.getName());
		Document xml = llegir(fitxer.getText());
		Element proces = (Element) xml.getElementsByTagNameNS(BPMN, "process").item(0);
		assertEquals("Alta_d_expedient_tramit", proces.getAttribute("id"));
		assertEquals("Alta d'expedient & tràmit", proces.getAttribute("name"));
		assertEquals("true", proces.getAttribute("isExecutable"));
		Element inici = (Element) proces.getElementsByTagNameNS(BPMN, "startEvent").item(0);
		assertNotNull(inici);
		// El diagrama (DI) dibuixa el procés i l'esdeveniment d'inici
		Element pla = (Element) xml.getElementsByTagNameNS(BPMNDI, "BPMNPlane").item(0);
		assertEquals(proces.getAttribute("id"), pla.getAttribute("bpmnElement"));
		Element forma = (Element) pla.getElementsByTagNameNS(BPMNDI, "BPMNShape").item(0);
		assertEquals(inici.getAttribute("id"), forma.getAttribute("bpmnElement"));
	}

	public void testLAccioEsAlMenuNewAbansDeLesPlantilles() {
		ActionManager actions = ActionManager.getInstance();
		DefaultActionGroup nou = (DefaultActionGroup) actions.getAction("NewGroup");
		// El grup guarda referències diferides a les accions: es compara per identificador
		List<String> ids = Arrays.stream(nou.getChildActionsOrStubs()).map(actions::getId).toList();
		assertTrue(ids.toString(), ids.contains("HeliumBpmn.NouFitxer"));
		assertEquals(ids.indexOf("NewFromTemplate") - 1, ids.indexOf("HeliumBpmn.NouFitxer"));
		assertInstanceOf(actions.getAction("HeliumBpmn.NouFitxer"), NouFitxerBpmnAction.class);
	}

	public void testSiSEscriuLExtensioNoEsRepeteix() throws Exception {
		assertEquals("proces.bpmn", crear("proces.bpmn").getName());
	}

	private PsiFile crear(String nom) throws Exception {
		PsiDirectory directori = myFixture.getPsiManager().findDirectory(myFixture.getTempDirFixture().findOrCreateDir("bpmn"));
		return WriteCommandAction.writeCommandAction(getProject())
				.compute(() -> NouFitxerBpmnAction.crear(nom, directori, false));
	}

	private static Document llegir(String text) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder().parse(new InputSource(new StringReader(text)));
	}

}
