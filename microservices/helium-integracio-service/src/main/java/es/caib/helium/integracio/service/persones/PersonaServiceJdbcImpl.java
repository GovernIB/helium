package es.caib.helium.integracio.service.persones;


import com.netflix.servo.util.Strings;
import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PersonaServiceJdbcImpl implements PersonaService {

	@Autowired
	private JdbcTemplate jdbcSeycon;
	@Autowired
	private Environment env;
	@Autowired
	private MonitorIntegracionsService monitor;

	@Override
	public List<Persona> getPersones(String textSearch, Long entornId) throws PersonaException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("textSearch", textSearch));
		var descripcio = "Consulta d'usuaris amb like ";
		var t0 = System.currentTimeMillis();
		try {
			List<Persona> resultat = jdbcSeycon.query(
					env.getProperty("es.caib.helium.integracio.persones.jdbc.filter.name"), new PersonaRowMapper(env),
					textSearch);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PERSONA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			log.debug("Consulta ok d'usuaris amb like " + textSearch);
			return resultat.size() > 0 ? resultat : new ArrayList<>();
			
		} catch (Exception ex) {
			
			var error = "No s'ha pogut trobar cap usuari amb el filtre " + textSearch;
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PersonaException(error, ex);
		}
	}

	@Override
	public Persona getPersonaByCodi(String codi, Long entornId) throws PersonaException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("codi", codi));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta d'usuari amb codi " + codi;
		try {
			var resultat = jdbcSeycon.query(env.getProperty("es.caib.helium.integracio.persones.jdbc.filter.code"), new PersonaRowMapper(env), codi);
			if (resultat == null || resultat.isEmpty()) {
				return null;
			}
			
			if (resultat.size() > 1) {
				throw new PersonaException("Existeix més d'un usuari amb codi: " + codi + " nPersones: " + resultat.size());
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PERSONA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Consulta ok d'usuaris amb codi" + codi);
			return (Persona) resultat.get(0);
		
		} catch (Exception ex) {
			
			var error = "Consulta d'usuaris amb error pel codi " + codi;
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PersonaException(error, ex);
		}
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi, Long entornId) throws PersonaException {

		List<Parametre> params = new ArrayList<>();
		params.add(new Parametre("codi", codi));
		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta de rols de l'usuari amb codi " + codi;
		try {
			var query = env.getProperty("es.caib.helium.iwntegracio.persones.jdbc.filter.roles");
			if (Strings.isNullOrEmpty(query)) {
				return new ArrayList<>();
			}
			
			Map<String, Object> parametres = new HashMap<String, Object>();
			parametres.put("codi", codi);
			
			var jndi = env.getProperty("es.caib.helium.integracio.persones.jdbc.jndi.parameter");
			var initContext = new InitialContext();
			var ds = (DataSource)initContext.lookup(jndi);
			var namedJdbcTemplate = new NamedParameterJdbcTemplate(ds);
			var parameterSource = new MapSqlParameterSource(parametres) {
				public boolean hasValue(String paramName) {
					return true;
				} // TODO MS: S'HAURIA DE FER COMPARACIO AMB NUULL O EMPTY?
			};
			
			var rols = (List<String>) namedJdbcTemplate.query(
					query,
					parameterSource,
					new RowMapper() {
						public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
							return rs.getString(1);
						}
					});
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PERSONA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(params)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			log.debug("Consulta dels rols de les persones ok pel codi " + codi);
			return rols;
			
		} catch (Exception ex) {
			var error = "Consulta de rols de les persones amb error pel codi " + codi;
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(params)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new PersonaException(error, ex);
		}
	}
}
