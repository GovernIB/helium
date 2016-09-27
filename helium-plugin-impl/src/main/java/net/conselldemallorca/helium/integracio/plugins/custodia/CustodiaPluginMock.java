/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.custodia;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.signatura.RespostaValidacioSignatura;

/**
 * Implementació del plugin de custodia documental que guarda
 * les signatures a dins l'aplicació de custòdia de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginMock implements CustodiaPlugin {

	public String addSignature(
			String id,
			String gesdocId,
			String arxiuNom,
			String tipusDocument,
			byte[] signatura) throws CustodiaPluginException {
		
		if ("true".equals(GlobalProperties.getInstance().getProperty("app.custodia.plugin.mock.custodiar")))
			return id;
		else
			throw new CustodiaPluginException("CUSTODIA ERROR MOCK");
		
	}

	public List<byte[]> getSignatures(String id) throws CustodiaPluginException {
		byte[] array1 = new byte[50];
//		byte[] array2 = new byte[50];
		List<byte[]> result = new ArrayList<byte[]>();
		result.add(array1);
//		result.add(array2);
		return result;
	}


	public String getUrlComprovacioSignatura(
			String id) throws CustodiaPluginException {
		return "http://oficina.limit.es/";
	}


	@Override
	public byte[] getSignaturesAmbArxiu(String id) throws CustodiaPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSignatures(String id) throws CustodiaPluginException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean potObtenirInfoSignatures() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidacioImplicita() {
		// TODO Auto-generated method stub
		return false;
	}

}
