package net.conselldemallorca.helium.wsintegraciones.formulari;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.ws.formulari.GuardarFormulari;
import net.conselldemallorca.helium.ws.formulari.ParellaCodiValor;

/**
 * Test per al servei de formularis externs.
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class FormextClient {

	private static final String SERVICE_URL = "http://localhost:8080/helium/ws/FormulariExtern";

	public static void main(String[] args) {
		try {
			new FormextClient().testGuardarFormulari();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testGuardarFormulari() {
		List<ParellaCodiValor> valors = new ArrayList<ParellaCodiValor>();
		valors.add(newParellaCodiValor("import", new BigDecimal("100")));
		getClientFormulariExtern().guardar(
				"7326446",
				valors);
	}

	private GuardarFormulari getClientFormulariExtern() {
		return (GuardarFormulari)net.conselldemallorca.helium.core.util.ws.WsClientUtils.getWsClientProxy(
				GuardarFormulari.class,
				SERVICE_URL,
				null,
				null,
				"NONE",
				false,
				true,
				true,
				null);
	}
	private ParellaCodiValor newParellaCodiValor(
			String codi,
			Object valor) {
		ParellaCodiValor parella = new ParellaCodiValor();
		parella.setCodi(codi);
		parella.setValor(valor);
		return parella;
	}

}
