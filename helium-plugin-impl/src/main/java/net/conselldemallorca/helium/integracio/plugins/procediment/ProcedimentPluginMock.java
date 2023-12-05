package net.conselldemallorca.helium.integracio.plugins.procediment;


import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;


/**
 * Implementació del plugin de consulta de procediments emprant MOCK.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginMock implements ProcedimentPlugin {

	@Override
	public List<Procediment> findAmbCodiDir3(
			String codiDir3) throws SistemaExternException {
		List<Procediment> response = new ArrayList<Procediment>();
		Procediment p = new Procediment();
		p.setCodi("1234");
		p.setCodiSia("12345");
		p.setNom("Procediment Mock");
		response.add(p);
		return response;
	}

	@Override
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {
		UnitatAdministrativa ua = new UnitatAdministrativa();
		ua.setCodi(codi);
		ua.setCodiDir3("123456789");
		ua.setNom("Unitat Administrativa Mock");
		ua.setPareCodi(null);
		return ua;
	}
}
