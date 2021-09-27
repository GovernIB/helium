package es.caib.helium.integracio.service.persones;

import com.netflix.servo.util.Strings;
import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
public class PersonaServiceLdapImpl implements PersonaService {

	@Autowired
	@Qualifier("ldapPersones")
	private LdapTemplate ldapPersones;

	@Autowired
	private Environment env; //TODO MS: ACAVAR DE MOURE LES CRIDES A env AL CONFIG I TENIR-HO COM ATRIBUTS
	
	@Autowired
	private MonitorIntegracionsService monitor;
	@Setter
	private String userFilter;
	@Setter
	private String likeFilter;

	@Override
	public Persona getPersonaByCodi(String codi, Long entornId) throws PersonaException {

		var t0 = System.currentTimeMillis();
		var descripcio = "Consulta usuaris amb codi " + codi;
		List<Persona> persones = getPersonesLdap(userFilter.replace("###", codi), entornId, descripcio);
		if (persones.size() > 1) {
			var error = "Hi han " + persones.size() + " persones amb el mateix codi";
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error).build());
			throw new PersonaException(error);
		}
		log.info("Consulta ok d'usuaris amb codi " + codi);
		return persones.size() == 1 ? persones.get(0) : null;
	}

	public List<Persona> getPersones(String textSearch, Long entornId) throws PersonaException {

		var persones =  getPersonesLdap(likeFilter.replace("###", textSearch), entornId, "Consulta d'usuaris amb like " + textSearch);
		log.info("Consulta ok d'usuaris amb like" + textSearch);
		return persones;
	}

	public List<Persona> findAll(Long entornId) throws PersonaException {

		var persones = getPersonesLdap(likeFilter.replace("###", "*"), entornId, "Consulta tots els usuaris");
		log.info("Consulta ok de tots els usuaris");
		return persones;
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi, Long entornId) throws PersonaException {

		List<String> roles = new ArrayList<String>();
		var filter = new String(userFilter).replace("###", codi);
		var roleAtt = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.attribute.role");
		var roleName = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.attribute.name");

		if (roleAtt == null || roleName == null) {
			return roles;
		}
		// TODO MS: falta entendre quina query s'ha de fer no hi han les propietats definides a helium.properties
//		ldapPersones.search(query().filter(filter), new AttributesMapper<String>() {
//			@Override
//			public String mapFromAttributes(Attributes attributes) throws NamingException {
//				// TODO Auto-generated method stub
//				
//				var memberOf = attributes.get(roleAtt);
//				if (memberOf == null) {
//					return null;
//				}
//
//				for (int i = 0; i < memberOf.size(); i++) {
////					Attributes atts = ctx.getAttributes(memberOf.get(i).toString(), new String[] { roleName });
////					Attribute att = atts.get(roleName);
////					if (att.get() != null)
////						roles.add(att.get().toString());
//				}
//			}
//		});
		return roles;
	}

	@Override
	public List<String> getPersonesCodiByRol(String grup, Long entornId) throws PersonaException {
		return new ArrayList<>(); //TODO MS: Pendent de implementar
	}

	@Override
	public List<Persona> getPersonesByCodi(List<String> codis, Long entornId) throws PersonaException {
		return new ArrayList<>(); //TODO MS: Pendent de implementar
	}

	private List<Persona> getPersonesLdap(String filter, Long entornId, String descripcio) throws PersonaException {

		var t0 = System.currentTimeMillis();
		try {
			var persones = ldapPersones.search(query().filter(filter), new AttributesMapper<Persona>() {
				@Override
				public Persona mapFromAttributes(Attributes attributes) throws NamingException {

					var attrs = env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.attributes")
							.split(",");
					var persona = new Persona();
					persona.setCodi(attributes.get(attrs[0]).get().toString());
					persona.setNom(attributes.get(attrs[1]).get().toString());
					persona.setSexe(Utilitats.sexePerNom(persona.getNom()));
					persona.setLlinatges(attrs.length > 2 && !Strings.isNullOrEmpty(attrs[2]) && attributes.get(attrs[2]) != null
									? attributes.get(attrs[2]).get().toString()	: null);
					persona.setDni(attrs.length > 3 && !Strings.isNullOrEmpty(attrs[3]) && attributes.get(attrs[3]) != null
									? attributes.get(attrs[3]).get().toString() : null);
					persona.setEmail(attrs.length > 4 && !Strings.isNullOrEmpty(attrs[4]) && attributes.get(attrs[4]) != null
									? construirEmail(attributes.get(attrs[4]).get().toString())	: null);
					persona.setContrasenya(attrs.length > 5 && !Strings.isNullOrEmpty(attrs[5]) && attributes.get(attrs[5]) != null
									? attributes.get(attrs[5]).get().toString()	: null);
					return persona;
				}
			});
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.PERSONA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			return persones;
		} catch (Exception ex) {
			
			var error = "No s'ha pogut trobar cap usuari ";
			log.error(error);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.PERSONA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			
			throw new PersonaException("No s'ha pogut trobar cap persona.", ex);
		}
	}

	private String construirEmail(String email) {

		var dominiEmail = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.email.domini");
		return Strings.isNullOrEmpty(dominiEmail) ? email : email + "@" + dominiEmail;
	}
}
