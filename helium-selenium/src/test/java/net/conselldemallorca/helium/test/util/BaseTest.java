package net.conselldemallorca.helium.test.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
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
	protected static ScreenshotHelper screenshotHelper;
	protected static Actions actions;

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

	protected void eliminarExpediente(String tituloExpediente) {
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}		
		
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();		
		assertTrue("No se encontró el expediente", driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + tituloExpediente + "')]")) != null);			
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + tituloExpediente + "')]/td[8]/a/img")).click();		
		acceptarAlerta();		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut borrar el expediente");
	}
	
	protected void desplegarDatosExp(String nomVarExp, String path) {		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		for (WebElement option : driver.findElement(By.id("registre")).findElements(By.tagName("a"))) {
			if (option.getText().equals(nomVarExp)) {
				option.click();
				break;
			}
		}

		// Deploy
		driver.findElement(By.xpath("//*[@id='content']/div[1]/h3/img")).click();
		driver.findElement(By.id("arxiu0")).sendKeys(path);
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut importar la definició de procés de test");
	}
}
