package es.caib.helium.logic.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.bpmn.handler.HandlerParametres;
import es.caib.helium.bpmn.handler.HeliumBpmnHandler;

/**
 * Comprova que el catàleg de handlers predefinits ({@code handlersPredefinits.json}) està sincronitzat amb les
 * implementacions de {@code logic.bpmn} i les interfícies de {@code disseny.handler}: cada entrada té la classe, la
 * classe implementa la interfície i accepta tots els paràmetres del catàleg, i la interfície declara exactament els
 * paràmetres del catàleg.
 */
public class CatalegHandlersPredefinitsTest {

	/**
	 * Handlers del catàleg encara pendents: els de terminis (els desenvolupa un altre company) i el de buidar els logs
	 * (va amb les funcionalitats de retrocés).
	 */
	private static final Set<String> PENDENTS = Set.of(
		"TerminiContinuarHandler",
		"TerminiCancelarHandler",
		"TerminiCalcularDataIniciHandler",
		"TerminiCalcularDataFiHandler",
		"TerminiVariableGuardarHandler",
		"ConfigurarAmbTerminiHandler",
		"ExpedientBuidaLogHandler");

	@Test
	void totsElsHandlersDelCatalegEstanImplementatsISincronitzats() throws Exception {
		List<String> errors = new ArrayList<>();
		for (JsonNode handler : llegirCataleg()) {
			String classe = handler.get("classe").asText();
			String nomSimple = classe.substring(classe.lastIndexOf('.') + 1);
			if (PENDENTS.contains(nomSimple)) {
				continue;
			}
			List<String> parametres = new ArrayList<>();
			handler.get("parametres").forEach(p -> parametres.add(p.get("codi").asText()));

			Class<?> implementacio;
			try {
				implementacio = Class.forName(classe);
			} catch (ClassNotFoundException ex) {
				errors.add(classe + ": no existeix la classe");
				continue;
			}
			if (!HeliumBpmnHandler.class.isAssignableFrom(implementacio) || Modifier.isAbstract(implementacio.getModifiers())) {
				errors.add(classe + ": no és un handler instanciable");
				continue;
			}
			Class<?> interficie;
			try {
				interficie = Class.forName("es.caib.helium.disseny.handler." + nomSimple);
			} catch (ClassNotFoundException ex) {
				errors.add(classe + ": no existeix la interfície de disseny");
				continue;
			}
			if (!interficie.isAssignableFrom(implementacio)) {
				errors.add(classe + ": no implementa " + interficie.getName());
			}
			Set<String> setters = Arrays.stream(interficie.getMethods())
				.map(Method::getName)
				.filter(n -> n.startsWith("set"))
				.map(n -> Character.toLowerCase(n.charAt(3)) + n.substring(4))
				.collect(Collectors.toCollection(TreeSet::new));
			if (!setters.equals(new TreeSet<>(parametres))) {
				errors.add(classe + ": la interfície té " + setters + " i el catàleg " + new TreeSet<>(parametres));
			}
			// Tots els paràmetres del catàleg s'han de poder injectar
			for (String parametre : parametres) {
				if (!HandlerParametres.teParametre(implementacio, parametre)) {
					errors.add(classe + ": la implementació no té el paràmetre " + parametre);
				}
			}
		}
		if (!errors.isEmpty()) {
			fail("Catàleg de handlers no sincronitzat:\n" + String.join("\n", errors));
		}
	}

	@Test
	void elsHandlersPendentsContinuenAlCataleg() throws Exception {
		Set<String> cataleg = new TreeSet<>();
		for (JsonNode handler : llegirCataleg()) {
			String classe = handler.get("classe").asText();
			cataleg.add(classe.substring(classe.lastIndexOf('.') + 1));
		}
		// Si se n'implementa o se'n treu algun, s'ha de treure de PENDENTS
		assertTrue(cataleg.containsAll(PENDENTS), "Pendents que ja no són al catàleg: " + PENDENTS);
		assertFalse(cataleg.isEmpty());
		assertEquals(41, cataleg.size());
	}

	private static JsonNode llegirCataleg() throws Exception {
		try (InputStream in = CatalegHandlersPredefinitsTest.class.getClassLoader().getResourceAsStream("handlersPredefinits.json")) {
			return new ObjectMapper().readTree(in);
		}
	}

}
