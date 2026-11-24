package es.caib.helium.intellij;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.util.Alarm;

/**
 * Diagnòstic de la càrrega de l'editor visual dins el navegador incrustat (JCEF).
 *
 * <p>Si l'editor no arriba a funcionar, la pestanya no es queda en blanc sense explicació:</p>
 * <ul>
 *   <li>Cada pas (inici i final de la càrrega, errors, missatges d'error de la consola de JavaScript, editor preparat)
 *   s'escriu al registre de l'IDE ({@code idea.log}) amb el nom del fitxer.</li>
 *   <li>Si la pàgina no es pot carregar, o l'editor no avisa que està preparat en {@link #ESPERA_EDITOR_MS} ms, es
 *   mostra un avís a dalt de la pestanya amb el motiu i accions per tornar-ho a provar, obrir les eines de
 *   desenvolupador del navegador o obrir el registre.</li>
 * </ul>
 *
 * <p>Si el registre diu que l'editor està preparat però la pestanya es veu buida, el problema és de com el navegador
 * pinta (acceleració gràfica): la vista prèvia de Markdown, que fa servir el mateix navegador, també es veurà buida.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class DiagnosticEditor {

	static final int ESPERA_EDITOR_MS = 20_000;

	private static final Logger LOG = Logger.getInstance(DiagnosticEditor.class);
	private static final String CONSELL_GRAFICS = "Si la vista prèvia dels fitxers Markdown tampoc es veu, el problema és del "
			+ "navegador de l'IDE: activa ide.browser.jcef.gpu.disable a Help → Find Action → Registry i reinicia l'IDE.";

	private final Project project;
	private final String nomFitxer;
	private final JBCefBrowser browser;
	private final Runnable recarregar;
	private final JPanel contenidor;
	private final Alarm alarm;

	private EditorNotificationPanel avis;
	/** Darrer error de JavaScript abans que l'editor estigui preparat (el fil de CEF l'escriu). */
	private volatile String darrerErrorConsola;
	private boolean preparat;

	/**
	 * @param recarregar torna a carregar la pàgina de l'editor (acció de l'avís).
	 */
	DiagnosticEditor(Disposable pare, Project project, String nomFitxer, JBCefBrowser browser, Runnable recarregar) {
		this.project = project;
		this.nomFitxer = nomFitxer;
		this.browser = browser;
		this.recarregar = recarregar;
		this.contenidor = new JPanel(new BorderLayout());
		this.contenidor.add(browser.getComponent(), BorderLayout.CENTER);
		this.alarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, pare);
		browser.getJBCefClient().addLoadHandler(new Carrega(), browser.getCefBrowser());
		browser.getJBCefClient().addDisplayHandler(new Consola(), browser.getCefBrowser());
		LOG.info("Editor BPMN de " + nomFitxer + ": JCEF " + versioJcef()
				+ (JBCefApp.isOffScreenRenderingModeEnabled() ? " (renderització fora de pantalla)" : " (finestra pròpia)"));
	}

	/** Component de la pestanya: el navegador, amb l'avís a sobre quan n'hi ha. */
	JComponent component() {
		return contenidor;
	}

	/** S'ha començat a carregar la pàgina de l'editor: es compta el temps fins que avisi que està preparat. */
	void carregant(String url) {
		LOG.info("Editor BPMN de " + nomFitxer + ": carregant " + url);
		preparat = false;
		darrerErrorConsola = null;
		amagarAvis();
		alarm.cancelAllRequests();
		alarm.addRequest(this::senseResposta, ESPERA_EDITOR_MS);
	}

	/** L'editor ha avisat que està preparat (missatge "preparat"). */
	void editorPreparat() {
		LOG.info("Editor BPMN de " + nomFitxer + ": editor preparat");
		preparat = true;
		alarm.cancelAllRequests();
		amagarAvis();
	}

	private void senseResposta() {
		if (preparat) {
			return;
		}
		String text = textSenseResposta(ESPERA_EDITOR_MS / 1000, darrerErrorConsola);
		LOG.warn("Editor BPMN de " + nomFitxer + ": " + text);
		mostrarAvis(text);
	}

	private void errorCarrega(String detall) {
		alarm.cancelAllRequests();
		String text = "No s'ha pogut carregar l'editor visual: " + detall + ".";
		LOG.warn("Editor BPMN de " + nomFitxer + ": " + text);
		mostrarAvis(text);
	}

	static String textSenseResposta(int segons, @Nullable String darrerError) {
		StringBuilder text = new StringBuilder("L'editor visual no ha respost en ").append(segons).append(" s.");
		if (darrerError != null) {
			text.append(" Darrer error de JavaScript: ").append(darrerError).append('.');
		}
		return text.append(' ').append(CONSELL_GRAFICS).toString();
	}

	/** Els errors de càrrega que no són una fallada: una navegació substituïda per una altra (p. ex. una recàrrega). */
	static boolean esErrorReal(CefLoadHandler.ErrorCode codi) {
		return codi != CefLoadHandler.ErrorCode.ERR_NONE && codi != CefLoadHandler.ErrorCode.ERR_ABORTED;
	}

	// --- Avís a la pestanya -------------------------------------------------------------------------------------

	private void mostrarAvis(String text) {
		amagarAvis();
		avis = new EditorNotificationPanel(EditorNotificationPanel.Status.Error);
		avis.setText(text);
		avis.createActionLabel("Tornar a carregar", recarregar);
		avis.createActionLabel("Obrir DevTools", browser::openDevtools);
		avis.createActionLabel("Obrir el registre de l'IDE", this::obrirRegistre);
		contenidor.add(avis, BorderLayout.NORTH);
		contenidor.revalidate();
		contenidor.repaint();
	}

	private void amagarAvis() {
		if (avis != null) {
			contenidor.remove(avis);
			avis = null;
			contenidor.revalidate();
			contenidor.repaint();
		}
	}

	private void obrirRegistre() {
		VirtualFile registre = LocalFileSystem.getInstance()
				.refreshAndFindFileByNioFile(PathManager.getLogDir().resolve("idea.log"));
		if (registre != null) {
			FileEditorManager.getInstance(project).openFile(registre, true);
		}
	}

	private static String versioJcef() {
		try {
			return JBCefApp.getNativeBundleVersionString();
		} catch (RuntimeException | LinkageError ex) {
			return "(versió desconeguda)";
		}
	}

	private static void aLaUi(Runnable accio) {
		ApplicationManager.getApplication().invokeLater(accio, ModalityState.any());
	}

	// --- Esdeveniments del navegador (fil de CEF) ---------------------------------------------------------------

	private final class Carrega extends CefLoadHandlerAdapter {

		@Override
		public void onLoadEnd(CefBrowser cefBrowser, CefFrame frame, int httpStatusCode) {
			if (!frame.isMain()) {
				return;
			}
			LOG.info("Editor BPMN de " + nomFitxer + ": pàgina carregada (HTTP " + httpStatusCode + ")");
			if (httpStatusCode != 200 && httpStatusCode != 0) {
				aLaUi(() -> errorCarrega("la pàgina ha respost amb l'estat HTTP " + httpStatusCode));
			}
		}

		@Override
		public void onLoadError(CefBrowser cefBrowser, CefFrame frame, CefLoadHandler.ErrorCode errorCode,
				String errorText, String failedUrl) {
			if (frame.isMain() && esErrorReal(errorCode)) {
				aLaUi(() -> errorCarrega(errorText + " (" + errorCode + ", " + failedUrl + ")"));
			}
		}

	}

	private final class Consola extends CefDisplayHandlerAdapter {

		@Override
		public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity level, String message,
				String source, int line) {
			if (level == CefSettings.LogSeverity.LOGSEVERITY_ERROR || level == CefSettings.LogSeverity.LOGSEVERITY_FATAL) {
				LOG.warn("Editor BPMN de " + nomFitxer + ", consola: " + message + " (" + source + ":" + line + ")");
				darrerErrorConsola = message;
			} else if (level == CefSettings.LogSeverity.LOGSEVERITY_WARNING) {
				LOG.info("Editor BPMN de " + nomFitxer + ", consola: " + message + " (" + source + ":" + line + ")");
			}
			return false;
		}

	}

}
