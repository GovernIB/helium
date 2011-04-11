/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.persones;

import java.util.List;

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
			test.findAll();
			//test.findAmbCodi("rmiralles");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findAll() throws Exception {
		PersonesPluginLdap plugin = new PersonesPluginLdap();
		List<Persona> persones = plugin.findAll();
		for (Persona persona: persones) {
			System.out.println(persona.getCodi() + "\t" + persona.getNomSencer() + "\t" + persona.getDni() + "\t" + persona.getEmail());
		}
		System.out.println(">>> núm. persones: " + persones.size());
	}
	
	public void findAmbCodi(String codi) throws Exception {
		PersonesPluginLdap plugin = new PersonesPluginLdap();
		Persona persona = plugin.findAmbCodi(codi);
		System.out.println(persona.getCodi() + "\t" + persona.getNomSencer() + "\t" + persona.getDni() + "\t" + persona.getEmail());
	}

}
