package es.caib.helium.bpmn.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;
import es.caib.helium.bpmn.exception.HeliumHandlerException;

/**
 * Tests de la injecció de paràmetres dels handlers.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HandlerParametresTest {

	@Test
	void injectaParametresString() {
		HandlerProva handler = new HandlerProva();
		Map<String, Object> valors = new LinkedHashMap<>();
		valors.put("motiu", "Motiu de prova");
		valors.put("varMotiu", "");
		HandlerParametres.injectar(handler, valors);
		assertEquals("Motiu de prova", handler.motiu);
		assertEquals("", handler.varMotiu);
	}

	@Test
	void injectaCampSenseSetter() {
		HandlerProva handler = new HandlerProva();
		HandlerParametres.injectar(handler, Map.of("senseSetter", "valor"));
		assertEquals("valor", handler.senseSetter);
	}

	@Test
	void converteixValorsAlTipusDelParametre() throws Exception {
		HandlerProva handler = new HandlerProva();
		Map<String, Object> valors = new HashMap<>();
		valors.put("numero", "42");
		valors.put("actiu", "true");
		valors.put("data", "23/11/2026");
		valors.put("motiu", 7L);
		HandlerParametres.injectar(handler, valors);
		assertEquals(Integer.valueOf(42), handler.numero);
		assertTrue(handler.actiu);
		assertEquals(new SimpleDateFormat("dd/MM/yyyy").parse("23/11/2026"), handler.data);
		assertEquals("7", handler.motiu);
	}

	@Test
	void converteixDatesAStringAmbElFormatDeHelium() throws Exception {
		HandlerProva handler = new HandlerProva();
		Date data = new SimpleDateFormat("dd/MM/yyyy").parse("01/02/2026");
		HandlerParametres.injectar(handler, Map.of("motiu", data));
		assertEquals("01/02/2026", handler.motiu);
	}

	@Test
	void valorsBuitsSonNullOFalsPerALesPrimitives() {
		HandlerProva handler = new HandlerProva();
		handler.numero = 3;
		handler.actiu = true;
		Map<String, Object> valors = new HashMap<>();
		valors.put("numero", "");
		valors.put("actiu", null);
		HandlerParametres.injectar(handler, valors);
		assertNull(handler.numero);
		assertFalse(handler.actiu);
	}

	@Test
	void fallaSiElParametreNoExisteix() {
		HeliumHandlerException ex = assertThrows(
			HeliumHandlerException.class,
			() -> HandlerParametres.injectar(new HandlerProva(), Map.of("inexistent", "x")));
		assertTrue(ex.getMessage().contains("inexistent"));
	}

	@Test
	void fallaSiElValorNoEsPotConvertir() {
		assertThrows(
			HeliumHandlerException.class,
			() -> HandlerParametres.injectar(new HandlerProva(), Map.of("numero", "no-es-un-numero")));
	}

	public static class HandlerProva extends AbstractHeliumActionHandler {
		private String motiu;
		private String varMotiu;
		private Integer numero;
		private boolean actiu;
		private Date data;
		private String senseSetter;

		@Override
		public void execute(HeliumApi heliumApi) {
		}

		public void setMotiu(String motiu) { this.motiu = motiu; }
		public void setVarMotiu(String varMotiu) { this.varMotiu = varMotiu; }
		public void setNumero(Integer numero) { this.numero = numero; }
		public void setActiu(boolean actiu) { this.actiu = actiu; }
		public void setData(Date data) { this.data = data; }
	}

}
