package es.caib.helium.integracio.service.persones;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaException;

@Service
public interface PersonaService {

	public List<Persona> getPersones(String textSearch, Long entornId) throws PersonaException;
	public Persona getPersonaByCodi(String codi, Long entornId) throws PersonaException;
	public List<String> getPersonaRolsByCodi(String codi, Long entornId) throws PersonaException;
}
