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
		p.setCodigo("1324");
		p.setCodigoSIA("1315");
		p.setNombre("Procediment Mock");
		response.add(p);
		return response;
	}
}
