package net.conselldemallorca.helium.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

@Ignore("Classe base per a la execució dels tests amb Selenium")
public abstract class BaseTest {

	protected static WebDriver driver;
	protected static Selenium selenium;
	protected static Properties properties;
	protected static String browser;
	protected static String baseUrl;
	protected static boolean seycon;
	protected static ScreenshotHelper screenshotHelper;
	protected static Actions actions;

	protected static String entornActual;
	
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
		// Carreguem el fitxer de propietats
		properties = new Properties();
		properties.load(BaseTest.class.getResourceAsStream("test.properties"));
	}

	@Before
	public void setUp() throws Exception {
		// Configurarem el driver depenent del navegador
		browser = properties.getProperty("test.browser");
				
		String driverName = "";
		if ("chrome".equals(browser)) {
			driverName = properties.getProperty("webdriver.chrome.driver");
			assertNotNull("Driver per chrome no configurat al fitxer de properties", driverName);
			System.setProperty("webdriver.chrome.driver", driverName);
			
			DesiredCapabilities capability = DesiredCapabilities.chrome();
			capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			
			driver = new ChromeDriver(capability);
			driverConfig("CR");
		} else if ("iexplorer".equals(browser)) {
			driverName = properties.getProperty("webdriver.ie.driver");
			assertNotNull("Driver per iexplorer no configurat al fitxer de properties", driverName);
			System.setProperty("webdriver.ie.driver", driverName);
			driver = new InternetExplorerDriver();
			driverConfig("IE");
		} else { //"firefox":
			FirefoxProfile fp = new FirefoxProfile();
			fp.setAcceptUntrustedCertificates( true );
			fp.setPreference( "security.enable_java", true ); 
			fp.setPreference( "plugin.state.java", 2 );
//				plugin.state.java = 0 --> never activate
//				plugin.state.java = 1 --> ask to activate
//				plugin.state.java = 2 --> always activate
			driver = new FirefoxDriver( fp );
			driverConfig("FF");
		}

		// Configuram l'element actions
		actions = new Actions(driver);
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		baseUrl = null;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	protected void driverConfig(String prefix) {
		assertNotNull("No s'ha pogut inicialitzar el driver", driver);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		screenshotHelper = new ScreenshotHelper(driver, prefix);
	}
	
	protected String carregarPropietat(String propertyKey, String msgError) {
		String property = properties.getProperty(propertyKey);
		assertNotNull(msgError, property);
		return property;
	}
	protected String carregarPropietatPath(String propertyKey, String msgError) {
		String property = properties.getProperty(propertyKey);
		assertNotNull(msgError, property);
		String subpath = "/src/test/resources/net/conselldemallorca/helium";
		String path = MessageFormat.format(property, System.getProperty("user.dir") + subpath);
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			path = path.replace("/", "\\");
		}
		return path;
	}
	protected void carregarUrl(String urlKey) {
		baseUrl = properties.getProperty(urlKey);
		assertNotNull("Url base no configurada al fitxer de properties", baseUrl);
		driver.get(baseUrl);
	}
	protected void carregarUrlConfiguracio() {
		baseUrl = properties.getProperty("test.base.url.configuracio");
		assertNotNull("Url base no configurada al fitxer de properties", baseUrl);
		driver.get(baseUrl);
		
		seycon = "true".equals(properties.getProperty("test.base.url.inicio.seycon"));
		if (seycon) {
			String user = properties.getProperty("test.base.usuari.configuracio");
			String pass = properties.getProperty("test.base.usuari.configuracio.pass");
			driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
			driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
			driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
		}
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
	}
	protected void carregarUrlDisseny() {
		baseUrl = properties.getProperty("test.base.url.disseny");
		assertNotNull("Url base no configurada al fitxer de properties", baseUrl);
		driver.get(baseUrl);
	}
	protected void carregarUrlFeina() {
		baseUrl = properties.getProperty("test.base.url.feina");
		assertNotNull("Url base no configurada al fitxer de properties", baseUrl);
		driver.get(baseUrl);
	}

	// Funciona d'ajuda
	protected boolean existeixElementAssert(String xpath, String msgNotFound) {
		return comprovaElement(xpath, null, 0L, msgNotFound, true, false);
	}
	protected boolean existeixElementAssert(String xpath, String screenShot, String msgNotFound) {
		return comprovaElement(xpath, screenShot, 0L, msgNotFound, true, false);
	}
	protected boolean existeixElementAssert(String xpath, String screenShot, long waitTime, String msgNotFound) {
		return comprovaElement(xpath, screenShot, waitTime, msgNotFound, true, false);
	}
	protected boolean existeixElement(String xpath) {
		return comprovaElement(xpath, null, 0L, null, true, true);
	}
	protected boolean existeixElement(String xpath, String screenShot) {
		return comprovaElement(xpath, screenShot, 0L, null, true, true);
	}
	protected boolean existeixElement(String xpath, String screenShot, long waitTime) {
		return comprovaElement(xpath, screenShot, waitTime, null, true, true);
	}
	protected boolean noExisteixElementAssert(String xpath, String msgNotFound) {
		return comprovaElement(xpath, null, 0L, msgNotFound, false, false);
	}
	protected boolean noExisteixElementAssert(String xpath, String screenShot, String msgNotFound) {
		return comprovaElement(xpath, screenShot, 0L, msgNotFound, false, false);
	}
	protected boolean noExisteixElementAssert(String xpath, String screenShot, long waitTime, String msgNotFound) {
		return comprovaElement(xpath, screenShot, waitTime, msgNotFound, false, false);
	}
	protected boolean noExisteixElement(String xpath) {
		return comprovaElement(xpath, null, 0L, null, false, true);
	}
	protected boolean noExisteixElement(String xpath, String screenShot) {
		return comprovaElement(xpath, screenShot, 0L, null, false, true);
	}
	protected boolean noExisteixElement(String xpath, String screenShot, long waitTime) {
		return comprovaElement(xpath, screenShot, waitTime, null, false, true);
	}
	
	protected boolean comprovaElement(String xpath, String screenShot, long waitTime, String msgNotFound, boolean existeix, boolean continuarTest) {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(xpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		if (screenShot != null)
			if (waitTime > 0L)
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			screenshotHelper.saveScreenshot(screenShot);
		if (!continuarTest) {
			if (existeix) assertTrue(msgNotFound, isPresent);
			else assertFalse(msgNotFound, isPresent);
		}
		return (existeix ? isPresent : !isPresent);
	}
	
	protected boolean checkboxSelectedAssert(String xpath, String msgNotFound, boolean selected) {
		return comprovaCheckbox(xpath, msgNotFound, selected, false);
	}
	protected boolean checkboxSelected(String xpath, boolean selected) {
		return comprovaCheckbox(xpath, null, selected, true);
	}
	protected boolean checkboxSelected(String xpath, String msgNotFound, boolean selected) {
		return comprovaCheckbox(xpath, msgNotFound, selected, true);
	}
	protected boolean comprovaCheckbox(String xpath, String msgNotFound, boolean selected, boolean continuarTest) {
		boolean isChecked = driver.findElement(By.xpath(xpath)).isSelected();
		if (!continuarTest) {
			if (selected) assertTrue(msgNotFound, isChecked);
			else assertFalse(msgNotFound, isChecked);
		}
		return (selected ? isChecked : !isChecked);
	}
	protected void checkboxIsSelectedAssert(String xpath, String msg) {
		assertTrue(msg, driver.findElement(By.xpath(xpath)).isSelected());
	}
	
	protected boolean modalObertaAssert(String modal) {
		return existeixElementAssert("//iframe[contains(@src, '" + modal + "')]", 
						null,
						"No s'ha obert la finestra modal de " + modal);
	}
	protected boolean modalObertaAssert(String modal, String screenShot) {
		return existeixElementAssert("//iframe[contains(@src, '" + modal + "')]", 
						screenShot,
						500,
						"No s'ha obert la finestra modal de " + modal);
	}
	protected boolean modalOberta(String modal) {
		return existeixElement("//iframe[contains(@src, '" + modal + "')]", 
						null);
	}
	protected boolean modalOberta(String modal, String screenShot) {
		return existeixElement("//iframe[contains(@src, '" + modal + "')]", 
						screenShot,
						500);
	}
	protected void vesAModal(String modal) {
		driver.switchTo().frame(driver.findElement(By.xpath("//iframe[contains(@src, '" + modal + "')]")));
	}
	protected void tornaAPare() {
		driver.switchTo().defaultContent();
	}
	
	public boolean isAlertPresent() { 
		try {
			driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
	} 
	
	protected void acceptarAlerta() {
		try {
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			fail("Error acceptant alert.");
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	protected void acceptarConfirm(String msg) {
		try {
			driver.switchTo().alert().accept();
			driver.switchTo().alert().sendKeys(msg);
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			fail("Error acceptant alert.");
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	protected void rebutjarAlerta() {
		try {
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {
			fail("Error rebutjant alert.");
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	protected String getTexteAlerta() {
		String out = "";
		try {
			out = driver.switchTo().alert().getText();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error agafant el texte de alert.");
		}
		return out;
	}

	// ............................................................................................................
	// Funcions ajuda per a INICIALITZAR proves
	// ............................................................................................................	
	
	// ENTORN
	// ............................................................................................................	
	protected void saveEntornActual() {
		entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
	}
	
	protected void crearEntorn(String entorn, String titolEntorn) {
//		entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		// Crear entorn
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).clear();
			driver.findElement(By.id("codi0")).sendKeys(entorn);
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(titolEntorn);
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha pogut crear l'entorn");
		}
	}
	
	protected void assignarPermisosEntorn(String entorn, String usuari, String... permisos) {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha trobat l'entorn");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
		
		// Eliminamos los permisos anteriores
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]/td[4]/a/img")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se borraron los permisos correctamente");
		}
		
		// Ponemos los nuevos
		driver.findElement(By.id("nom0")).sendKeys(usuari);
		for (String permis: permisos) {
			if ("DESIGN".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
			} else if ("ORGANIZATION".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='ORGANIZATION']")).click();
			} else if ("READ".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='READ']")).click();
			} else if ("ADMINISTRATION".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
			}
		}
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut assignar permisos");
	}
	
	protected void importarDadesEntorn(String entorn, String pathExportEntorn) {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha trobat l'entorn");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]")).click();
		existeixElementAssert("//h3[@class='titol-tab titol-delegacio']/img", "No s'ha trobat la secció d'importació");
		driver.findElement(By.xpath("//h3[@class='titol-tab titol-delegacio']/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(pathExportEntorn);
		driver.findElement(By.xpath("//*[@id='commandImportacio']//button")).click();
		acceptarAlerta();
	}
	
	protected void seleccionarEntorn(String titolEntorn) {
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + titolEntorn + "')]/a")));
		actions.click();
		actions.build().perform();
	}
	
	protected void marcarEntornDefecte(String titolEntorn) {
		driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]", "No s'ha trobat l'entorn a marcar per defecte");
		if (!driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a/img")).getAttribute("src").endsWith("star.png")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + titolEntorn + "')]/td[1]/a")).click();
		}
	}
	
	protected void eliminarEntorn(String entorn) {
//		//Entorn actual per defecte
//		if (entornActual != null) {
//			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
//			if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]") &&
//					!driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a/img")).getAttribute("src").endsWith("star.png")) {
//				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a")).click();
//			}
//		}
		
		// Eliminam l'entorn de test
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
			acceptarAlerta();
			actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/entorn/llistat.html')]")));
			actions.click();
			actions.build().perform();
			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha pogut eliminar l'entorn de proves");
		}
	}

	// DEFINICIÓ DE PROCÉS
	// ............................................................................................................	
	public enum TipusDesplegament {
		JBPM,
		EXPORTDEFPRC,
		EXPORTTIPEXP
	}
	
	protected void desplegarDefinicioProcesEntorn(String nomDefProc, String pathDefProc) {
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, null, pathDefProc, null, false, false);
	}
	
	protected void desplegarDefinicioProcesEntorn(String tipusExpedient, String nomDefProc, String pathDefProc) {
		desplegarDefPro(TipusDesplegament.JBPM, nomDefProc, tipusExpedient, pathDefProc, null, false, false);
	}
	
	protected void desplegarDefPro(TipusDesplegament tipDesp, String nomDefProc, String nomTipusExp, String path, String etiqueta, boolean actualitzarExps, boolean captures) {
		// Comprovam si existeix. En cas de que existeixi, comprovam si està a tipus d'expedient, i el número de versió
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		Integer versio = null;
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]")) {
			try {
				versio = Integer.parseInt(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[2]")).getText().trim());
				String tipusExp = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[3]")).getText().trim();
				if (nomTipusExp == null) {
					if(!"".equals(tipusExp)) fail("La definició de procés està desplegada a un tipus d'expedient");
				} else if (!nomTipusExp.equals(tipusExp)) {
					fail("La definició de procés ja està desplegada a un altre tipus d'expedient");
				}
			} catch (NumberFormatException nfe) {
				fail("Número de versió actual incorrecte");
			}
		}
		
		// Anem a fer el deploy
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/deploy.html')]")));
		actions.click();
		actions.build().perform();
		
		// Deploy
		driver.findElement(By.xpath("//option[@value='" + tipDesp.name() + "']")).click();
		
		boolean tipExp = false;
		// tipus d'expedient
		if (nomTipusExp != null) {
			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
				if (option.getText().equals(nomTipusExp)) {
					option.click();
					break;
				}
			}
			tipExp = true;
		}		

		// Path
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		
		// Etiqueta
		if (etiqueta != null) {
			driver.findElement(By.id("etiqueta0")).clear();
			driver.findElement(By.id("etiqueta0")).sendKeys(etiqueta);
		}
		
		// Actualitza expedients
		if (actualitzarExps) {
			driver.findElement(By.id("actualitzarProcessosActius0")).click();
		}
		long tm = System.currentTimeMillis();
		
		if (captures) screenshotHelper.saveScreenshot("defproces/import/" + nomDefProc + "_" + tm + "/" + (tipExp ? "tipusExp" : "global") + "/1_importPar.png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		if (captures) screenshotHelper.saveScreenshot("defproces/importPar/" + nomDefProc + "_" + tm + "/" + (tipExp ? "tipusExp" : "global") + "/2_importPar.png");
		
		if (actualitzarExps) {
			existeixElementAssert("//*[@id='infos']/p[2]", "No s'ha programat el canvi de versió en els expedients");
		}
		if (etiqueta != null) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]")).click();
			existeixElementAssert("//*[@id='content']/dl/dd[5][text() = '" + etiqueta + "']", "No s'ha desat la etiqueta de la definició de procés");
		}
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (tipExp) {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][text() = '" + nomTipusExp + "']]", "defproces/importPar/tipusExp/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés ("+nomDefProc+") a nivell de tipus d'expedient");
		} else {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "No s'ha pogut importar la definició de procés ("+nomDefProc+") a nivell global");
		}
		if (versio != null) {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[2][text() = '" + (versio + 1) + "']]", "No s'ha actualitzat la versió de la definició de procés ("+nomDefProc+")");
		}
	}
	
	protected void seleccionarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "No s'ha trobat la defició de procés de prova");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();
	}

	
	protected void eliminarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		while (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[4]/a")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se borró la definición del proceso '" + nomDefProc+"'");
		}
	}
	
	protected void crearAgrupacio(String nomDefProc, String codi, String nom) {
		seleccionarDefinicioProces(nomDefProc);
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/campAgrupacioLlistat.html')]")).click();			
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).clear();
			driver.findElement(By.id("codi0")).sendKeys(codi);
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(nom);
			driver.findElement(By.xpath("//button[@value='submit']")).click();
		}
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut crear l'agrupació de test " + nom);
	}
	
	protected void crearVar(String codi, String nom, TipusVar tipus, String agrupacio, boolean multiple, boolean oculta, boolean noRetrocedir) {
		crearVar(codi, nom, tipus, agrupacio, multiple, oculta, noRetrocedir, null);
	}
	protected void crearVar(String codi, String nom, TipusVar tipus, String agrupacio, boolean multiple, boolean oculta, boolean noRetrocedir, Object parametres) {
		// Definim sufix per a les variables depenent de la confifuració
		String params = "";
		if (multiple) params += "M";
		if (oculta) params += "O";
		if (noRetrocedir) params += "R";
		if (!params.isEmpty()) {
			params = "_" + params; 
			codi += params;
			nom += params;
		}
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/campLlistat.html')]")).click();	
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/variable/" + tipus.getId() + params + "/01_crea_var.png", "La variable a crear ja existeix");
				
  	    // Botó nova variable
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
  	    // Paràmetres de la variable
  	    driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.xpath("//*[@id='tipus0']/option[@value='" + tipus.getLabel() + "']")).click();
		driver.findElement(By.id("etiqueta0")).clear();
		driver.findElement(By.id("etiqueta0")).sendKeys(nom);
		driver.findElement(By.id("observacions0")).clear();
		driver.findElement(By.id("observacions0")).sendKeys("Variable de tipus" + tipus.getLabel() + "\n" +
															"Multiple: " + (multiple ? "Sí" : "No") + "\n" +
															"Oculta: " + (oculta ? "Sí" : "No") + "\n" +
															"Retrocedir: " + (noRetrocedir ? "No" : "Sí"));
		if (agrupacio != null) driver.findElement(By.xpath("//*[@id='agrupacio0']/option[normalize-space(text())='" + agrupacio + "']")).click();
		if (multiple) driver.findElement(By.id("multiple0")).click();
		if (oculta) driver.findElement(By.id("ocult0")).click();
		if (noRetrocedir) driver.findElement(By.id("ignored0")).click();
		
		if (tipus == TipusVar.SEL_CONSULTA || tipus == TipusVar.SUG_CONSULTA) {
			String[] vars = (String[])parametres;
			driver.findElement(By.xpath("//*[@id='consulta0']/option[normalize-space(text())='" + vars[0] + "']")).click();
			driver.findElement(By.id("consultaParams0")).clear();
			driver.findElement(By.id("consultaParams0")).sendKeys(vars[1]);
			driver.findElement(By.id("consultaCampValor0")).clear();
			driver.findElement(By.id("consultaCampValor0")).sendKeys(vars[2]);
			driver.findElement(By.id("consultaCampText0")).clear();
			driver.findElement(By.id("consultaCampText0")).sendKeys(vars[3]);
		} else if (tipus == TipusVar.SEL_DOMINI || tipus == TipusVar.SUG_DOMINI) {
			String[] vars = (String[])parametres;
			driver.findElement(By.xpath("//*[@id='domini0']/option[normalize-space(text())='" + vars[0] + "']")).click();
			driver.findElement(By.id("dominiId0")).clear();
			driver.findElement(By.id("dominiId0")).sendKeys("XXX");
			driver.findElement(By.id("dominiParams0")).clear();
			driver.findElement(By.id("dominiParams0")).sendKeys(vars[1]);
			driver.findElement(By.id("dominiCampValor0")).clear();
			driver.findElement(By.id("dominiCampValor0")).sendKeys(vars[2]);
			driver.findElement(By.id("dominiCampText0")).clear();
			driver.findElement(By.id("dominiCampText0")).sendKeys(vars[3]);
		} else if (tipus == TipusVar.SEL_ENUM || tipus == TipusVar.SUG_ENUM) {
			String enumeracio = (String)parametres;
			driver.findElement(By.xpath("//*[@id='enumeracio0']/option[normalize-space(text())='" + enumeracio + "']")).click();
		} else if (tipus == TipusVar.SEL_INTERN || tipus == TipusVar.SUG_INTERN) {
			driver.findElement(By.id("dominiIntern0")).click();
			String[] vars = (String[])parametres;
			driver.findElement(By.id("dominiId0")).clear();
			driver.findElement(By.id("dominiId0")).sendKeys(vars[0]);
			driver.findElement(By.id("dominiParams0")).clear();
			driver.findElement(By.id("dominiParams0")).sendKeys(vars[1]);
			driver.findElement(By.id("dominiCampValor0")).clear();
			driver.findElement(By.id("dominiCampValor0")).sendKeys(vars[2]);
			driver.findElement(By.id("dominiCampText0")).clear();
			driver.findElement(By.id("dominiCampText0")).sendKeys(vars[3]);
		} else if (tipus == TipusVar.ACCIO) {
			String accio = (String)parametres;
			driver.findElement(By.xpath("//*[@id='jbpmAction0']/option[@value='" + accio + "']")).click();
		}
		screenshotHelper.saveScreenshot("defproces/variable/" + tipus.getId() + params + "/02_crea_var.png");
		
		// Crear variable
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		// Comprovar que s'ha creat
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/variable/" + tipus.getId() + params + "/03_crea_var.png", "La variable no s'ha pogut crear");
		// Comprovar paràmetres
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]")).click();
		if (agrupacio != null)
			existeixElementAssert("//*[@id='agrupacio0']/option[@selected='selected' and normalize-space(text())='" + agrupacio + "']", "La agrupació de la variable no s'ha gravat correctament");
		else 
			noExisteixElementAssert("//*[@id='agrupacio0']/option[@selected='selected']", "La agrupació de la variable no s'ha gravat correctament");
		checkboxSelectedAssert("//*[@id='multiple0']", "El paràmetre múltiple de la variable no s'ha gravat correctament", multiple);
		checkboxSelectedAssert("//*[@id='ocult0']", "El paràmetre ocult de la variable no s'ha gravat correctament", oculta);
		checkboxSelectedAssert("//*[@id='ignored0']", "El paràmetre no retrocedir de la variable no s'ha gravat correctament", noRetrocedir);
		
		if (tipus == TipusVar.SEL_CONSULTA || tipus == TipusVar.SUG_CONSULTA) {
			String[] vars = (String[])parametres;
			existeixElementAssert("//*[@id='consulta0']/option[@selected='selected' and normalize-space(text())='" + vars[0] + "']", "La consulta de la variable no s'ha gravat correctament");
			existeixElementAssert("//*[@id='consultaParams0' and normalize-space(text())=\"" + vars[1] + "\"]", "Els paràmetres de la consulta no s'ha gravat correctament");
			existeixElementAssert("//*[@id='consultaCampValor0' and @value='" + vars[2] + "']", "Els valor de la consulta no s'ha gravat correctament");
			existeixElementAssert("//*[@id='consultaCampText0' and normalize-space(text())='" + vars[3] + "']", "Els text de la consulta no s'ha gravat correctament");
		} else if (tipus == TipusVar.SEL_DOMINI || tipus == TipusVar.SUG_DOMINI) {
			String[] vars = (String[])parametres;
			existeixElementAssert("//*[@id='domini0']/option[@selected='selected' and normalize-space(text())='" + vars[0] + "']", "El domini de la variable no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiParams0' and normalize-space(text())=\"" + vars[1] + "\"]", "Els paràmetres del domini no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiCampValor0' and @value='" + vars[2] + "']", "Els valor del domini no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiCampText0' and normalize-space(text())='" + vars[3] + "']", "Els text del domini no s'ha gravat correctament");
		} else if (tipus == TipusVar.SEL_ENUM || tipus == TipusVar.SUG_ENUM) {
			String enumeracio = (String)parametres;
			existeixElementAssert("//*[@id='enumeracio0']/option[@selected='selected' and normalize-space(text())='" + enumeracio + "']", "L'enumeració de la variable no s'ha gravat correctament");
		} else if (tipus == TipusVar.SEL_INTERN || tipus == TipusVar.SUG_INTERN) {
			driver.findElement(By.id("dominiIntern0")).click();
			driver.findElement(By.id("dominiIntern0")).click();
			checkboxSelectedAssert("//*[@id='dominiIntern0']", "La opció domini intern de la variable no s'ha gravat correctament", true);
			String[] vars = (String[])parametres;
			existeixElementAssert("//*[@id='dominiId0' and @value='" + vars[0] + "']", "L'identificador del domini intern no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiParams0' and normalize-space(text())=\"" + vars[1] + "\"]", "Els paràmetres del domini intern no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiCampValor0' and @value='" + vars[2] + "']", "Els valor del domini intern no s'ha gravat correctament");
			existeixElementAssert("//*[@id='dominiCampText0' and normalize-space(text())='" + vars[3] + "']", "Els text del domini intern no s'ha gravat correctament");
		} else if (tipus == TipusVar.ACCIO) {
			String accio = (String)parametres;
			existeixElementAssert("//*[@id='jbpmAction0']/option[@selected='selected' and normalize-space(text())='" + accio + "']", "La acció de la variable no s'ha gravat correctament");
		}
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();

		// Assignar elements a la variable tipus registre
		if (tipus == TipusVar.REGISTRE) {
			String[] vars = (String[])parametres;
			// Assignam variables
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[6]/form/button")).click();
			for (String textVar: vars) {
				driver.findElement(By.xpath("//*[@id='membreId0']/option[normalize-space(text())='" + textVar + "']")).click();
				driver.findElement(By.xpath("//button[@value='submit']")).click();
				existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + textVar + "')]", "La variable no s'ha assignat correctament al registre");
			}
		}
	}
	
	protected void eliminarEnumeracio(String nomEnumeracio) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomEnumeracio + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomEnumeracio + "')]/td[4]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomEnumeracio + "')]", "No s'han pogut eliminar l'enumeració");
	}
	
	protected void eliminarDomini(String codiDomini) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/domini/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]/td[5]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]", "No s'han pogut eliminar el domini");
	}
	
	protected void eliminarConsultaTipus(String codiConsulta) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/consulta/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiConsulta + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiConsulta + "')]/td[8]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiConsulta + "')]", "No s'han pogut eliminar la consulta");
	}
	
	protected void crearDocumentDefProc(String nomDefProc) {
		crearDocumentDefProc(nomDefProc, null);
	}
	
	protected void crearDocumentDefProc(String nomDefProc, String prefixeScreenShot) {
		
		seleccionarDefinicioProces(nomDefProc);
		
		driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/documentLlistat.html')]")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_pipella_documents.png"); }
		
		driver.findElement(By.xpath("//*[@id='content']/form/button[text() = 'Nou document']")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_formulari_doc.png"); }
		
		driver.findElement(By.id("codi0")).sendKeys("DC1");
		
		driver.findElement(By.id("nom0")).sendKeys("Document tramit");
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_dades_emplenades.png"); }
		
		driver.findElement(By.xpath("//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Crear']")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_4_resultat_insercio.png"); }
	}
	
	// ............................................................................................................
	// TIPUS D'EXPEDIENT
	// ............................................................................................................
	
	protected void crearTipusExpedient(String nom, String codi) {
		crearTipusExpedient(nom, codi, null);
	}
	
	protected void crearTipusExpedient(String nom, String codi, String prefixeScreenShot) {
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_llistat_inicial.png"); }
		
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys(codi);
			driver.findElement(By.id("nom0")).sendKeys(nom);
			
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_dades_tipexp_basic.png"); }
			
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			actions.moveToElement(driver.findElement(By.id("menuDisseny")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
			actions.click();
			actions.build().perform();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut crear el tipus d'expedient de test");
			
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_comprovacio_apareix_llistat.png"); }
		}else{
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_4_expedient_ja_existent.png"); }
		}
	}
	
	protected void modificarTipusExpedientComplet(String codi, String nom, String expressio, String anySeq0, String seqSeq0, String anySeq1, String seqSeq1, String responsable) {
		modificarTipusExpedientComplet(codi, nom, expressio, anySeq0, seqSeq0, anySeq1, seqSeq1, responsable, null);
	}
	
	protected void modificarTipusExpedientComplet(String codi, String nom, String expressio, String anySeq0, String seqSeq0, String anySeq1, String seqSeq1, String responsable, String prefixeScreenShot) {
		
		driver.findElement(By.xpath("//*[@id='content']/div/form[contains(@action, '/expedientTipus/form.html')]/button")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_dades_inicials.png"); }
		
		//driver.findElement(By.id("codi0")).sendKeys(codi);
		//driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("teTitol0")).click();
		driver.findElement(By.id("demanaTitol0")).click();
		driver.findElement(By.id("teNumero0")).click();
		driver.findElement(By.id("demanaNumero0")).click();
		
		driver.findElement(By.id("expressioNumero0")).sendKeys(expressio);
		
		driver.findElement(By.id("reiniciarCadaAny0")).click();
		
		driver.findElement(By.xpath("//*[@id='seqMultiple']/div/button[text() = 'Afegir']")).click();
		
		driver.findElement(By.id("seqany_0")).sendKeys(anySeq0);
		driver.findElement(By.id("seqseq_0")).sendKeys(seqSeq0);
		
		driver.findElement(By.xpath("//*[@id='seqMultiple']/div/button[text() = 'Afegir']")).click();
		
		driver.findElement(By.id("seqany_1")).sendKeys(anySeq1);
		driver.findElement(By.id("seqseq_1")).sendKeys(seqSeq1);
		
		String suggestUsuDes = responsable;
		if (responsable.length()>3) { suggestUsuDes = responsable.substring(0, 3); }
		
		driver.findElement(By.id("suggest_responsableDefecteCodi0")).sendKeys(suggestUsuDes);
		try { Thread.sleep(2000); }catch (Exception ex) {}
		driver.findElement(By.xpath("//html/body/div[@class='ac_results']/ul/li[contains(text(), '"+responsable+"')]")).click();
		
		driver.findElement(By.id("restringirPerGrup0")).click();
		driver.findElement(By.id("seleccionarAny0")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_dades_modificades.png"); }
		
		driver.findElement(By.xpath("//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Modificar']")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_resultat_modificacio.png"); }
	}

	protected void comprobarTipusExpedientComplet(String codi, String nom, String expressio, String anySeq0, String seqSeq0, String anySeq1, String seqSeq1, String responsable) {
		
		driver.findElement(By.xpath("//*[@id='content']/div/form[contains(@action, '/expedientTipus/form.html')]/button")).click();
			
		if (!nom.equals(driver.findElement(By.id("nom0")).getAttribute("value"))) { fail("El nom del tipus d´expedient no coincideix amb l´esperat ("+nom+")"); }
		if (!checkboxSelected("//*[@id='teTitol0']", true)) {fail("El check 'Amb títol' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='demanaTitol0']", true)) {fail("El check 'Demana títol' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='teNumero0']", true)) {fail("El check 'Amb numero' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='demanaNumero0']", true)) {fail("El check 'Demana numero' del tipus d´expedient hauria de estar seleccionat"); }
		if (!expressio.equals(driver.findElement(By.id("expressioNumero0")).getAttribute("value"))) { fail("La expressió calculada del tipus d´expedient no coincideix amb l´esperada ("+expressio+")"); }
		
		if (!responsable.equals(driver.findElement(By.id("suggest_responsableDefecteCodi0")).getAttribute("value"))) { fail("El responsable del tipus d´expedient no coincideix amb l´esperat ("+responsable+")"); }
		
		if (!checkboxSelected("//*[@id='reiniciarCadaAny0']", true)) {fail("El check 'Reiniciar Sequencia anualmetn' del tipus d´expedient hauria de estar seleccionat"); }
		
		if (!anySeq0.equals(driver.findElement(By.id("seqany_1")).getAttribute("value"))) { fail("L'any de la sequencia 0 del tipus d´expedient no coincideix amb l´esperada ("+anySeq0+")"); }
		if (!seqSeq0.equals(driver.findElement(By.id("seqseq_1")).getAttribute("value"))) { fail("La seq de la sequencia 0 del tipus d´expedient no coincideix amb l´esperada ("+seqSeq0+")"); }
		if (!anySeq1.equals(driver.findElement(By.id("seqany_0")).getAttribute("value"))) { fail("L'any de la sequencia 1 del tipus d´expedient no coincideix amb l´esperada ("+anySeq1+")"); }
		if (!seqSeq1.equals(driver.findElement(By.id("seqseq_0")).getAttribute("value"))) { fail("La seq de la sequencia 1 del tipus d´expedient no coincideix amb l´esperada ("+seqSeq1+")"); }
		
		if (!checkboxSelected("//*[@id='restringirPerGrup0']", true)) {fail("El check 'Restringir accés segons el grups de l'usuari' del tipus d´expedient hauria de estar seleccionat"); }
		if (!checkboxSelected("//*[@id='seleccionarAny0']", true)) {fail("El check 'Permetre seleccionar any d'inici d'expedient' del tipus d´expedient hauria de estar seleccionat"); }
	}
	
	protected void seleccionarTipExp(String codTipExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]", "No s'ha trobat el tipus d'expedient");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[1]/a")).click();
	}
	
	protected void eliminarTipusExpedient(String codiTipusExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]/td[4]/a")).click();
			acceptarAlerta();
		}
	}

	protected void importarDadesTipExp(String codiTipusExp, String path) {
		importarDadesTipExp(codiTipusExp, path, null);
	}
	
	protected void importarDadesTipExp(String codiTipusExp, String path, String prefixeScreenShot) {
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a, '"+codiTipusExp+"')]/a")).click();

		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_importar_tipexp-inici.png"); }
		
		// Deploy
		driver.findElement(By.xpath("//*[@id='content']/div/h3/img[contains(@src,'magnifier_zoom_in.png')]")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_importar_tipexp-arxiu_seleccionat.png"); }
		
		driver.findElement(By.xpath("//*[@id='command']//div[@class='buttonHolder']/button[text() = 'Importar']")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_importar_tipexp-resultat.png"); }
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar el tipus d´expedient de test");
	}
	
	
	// EXPEDIENT
	// ............................................................................................................
	protected void importarDadesDefPro(String defProc, String path) {		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+defProc+"')]")).click();

		// Deploy
		driver.findElement(By.xpath("//*[@id='content']/div[1]/h3/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar los datos de la definició de procés de '"+defProc+"' de la ruta '"+path+"'");
	}
	
	protected void desplegarTipExp(TipusDesplegament tipDesp, String nomTipusExp, String path, String etiqueta) {
		// Comprovam si existeix. En cas de que existeixi, comprovam si està a tipus d'expedient, i el número de versió
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		noExisteixElementAssert("//*[@id='registre']//tr[contains(td[2],'" + nomTipusExp + "')]", "El tipus d'expedient ja està desplegat");
		
		// Anem a fer el deploy
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/deploy.html')]")));
		actions.click();
		actions.build().perform();

		// Deploy
		driver.findElement(By.xpath("//option[@value='" + tipDesp.name() + "']")).click();

		// Path
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		
		// Etiqueta
		if (etiqueta != null) {
			driver.findElement(By.id("etiqueta0")).clear();
			driver.findElement(By.id("etiqueta0")).sendKeys(etiqueta);
		}

		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		if (etiqueta != null) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomTipusExp + "')]")).click();
			existeixElementAssert("//*[@id='content']/dl/dd[5][text() = '" + etiqueta + "']", "No s'ha desat la etiqueta del tipus d´expedient.");
		}
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']//tr[contains(td[2],'" + nomTipusExp + "')]", "No s'ha pogut importar el tipus d'expedient");
	}
	
	protected void eliminarExpedient(String numExpediente, String tituloExpediente) {
		eliminarExpedient(numExpediente, tituloExpediente, null);
	}
	
	protected void eliminarExpedient(String numExpediente, String tituloExpediente, String tipusExp) {
		
		consultarExpedientes(numExpediente, tituloExpediente, tipusExp);
		
		while (existeixElement("//*[@id='registre']/tbody/tr[1]")) {
			borrarPrimerExpediente();
		}
	}

	protected void borrarPrimerExpediente() {
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")).click();
		
		acceptarAlerta();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut borrar el expediente");
	}

	protected String[] iniciarExpediente(String codTipusExp, String numero, String titulo) {
		
		String[] res = new String[2];
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td,'" + codTipusExp + "')]", "No s'ha trobat la fila corresponent al tipus d'expedient");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td,'" + codTipusExp + "')]/td/form/button[contains(text(), 'Iniciar')]")).click();

		if (!isAlertPresent()) {			
			if (existeixElement("//*[@id='numero0']")) {
				driver.findElement(By.xpath("//*[@id='numero0']")).clear();
				driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
			} else {
				numero = null;
			}
			if (existeixElement("//*[@id='titol0']")) {
				driver.findElement(By.xpath("//*[@id='titol0']")).clear();
				driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
			} else {
				titulo = null;
			}
			driver.findElement(By.xpath("//button[@value='submit']")).click();
		}
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		if (textoInfo.indexOf("]") != -1) {
			res[0] = textoInfo.substring(textoInfo.indexOf("[")+1, textoInfo.indexOf("]")).trim();
			res[1] = textoInfo.substring(textoInfo.indexOf("]")+1).trim();
		} else {
			if (numero != null)
				res[0] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
			if (titulo != null)
				res[1] = textoInfo.substring(textoInfo.indexOf(":")+1).trim();
		}
		
		return res;
	}
	
	protected void assignarPermisosTipusExpedient(String tipusExp, String usuari, String... permisos) {
		assignarPermisosTipusExpedient(tipusExp, usuari, null, false, permisos);
	}
	
	protected void assignarPermisosTipusExpedient(String tipusExp, String usuari, boolean esRol, String... permisos) {
		assignarPermisosTipusExpedient(tipusExp, usuari, null, esRol, permisos);
	}
	
	protected void assignarPermisosTipusExpedient(String tipusExp, String usuari, String prefixeScreenShot, boolean esRol, String... permisos) {
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipusExp + "')]", "No s'ha trobat l'tipus");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipusExp + "')]/td[3]/form/button")).click();
		
		// Eliminamos los permisos anteriores
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]/td[4]/a/img")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se borraron los permisos correctamente");
		}
		
		// Ponemos los nuevos
		driver.findElement(By.id("nom0")).sendKeys(usuari);
		for (String permis: permisos) {
			if ("DESIGN".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
			} else if ("CREATE".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='CREATE']")).click();
			} else if ("SUPERVISION".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='SUPERVISION']")).click();
			} else if ("WRITE".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='WRITE']")).click();
			} else if ("MANAGE".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='MANAGE']")).click();
			} else if ("DELETE".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='DELETE']")).click();
			} else if ("READ".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='READ']")).click();
			} else if ("ADMINISTRATION".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
			} else if ("REASSIGNMENT".equals(permis)) {
				driver.findElement(By.xpath("//input[@value='REASSIGNMENT']")).click();
			}
		}
		
		if (esRol) {
			if (driver.findElement(By.id("usuari0")).isSelected()) {
				driver.findElement(By.id("usuari0")).click();
			}
		}else{
			if (!driver.findElement(By.id("usuari0")).isSelected()) {
				driver.findElement(By.id("usuari0")).click();
			}
		}
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_asigna_permisos.png"); }
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut assignar permisos");
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_asigna_permisos_resultat.png"); }
	}

	protected void desasignarPermisosTipusExpedient(String tipusExp, String usuari) {
		desasignarPermisosTipusExpedient(tipusExp, usuari, null);
	}
	
	protected void desasignarPermisosTipusExpedient(String tipusExp, String usuari, String prefixeScreenShot) {
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipusExp + "')]", "No s'ha trobat el tipus d´expedient");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipusExp + "')]/td[3]/form/button")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_borra_permisos_inici.png"); }
		
		//driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td, '"+usuari+"')]/td/a[contains(@href, '/permisos/expedientTipusEsborrar.html')]")).click();
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[td//text()[contains(., '"+usuari+"')]]/td/a[contains(@href, '/permisos/expedientTipusEsborrar.html')]")).click();

		if (isAlertPresent()) {acceptarAlerta();}
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut borrar els permisos");
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_borra_permisos_resultat.png"); }
	}
	
	protected byte[] downloadFile(String xpath, String fitxer) {
		
		FileDownloader downloader = new FileDownloader(driver);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(xpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("Enllaç al fitxer " + fitxer + " no existeix" , isPresent);
		
		byte[] downloadedFile = null;
		try {
			downloadedFile = downloader.downloadFile(driver.findElement(By.xpath(xpath)));
		} catch (Exception e) {
			e.printStackTrace();
			fail("No s'ha pogut descarregar el fitxer " + fitxer);
		}
		assertThat(downloader.getHTTPStatusOfLastDownloadAttempt(), is(equalTo(200)));
		return downloadedFile;
	}
	
	protected byte[] downloadFileFromForm(String xpath, String fitxer) {
		
		FileDownloader downloader = new FileDownloader(driver);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(xpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("Enllaç al fitxer " + fitxer + " no existeix" , isPresent);
		
		byte[] downloadedFile = null;
		try {
			downloadedFile = downloader.downloadFileFromForm(driver.findElement(By.xpath(xpath)));
		} catch (Exception e) {
			e.printStackTrace();
			fail("No s'ha pogut descarregar el fitxer " + fitxer);
		}
		assertThat(downloader.getHTTPStatusOfLastDownloadAttempt(), is(equalTo(200)));
		return downloadedFile;
	}
	
	protected void downloadFileHash(String xpath, String md5, String fitxer) {
		byte[] downloadedFile = downloadFile(xpath, fitxer);
		
		try {
			CheckFileHash fileToCheck = new CheckFileHash();
			fileToCheck.fileToCheck(downloadedFile);
			fileToCheck.hashDetails(md5, HashType.MD5);
			assertThat("El fitxer " + fitxer + " descarregat no té el hash esperat", fileToCheck.hasAValidHash(), is(equalTo(true)));
		} catch (Exception e) {
			fail("No s'ha pogut comprovar el fitxer " + fitxer + " descarregat");
		}
	}
	
	protected byte[] postDownloadFile(String formXpath) {
		return postDownloadFile(formXpath, null, null, null, null);
	}
	
	protected byte[] postDownloadFile(String formXpath, String[] parametres, String[] valors) {
		return postDownloadFile(formXpath, parametres, valors, null, null);
	}
	
	protected byte[] postDownloadFile(String formXpath, String replaceURL, String toThisURL) {
		return postDownloadFile(formXpath, null, null, replaceURL, toThisURL);
	}
	
	/**
	 * Funció ampliada de postDownloadFile(String formXpath) que permet asignarli parametres extra al formulari.
	 * També permet assignarli la direcció final de la petició per si la de per defecte produeix una redireccio 302 (que no funcionara)
	 */
	protected byte[] postDownloadFile(String formXpath, String[] parametres, String[] valors, String replaceURL, String toThisURL) {
		
		FileDownloader downloader = new FileDownloader(driver);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(formXpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("El formulari indicat no existeix" , isPresent);
		
		WebElement form = driver.findElement(By.xpath(formXpath));
		String formAction = form.getAttribute("action");
		
		if (formAction == null || "".equals(formAction))
			fail("El formulari no té una acció definida");
	
		List<WebElement> inputs = form.findElements(By.tagName("input"));
		List<NameValuePair> params = new ArrayList<NameValuePair>(inputs.size());
		for (WebElement input: inputs) {
			params.add(new BasicNameValuePair(input.getAttribute("name"), input.getAttribute("value")));
		}
		
		if (parametres!=null && valors!=null && valors.length==parametres.length) {
			for (int p=0; p<parametres.length; p++) {
				params.add(new BasicNameValuePair(parametres[p], valors[p]));
			}
		}
		
		if (replaceURL!=null && !"".equals(replaceURL) && toThisURL!=null && !"".equals(toThisURL)) {
			formAction = formAction.replace(replaceURL, toThisURL);
		}
		
		byte[] downloadedFile = null;
		try {
			downloadedFile = downloader.postDownloaderWithRedirects(formAction, params);
		} catch (Exception e) {
			fail("No s'ha pogut descarregar el formulari");
		}
	
		assertThat(downloader.getHTTPStatusOfLastDownloadAttempt(), is(equalTo(200)));
		return downloadedFile;
	}
	
	protected void postDownloadFileHash(String formXpath, String md5) {
		byte[] downloadedFile = postDownloadFile(formXpath);
		
		try {
			CheckFileHash fileToCheck = new CheckFileHash();
			fileToCheck.fileToCheck(downloadedFile);
			fileToCheck.hashDetails(md5, HashType.MD5);
			assertThat("El fitxer descarregat no té el hash esperat", fileToCheck.hasAValidHash(), is(equalTo(true)));
		} catch (Exception e) {
			fail("No s'ha pogut comprovar el fitxer descarregat");
		}
	}
	
	protected String getMd5(byte[] hash) {
		// converting byte array to Hexadecimal String
		StringBuilder sb = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}

		return sb.toString();
	}
	
	protected void adjuntarDocExpediente(String numExpediente, String tituloExpediente, String tituloDocumento, String fechaDocumento, String pathDocumento) {
		consultarExpedientes(numExpediente, tituloExpediente, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		screenshotHelper.saveScreenshot("documentsexpedient/adjuntar_documents/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[3]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='content']/form/button")).click();
		
		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys(tituloDocumento);
		
		driver.findElement(By.xpath("//*[@id='data0']")).clear();
		driver.findElement(By.xpath("//*[@id='data0']")).sendKeys(fechaDocumento);		
		
		driver.findElement(By.id("contingut0")).sendKeys(pathDocumento);
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se adjuntó el documento");
	}
	
	protected void sortTable(String taulaId, int origen, int desti) {
		int max = Math.max(origen, desti);
		int min = Math.min(origen, desti);
		String[] elements = new String[max];
		String[] elementOrdenats = new String[max];
		
		for (int i = 1; i <= max; i++) {
			elements[i-1] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
		}
		
		WebElement elOrigen = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + origen + "]/td[1]"));
		WebElement elDesti = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + desti + "]/td[1]"));
		// Drag and drop
		actions.clickAndHold(elOrigen).moveToElement(elDesti).release(elDesti).build().perform();
		
		// Comprovam la ordenació
		driver.navigate().refresh();
		
		// Bug: el drag and drop pot no deixar-ho a la fila que li hem dit
		for (int i = 1; i <= max; i++) {
			if (elements[origen-1].equals(driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText())) {
				desti = i;
				max = Math.max(origen, desti);
				min = Math.min(origen, desti);
				break;
			}
		}
		// End Bug
		
		for (int i = 1; i <= max; i++) {
			if (desti > origen) {
				if (i == desti) {
					elementOrdenats[origen-1] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				} else if (i >= min && i < max) {
					elementOrdenats[i] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				} else {
					elementOrdenats[i-1] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				}
			} else {
				if (i == desti) {
					elementOrdenats[origen-1] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				} else if (i > min && i <= max) {
					elementOrdenats[i-2] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				} else {
					elementOrdenats[i-1] = driver.findElement(By.xpath("//*[@id='" + taulaId + "']/tbody/tr[" + i + "]/td[1]")).getText();
				}
			}
		}
		assertArrayEquals("La taula no s'ha ordenat correctament", elementOrdenats, elements);
	}
	
	protected boolean isDate(String fecha, String pattern) {
		if (fecha == null)  
			return false;  
			  
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		  
		if (fecha.trim().length() != dateFormat.toPattern().length())  
			return false;  
		  
		dateFormat.setLenient(false);  
		  
		try {  
			dateFormat.parse(fecha.trim());  
		}  
		catch (ParseException pe) {  
			return false;  
		}  
		return true;
	}

	protected void consultarExpedientes(String numExpediente, String tituloExpediente, String tipusExp) {
		consultarExpedientes(numExpediente, tituloExpediente, tipusExp, false);
	}
	
	protected void consultarExpedientes(String numExpediente, String tituloExpediente, String tipusExp, boolean mostrarAnulats) {
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']//a[contains(@href, '/expedient/consulta.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='titol0']")).clear();
		if (tituloExpediente != null)
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(tituloExpediente);
		
		driver.findElement(By.xpath("//*[@id='numero0']")).clear();
		if (numExpediente != null)
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numExpediente);
		
		if (tipusExp != null) {
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
			List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(tipusExp)) {
					option.click();
					break;
				}
			}
		}
		
		if (mostrarAnulats) {
			// Seleccionamos los activos y anulados
			WebElement select = driver.findElement(By.xpath("//*[@id='mostrarAnulats0']"));
			List<WebElement> options = select.findElements(By.tagName("option"));
			options.get(2).click();	
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();
	}
	
	protected void consultarTareas(String tasca, String expediente, String tipusExp, boolean grupo) throws InterruptedException {
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		if (grupo)
			actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']//a[@href='/helium/tasca/grupLlistat.html']")));
		else
			actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']//a[@href='/helium/tasca/personaLlistat.html']")));
		actions.click();
		actions.build().perform();
		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		if (tasca != null)
			driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys(tasca);
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();
		if (expediente != null)
			driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(expediente);
		
		if (tipusExp != null) {
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
			List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(tipusExp)) {
					option.click();
					break;
				}
			}
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/2.png");
		
		Thread.sleep(1000*5);
		driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
	}
	
	public void desplegarDP(String nomDefProc, String pathDefProc, String tipusExp){
		desplegarDP(nomDefProc, pathDefProc, tipusExp, TipusDesplegament.JBPM);
	}
	
	public void desplegarDP(String nomDefProc, String pathDefProc, String tipusExp, TipusDesplegament td) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
		actions.click();
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.id("tipus0")));
		if (nomDefProc != null) {
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipus0']"));
			List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(td)) {
					option.click();
					break;
				}
			}
		}
		if (tipusExp != null) {
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipusId0']"));
			List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(tipusExp)) {
					option.click();
					break;
				}
			}
		}
		driver.findElement(By.xpath("//*[@id='arxiu0']")).sendKeys(pathDefProc);
		driver.findElement(By.xpath("//*[@id='command']/div/div[7]/button[1]")).click();
	}
	
	//Marca una defició de proces com a procés inicial del tipus d´expedient
	public void marcarDefinicioProcesInicial(String codiTipusExp, String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+codiTipusExp+"')]")).click();
		
		driver.findElement(By.xpath("//a[contains(@href, 'expedientTipus/definicioProcesLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]//button[@type='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut marcar el procés com a inicial");
	}
	
	public void crearConsultaInforme(String codi,String titol, String tipusExp,String jrxml) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
	}

	public void crearEstatTipusExpedient(String codTipusExp, String codiEstat, String nomEstat) {
		crearEstatTipusExpedient(codTipusExp, codiEstat, nomEstat, null);
	}
	
	public void crearEstatTipusExpedient(String codTipusExp, String codiEstat, String nomEstat, String prefixeScreenShot) {
		
		String botoCrearEstat	= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Afegir']";
		
		accedirEstatsExpedient(codTipusExp);
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_estats_inicials.png"); }
		
		driver.findElement(By.id("codi0")).sendKeys(codiEstat);
		driver.findElement(By.id("nom0")).sendKeys(nomEstat);
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_dades_omplertes.png"); }
		
		driver.findElement(By.xpath(botoCrearEstat)).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_resultat_insercio.png"); }
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut creat l'estat per el tipus d´expedient.");
	}
	
	public void accedirEstatsExpedient(String codTipusExp) {

		String xPathLinkTipExp = "//*[@id='registre']/tbody/tr/td[contains(a, '"+codTipusExp+"')]/a";
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));

		actions.build().perform();

		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/expedientTipus/llistat.html')]")));

		actions.click();

		actions.build().perform();

		existeixElementAssert(xPathLinkTipExp, "El tipus de expedient no existeix.");

		driver.findElement(By.xpath(xPathLinkTipExp)).click();

		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/estats.html')]")).click();
	}
	
	public void eliminarEstatTipusExpedient(String codTipusExp, String codiEstat) {
		
		accedirEstatsExpedient(codTipusExp);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "L´estat no existeix per al tipus de expedient.");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado.");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")).click();
		
		if (isAlertPresent()) { acceptarAlerta(); }
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut esborrar l'estat per el tipus d´expedient.");
	}
	
	public void eliminarTotsEstatsTipusExpedient(String codTipusExp) {
		eliminarTotsEstatsTipusExpedient(codTipusExp, null);
	}
	
	public void eliminarTotsEstatsTipusExpedient(String codTipusExp, String prefixeScreenShot) {
		
		accedirEstatsExpedient(codTipusExp);
		
		int contadorScreenShots = 1;
		while(existeixElement("//*[@id='registre']/tbody/tr")) {
			
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_"+contadorScreenShots+"_1_abans.png"); }
			
			existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img", "No tenía permisos de borrado.");
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")).click();
			
			if (isAlertPresent()) { acceptarAlerta(); }
			
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_"+contadorScreenShots+"_2_despres.png"); }
			
			existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut esborrar l'estat per el tipus d´expedient.");
			
			contadorScreenShots++;
		}
	}
	
	protected void comprobarVariable(VariableExpedient variable, boolean esModal) {
		comprobarVariable(variable, esModal, true);
	}
	
	protected void comprobarVariable(VariableExpedient variable, boolean esModal, boolean validar) {
		existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']","La variable no oculta no se ha mostrado : " + variable.getEtiqueta());
		if (validar && variable.isReadOnly()) {
			// Es readonly
			existeixElementAssert("//*[@id='commandReadOnly']/div/div/label[contains(text(), '"+variable.getEtiqueta()+"')]","La variable readonly no se ha mostrado : " + variable.getEtiqueta());
		} else {
			// No es readonly
			existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']", "La etiqueta no coincidia: " + variable.getEtiqueta());
			
			if (!esModal) {
				if (validar) {
					if (variable.isObligatorio()) {
						existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable no estaba como obligatoria : " + variable.getEtiqueta());
					} else {
						noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/*/em/img", "La variable estaba como obligatoria : " + variable.getEtiqueta());
					}
				}
				
				if (!"REGISTRE".equals(variable.getTipo())) {
					if (variable.isMultiple() && !esModal) {
						existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable no contenía el botón de múltiple : " + variable.getEtiqueta());
					} else {
						noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]", "La variable contenía el botón de múltiple : " + variable.getEtiqueta());
					}	
				}
				if (variable.getObservaciones() != null && !variable.getObservaciones().isEmpty()) {
					existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable debe mostrar observaciones : " + variable.getEtiqueta());
					String obs = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']")).getText();
					assertTrue("La observación no coincidía : " + variable.getEtiqueta(), obs.equals(variable.getObservaciones()));
				} else {
					noExisteixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/p[@class='formHint']","La variable no debe mostrar observaciones : " + variable.getEtiqueta());
				}
			}
			if ("STRING".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("Texto 1 " + variable.getEtiqueta());
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("Texto 1 " + variable.getEtiqueta());
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("Texto 2 " + variable.getEtiqueta());
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("Texto 1 " + variable.getEtiqueta());
				}
			} else if ("INTEGER".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("1234");
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("12345");
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("1234");
				}
			} else if ("FLOAT".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
								
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("1234");
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("12345");
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("1234");
				}
			} else if ("BOOLEAN".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']", "No tenía un checkbox: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@type='checkbox']")).click();
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).click();
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("Si");
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("Si");
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("Si");
				}
			} else if ("TEXTAREA".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea", "No tenía un textarea: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/textarea")).sendKeys("Texto 1 " + variable.getEtiqueta());
				variable.setValor("Texto 1 " + variable.getEtiqueta());
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("Texto 2 " + variable.getEtiqueta());
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("Texto 1 " + variable.getEtiqueta());
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("Texto 2 " + variable.getEtiqueta());
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("Texto 1 " + variable.getEtiqueta());
				}
			} else if ("DATE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput hasDatepicker']")).sendKeys("13/11/2014");
				
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("14/12/2014");
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("13/11/2014");
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("14/12/2014");
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("13/11/2014");
				}
			} else if ("PRICE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys("1234");
								
				if (variable.isMultiple() && !esModal) {
					driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/button[contains(text(), '+')]")).click();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).clear();
					driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"1']")).sendKeys("12345");
					
					VariableExpedient var1 = new VariableExpedient();
					var1.setValor("12,34");
					variable.getRegistro().add(var1);
					VariableExpedient var2 = new VariableExpedient();
					var2.setValor("123,45");
					variable.getRegistro().add(var2);
				} else {
					variable.setValor("12,34");
				}
			} else if ("TERMINI".equals(variable.getTipo())) {
				boolean cond = driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/select")).size() == 2
						&& driver.findElements(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).size() == 1;
				assertTrue("El termini no era correcto : " + variable.getEtiqueta(), cond);
				WebElement select1 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[1]/label/select"));
				List<WebElement> options1 = select1.findElements(By.tagName("option"));
				options1.get(options1.size()-1).click();
				WebElement select2 = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li[2]/label/select"));
				List<WebElement> options2 = select2.findElements(By.tagName("option"));
				options2.get(options2.size()-1).click();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/ul/li/label/input")).sendKeys("2");
				variable.setValor("12 anys, 12 mesos i 2 dies");
			} else if ("SELECCIO".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select", "No tenía un select: " + variable.getEtiqueta());
				WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/select"));
				List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
				options.get(options.size()-1).click();
				variable.setValor(options.get(options.size()-1).getText());
			} else if ("SUGGEST".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']", "No tenía un input: " + variable.getEtiqueta());
				String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).clear();
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/input[@class='textInput']")).sendKeys(usuari);
				variable.setValor(usuari);
				driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();
			} else if ("REGISTRE".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
				driver.findElement(By.xpath("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button")).click();
				
				String url = driver.findElement(By.xpath("//*[@id='"+variable.getCodi()+"']")).getAttribute("src");
				url = url.substring(url.indexOf(".html"));
				if (modalObertaAssert(url)) {
					vesAModal(url);
					
					for (VariableExpedient variableReg : variable.getRegistro()) {
						comprobarVariable(variableReg, true);
					}
					
					driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
					tornaAPare();
				}
			} else if ("ACCIO".equals(variable.getTipo())) {
				existeixElementAssert("//*[@class='ctrlHolder'][*/text()='"+variable.getEtiqueta()+"']/div/button", "No tenía un botón: " + variable.getEtiqueta());
			}
		}

		screenshotHelper.saveScreenshot("TasquesDadesTasca/visualizacio_tasca_dades/4-"+variable.getCodi()+".png");
	}
	
	protected void esperaFinExecucioMassiva() throws InterruptedException {
		esperaFinExecucioMassiva(null);
	}
	
	protected void esperaFinExecucioMassiva(List<String[]> expedientes) throws InterruptedException {
		final int esperaMax = 1000*60*2;
		int espera = esperaMax;
		if (existeixElement("//*[@id='botoMassiu']")) {
			if (expedientes == null) {
				// Supondremos que es la última
				while (espera > 0 && !terminadaUltimaExecucioMassiva()) {
					espera -= 1000*15;
					Thread.sleep(1000*15);
				};
			} else {
				for (String[] expedient : expedientes) {
					while (espera > 0 && !finalizadoExpedientExecucioMassiva(expedient))  {
						espera -= 1000*15;
						Thread.sleep(1000*15);
					};
				}
			}
		} else {
			// No se tenían permisos
			Thread.sleep(espera);
		}
		
		actions.sendKeys(Keys.ESCAPE);
	}
	
	private boolean finalizadoExpedientExecucioMassiva(String[] expedient) throws InterruptedException {
		driver.findElement(By.xpath("//*[@id='botoMassiu']")).click();
		for (WebElement fila : driver.findElements(By.xpath("//span[contains(@class,'ui-icon-triangle-1-')]"))) {
			if (fila.getAttribute("class").contains("ui-icon-triangle-1-e"))
				fila.click();
			noExisteixElementAssert("//tbody/tr[contains(td/text(),'"+expedient[1]+"')]//img[contains(@src,'/img/mass_error.png')]", "El expediente '"+expedient[1]+"' dio error");
			return existeixElement("//tbody/tr[contains(td/text(),'"+expedient[1]+"')]//img[contains(@src,'/img/mass_fin.png')]");
		}
		
		return false;
	}
	
	private boolean terminadaUltimaExecucioMassiva() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id='botoMassiu']")).click();
		driver.findElements(By.xpath("//span[contains(@class,'ui-icon-triangle-1-')]")).get(0).click();
		
		int expedientes = driver.findElements(By.xpath("//*[@id='accordio_massiva']//tbody//tr")).size();
		int terminados = driver.findElements(By.xpath("//*[@id='accordio_massiva']//tbody//img[contains(@src,'/img/mass_fin.png')]")).size();
		noExisteixElementAssert("//*[@id='accordio_massiva']//tbody//img[contains(@src,'/img/mass_error.png')]", "Algún expediente dio error");
		
		return expedientes == terminados;
	}
	
	protected int estadoExpedientExecucioMassiva(String expedient) throws InterruptedException {
		// -1 : No encontrado
		//  0 : Pendiente
		//  1 : Finalizado
		//  2 : Error
		int estado = -1;
		driver.findElement(By.xpath("//*[@id='botoMassiu']")).click();
		for (WebElement fila : driver.findElements(By.xpath("//span[contains(@class,'ui-icon-triangle-1-')]"))) {
			if (fila.getAttribute("class").contains("ui-icon-triangle-1-e"))
				fila.click();			
			if (existeixElement("//tbody/tr[contains(td/text(),'"+expedient+"')]//label")) {
				if (existeixElement("//tbody/tr[contains(td/text(),'"+expedient+"')]//img[contains(@src,'/img/mass_fin.png')]")) {
					estado = 1;
				} else if (existeixElement("//tbody/tr[contains(td/text(),'"+expedient+"')]//img[contains(@src,'/img/mass_error.png')]")) {
					estado = 2;
				} else if (existeixElement("//tbody/tr[contains(td/text(),'"+expedient+"')]//img[contains(@src,'/img/mass_pend.png')]")) {
					estado = 0;
				}
				break;
			}
		}
		actions.sendKeys(Keys.ESCAPE);
		return estado;
	}

	// ---------------------------------------------
	// Tipus d´expedient >> Dominis
	// ---------------------------------------------
	
	protected void emplenaDadesDominiSQL(String codi, String nom, String segonsCache, String desc, String jndi, String sql) {
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("CONSULTA_SQL".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("cacheSegons0")).clear();
		driver.findElement(By.id("cacheSegons0")).sendKeys(segonsCache);
		
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys(desc);
		
		driver.findElement(By.id("jndiDatasource0")).clear();
		driver.findElement(By.id("jndiDatasource0")).sendKeys(jndi);
		
		driver.findElement(By.id("sql0")).clear();
		driver.findElement(By.id("sql0")).sendKeys(sql);
	}
	
	protected void emplenaDadesDominiWS(String codi, String nom, String segonsCache, String desc, String url, String tipusAuth, String origenCred, String usu, String pass) {
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("CONSULTA_WS".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("cacheSegons0")).clear();
		driver.findElement(By.id("cacheSegons0")).sendKeys(segonsCache);
		
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys(desc);
		
		driver.findElement(By.id("url0")).clear();
		driver.findElement(By.id("url0")).sendKeys(url);
		
		for (WebElement option : driver.findElement(By.id("tipusAuth0")).findElements(By.tagName("option"))) {
			if (tipusAuth.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		for (WebElement option : driver.findElement(By.id("origenCredencials0")).findElements(By.tagName("option"))) {
			if (origenCred.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("usuari0")).clear();
		driver.findElement(By.id("usuari0")).sendKeys(usu);
		
		driver.findElement(By.id("contrasenya0")).clear();
		driver.findElement(By.id("contrasenya0")).sendKeys(pass);
	}
	
	// ---------------------------------------------
	// Tipus d´expedient >> Consultes
	// ---------------------------------------------
	
	protected void emplenaDadesConsulta(String codi, String titol, String descripcio, String path, String formatExp, String valorPred, boolean isExpActiu, boolean isDesactivar) {
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(titol);
		
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys(descripcio);
		
		driver.findElement(By.id("informeContingut0")).sendKeys(path);
		
		for (WebElement option : driver.findElement(By.id("formatExport0")).findElements(By.tagName("option"))) {
			if (formatExp.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("valorsPredefinits0")).clear();
		driver.findElement(By.id("valorsPredefinits0")).sendKeys(valorPred);
		
		if (isExpActiu && !driver.findElement(By.id("exportarActiu0")).isSelected()) {
			driver.findElement(By.id("exportarActiu0")).click();
		}
		
		if (isDesactivar && !driver.findElement(By.id("ocultarActiu0")).isSelected()) {
			driver.findElement(By.id("ocultarActiu0")).click();
		}
	}
	
	protected void comprovaDadesConsulta(String codi, String titol, String descripcio, String path, String formatExp, String valorPred, boolean isExpActiu, boolean isDesactivar) {
		
		if (!codi.equals(driver.findElement(By.id("codi0")).getAttribute("value"))) { fail("El codi de la consulta ("+codi+") del tipus d´expedient no s´ha modificat!"); }
		if (!titol.equals(driver.findElement(By.id("nom0")).getAttribute("value"))) { fail("El titol de la consulta ("+titol+") del tipus d´expedient no s´ha modificat!"); }
		if (!descripcio.equals(driver.findElement(By.id("descripcio0")).getAttribute("value"))) { fail("La descripcio0 de la consulta ("+descripcio+") del tipus d´expedient no s´ha modificat!"); }
		if (!path.equals(driver.findElement(By.id("informeContingut0")).getAttribute("value"))) { fail("El path de la consulta ("+path+") del tipus d´expedient no s´ha modificat!"); }
		if (!formatExp.equals(driver.findElement(By.id("formatExport0")).getAttribute("value"))) { fail("El format de exportacio de la consulta ("+formatExp+") del tipus d´expedient no s´ha modificat!"); }
		if (!valorPred.equals(driver.findElement(By.id("valorsPredefinits0")).getAttribute("value"))) { fail("El valors predet. de la consulta ("+valorPred+") del tipus d´expedient no s´ha modificat!"); }

		if (isExpActiu ^ driver.findElement(By.id("exportarActiu0")).isSelected()) {
			fail("El check de Exportar Actiu de la cosulta amb codi " + codi + " hauria de tenir marcada la opció: "+ isExpActiu);
		}
		
		if (isDesactivar ^ driver.findElement(By.id("ocultarActiu0")).isSelected()) {
			fail("El check de Exportar Actiu de la cosulta amb codi " + codi + " hauria de tenir marcada la opció: "+ isExpActiu);
		}
	}
	
	protected void afegirParametreAconsulta (String codTipusExp, String codiCons, String codiParam, String descParam, String tipusParam) {
		afegirParametreAconsulta (codTipusExp, codiCons, codiParam, descParam, tipusParam, null);
	}
	
	protected void afegirParametreAconsulta (String codTipusExp, String codiCons, String codiParam, String descParam, String tipusParam, String prefixeScreenShot) {
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/consultaLlistat.html')]")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_pipella_consultes.png"); }
		
		String botonParametres  = "//*[@id='registre']/tbody/tr[contains(td/a, '"+codiCons+"')]/td/form/button[contains(text(), 'arams')]";
		driver.findElement(By.xpath(botonParametres)).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_form_param_consulta.png"); }
		
		driver.findElement(By.id("codi0")).sendKeys(codiParam);
		
		driver.findElement(By.id("descripcio0")).sendKeys(descParam);
		
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if (tipusParam.equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_dades_emplenades.png"); }
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/button[1]")).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_4_resultat_asignacio_param_consulta.png"); }
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al insertar el parametre de una consulta per el tipus d´expedient "+codTipusExp+".");
	}
	
	protected void crearRol(String codiRol, String descRol) {
		crearRol(codiRol, descRol, null);
	}
	
	protected void crearRol(String codiRol, String descRol, String prefixeScreenShot) {
		
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/rol/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).clear();
			driver.findElement(By.id("codi0")).sendKeys(codiRol);
			driver.findElement(By.id("descripcio0")).clear();
			driver.findElement(By.id("descripcio0")).sendKeys(descRol);
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_emplenant_rol.png"); }
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]", "No s'ha pogut crear el rol");
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_rol_guardat.png"); }
		}else{
			if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_rol_ja_existent.png"); }
		}
	}
	
	protected void eliminarRol(String codiRol) {
		eliminarRol(codiRol, null);
	}
	
	protected void eliminarRol(String codiRol, String prefixeScreenshot) {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/rol/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (prefixeScreenshot!=null && !"".equals(prefixeScreenshot)) { screenshotHelper.saveScreenshot(prefixeScreenshot+"_llista_de_rols.png"); }		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td/a, '"+codiRol+"')]/td/a[contains(@href, '/rol/delete.html')]")).click();
			if (isAlertPresent()) {acceptarAlerta();}
			if (prefixeScreenshot!=null && !"".equals(prefixeScreenshot)) { screenshotHelper.saveScreenshot(prefixeScreenshot+"_rol_eliminat_ok.png"); }
			existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut eliminar el rol ("+codiRol+").");
		}else{
			if (prefixeScreenshot!=null && !"".equals(prefixeScreenshot)) { screenshotHelper.saveScreenshot(prefixeScreenshot+"_rol_no_existeix.png"); }
		}
	}
	
	protected void desplegarArxiuHeliumTipusExpedient (String codTipusExp, String x_pestanyaDefProc, String x_botoDesplegarArxiu, String defProcHELIUMPath, String etiquetaDoc, String x_botoDesplegarArxiu2) {
		desplegarArxiuHeliumTipusExpedient (codTipusExp, x_pestanyaDefProc, x_botoDesplegarArxiu, defProcHELIUMPath, etiquetaDoc, x_botoDesplegarArxiu2, null);
	}
	
	protected void desplegarArxiuHeliumTipusExpedient (String codTipusExp, String x_pestanyaDefProc, String x_botoDesplegarArxiu, String defProcHELIUMPath, String etiquetaDoc, String x_botoDesplegarArxiu2, String prefixeScreenShot) {
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(x_pestanyaDefProc)).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_1_pipella_defproces.png"); }
		
		driver.findElement(By.xpath(x_botoDesplegarArxiu)).click();
		
		//Seleccionar Tipus d´arxiu
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("EXPORT".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("arxiu0")).sendKeys(defProcHELIUMPath);
		
		driver.findElement(By.id("etiqueta0")).sendKeys(etiquetaDoc);
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_2_dades_arxiu.png"); }
		
		driver.findElement(By.xpath(x_botoDesplegarArxiu2)).click();
		
		if (prefixeScreenShot!=null && !"".equals(prefixeScreenShot)) { screenshotHelper.saveScreenshot(prefixeScreenShot+"_3_resultat_importacio.png"); }
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar la definicio de proces (helium) dins el tipus d'expedient.");
	}
	
	/**
	 *   F U N C I O N S   D E   C O N F I G U R A C I O   D E   L A   A P L I C A C I O
	 */
	
	protected void accedirConfiguracioPersones() {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/persona/consulta.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	protected void accedirConfiguracioRols() {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/rol/llistat.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	protected void accedirConfiguracioFestius() {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/festiu/calendari.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	protected void accedirConfiguracioReassignacions() {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/reassignar/llistat.html')]")));
		actions.click();
		actions.build().perform();
	}
	
	protected void crearNovaPersona(String codi, String nom, String llinatge, String llinatge_dos, String dni, String mail, String sexe, boolean accesApp) {
		
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("llinatge10")).sendKeys(llinatge);
		if (llinatge_dos!=null) { driver.findElement(By.id("llinatge20")).sendKeys(llinatge_dos); }
		if (dni!=null) { driver.findElement(By.id("dni0")).sendKeys(dni); }
		driver.findElement(By.id("email0")).sendKeys(mail);
		
		for (WebElement option : driver.findElement(By.id("sexe0")).findElements(By.tagName("option"))) {
			if (sexe.equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		if (accesApp && !driver.findElement(By.id("login0")).isSelected()) {
			driver.findElement(By.id("login0")).click();
		}
		
		if (!accesApp && driver.findElement(By.id("login0")).isSelected()) {
			driver.findElement(By.id("login0")).click();
		}
		
		String pathBotoNovaPersona = "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Crear']";
		driver.findElement(By.xpath(pathBotoNovaPersona)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut crear la persona ("+nom +" "+llinatge+") a la configuració de Helium.");
	}
	
	protected void filtraPersones(String codi, String nom, String email) {
		
		driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("email0")).clear();
		driver.findElement(By.id("email0")).sendKeys(email);
		
		String pathBotoNovaPersona = "//*[@id='command']/div/div[@class='buttonHolder']/button[text() = 'Consultar']";
		driver.findElement(By.xpath(pathBotoNovaPersona)).click();
	}
}