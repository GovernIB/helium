package es.caib.helium.integracio.plugins.persones;

import es.caib.helium.client.integracio.persones.PersonaClientService;
import es.caib.helium.client.integracio.persones.model.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
    }

    @Override
    public List<Persona> findAll(Long entornId) throws PersonesPluginException {

        return personaClientService.findAll(entornId);
    }

    @Override
    public List<String> findRolsAmbCodi(String codi, Long entornId) throws PersonesPluginException {

        return personaClientService.getPersonaRolsByCodi(codi, entornId);
    }
}
