package es.caib.helium.integracio.service.persones;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaServiceException;

@Service
public interface PersonaService {

	public List<Persona> getPersones(String textSearch) throws PersonaServiceException;
	public Persona getPersonaByCodi(String codi) throws PersonaServiceException;
	public List<String> getPersonaRolsByCodi(String codi);
}
