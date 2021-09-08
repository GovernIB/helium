package es.caib.helium.integracio.service.persones;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PersonaService {

	List<Persona> getPersones(String textSearch, Long entornId) throws PersonaException;
	Persona getPersonaByCodi(String codi, Long entornId) throws PersonaException;
	List<String> getPersonaRolsByCodi(String codi, Long entornId) throws PersonaException;
}
