package es.caib.helium.intellij;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Recursos de l'editor visual de Helium empaquetats dins el plugin.
 *
 * <p>El build de l'editor ({@code helium-back/src/main/jsapp/designer}) es copia al classpath, a {@code designer/}.
 * El navegador el carrega des d'un origen fictici ({@link #ORIGEN}) amb la mateixa ruta base que té a Helium
 * ({@link #BASE}), així les rutes absolutes del build (scripts, estils, fonts) funcionen sense canvis. Les peticions
 * a aquest origen no surten a la xarxa: les respon {@link PeticionsEditor} amb {@link #recurs(String, String)}.</p>
 *
 * <p>La pàgina d'entrada ({@link #URL_EDITOR}) es genera a partir del manifest de Vite: carrega el mateix bundle
 * que la pàgina de Helium, però amb la configuració del mode IDE i el pont de comunicació amb el plugin.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class RecursosEditor {

	/** Origen fictici de l'editor; {@code .localhost} no es resol mai fora de la màquina. */
	static final String ORIGEN = "http://helium-designer.localhost";
	/** Ruta base amb què es compila l'editor (base de vite.config.ts). */
	static final String BASE = "/helium2back/designer/";
	static final String PAGINA = "ide.html";
	static final String URL_EDITOR = ORIGEN + BASE + PAGINA;

	private static final String DIRECTORI = "designer/";
	private static final String MANIFEST = DIRECTORI + ".vite/manifest.json";

	private static final Map<String, String> MIME = Map.ofEntries(
			Map.entry("html", "text/html"),
			Map.entry("js", "text/javascript"),
			Map.entry("mjs", "text/javascript"),
			Map.entry("css", "text/css"),
			Map.entry("json", "application/json"),
			Map.entry("svg", "image/svg+xml"),
			Map.entry("png", "image/png"),
			Map.entry("ico", "image/x-icon"),
			Map.entry("woff", "font/woff"),
			Map.entry("woff2", "font/woff2"),
			Map.entry("ttf", "font/ttf"),
			Map.entry("eot", "application/vnd.ms-fontobject"));

	record Recurs(byte[] contingut, String mimeType) {
	}

	/**
	 * Colors de l'editor, presos del tema de l'IDE perquè el diagrama s'hi integri (colors CSS {@code #rrggbb}).
	 *
	 * @param fosc      l'editor fa servir el tema fosc ({@code data-tema="fosc"}, veure {@code app.css}).
	 * @param fons      fons del llenç i de la pàgina.
	 * @param text      text, línies i vores.
	 * @param farciment interior dels elements del diagrama.
	 */
	record Tema(boolean fosc, String fons, String text, String farciment) {

		static final Tema CLAR = new Tema(false, "#ffffff", "#22242a", "#ffffff");

		Tema {
			for (String color : new String[] {fons, text, farciment}) {
				if (color == null || !color.matches("#[0-9a-fA-F]{6}")) {
					throw new IllegalArgumentException("Color no vàlid: " + color);
				}
			}
		}

	}

	private RecursosEditor() {
	}

	static boolean esDeLEditor(String url) {
		return url != null && url.startsWith(ORIGEN + "/");
	}

	/**
	 * Recurs que correspon a la URL, o null si no és de l'editor o no existeix.
	 *
	 * @param paginaEditor HTML de la pàgina d'entrada (veure {@link #paginaEditor(String)}).
	 */
	static Recurs recurs(String url, String paginaEditor) {
		if (!esDeLEditor(url)) {
			return null;
		}
		String ruta;
		try {
			ruta = URI.create(url).normalize().getPath();
		} catch (IllegalArgumentException ex) {
			return null;
		}
		if (ruta == null || !ruta.startsWith(BASE) || ruta.contains("..")) {
			return null;
		}
		String relativa = ruta.substring(BASE.length());
		if (PAGINA.equals(relativa)) {
			return new Recurs(paginaEditor.getBytes(StandardCharsets.UTF_8), "text/html");
		}
		byte[] contingut = llegir(DIRECTORI + relativa);
		return contingut != null ? new Recurs(contingut, mimeType(relativa)) : null;
	}

	/**
	 * Pàgina d'entrada de l'editor en mode IDE.
	 *
	 * @param codiEnviar cos de la funció JavaScript {@code __heliumIdeEnviar(missatge)} que envia un missatge al
	 *                   plugin (el codi que genera {@code JBCefJSQuery.inject("missatge")}).
	 * @param tema       colors de l'IDE.
	 */
	static String paginaEditor(String codiEnviar, Tema tema) {
		byte[] manifest = llegir(MANIFEST);
		if (manifest == null) {
			throw new IllegalStateException("El plugin no conté l'editor de Helium (falta " + MANIFEST + ")");
		}
		JsonObject entrada = JsonParser.parseString(new String(manifest, StandardCharsets.UTF_8))
				.getAsJsonObject()
				.getAsJsonObject("index.html");
		StringBuilder estils = new StringBuilder();
		JsonArray css = entrada.getAsJsonArray("css");
		if (css != null) {
			for (JsonElement fitxer : css) {
				estils.append("<link rel=\"stylesheet\" href=\"").append(BASE).append(fitxer.getAsString()).append("\">\n");
			}
		}
		// Els colors estan validats (Tema): es poden posar directament a l'HTML i al JavaScript
		return "<!doctype html>\n"
				+ "<html lang=\"ca\" data-tema=\"" + (tema.fosc() ? "fosc" : "clar") + "\""
				+ " style=\"--ide-fons: " + tema.fons() + "; --ide-text: " + tema.text() + "\">\n"
				+ "<head>\n<meta charset=\"UTF-8\">\n<title>Helium BPMN</title>\n"
				+ "<script>\n"
				+ "window.__APP_CONFIG__ = {mode: 'ide', isNew: false, baseUrl: '', returnUrl: '', entornId: '',"
				+ " expedientTipusId: '', definicioProcesId: '', definicioProcesEtiqueta: '', hasStartTask: false,"
				+ " tema: {fosc: " + tema.fosc() + ", fons: '" + tema.fons() + "', text: '" + tema.text() + "',"
				+ " farciment: '" + tema.farciment() + "'}};\n"
				+ "window.__heliumIdeEnviar = function (missatge) { " + codiEnviar + " };\n"
				+ "</script>\n"
				+ estils
				+ "<script type=\"module\" src=\"" + BASE + entrada.get("file").getAsString() + "\"></script>\n"
				+ "</head>\n<body style=\"margin: 0; background: " + tema.fons() + "\">\n<div id=\"root\"></div>\n</body>\n</html>\n";
	}

	static String mimeType(String ruta) {
		int punt = ruta.lastIndexOf('.');
		String extensio = punt >= 0 ? ruta.substring(punt + 1).toLowerCase(Locale.ROOT) : "";
		return MIME.getOrDefault(extensio, "application/octet-stream");
	}

	private static byte[] llegir(String recurs) {
		try (InputStream in = RecursosEditor.class.getClassLoader().getResourceAsStream(recurs)) {
			return in != null ? in.readAllBytes() : null;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
