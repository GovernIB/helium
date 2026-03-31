package net.conselldemallorca.helium.integracio.plugins.rolsac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import es.caib.helium.integracio.plugins.procediment.Procediment;
import es.caib.helium.integracio.plugins.procediment.ProcedimentPlugin;
import es.caib.helium.integracio.plugins.procediment.Rolsac2ProcedimentPlugin;

public class Rolasc2PluginTest {
	private final String URL = "https://dev.caib.es/rolsac2api/interna/services/v1";
	private final String USERNAME = "*********";
	private final String PASSWORD = "*********";
//	private final String TIMEOUT = null;
	
	private final String CODI_DIR3 = "A04003003";
	
	
	@Before
	public void init() {
		System.setProperty("app.plugins.procediments.rolsac.service.url", URL);
		System.setProperty("app.plugins.procediments.rolsac.service.username", USERNAME);
		System.setProperty("app.plugins.procediments.rolsac.service.password", PASSWORD);
		//System.setProperty("app.plugins.procediments.rolsac.service.timeout", TIMEOUT);
	}
	
	@Test
	public void procediments() {
		ProcedimentPlugin plugin = new Rolsac2ProcedimentPlugin();
		
		try {
			List<Procediment> llista = plugin.findAmbCodiDir3(CODI_DIR3);
			
			Set<String> codis = new HashSet<String>();
			Map<String, Integer> codisCount = new HashMap<String, Integer>();
			for(Procediment p : llista) {
				if(p.getCodi() == null)
					System.out.println(">>> NULL");
				
				int num = 1;
				if(codisCount.containsKey(p.getCodi()))
					num = codisCount.get(p.getCodi()) + 1;
				codisCount.put(p.getCodi(), num);
				
				if(!codis.contains(p.getCodi()))
					codis.add(p.getCodi());
			}
			
			System.out.println("S'han trobat " + llista.size() + " procediments");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void serveis() {
		ProcedimentPlugin plugin = new Rolsac2ProcedimentPlugin();
		
		try {
			List<Procediment> llista = plugin.findServeisAmbCodiDir3(CODI_DIR3);
			
			Set<String> codis = new HashSet<String>();
			Map<String, Integer> codisCount = new HashMap<String, Integer>();
			for(Procediment p : llista) {
				if(p.getCodi() == null)
					System.out.println(">>> NULL");
				
				int num = 1;
				if(codisCount.containsKey(p.getCodi()))
					num = codisCount.get(p.getCodi()) + 1;
				codisCount.put(p.getCodi(), num);
				
				if(!codis.contains(p.getCodi()))
					codis.add(p.getCodi());
			}
			
			System.out.println("S'han trobat " + llista.size() + " serveis");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
