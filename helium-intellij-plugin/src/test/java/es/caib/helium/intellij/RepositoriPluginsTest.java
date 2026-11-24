package es.caib.helium.intellij;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.intellij.ide.plugins.newui.PluginUiModel;
import com.intellij.ide.plugins.RepositoryHelper;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * El repositori de plugins propi (dist/, generat per ./gradlew distribuir) es pot llegir amb el codi d'IntelliJ que
 * fa servir per als repositoris configurats a Manage Plugin Repositories (el mateix que assigna l'origen dels plugins
 * instal·lats), i apunta al ZIP que hi ha a dist/.
 */
public class RepositoriPluginsTest extends BasePlatformTestCase {

	private static final Path DIST = Path.of("dist");

	public void testIntelliJLlegeixElRepositoriIElZipHiEs() throws Exception {
		Path updatePlugins = DIST.resolve("updatePlugins.xml");
		assertTrue("Falta " + updatePlugins.toAbsolutePath() + ": ./gradlew distribuir", Files.exists(updatePlugins));

		List<PluginUiModel> plugins = RepositoryHelper.loadPluginModels(updatePlugins.toUri().toString(), null, null);

		assertEquals(1, plugins.size());
		PluginUiModel plugin = plugins.get(0);
		assertEquals("es.caib.helium.bpmn", plugin.getPluginId().getIdString());
		assertEquals("263", plugin.getSinceBuild());
		String url = plugin.getDownloadUrl();
		assertTrue(url, url.startsWith("https://raw.githubusercontent.com/GovernIB/helium/helium-4.0/helium-intellij-plugin/dist/"));
		// El ZIP publicat és a dist/ i és el de la versió anunciada
		String zip = url.substring(url.lastIndexOf('/') + 1);
		assertEquals("helium-intellij-plugin-" + plugin.getVersion() + ".zip", zip);
		assertTrue(zip, Files.exists(DIST.resolve(zip)));
	}

}
