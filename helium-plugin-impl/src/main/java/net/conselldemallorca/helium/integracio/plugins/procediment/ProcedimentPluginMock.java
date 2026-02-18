package net.conselldemallorca.helium.integracio.plugins.procediment;


import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentTipusEnumDto;


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
		p.setTipus(ProcedimentTipusEnumDto.PROCEDIMENT);
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

	@Override
	public List<Procediment> findServeisAmbCodiDir3(String codiDir3) throws SistemaExternException {
		List<Procediment> response = new ArrayList<Procediment>();
		Procediment p = new Procediment();
		p.setCodi("4321");
		p.setCodiSia("4321");
		p.setNom("Servei Mock");
		p.setTipus(ProcedimentTipusEnumDto.SERVEI);
		response.add(p);
		return response;
	}
}
