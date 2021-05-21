package es.caib.helium.integracio.service.persones;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaServiceException;

@Service
//@Primary
public class PersonaServiceJdbcImpl implements PersonaService {

	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private JdbcTemplate jdbcSeycon;
	
	@Autowired
	private Environment env;

	@Override
	public List<Persona> getPersones(String textSearch) throws PersonaServiceException {
		
		try {
			List<Persona> resultat = jdbcSeycon.query(
					env.getProperty("es.caib.helium.integracio.persones.jdbc.filter.name"), new PersonaRowMapper(env),
					textSearch);
			return resultat.size() > 0 ? resultat : new ArrayList<>();
			
		} catch (Exception ex) {
			throw new PersonaServiceException("No s'ha pogut trobar cap persona", ex);
		}
	}

	@Override
	public Persona getPersonaByCodi(String codi) throws PersonaServiceException {
		
		try {
			var foo = env.getProperty("es.caib.helium.integracio.persones.jdbc.filter.code");
			var resultat = jdbcSeycon.query(env.getProperty("es.caib.helium.integracio.persones.jdbc.filter.code"), new PersonaRowMapper(env), codi);
			if (resultat == null || resultat.isEmpty()) {
				return null;
			}
			
			if (resultat.size() > 1) {
				throw new PersonaServiceException("Existeix més d'una persona amb codi: " + codi + " nPersones: " + resultat.size());
			}
			return (Persona) resultat.get(0);
		
		} catch (Exception ex) {
			throw new PersonaServiceException("No s'ha pogut trobar cap persona", ex);
		}
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi) {
		// TODO No hi ha cap SELECT definida a helium.properties per aquest mètode
		return new ArrayList<>();
	}
}
