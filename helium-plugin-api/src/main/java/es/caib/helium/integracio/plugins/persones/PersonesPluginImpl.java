package es.caib.helium.integracio.plugins.persones;

import es.caib.helium.client.integracio.persones.PersonaClientService;
import es.caib.helium.client.integracio.persones.model.Persona;
import es.caib.helium.integracio.plugins.persones.DadesPersona.Sexe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//TODO: implementar un plugin de dades de persones per keycloak
public class PersonesPluginImpl implements PersonesPlugin {

    @Autowired
    private PersonaClientService personaClientService;

    @Override
    //public List<DadesPersona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException {
    public List<Persona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException {
        return personaClientService.getPersones(text, entornId);
    }

    @Override
    public Persona findAmbCodi(String codi, Long entornId) throws PersonesPluginException {

        return personaClientService.getPersonaByCodi(codi, entornId);
    public DadesPersona findAmbCodi(String codi) throws PersonesPluginException {
        return this.getHeladmin();
    }


    private DadesPersona getHeladmin() {
    	DadesPersona heladmin = new DadesPersona("heladmin", "Hel", "Admin", "proves_limit@limit.es", Sexe.SEXE_DONA);
		return heladmin;
	}

    @Override
    public List<Persona> findAll(Long entornId) throws PersonesPluginException {

        return personaClientService.findAll(entornId);
	@Override
    public List<DadesPersona> findAll() throws PersonesPluginException {
        return null;
    }

    @Override
    public List<String> findRolsAmbCodi(String codi, Long entornId) throws PersonesPluginException {

        return personaClientService.getPersonaRolsByCodi(codi, entornId);
    }
}
