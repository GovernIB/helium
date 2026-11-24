package es.caib.helium.intellij;

import java.util.function.Supplier;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import com.intellij.ide.BrowserUtil;

/**
 * Respon les peticions del navegador a l'origen de l'editor amb els recursos del plugin, sense sortir a la xarxa.
 * Els enllaços cap a fora (p. ex. el logotip de bpmn.io) s'obren al navegador del sistema.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class PeticionsEditor extends CefRequestHandlerAdapter {

	/** HTML de la pàgina d'entrada; pot canviar (p. ex. en canviar el tema de l'IDE). */
	private final Supplier<String> paginaEditor;

	PeticionsEditor(Supplier<String> paginaEditor) {
		this.paginaEditor = paginaEditor;
	}

	@Override
	public boolean onBeforeBrowse(CefBrowser browser, CefFrame frame, CefRequest request, boolean userGesture,
			boolean isRedirect) {
		String url = request.getURL();
		if (RecursosEditor.esDeLEditor(url) || url.startsWith("about:")) {
			return false;
		}
		if (userGesture) {
			BrowserUtil.browse(url);
		}
		return true;
	}

	@Override
	public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser, CefFrame frame,
			CefRequest request, boolean isNavigation, boolean isDownload, String requestInitiator,
			BoolRef disableDefaultHandling) {
		if (!RecursosEditor.esDeLEditor(request.getURL())) {
			return null;
		}
		return new CefResourceRequestHandlerAdapter() {
			@Override
			public CefResourceHandler getResourceHandler(CefBrowser browser, CefFrame frame, CefRequest request) {
				return new Resposta(RecursosEditor.recurs(request.getURL(), paginaEditor.get()));
			}
		};
	}

	/** Resposta amb un recurs en memòria (o 404). */
	private static final class Resposta extends CefResourceHandlerAdapter {

		private final RecursosEditor.Recurs recurs;
		private int posicio;

		Resposta(RecursosEditor.Recurs recurs) {
			this.recurs = recurs;
		}

		@Override
		public boolean processRequest(CefRequest request, CefCallback callback) {
			callback.Continue();
			return true;
		}

		@Override
		public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
			if (recurs == null) {
				response.setStatus(404);
				response.setStatusText("Not Found");
				responseLength.set(0);
				return;
			}
			response.setStatus(200);
			response.setStatusText("OK");
			response.setMimeType(recurs.mimeType());
			responseLength.set(recurs.contingut().length);
		}

		@Override
		public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
			if (recurs == null || posicio >= recurs.contingut().length) {
				bytesRead.set(0);
				return false;
			}
			int llegits = Math.min(bytesToRead, recurs.contingut().length - posicio);
			System.arraycopy(recurs.contingut(), posicio, dataOut, 0, llegits);
			posicio += llegits;
			bytesRead.set(llegits);
			return true;
		}

	}

}
