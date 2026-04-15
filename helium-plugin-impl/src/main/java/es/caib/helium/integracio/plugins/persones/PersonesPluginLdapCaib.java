package es.caib.helium.integracio.plugins.persones;

import java.util.List;

import es.caib.helium.integracio.plugins.persones.DadesPersona;
import es.caib.helium.integracio.plugins.persones.PersonesPlugin;
import es.caib.helium.integracio.plugins.persones.PersonesPluginException;

public class PersonesPluginLdapCaib implements PersonesPlugin {

	@Override
	public List<DadesPersona> findLikeNomSencer(String text) throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DadesPersona> findLikeCodiOrNomSencer(String text) throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DadesPersona findAmbCodi(String codi) throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DadesPersona> findAll() throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> findRolsAmbCodi(String codi) throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DadesPersona> findAmbGrup(String grupCodi) throws PersonesPluginException {
		// TODO Auto-generated method stub
		return null;
	}

}
