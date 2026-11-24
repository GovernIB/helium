package es.caib.helium.intellij;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cef.handler.CefLoadHandler;
import org.junit.Test;

public class DiagnosticEditorTest {

	@Test
	public void unaNavegacioSubstituidaNoEsUnError() {
		// En tornar a carregar l'editor (p. ex. canvi de tema), la càrrega anterior acaba amb ERR_ABORTED
		assertFalse(DiagnosticEditor.esErrorReal(CefLoadHandler.ErrorCode.ERR_ABORTED));
		assertFalse(DiagnosticEditor.esErrorReal(CefLoadHandler.ErrorCode.ERR_NONE));
		assertTrue(DiagnosticEditor.esErrorReal(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND));
	}

	@Test
	public void lAvisDeSenseRespostaInclouElDarrerErrorIElConsell() {
		String text = DiagnosticEditor.textSenseResposta(20, "Uncaught TypeError: x is undefined");
		assertTrue(text, text.startsWith("L'editor visual no ha respost en 20 s."));
		assertTrue(text, text.contains("Darrer error de JavaScript: Uncaught TypeError: x is undefined."));
		assertTrue(text, text.contains("ide.browser.jcef.gpu.disable"));
		assertFalse(DiagnosticEditor.textSenseResposta(20, null).contains("Darrer error"));
	}

}
