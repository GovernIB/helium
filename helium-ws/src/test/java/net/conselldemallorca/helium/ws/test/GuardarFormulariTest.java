/**
 * 
 */
package net.conselldemallorca.helium.ws.test;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.util.ws.WsClientUtils;
import net.conselldemallorca.helium.ws.formulari.GuardarFormulari;
import net.conselldemallorca.helium.ws.formulari.ParellaCodiValor;

/**
 * @author perep
 *
 */
public class GuardarFormulariTest {

	private static final String SERVICE_URL = "http://localhost:8080/helium/ws/FormulariExtern";

	public static void main(String[] args) {
		try {
			new GuardarFormulariTest().testGuardarFormulari();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testGuardarFormulari() {
		List<ParellaCodiValor> valors = new ArrayList<ParellaCodiValor>();
		valors.add(newParellaCodiValor("text", "hola que tal"));
		getClientFormulariExtern().guardar(
				"TIE_1430304435516",
				valors);
	}

	private GuardarFormulari getClientFormulariExtern() {
		return (GuardarFormulari)WsClientUtils.getWsClientProxy(
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
