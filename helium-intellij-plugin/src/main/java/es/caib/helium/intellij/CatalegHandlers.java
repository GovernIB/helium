package es.caib.helium.intellij;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.concurrency.AppExecutorUtil;

import org.jetbrains.concurrency.CancellablePromise;

/**
 * Catàleg de handlers que l'editor ofereix per a les service tasks, amb el mateix format que el que retorna Helium
 * ({@code HandlerDto}).
 *
 * <ul>
 *   <li><b>Predefinits:</b> el {@code handlersPredefinits.json} de {@code helium-disseny} del projecte (així coincideix
 *   amb la versió de Helium que fa servir el projecte). Si el projecte no en té, el que porta el plugin.</li>
 *   <li><b>Propis:</b> les classes del projecte que implementen {@code HeliumBpmnHandler}, amb els seus camps com a
 *   paràmetres (igual que Helium fa amb els handlers pujats com a recursos).</li>
 * </ul>
 *
 * <p>S'ha de cridar dins una read action i amb els índexs disponibles (smart mode).</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
final class CatalegHandlers {

	static final String INTERFICIE_HANDLER = "es.caib.helium.bpmn.handler.HeliumBpmnHandler";
	static final String FITXER_CATALEG = "handlersPredefinits.json";
	private static final String CATALEG_PLUGIN = "helium/" + FITXER_CATALEG;

	private static final Logger LOG = Logger.getInstance(CatalegHandlers.class);

	private CatalegHandlers() {
	}

	/**
	 * Calcula el catàleg en segon pla quan els índexs estan disponibles i el lliura al fil de la UI (JSON).
	 *
	 * @param pare  el càlcul es cancel·la quan es tanca.
	 * @param clau  identifica el càlcul: un de nou amb la mateixa clau substitueix el pendent. Ha de ser un objecte
	 *              propi (la plataforma no admet un FileEditor, Project, Document... com a clau).
	 */
	static CancellablePromise<String> calcular(Project project, Disposable pare, Object clau, Consumer<String> enAcabar) {
		return ReadAction.nonBlocking(() -> handlers(project).toString())
				.inSmartMode(project)
				.expireWith(pare)
				.coalesceBy(clau)
				.finishOnUiThread(ModalityState.nonModal(), enAcabar)
				.submit(AppExecutorUtil.getAppExecutorService());
	}

	static JsonArray handlers(Project project) {
		JsonArray handlers = predefinits(project);
		Set<String> classes = new HashSet<>();
		for (JsonElement handler : handlers) {
			classes.add(handler.getAsJsonObject().get("classe").getAsString());
		}
		for (JsonObject propi : propis(project)) {
			// Si el projecte és el mateix Helium, les implementacions dels predefinits també hi són
			if (!classes.contains(propi.get("classe").getAsString())) {
				handlers.add(propi);
			}
		}
		return handlers;
	}

	static JsonArray predefinits(Project project) {
		for (VirtualFile fitxer : FilenameIndex.getVirtualFilesByName(FITXER_CATALEG, GlobalSearchScope.allScope(project))) {
			try {
				JsonArray cataleg = llegirCataleg(new String(fitxer.contentsToByteArray(), StandardCharsets.UTF_8));
				if (!cataleg.isEmpty()) {
					return cataleg;
				}
			} catch (IOException | JsonParseException | IllegalStateException ex) {
				LOG.warn("No s'ha pogut llegir el catàleg de handlers " + fitxer.getPath(), ex);
			}
		}
		return catalegPlugin();
	}

	static JsonArray catalegPlugin() {
		try (InputStream in = CatalegHandlers.class.getClassLoader().getResourceAsStream(CATALEG_PLUGIN)) {
			return in != null ? llegirCataleg(new String(in.readAllBytes(), StandardCharsets.UTF_8)) : new JsonArray();
		} catch (IOException | JsonParseException | IllegalStateException ex) {
			LOG.warn("No s'ha pogut llegir el catàleg de handlers del plugin", ex);
			return new JsonArray();
		}
	}

	static JsonArray llegirCataleg(String json) {
		return JsonParser.parseString(json).getAsJsonArray();
	}

	static List<JsonObject> propis(Project project) {
		PsiClass interficie = JavaPsiFacade.getInstance(project)
				.findClass(INTERFICIE_HANDLER, GlobalSearchScope.allScope(project));
		if (interficie == null) {
			return List.of();
		}
		return ClassInheritorsSearch.search(interficie, GlobalSearchScope.projectScope(project), true)
				.findAll()
				.stream()
				.filter(CatalegHandlers::esInstanciable)
				.sorted(Comparator.comparing(PsiClass::getQualifiedName))
				.map(CatalegHandlers::handlerPropi)
				.toList();
	}

	private static boolean esInstanciable(PsiClass classe) {
		return classe.getQualifiedName() != null
				&& !classe.isInterface()
				&& !classe.isEnum()
				&& !classe.isAnnotationType()
				&& !classe.hasModifierProperty(PsiModifier.ABSTRACT);
	}

	private static JsonObject handlerPropi(PsiClass classe) {
		JsonObject handler = new JsonObject();
		handler.addProperty("classe", classe.getQualifiedName());
		handler.addProperty("nom", classe.getName());
		JsonArray parametres = new JsonArray();
		for (PsiField camp : classe.getFields()) {
			if (camp.hasModifierProperty(PsiModifier.STATIC)) {
				continue;
			}
			JsonObject parametre = new JsonObject();
			parametre.addProperty("codi", camp.getName());
			parametre.addProperty("nom", camp.getName());
			parametre.addProperty("obligatori", false);
			parametre.add("param", JsonNull.INSTANCE);
			parametre.add("paramDesc", JsonNull.INSTANCE);
			parametres.add(parametre);
		}
		handler.add("parametres", parametres);
		return handler;
	}

}
