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
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
			acceptarAlerta();
			actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]")));
			actions.click();
			actions.build().perform();
			noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]", "No s'ha pogut eliminar l'entorn de proves");
		}
	}
	
	// DEFINICIÓ DE PROCÉS
	// ............................................................................................................	
	protected enum TipusDesplegament {
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (tipExp) {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][text() = '" + nomTipusExp + "']]", "defproces/importPar/tipusExp/3_definicionsExistents.png", "No s'ha pogut importar la definició de procés a nivell de tipus d'expedient");
		} else {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "No s'ha pogut importar la definició de procés a nivell global");
		}
		if (versio != null) {
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[2][text() = '" + (versio + 1) + "']]", "No s'ha actualitzat la versió de la definició de procés");
		}
	}
	
	protected void seleccionarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "No s'ha trobat la defició de procés de prova");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();
	}

	
	protected void eliminarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
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
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
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
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();	
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
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
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/domini/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]/td[5]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiDomini + "')]", "No s'han pogut eliminar el domini");
	}
	
	// TIPUS D'EXPEDIENT
	// ............................................................................................................	
	protected void crearTipusExpedient(String nom, String codi) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if (noExisteixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]")) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys(codi);
			driver.findElement(By.id("nom0")).sendKeys(nom);
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			actions.moveToElement(driver.findElement(By.id("menuDisseny")));
			actions.build().perform();
			actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
			actions.click();
			actions.build().perform();
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "No s'ha pogut crear el tipus d'expedient de test");
		}
	}
	
	protected void seleccionarTipExp(String codTipExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]", "No s'ha trobat el tips d'expedient");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[1]/a")).click();
	}
	
	protected void eliminarTipusExpedient(String codiTipusExp) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		if(existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codiTipusExp + "')]/td[4]/a")).click();
			acceptarAlerta();
		}
	}

	protected void importarDadesTipExp(String codiTipusExp, String path) {		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+codiTipusExp+"')]")).click();

		// Deploy
		driver.findElement(By.xpath("//*[@id='content']/div[2]/h3/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar la definició de procés de test");
	}
	
	
	// EXPEDIENT
	// ............................................................................................................
	protected void importarDadesDefPro(String defProc, String path) {		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+defProc+"')]")).click();

		// Deploy
		driver.findElement(By.xpath("//*[@id='content']/div[1]/h3/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar los datos de la definició de procés de '"+defProc+"' de la ruta '"+path+"'");
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
				
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]", "No s'ha trobat el tipus d'expedient");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
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
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]")));
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
			}
		}
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]", "No s'han pogut assignar permisos");
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
		FileDownloader downloader = new FileDownloader(driver);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(formXpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("El formulari indicat no existeix" , isPresent);
		WebElement form = driver.findElement(By.xpath(formXpath));
		String formAction = form.getAttribute("action");
		if (formAction == null || "".equals(formAction))
			fail("El formulari té una acció definida");
	
		List<WebElement> inputs = form.findElements(By.tagName("input"));
		List<NameValuePair> params = new ArrayList<NameValuePair>(inputs.size());
		for (WebElement input: inputs) {
			params.add(new BasicNameValuePair(input.getAttribute("name"), input.getAttribute("value")));
		}
		
		byte[] downloadedFile = null;
		try {
			downloadedFile = downloader.postDownloadFile(formAction, params);
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
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']//a[contains(@href, '/helium/expedient/consulta.html')]")));
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
}
