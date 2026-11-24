package es.caib.helium.intellij;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.DocumentReference;
import com.intellij.openapi.command.undo.DocumentReferenceManager;
import com.intellij.openapi.command.undo.DocumentReferenceProvider;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.util.Alarm;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

/**
 * Pestanya "Disseny" dels fitxers BPMN: l'editor visual de Helium dins un navegador JCEF.
 *
 * <p>El document del fitxer és la font de veritat, i la pestanya de text continua disponible:</p>
 * <ul>
 *   <li>Cada canvi del diagrama arriba com a XML sencer i s'escriu al document amb una comanda, de manera que desar,
 *   el control de versions i desfer/refer són els de l'IDE.</li>
 *   <li>Quan el document canvia per una altra via (pestanya de text, desfer, control de versions...), el diagrama es
 *   torna a carregar.</li>
 *   <li>Ctrl+Z / Ctrl+Shift+Z dins l'editor visual s'envien aquí i es desfan amb l'{@link UndoManager} de l'IDE.</li>
 * </ul>
 *
 * <p>El protocol de missatges està descrit a {@code designer/src/ide.ts}.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class HeliumBpmnEditor extends UserDataHolderBase implements FileEditor, DocumentReferenceProvider {

	static final String NOM = "Disseny";
	private static final String GRUP_NOTIFICACIONS = "Helium BPMN";
	private static final int RETARD_ACTUALITZACIO_MS = 300;
	/** Espera després de l'últim canvi al codi abans de recalcular els handlers propis. */
	private static final int RETARD_HANDLERS_MS = 1000;
	/** Canviar de tema dispara diversos esdeveniments seguits: s'aplica quan s'acaben. */
	private static final int RETARD_TEMA_MS = 300;

	private static final Logger LOG = Logger.getInstance(HeliumBpmnEditor.class);
	private static final Gson GSON = new Gson();

	private final Project project;
	private final VirtualFile file;
	private final Document document;
	private final JComponent component;
	private final JBCefBrowser browser;
	private final Alarm alarm;
	private final Alarm alarmHandlers;
	private final Alarm alarmTema;
	private final DiagnosticEditor diagnostic;
	/** Codi JavaScript que envia un missatge al plugin (veure {@link RecursosEditor#paginaEditor}). */
	private final String codiEnviar;

	/** El document s'està modificant des del diagrama: no cal tornar-lo a enviar a l'editor. */
	private boolean escrivint;
	/** L'editor ja ha rebut el diagrama inicial. */
	private boolean carregat;
	/** HTML de la pàgina de l'editor, amb els colors del tema actual de l'IDE (el llegeix el fil de CEF). */
	private volatile String pagina;
	private RecursosEditor.Tema tema;
	/**
	 * Identifica el càlcul del catàleg d'aquest editor perquè un càlcul nou substitueixi el pendent. No pot ser
	 * l'editor: la plataforma no admet un FileEditor (ni un Project, Document...) com a clau de coalesceBy.
	 */
	private final Object calculHandlers = new Object();
	/** Catàleg de handlers que té l'editor (JSON), per no tornar-lo a enviar si no ha canviat. */
	private String handlersEnviats;
	/** Comptador de recàrregues de la pàgina, perquè cada recàrrega tingui una URL diferent. */
	private int recarregues;
	private boolean disposed;

	HeliumBpmnEditor(@NotNull Project project, @NotNull VirtualFile file) {
		this.project = project;
		this.file = file;
		this.document = FileDocumentManager.getInstance().getDocument(file);
		if (!JBCefApp.isSupported() || document == null) {
			LOG.warn("No es pot obrir l'editor BPMN de " + file.getPath()
					+ (document == null ? ": el fitxer no es pot llegir com a text" : ": JCEF no està disponible"));
			this.browser = null;
			this.alarm = null;
			this.alarmHandlers = null;
			this.alarmTema = null;
			this.codiEnviar = null;
			this.diagnostic = null;
			this.component = new JBLabel(
					document == null
							? "No s'ha pogut obrir el fitxer com a text."
							: "L'editor visual necessita el navegador incrustat de l'IDE (JCEF), que no està disponible.",
					SwingConstants.CENTER);
			return;
		}
		LOG.info("Obrint l'editor BPMN de " + file.getPath());
		// "Open DevTools" al menú del botó dret, per veure els errors de l'editor
		this.browser = JBCefBrowser.createBuilder().setEnableOpenDevToolsMenuItem(true).build();
		Disposer.register(this, browser);
		this.alarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
		this.alarmHandlers = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
		this.alarmTema = new Alarm(Alarm.ThreadToUse.SWING_THREAD, this);
		this.diagnostic = new DiagnosticEditor(this, project, file.getName(), browser, this::recarregar);

		JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
		Disposer.register(this, query);
		query.addHandler(missatge -> {
			// Arriba pel fil de CEF. Els canvis al document s'han de fer en un context "write-safe": no es pot fer
			// servir ModalityState.any(), i l'editor de fitxers no és modal.
			ApplicationManager.getApplication().invokeLater(() -> rebre(missatge), ModalityState.nonModal(),
					o -> disposed);
			return null;
		});
		this.codiEnviar = query.inject("missatge");
		this.tema = temaIde();
		pagina = RecursosEditor.paginaEditor(codiEnviar, tema);
		browser.getJBCefClient().addRequestHandler(new PeticionsEditor(() -> pagina), browser.getCefBrowser());
		// El pont ja és a la pàgina, però es torna a definir en acabar la càrrega (quan segur que la funció nativa
		// del JBCefJSQuery existeix) i l'editor envia els missatges que tenia pendents
		browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
			@Override
			public void onLoadEnd(CefBrowser cefBrowser, CefFrame frame, int httpStatusCode) {
				if (frame.isMain()) {
					cefBrowser.executeJavaScript(
							"window.__heliumIdeEnviar = function (missatge) { " + codiEnviar + " };"
									+ " window.__heliumIdeConnectat && window.__heliumIdeConnectat();",
							cefBrowser.getURL(), 0);
				}
			}
		}, browser.getCefBrowser());

		document.addDocumentListener(new DocumentListener() {
			@Override
			public void documentChanged(@NotNull DocumentEvent event) {
				if (!escrivint && carregat) {
					alarm.cancelAllRequests();
					alarm.addRequest(HeliumBpmnEditor.this::enviarDocument, RETARD_ACTUALITZACIO_MS);
				}
			}
		}, this);

		// Canvi de tema de l'IDE (aparença o esquema de colors de l'editor)
		var busAplicacio = ApplicationManager.getApplication().getMessageBus().connect(this);
		busAplicacio.subscribe(EditorColorsManager.TOPIC, esquema -> programar(alarmTema, this::aplicarTema, RETARD_TEMA_MS));
		busAplicacio.subscribe(LafManagerListener.TOPIC, laf -> programar(alarmTema, this::aplicarTema, RETARD_TEMA_MS));
		// Canvis al codi del projecte (p. ex. un handler nou o un camp nou d'un handler): es recalcula el catàleg
		project.getMessageBus().connect(this).subscribe(PsiModificationTracker.TOPIC, () -> {
			if (carregat) {
				programar(alarmHandlers, this::enviarHandlers, RETARD_HANDLERS_MS);
			}
		});

		carregarPagina(RecursosEditor.URL_EDITOR);
		this.component = diagnostic.component();
	}

	private void carregarPagina(String url) {
		diagnostic.carregant(url);
		browser.loadURL(url);
	}

	/** Torna a carregar la pàgina de l'editor; el diagrama surt del document i l'editor conserva la vista. */
	private void recarregar() {
		if (disposed) {
			return;
		}
		carregat = false;
		alarm.cancelAllRequests();
		alarmHandlers.cancelAllRequests();
		// URL diferent a cada recàrrega: el navegador no pot reaprofitar la pàgina anterior
		carregarPagina(RecursosEditor.URL_EDITOR + "?v=" + (++recarregues));
	}

	private static void programar(Alarm alarm, Runnable accio, int retard) {
		alarm.cancelAllRequests();
		alarm.addRequest(accio, retard);
	}

	/**
	 * Si el tema ha canviat, es torna a carregar la pàgina amb els colors nous. El diagrama surt del document i
	 * l'editor conserva la vista; no es pot canviar en calent perquè bpmn-js fixa els colors en crear el modelador.
	 */
	private void aplicarTema() {
		RecursosEditor.Tema nou = temaIde();
		if (disposed || nou.equals(tema)) {
			return;
		}
		LOG.info("Tema de l'IDE canviat: es torna a carregar l'editor BPMN de " + file.getName() + " (" + nou + ")");
		tema = nou;
		pagina = RecursosEditor.paginaEditor(codiEnviar, nou);
		recarregar();
	}

	/** Colors de l'editor a partir de l'esquema de colors de l'editor de text de l'IDE. */
	private static RecursosEditor.Tema temaIde() {
		EditorColorsScheme esquema = EditorColorsManager.getInstance().getGlobalScheme();
		Color fons = esquema.getDefaultBackground();
		Color text = esquema.getDefaultForeground();
		if (!ColorUtil.isDark(fons)) {
			// En temes clars l'editor es veu com a Helium
			return RecursosEditor.Tema.CLAR;
		}
		return new RecursosEditor.Tema(true, hex(fons), hex(text), hex(ColorUtil.mix(fons, text, 0.08)));
	}

	/** Color CSS {@code #rrggbb}. */
	static String hex(Color color) {
		return "#" + ColorUtil.toHex(color, false);
	}

	// --- Missatges de l'editor ----------------------------------------------------------------------------------

	private void rebre(String json) {
		if (disposed) {
			return;
		}
		JsonObject missatge;
		try {
			missatge = JsonParser.parseString(json).getAsJsonObject();
		} catch (JsonParseException | IllegalStateException ex) {
			LOG.warn("Missatge de l'editor BPMN no vàlid: " + json, ex);
			return;
		}
		String tipus = missatge.has("tipus") ? missatge.get("tipus").getAsString() : "";
		switch (tipus) {
			case "preparat" -> {
				diagnostic.editorPreparat();
				carregar();
			}
			case "canvi" -> aplicarCanvi(missatge.get("xml").getAsString());
			case "desfer" -> desferORefer(true);
			case "refer" -> desferORefer(false);
			case "error" -> notificarError(missatge.has("missatge") ? missatge.get("missatge").getAsString() : "");
			default -> LOG.warn("Missatge de l'editor BPMN desconegut: " + tipus);
		}
	}

	/**
	 * Envia el diagrama de seguida i el catàleg de handlers quan els índexs estan disponibles (en obrir un projecte
	 * l'IDE pot estar indexant i no cal esperar per veure el diagrama).
	 */
	private void carregar() {
		executar("window.heliumIde && window.heliumIde.carregar(" + GSON.toJson(document.getText()) + ", "
				+ CatalegHandlers.catalegPlugin() + ")");
		carregat = true;
		handlersEnviats = null;
		enviarHandlers();
	}

	/** Calcula el catàleg de handlers amb els índexs disponibles i l'envia a l'editor si ha canviat. */
	private void enviarHandlers() {
		CatalegHandlers.calcular(project, this, calculHandlers, handlers -> {
			if (carregat && !handlers.equals(handlersEnviats)) {
				handlersEnviats = handlers;
				executar("window.heliumIde && window.heliumIde.handlers(" + handlers + ")");
			}
		});
	}

	private void aplicarCanvi(String xml) {
		String text = StringUtil.convertLineSeparators(xml);
		if (text.equals(document.getText())) {
			return;
		}
		if (!FileDocumentManager.getInstance().requestWriting(document, project)) {
			// Fitxer només de lectura: el diagrama torna a l'estat del document
			enviarDocument();
			return;
		}
		WriteCommandAction.writeCommandAction(project)
				.withName("Editar el diagrama BPMN")
				.run(() -> {
					escrivint = true;
					try {
						document.setText(text);
					} finally {
						escrivint = false;
					}
				});
	}

	private void desferORefer(boolean desfer) {
		UndoManager undoManager = UndoManager.getInstance(project);
		if (desfer && undoManager.isUndoAvailable(this)) {
			undoManager.undo(this);
		} else if (!desfer && undoManager.isRedoAvailable(this)) {
			undoManager.redo(this);
		}
	}

	private void notificarError(String detall) {
		NotificationGroupManager.getInstance()
				.getNotificationGroup(GRUP_NOTIFICACIONS)
				.createNotification(
						"No s'ha pogut mostrar el diagrama de " + file.getName(),
						StringUtil.escapeXmlEntities(detall),
						NotificationType.ERROR)
				.notify(project);
	}

	// --- Cap a l'editor -----------------------------------------------------------------------------------------

	private void enviarDocument() {
		if (!disposed) {
			executar("window.heliumIde && window.heliumIde.actualitzar(" + GSON.toJson(document.getText()) + ")");
		}
	}

	private void executar(String codi) {
		browser.getCefBrowser().executeJavaScript(codi, browser.getCefBrowser().getURL(), 0);
	}

	// --- FileEditor ---------------------------------------------------------------------------------------------

	@Override
	public @NotNull JComponent getComponent() {
		return component;
	}

	@Override
	public @Nullable JComponent getPreferredFocusedComponent() {
		return component;
	}

	@Override
	public @NotNull String getName() {
		return NOM;
	}

	@Override
	public @NotNull VirtualFile getFile() {
		return file;
	}

	@Override
	public void setState(@NotNull FileEditorState state) {
	}

	@Override
	public boolean isModified() {
		// Els canvis són al document: l'IDE ja mostra si està pendent de desar
		return false;
	}

	@Override
	public boolean isValid() {
		return file.isValid();
	}

	@Override
	public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
	}

	@Override
	public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
	}

	/** Desfer/refer d'aquest editor és el del document del fitxer. */
	@Override
	public @NotNull Collection<DocumentReference> getDocumentReferences() {
		return document != null ? List.of(DocumentReferenceManager.getInstance().create(document)) : List.of();
	}

	@Override
	public void dispose() {
		disposed = true;
	}

}
