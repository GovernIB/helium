package net.conselldemallorca.helium.test;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import net.conselldemallorca.helium.helper.PropietatsHelper;

/** Mètode base amb mètodes comuns per a tots els tests.
 */
public class BaseTest {

	public static PropietatsHelper propietats;
	
	protected static enum TipusVar {
		STRING			("STRING"),
		INTEGER			("INTEGER"),
		FLOAT			("FLOAT"),
		BOOLEAN			("BOOLEAN"),
		TEXTAREA		("TEXTAREA"),
		DATE			("DATE"),
		PRICE			("PRICE"),
		TERMINI			("TERMINI"),
		SEL_ENUM		("SELECCIO"),
		SEL_DOMINI		("SELECCIO"),
		SEL_INTERN		("SELECCIO"),
		SEL_CONSULTA	("SELECCIO"),
		SUG_ENUM		("SUGGEST"),
		SUG_DOMINI		("SUGGEST"),
		SUG_INTERN		("SUGGEST"),
		SUG_CONSULTA	("SUGGEST"),
		ACCIO			("ACCIO"),
		REGISTRE		("REGISTRE");
		
		private final  String label;
		private final String id;
		
		TipusVar (String label) {
			this.label = label;
			this.id = this.name();
			
		}
	 
		public String getLabel() {
			return this.label;
		}
		public String getId() {
			return id;
		}
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Creem la classe helper per llegir les propietats.
		if (propietats == null)
			propietats = new PropietatsHelper();
		
		// Carreguem el fitxer de propietats
		Properties properties = new Properties();
		properties.load(ClassLoader.getSystemResourceAsStream("test.properties"));

		// Carreguem les propietats específiques de l'entorn
		String activeProfile = properties.getProperty("entorn", "test");
        if (activeProfile != null && !"".equals(activeProfile)) {
        	String propertiesFilename = "test-" + activeProfile + ".properties";
    		properties.load(ClassLoader.getSystemResourceAsStream(propertiesFilename));	
        }
		propietats.setProperties(properties);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
}