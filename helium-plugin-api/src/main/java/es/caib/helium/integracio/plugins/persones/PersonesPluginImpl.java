package es.caib.helium.integracio.plugins.persones;

import es.caib.helium.client.integracio.persones.PersonaClientService;
import es.caib.helium.client.integracio.persones.model.Persona;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PersonesPluginImpl implements PersonesPlugin {

    @Autowired
    private PersonaClientService personaClientService;

    @Override
    //public List<DadesPersona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException {
    public List<Persona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException {
        return personaClientService.getPersones(text, entornId);
    }

    @Override
    public DadesPersona findAmbCodi(String codi) throws PersonesPluginException {
        return null;
    }

    @Override
    public List<DadesPersona> findAll() throws PersonesPluginException {
        return null;
    }

    @Override
    public List<String> findRolsAmbCodi(String codi) throws PersonesPluginException {
        return null;
    }
}
