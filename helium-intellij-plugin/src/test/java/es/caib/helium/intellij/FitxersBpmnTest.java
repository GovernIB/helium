package es.caib.helium.intellij;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FitxersBpmnTest {

	@Test
	public void acceptaElsFitxersBpmn() {
		assertTrue(FitxersBpmn.esNomBpmn("proces.bpmn"));
		assertTrue(FitxersBpmn.esNomBpmn("Proces.BPMN"));
		assertTrue(FitxersBpmn.esNomBpmn("proces.bpmn20.xml"));
	}

	@Test
	public void rebutjaLaResta() {
		assertFalse(FitxersBpmn.esNomBpmn("proces.xml"));
		assertFalse(FitxersBpmn.esNomBpmn("bpmn"));
		assertFalse(FitxersBpmn.esNomBpmn("proces.bpmn.bak"));
		assertFalse(FitxersBpmn.esNomBpmn(null));
	}

	@Test
	public void nomIIdentificadorDelProcesDUnFitxerNou() {
		assertEquals("Alta d'expedient", FitxersBpmn.nomProces("alta/Alta d'expedient.bpmn"));
		assertEquals("proces", FitxersBpmn.nomProces("proces.BPMN20.xml"));
		assertEquals("proces", FitxersBpmn.nomProces("proces"));
		assertEquals("Alta_d_expedient", FitxersBpmn.idProces("Alta d'expedient"));
		assertEquals("Tramitacio_proces", FitxersBpmn.idProces("Tramitació  procés"));
		assertEquals("Process_2026_alta", FitxersBpmn.idProces("2026 alta"));
		assertEquals("Process_1", FitxersBpmn.idProces("¿?"));
	}

}
