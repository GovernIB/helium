/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.persones.DadesPersona.Sexe;

/**
 * Implementació de la interficie PersonesPlugin amb accés a un directori LDAP
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PersonesPluginLdap implements PersonesPlugin {



	public DadesPersona findAmbCodi(String codi) throws PersonesPluginException {
		try {
			String userFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.search.filter.user");
			String filter = new String(userFilter).replace("###", codi);
			List<DadesPersona> persones = findPersonesLdap(filter);
			if (persones.size() > 0)
				return persones.get(0);
			return null;
		} catch (Exception ex) {
			throw new PersonesPluginException("No s'ha pogut trobar la persona", ex);
		}
	}

	public List<DadesPersona> findLikeNomSencer(String text) throws PersonesPluginException {
		try {
			String likeFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.search.filter.like");
			String filter = new String(likeFilter).replace("###", text);
			return findPersonesLdap(filter);
		} catch (Exception ex) {
			throw new PersonesPluginException("No s'ha pogut trobar cap persona", ex);
		}
	}

	public List<DadesPersona> findAll() throws PersonesPluginException {
		try {
			String likeFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.search.filter.like");
			String filter = new String(likeFilter).replace("*###*", "*");
			return findPersonesLdap(filter);
		} catch (Exception ex) {
			throw new PersonesPluginException("No s'ha pogut trobar cap persona", ex);
		}
	}

	public List<String> findRolsAmbCodi(String codi) throws PersonesPluginException {
		List<String> roles = new ArrayList<String>();
		
		String userFilter = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.filter.user");
		String filter = new String(userFilter).replace("###", codi);
		
		String roleAtt = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.attribute.role");
		String roleName = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.attribute.role.name");
		
		if (roleAtt != null && roleName != null) {
			LdapContext ctx = null;
			
			try {
				ctx = getContext();
				SearchControls searchCtls = new SearchControls();
				searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
				NamingEnumeration<SearchResult> answer = ctx.search(
						GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.searchbase"),
						filter,
						searchCtls);
				
				if (answer.hasMoreElements()) {
					SearchResult sr = (SearchResult)answer.next();
					Attribute memberOf = sr.getAttributes().get(roleAtt);
					if (memberOf != null) {
						for (int i = 0; i < memberOf.size(); i++) {
							Attributes atts = ctx.getAttributes(memberOf.get(i).toString(), new String[] {roleName});
							Attribute att = atts.get(roleName);
							if (att.get() != null)
								roles.add(att.get().toString());
						}
					}
				}
				ctx.close();
			} catch (Exception ex) {
				throw new PersonesPluginException("No s'ha pogut trobar cap persona", ex);
			}
		}
		
		return roles;
	}


	private List<DadesPersona> findPersonesLdap(String filter) throws Exception {
		String[] returnedAtts = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.attributes").split(",");
		LdapContext ctx = getContext();
		SearchControls searchCtls = new SearchControls();
		//searchCtls.setReturningAttributes(returnedAtts);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> answer = ctx.search(
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.search.base"),
				filter,
				searchCtls);
		List<DadesPersona> resposta = new ArrayList<DadesPersona>();
		while (answer.hasMoreElements()) {
			SearchResult sr = (SearchResult)answer.next();
			Attributes attrs = sr.getAttributes();
			String codi = (String)attrs.get(returnedAtts[0]).get();
			String nom = (String)attrs.get(returnedAtts[1]).get();
			String llinatges = null;
			if (returnedAtts.length > 2 && attrs.get(returnedAtts[2]) != null)
				llinatges = (String)attrs.get(returnedAtts[2]).get();
			String dni = null;
			if (returnedAtts.length > 3 && attrs.get(returnedAtts[3]) != null)
				dni = (String)attrs.get(returnedAtts[3]).get();
			String email = null;
			if (returnedAtts.length > 4 && attrs.get(returnedAtts[4]) != null)
				email = construirEmail((String)attrs.get(returnedAtts[4]).get());
			String contrasenya = null;
			if (returnedAtts.length > 5 && attrs.get(returnedAtts[5]) != null)
				contrasenya = new String((byte[])attrs.get(returnedAtts[5]).get());
			DadesPersona persona = new DadesPersona(
					codi,
					nom,
					llinatges,
					email,
					sexePerNom(nom));
			persona.setDni(dni);
			persona.setContrasenya(contrasenya);
			resposta.add(persona);
		}
		ctx.close();
		return resposta;
	}
	
	private LdapContext getContext() throws Exception {
		Hashtable<String, String> envDC = new Hashtable<String, String>();
		envDC.put(
				Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		envDC.put(
				Context.PROVIDER_URL,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.url"));
		envDC.put(
				Context.SECURITY_AUTHENTICATION,
				"simple");
		envDC.put(
				Context.SECURITY_PRINCIPAL,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.principal"));
		envDC.put(
				Context.SECURITY_CREDENTIALS,
				GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.credentials"));
		return new InitialLdapContext(envDC, null);
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
		if (norm.toLowerCase().endsWith("a")) {
			if (norm.equalsIgnoreCase("sebastia"))
				return Sexe.SEXE_HOME;
			else
				return Sexe.SEXE_DONA;
		} else {
			return Sexe.SEXE_HOME;
		}
	}

	private String construirEmail(String email) {
		String dominiEmail = GlobalProperties.getInstance().getProperty("app.persones.plugin.ldap.email.domini");
		if (dominiEmail == null)
			return email;
		else
			return email + "@" +  dominiEmail;
	}

}
