package es.caib.helium.client.integracio.persones;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.persones.model.Persona;

@Service
public interface PersonaClientService {

	public List<Persona> getPersones(String textSearch, Long entornId);
	
	public Persona getPersonaByCodi(String codi, Long entornId);
	
	public List<String> getPersonaRolsByCodi(String codi, Long entornId);
}
