package es.caib.helium.logic.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;
import es.caib.helium.bpmn.handler.HandlerDefinicio;
import es.caib.helium.bpmn.handler.ParametresVar;

/** Paràmetres de les accions de tipus handler: literals, referències a dades i antics varXXX. */
class ExpedientHelperAccioParametresTest {

	private final HeliumApi heliumApi = mock(HeliumApi.class);

	private static HandlerDefinicio definicio(Map<String, String> dades) {
		return new HandlerDefinicio("x", dades.entrySet().stream()
			.map(e -> HandlerDefinicio.Parametre.deText(e.getKey(), e.getValue()))
			.collect(java.util.stream.Collectors.toList()));
	}

	@Test
	void resolLiteralsIReferenciesADades() {
		Date data = new Date();
		when(heliumApi.getVariable("dataInici")).thenReturn(data);
		when(heliumApi.getVariable("numero")).thenReturn("1/2026");
		Map<String, String> dades = new LinkedHashMap<>();
		dades.put("literal", "text");
		dades.put("data", "${dataInici}");
		dades.put("titol", "Expedient ${numero}");
		dades.put("buida", "${senseValor}");
		Map<String, Object> valors = ExpedientHelper.resoldreParametresAccio(definicio(dades), heliumApi);
		assertEquals("text", valors.get("literal"));
		assertEquals(data, valors.get("data"));
		assertEquals("Expedient 1/2026", valors.get("titol"));
		assertNull(valors.get("buida"));
	}

	@Test
	void elsAnticsVarDeLesAccionsLlegeixenLaDadaAmbElLiteralPerDefecte() {
		Map<String, String> dades = new LinkedHashMap<>();
		dades.put("motiu", "Motiu fix");
		dades.put("varMotiu", "motiuDada");
		HandlerDefinicio migrada = ParametresVar.migrar(definicio(dades), es.caib.helium.logic.bpmn.ExpedientAturarHandler.class);
		assertEquals(List.of("motiu"), migrada.getParametres().stream().map(HandlerDefinicio.Parametre::getNom).collect(java.util.stream.Collectors.toList()));
		assertEquals("Motiu fix", ExpedientHelper.resoldreParametresAccio(migrada, heliumApi).get("motiu"));
		when(heliumApi.getVariable("motiuDada")).thenReturn("Motiu de la dada");
		assertEquals("Motiu de la dada", ExpedientHelper.resoldreParametresAccio(migrada, heliumApi).get("motiu"));
	}

	@Test
	void lesExpressionsQueNoSonReferenciesADadesNoEsSuportenALesAccions() {
		HeliumHandlerException ex = assertThrows(
			HeliumHandlerException.class,
			() -> ExpedientHelper.resoldreParametresAccio(definicio(Map.of("p", "${a + b}")), heliumApi));
		assertTrue(ex.getMessage().contains("${codiDada}"), ex.getMessage());
	}

}
