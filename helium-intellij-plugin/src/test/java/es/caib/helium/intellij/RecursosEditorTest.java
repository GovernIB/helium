package es.caib.helium.intellij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/** Necessita el build de l'editor al classpath (processResources). */
public class RecursosEditorTest {

	private static final String PONT = "enviarAlPlugin(missatge);";

	@Test
	public void laPaginaCarregaElBundleAmbElModeIdeIElPont() {
		String pagina = RecursosEditor.paginaEditor(PONT, RecursosEditor.Tema.CLAR);
		assertTrue(pagina.contains("mode: 'ide'"));
		assertTrue(pagina.contains("window.__heliumIdeEnviar = function (missatge) { " + PONT + " };"));
		Matcher script = Pattern.compile("<script type=\"module\" src=\"(/helium2back/designer/[^\"]+\\.js)\"").matcher(pagina);
		assertTrue(pagina, script.find());
		// El bundle que referencia la pàgina existeix i es serveix com a JavaScript
		RecursosEditor.Recurs bundle = RecursosEditor.recurs(RecursosEditor.ORIGEN + script.group(1), pagina);
		assertNotNull(bundle);
		assertEquals("text/javascript", bundle.mimeType());
		assertTrue(pagina.contains("<link rel=\"stylesheet\" href=\"/helium2back/designer/assets/"));
	}

	@Test
	public void laPaginaPortaElsColorsDelTema() {
		RecursosEditor.Tema fosc = new RecursosEditor.Tema(true, "#1e1f22", "#bcbec4", "#2a2b2e");
		String pagina = RecursosEditor.paginaEditor(PONT, fosc);
		assertTrue(pagina.contains("data-tema=\"fosc\""));
		assertTrue(pagina.contains("--ide-fons: #1e1f22; --ide-text: #bcbec4"));
		assertTrue(pagina.contains("tema: {fosc: true, fons: '#1e1f22', text: '#bcbec4', farciment: '#2a2b2e'}"));
		assertTrue(RecursosEditor.paginaEditor(PONT, RecursosEditor.Tema.CLAR).contains("data-tema=\"clar\""));
	}

	@Test
	public void elTemaNomesAdmetColorsHexadecimals() {
		assertThrows(IllegalArgumentException.class,
				() -> new RecursosEditor.Tema(true, "#000000", "red'; alert(1)", "#000000"));
		assertEquals("#1e1f22", HeliumBpmnEditor.hex(new java.awt.Color(0x1e, 0x1f, 0x22)));
	}

	@Test
	public void serveixLaPaginaGenerada() {
		RecursosEditor.Recurs recurs = RecursosEditor.recurs(RecursosEditor.URL_EDITOR, "<html>pàgina</html>");
		assertNotNull(recurs);
		assertEquals("text/html", recurs.mimeType());
		assertEquals("<html>pàgina</html>", new String(recurs.contingut(), StandardCharsets.UTF_8));
	}

	@Test
	public void serveixLaPaginaAmbElParametreDeRecarrega() {
		RecursosEditor.Recurs recurs = RecursosEditor.recurs(RecursosEditor.URL_EDITOR + "?v=3", "<html>pàgina</html>");
		assertNotNull(recurs);
		assertEquals("<html>pàgina</html>", new String(recurs.contingut(), StandardCharsets.UTF_8));
	}

	@Test
	public void noServeixResFóraDeLEditor() {
		assertNull(RecursosEditor.recurs("https://bpmn.io/", ""));
		assertNull(RecursosEditor.recurs(RecursosEditor.ORIGEN + "/altres/fitxer.js", ""));
		assertNull(RecursosEditor.recurs(RecursosEditor.ORIGEN + RecursosEditor.BASE + "../../META-INF/plugin.xml", ""));
		assertNull(RecursosEditor.recurs(RecursosEditor.ORIGEN + RecursosEditor.BASE + "%2e%2e/META-INF/plugin.xml", ""));
		assertNull(RecursosEditor.recurs(RecursosEditor.ORIGEN + RecursosEditor.BASE + "assets/no-existeix.js", ""));
	}

	@Test
	public void tipusMime() {
		assertEquals("font/woff2", RecursosEditor.mimeType("assets/bpmn.woff2"));
		assertEquals("image/svg+xml", RecursosEditor.mimeType("assets/bpmn.SVG"));
		assertEquals("application/octet-stream", RecursosEditor.mimeType("assets/fitxer"));
	}

}
