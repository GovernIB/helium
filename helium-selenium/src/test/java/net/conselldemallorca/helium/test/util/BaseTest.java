package net.conselldemallorca.helium.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.By;
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
			String pass = properties.getProperty("test.base.usuari.configuracio");
			driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
			driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
			driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
		}
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
	protected void acceptarAlerta() {
		try {
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
	protected void crearEntorn(String entorn, String titolEntorn) {
		entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
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
		//Entorn actual per defecte
		if (entornActual != null) {
			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
			if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]") &&
					!driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a/img")).getAttribute("src").endsWith("star.png")) {
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a")).click();
			}
		}
		
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
	protected void seleccionarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "No s'ha trobat la defició de procés de prova");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();
	}
	
	protected void desplegarDefinicioProcesEntorn(String nomDefProc, String pathDefProc) {
		desplegarDefinicioProcesEntorn(null, nomDefProc, pathDefProc);
	}
	
	protected void desplegarDefinicioProcesEntorn(String tipusExpedient, String nomDefProc, String pathDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]")));
		actions.click();
		actions.build().perform();
		
		// tipus d'expedient
		if (tipusExpedient != null) {
			for (WebElement option : driver.findElement(By.id("expedientTipusId0")).findElements(By.tagName("option"))) {
				if (option.getText().equals(tipusExpedient)) {
					option.click();
					break;
				}
			}
		}
		
		// Deploy
		driver.findElement(By.xpath("//option[@value='JBPM']")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(pathDefProc);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (tipusExpedient == null)
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "') and td[3][not(text())]]", "No s'ha pogut importar la definició de procés de test");
		else
			existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]", "No s'ha pogut importar la definició de procés de test");
	}
	
	protected void eliminarDefinicioProces(String nomDefProc) {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();

		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[4]/a")).click();
			acceptarAlerta();
		}
	}
	
	// TIPUS D'EXPEDIENT
	// ............................................................................................................	
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
	
	
	// EXPEDIENT
	// ............................................................................................................
	protected void importarDadesExpedient(String defProc, String path) {		
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
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar la definició de procés de test");
	}
	
	protected void eliminarExpedient(String numExpediente, String tituloExpediente) {
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='titol0']")).clear();
		if (tituloExpediente != null)
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(tituloExpediente);
		
		driver.findElement(By.xpath("//*[@id='numero0']")).clear();
		if (numExpediente != null)
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numExpediente);
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();
		
		assertTrue("No se encontró el expediente", driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]")) != null);			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[contains(a/img/@src,'/helium/img/cross.png')]/a/img")).click();		
		
		acceptarAlerta();		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut borrar el expediente");
	}

	protected String[] iniciarExpediente(String defProc, String codTipusExp, String numero, String titulo) {
		String[] res = new String[2];
		
		existeixElementAssert("//li[@id='menuIniciar']", "No tiene permisos para iniciar un expediente");
		driver.findElement(By.xpath("//*[@id='menuIniciar']/a")).click();
		
		// Obtenir nom del tipus d'expedient i cercar-lo
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		assertTrue("No s'ha trobat el tipus d'expedient", isPresent);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipusExp + "')]/td[3]/form/button")).click();
		
		if (driver.findElements(By.xpath("//*[@id='numero0']")).size() > 0 && numero != null) {
			driver.findElement(By.xpath("//*[@id='numero0']")).clear();
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
		} else {
			numero = null;
		}
		if (driver.findElements(By.xpath("//*[@id='titol0']")).size() > 0 && titulo != null) {
			driver.findElement(By.xpath("//*[@id='titol0']")).clear();
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
		} else {
			titulo = null;
		}
		driver.findElement(By.xpath("//button[@value='submit']")).click();
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
		
		existeixElementAssert("//*[@id='infos']/p", "No se inició el expediente");
		
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
}
