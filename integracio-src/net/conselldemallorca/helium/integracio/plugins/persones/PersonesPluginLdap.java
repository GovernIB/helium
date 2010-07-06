/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import net.conselldemallorca.helium.integracio.plugins.persones.Persona.Sexe;
import net.conselldemallorca.helium.model.exception.PersonaPluginException;
import net.conselldemallorca.helium.util.GlobalProperties;

/**
 * Implementació de la interficie PersonesPlugin amb accés a un directori LDAP
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class PersonesPluginLdap implements PersonesPlugin {



	public Persona findAmbCodi(String codi) {
		try {
			String userFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.filter.user");
			String filter = new String(userFilter).replace("###", codi);
			List<Persona> persones = findPersonesLdap(filter);
			if (persones.size() > 0)
				return persones.get(0);
			return null;
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut trobar la persona", ex);
		}
	}

	public List<Persona> findLikeNomSencer(String text) {
		try {
			String likeFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.filter.like");
			String filter = new String(likeFilter).replace("###", text);
			return findPersonesLdap(filter);
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut trobar cap persona", ex);
		}
	}

	public List<Persona> findAll() {
		try {
			String likeFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.filter.like");
			String filter = new String(likeFilter).replace("*###*", "*");
			return findPersonesLdap(filter);
		} catch (Exception ex) {
			throw new PersonaPluginException("No s'ha pogut trobar cap persona", ex);
		}
	}



	@SuppressWarnings("unchecked")
	private List<Persona> findPersonesLdap(String filter) throws Exception {
		Hashtable envDC = new Hashtable();
		envDC.put(
				Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		envDC.put(
				Context.PROVIDER_URL,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.url"));
		envDC.put(
				Context.SECURITY_PRINCIPAL,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.principal"));
		envDC.put(
				Context.SECURITY_CREDENTIALS,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.credentials"));
		LdapContext ctx = new InitialLdapContext(envDC, null);
		String[] returnedAtts = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.attributes").split(",");
		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(returnedAtts);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration answer = ctx.search(
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.searchbase"),
				filter,
				searchCtls);
		List<Persona> resposta = new ArrayList<Persona>();
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult)answer.next();
			Attributes attrs = sr.getAttributes();
			String codi = (String)attrs.get(returnedAtts[0]).get();
			String nom = (String)attrs.get(returnedAtts[1]).get();
			String llinatges = null;
			if (attrs.get(returnedAtts[2]) != null)
				llinatges = (String)attrs.get(returnedAtts[2]).get();
			String email = null;
			if (attrs.get(returnedAtts[4]) != null)
				email = (String)attrs.get(returnedAtts[4]).get();
			Persona persona = new Persona(
					codi,
					nom,
					llinatges,
					email,
					sexePerNom(nom));
			if (attrs.get(returnedAtts[3]) != null)
			persona.setDni((String)attrs.get(returnedAtts[3]).get());
			resposta.add(persona);
		}
		ctx.close();
		return resposta;
	}

	private Sexe sexePerNom(String nom) {
		String[] parts = nom.trim().split(" ");
		String norm = parts[0];
		norm = norm.replaceAll("[àâ]","a");
		norm = norm.replaceAll("[èéêë]","e");
		norm = norm.replaceAll("[ïî]","i");
		norm = norm.replaceAll("Ô","o");
		norm = norm.replaceAll("[ûù]","u");
		norm = norm.replaceAll("[ÀÂ]","A");
		norm = norm.replaceAll("[ÈÉÊË]","E");
		norm = norm.replaceAll("[ÏÎ]","I");
		norm = norm.replaceAll("Ô","O");
		norm = norm.replaceAll("[ÛÙ]","U");
		if (norm.toLowerCase().endsWith("a"))
			return Sexe.SEXE_DONA;
		else
			return Sexe.SEXE_HOME;
	}

}
