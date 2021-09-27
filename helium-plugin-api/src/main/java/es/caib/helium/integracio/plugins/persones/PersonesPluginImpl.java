package es.caib.helium.integracio.plugins.persones;

import es.caib.helium.client.integracio.persones.PersonaClient;
import es.caib.helium.client.integracio.persones.enums.Sexe;
import es.caib.helium.client.integracio.persones.model.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
//TODO: implementar un plugin de dades de persones per keycloak
public class PersonesPluginImpl implements PersonesPlugin {

    @Autowired
    private PersonaClient personaClient;

    @Override
    public List<Persona> findLikeNomSencer(String text, Long entornId) throws PersonesPluginException {
        return findAll(entornId).stream().filter(p -> p.getCodi().toUpperCase().contains(text.toUpperCase()) ||
                p.getNomSencer().toUpperCase().contains(text.toUpperCase())).collect(Collectors.toList());
//        return personaClientService.getPersones(text, entornId);
    }

    @Override
    public Persona findAmbCodi(String codi, Long entornId) throws PersonesPluginException {
        if ("heluser".equals(codi))
            return this.getHeluser();
        else if ("heladmin".equals(codi))
        	return this.getHeladmin();
        else 
        	return new Persona(codi, codi, null, codi+"@limit.es", codi.length() % 2 == 0 ? Sexe.SEXE_DONA : Sexe.SEXE_HOME);
    }


    private Persona getHeladmin() {
    	Persona heladmin = new Persona("heladmin", "Hel", "Admin", "proves_limit@limit.es", Sexe.SEXE_DONA);
		return heladmin;
	}

    private Persona getHeluser() {
        Persona heluser = new Persona("heluser", "Hel", "User", "proves_limit@limit.es", Sexe.SEXE_HOME);
        return heluser;
    }

	@Override
    public List<Persona> findAll(Long entornId) throws PersonesPluginException {
        return List.of(getHeladmin(), getHeluser());
    }

    @Override
    public List<String> findRolsAmbCodi(String codi, Long entornId) throws PersonesPluginException {
        return null;
    }

    @Override
    public List<Persona> findPersonesAmbGrup(String grup) {
        return List.of(getHeladmin(), getHeluser());
    }
}
