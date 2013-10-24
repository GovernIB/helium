package net.conselldemallorca.helium.test.util;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.InicialitzarTests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import com.thoughtworks.selenium.Selenium;

public class BaseTest {

	protected static WebDriver driver;
	protected static Selenium selenium;
	protected static Properties properties;
	protected static String entorn;
	protected static String baseUrl;
	protected static ScreenshotHelper screenshotHelper;
	protected static Actions actions;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		properties = new Properties();
		properties.load(InicialitzarTests.class.getResourceAsStream("test.properties"));

		baseUrl = getProperty("test.base.url.configuracio");
		entorn = getProperty("entorn.nom");
	}

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void IEconfiguration() throws InterruptedException {
		if (getProperty("webdriver.ie").equals("true")) {
			System.setProperty("webdriver.ie.driver", getProperty("webdriver.ie.driver"));
			driver = new InternetExplorerDriver();
			driverConfig("IE");
			runTests();
		}
	}

	@Test
	public void FFconfiguration() throws InterruptedException {
		if (getProperty("webdriver.ff").equals("true")) {
			driver = new FirefoxDriver();
			driverConfig("FF");
			runTests();
		}
	}

	@Test
	public void CRconfiguration() throws InterruptedException {
		if (getProperty("webdriver.chrome").equals("true")) {
			System.setProperty("webdriver.chrome.driver", getProperty("webdriver.chrome.driver"));
			driver = new ChromeDriver();
			driverConfig("CR");
			runTests();
		}
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	protected void driverConfig(String prefix) {
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.get(baseUrl);
		screenshotHelper = new ScreenshotHelper(driver, prefix);
	}

	protected void runTests() throws InterruptedException {
		actions = new Actions(driver);
	}

	protected static String getProperty(String nom) {
		String resultado;

		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			resultado = properties.getProperty(nom + ".windows");
		} else {
			resultado = properties.getProperty(nom + ".linux");
		}
		if (resultado == null) {
			resultado = properties.getProperty(nom);
		}
		return resultado;
	}
}
