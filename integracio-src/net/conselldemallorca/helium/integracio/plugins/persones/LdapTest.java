/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import net.conselldemallorca.helium.util.GlobalProperties;

import org.springframework.core.io.FileSystemResource;

/**
 * Test plugin LDAP
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class LdapTest {

	public static void main(String[] args) throws Exception {
		try {
			new GlobalProperties(new FileSystemResource("c:/tmp/helium/ldap.properties"));
			LdapTest test = new LdapTest();
			//test.findAll();
			//test.findAmbCodi("josepg");
			test.auth("josepg", "XvEc.13");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findAll() throws Exception {
		PersonesPluginLdap plugin = new PersonesPluginLdap();
		List<Persona> persones = plugin.findAll();
		for (Persona persona: persones) {
			System.out.println(persona.getCodi() + "\t" + persona.getNomSencer() + "\t" + persona.getDni() + "\t" + persona.getEmail() + "\t" + persona.getContrasenya());
		}
		System.out.println(">>> núm. persones: " + persones.size());
	}

	public void findAmbCodi(String codi) throws Exception {
		PersonesPluginLdap plugin = new PersonesPluginLdap();
		Persona persona = plugin.findAmbCodi(codi);
		System.out.println(persona.getCodi() + "\t" + persona.getNomSencer() + "\t" + persona.getDni() + "\t" + persona.getEmail() + "\t" + persona.getContrasenya());
	}

	public void auth(String usuari, String contrasenya) {
		boolean autenticat = false;
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
				"cn=" + usuari + ",dc=LIMIT_CECOMASA,dc=LOCAL");
		envDC.put(
				Context.SECURITY_CREDENTIALS,
				contrasenya);
		try {
			LdapContext ctx = new InitialLdapContext(envDC, null);
			autenticat = true;
			ctx.close();
		} catch (NamingException ex) {
			ex.printStackTrace();
		}
		if (autenticat)
			System.out.println(">>> SI");
		else
			System.out.println(">>> NO");
	}

}
