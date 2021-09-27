package es.caib.helium.client.integracio.persones;

import es.caib.helium.client.integracio.persones.model.Persona;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonaClient {

	List<Persona> findAll(Long entornId);

	List<Persona> getPersones(String textSearch, Long entornId);

	Persona getPersonaByCodi(String codi, Long entornId);
	
	List<String> getPersonaRolsByCodi(String codi, Long entornId);

	List<String> getPersonesCodiByRol(String rol, Long entornId);

	List<Persona> getPersonesByCodi(List<String> codis, Long entornId);
}
