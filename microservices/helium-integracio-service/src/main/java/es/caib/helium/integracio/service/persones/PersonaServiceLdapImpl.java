package es.caib.helium.integracio.service.persones;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import com.netflix.servo.util.Strings;
import com.thoughtworks.xstream.mapper.AttributeMapper;

import es.caib.helium.integracio.domini.persones.Persona;
import es.caib.helium.integracio.excepcions.persones.PersonaServiceException;

// TODO PASSAR LES PROPIETATS AL CONFIG I QUE NOMÉS ES DAMNINI UNA VEGADA
@Service
public class PersonaServiceLdapImpl implements PersonaService {

	@Autowired
	@Qualifier("ldapPersones")
	private LdapTemplate ldapPersones;

	@Autowired
	private Environment env;

	@Override
	public Persona getPersonaByCodi(String codi) throws PersonaServiceException {

		var userFilter = env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.user");
		List<Persona> persones = getPersonesLdap(userFilter.replace("###", codi));
		if (persones.size() > 1) {
			throw new PersonaServiceException("Hi han " + persones.size() + " persones amb el mateix codi");
		}
		return persones.size() == 1 ? persones.get(0) : null;
	}

	public List<Persona> getPersones(String textSearch) throws PersonaServiceException {

		var likeFilter = env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.like");
		return getPersonesLdap(likeFilter.replace("###", textSearch));
	}

	public List<Persona> findAll() throws PersonaServiceException {

		var likeFilter = env.getRequiredProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.like");
		return getPersonesLdap(likeFilter.replace("###", "*"));
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi) {

		List<String> roles = new ArrayList<String>();
		var userFilter = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.search.filter.user");
		var filter = new String(userFilter).replace("###", codi);

		var roleAtt = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.attribute.role");
		var roleName = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.attribute.name");

		if (roleAtt == null || roleName == null) {
			return roles;
		}
		// TODO falta entendre quina query s'ha de fer no hi han les propietats definides a helium.properties
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

	private List<Persona> getPersonesLdap(String filter) throws PersonaServiceException {

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
			return persones;
		} catch (Exception e) {
			throw new PersonaServiceException("No s'ha pogut trobar cap persona.", e);
		}
	}

	private String construirEmail(String email) {

		var dominiEmail = env.getProperty("es.caib.helium.integracio.persones.plugin.ldap.email.domini");
		return Strings.isNullOrEmpty(dominiEmail) ? email : email + "@" + dominiEmail;
	}
}
