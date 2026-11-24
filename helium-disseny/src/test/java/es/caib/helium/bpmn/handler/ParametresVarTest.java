package es.caib.helium.bpmn.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;

import es.caib.helium.bpmn.api.HeliumApi;

class ParametresVarTest {

	/** Handler amb un paràmetre d'entrada (motiu) i un de sortida (varResultat). */
	public static class HandlerAmbSortida implements HeliumBpmnHandler {
		private String motiu;
		private String varResultat;
		public void setMotiu(String motiu) { this.motiu = motiu; }
		public void setVarResultat(String varResultat) { this.varResultat = varResultat; }
		@Override
		public void execute(HeliumApi heliumApi) {}
	}

	@Test
	void unVarSensePropiaEsSubstituitPelParametreSenseVar() {
		assertEquals("motiu", ParametresVar.substitut(HandlerAmbSortida.class, "varMotiu"));
	}

	@Test
	void elsVarQueElHandlerTeNoSonObsolets() {
		assertNull(ParametresVar.substitut(HandlerAmbSortida.class, "varResultat"));
		assertNull(ParametresVar.substitut(HandlerAmbSortida.class, "motiu"));
		assertNull(ParametresVar.substitut(HandlerAmbSortida.class, "varInexistent"));
	}

	@Test
	void migraVarAExpressioIConservaElLiteralComAValorPerDefecte() {
		HandlerDefinicio definicio = new HandlerDefinicio("x", List.of(
			new HandlerDefinicio.Parametre("motiu", "Motiu fix", null),
			new HandlerDefinicio.Parametre("varMotiu", "motiuDada", null),
			new HandlerDefinicio.Parametre("varResultat", "resultat", null)));
		HandlerDefinicio migrada = ParametresVar.migrar(definicio, HandlerAmbSortida.class);
		assertEquals(2, migrada.getParametres().size());
		HandlerDefinicio.Parametre motiu = migrada.getParametres().get(0);
		assertEquals("motiu", motiu.getNom());
		assertEquals("${motiuDada}", motiu.getExpressio());
		assertEquals("Motiu fix", motiu.getValor());
		assertEquals("varResultat", migrada.getParametres().get(1).getNom());
		assertEquals("resultat", migrada.getParametres().get(1).getValor());
	}

	@Test
	void unVarBuitSElimina() {
		HandlerDefinicio definicio = new HandlerDefinicio("x", List.of(
			new HandlerDefinicio.Parametre("motiu", "Motiu fix", null),
			new HandlerDefinicio.Parametre("varMotiu", "", null)));
		HandlerDefinicio migrada = ParametresVar.migrar(definicio, HandlerAmbSortida.class);
		assertEquals(1, migrada.getParametres().size());
		assertEquals("Motiu fix", migrada.getParametres().get(0).getValor());
		assertNull(migrada.getParametres().get(0).getExpressio());
	}

	@Test
	void senseVarObsoletsRetornaLaMateixaDefinicio() {
		HandlerDefinicio definicio = new HandlerDefinicio("x", List.of(
			new HandlerDefinicio.Parametre("motiu", null, "${a}")));
		assertSame(definicio, ParametresVar.migrar(definicio, HandlerAmbSortida.class));
	}

	@Test
	void referenciesADades() {
		assertEquals("${codi}", ParametresVar.expressioDada(" codi "));
		assertNull(ParametresVar.expressioDada("no vàlid"));
		assertEquals("codi", ParametresVar.codiDada("${ codi }"));
		assertNull(ParametresVar.codiDada("${a + b}"));
		assertNull(ParametresVar.codiDada("Text ${a}"));
	}

	@Test
	void unTextEsExpressioSiConteDollarOCoixinet() {
		assertEquals("${a}", HandlerDefinicio.Parametre.deText("p", "${a}").getExpressio());
		assertEquals("Hola #{a}", HandlerDefinicio.Parametre.deText("p", "Hola #{a}").getExpressio());
		assertEquals("literal", HandlerDefinicio.Parametre.deText("p", "literal").getValor());
	}

}
