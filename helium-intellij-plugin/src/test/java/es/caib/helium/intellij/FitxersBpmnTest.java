package es.caib.helium.intellij;

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

}
