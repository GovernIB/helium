package net.conselldemallorca.helium.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	
	/** Retorna el path cap al truststore.jks. Primer mira si existeix, si no el guarda des del
	 * classpath cap al directori temporal.
	 * 
	 * @return
	 */
	protected String getTrustStoreFilePath() {
		
		// Ruta destí a la carpeta selenium del directori temporal
		String folderPath = System.getProperty("java.io.tmpdir") + "selenium";
		String filePath = folderPath + File.separator + "truststore.jks";
		// Mira si existeix, si no el crea
		File file = new File(filePath);
		if (!file.exists()) {
			// Mira si crear el directori
			File folder = new File(folderPath);
			if (!folder.exists())
				folder.mkdirs();
			// Copia el contingut de l'arxiu del .jar al temporal
			InputStream stream = null;
	        OutputStream resStreamOut = null;
	        try {
	    		String resourceName = "/net/conselldemallorca/helium/test/certificates/truststore.jks";
	            stream = this.getClass().getResourceAsStream(resourceName);
	            if(stream == null) {
	            	fail("No es pot llegir el fitxer de proves del .jar " + resourceName);
	            }
	            int readBytes;
	            byte[] buffer = new byte[4096];
	            resStreamOut = new FileOutputStream(filePath);
	            while ((readBytes = stream.read(buffer)) > 0) {
	                resStreamOut.write(buffer, 0, readBytes);
	            }
	        } catch (Exception e) {
	            fail("Error creant el fitxer truststore.jdk de proves al directori temporal: " + e.getMessage());
	        } finally {
	        	try {
	        		if (stream != null)
	        			stream.close();
	        		if (resStreamOut != null)
	        			resStreamOut.close();
	        	} catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        }     
	    }
		return filePath;		
	}	
}