package net.conselldemallorca.helium.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

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
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

@Ignore("Classe base per a la execució dels tests amb Selenium")
public abstract class BaseAbstractTest {

	protected static WebDriver driver;
	protected static Properties properties;
	protected static String browser;
	protected static String baseUrl;
	protected static boolean seycon;
	protected static ScreenshotHelper screenshotHelper;
	protected static Actions actions;
	private static final int TIMEOUT_PRESENT = 20*1000;
	
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
		properties.load(BaseAbstractTest.class.getResourceAsStream("test.properties"));
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
//			plugin.state.java = 0 --> never activate
//			plugin.state.java = 1 --> ask to activate
//			plugin.state.java = 2 --> always activate
			
			String binari = properties.getProperty("webdriver.firefox.binary");
			if (binari != null) {
				File pathToBinary = new File(binari);
				FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
				driver = new FirefoxDriver(ffBinary, fp);
			} else {
				driver = new FirefoxDriver(fp);
			}
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
		
		seycon = "true".equals(properties.getProperty("test.base.url.inicio.seycon"));
		if (seycon) {
			String user = properties.getProperty("test.base.usuari.disseny");
			String pass = properties.getProperty("test.base.usuari.disseny.pass");
			driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
			driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
			driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
		}
		//existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
	}
	protected void carregarUrlFeina() {
		baseUrl = properties.getProperty("test.base.url.feina");
		assertNotNull("Url base no configurada al fitxer de properties", baseUrl);
		driver.get(baseUrl);
		
		seycon = "true".equals(properties.getProperty("test.base.url.inicio.seycon"));
		if (seycon) {
			String user = properties.getProperty("test.base.usuari.feina");
			String pass = properties.getProperty("test.base.usuari.feina.pass");
			driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
			driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
			driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
		}
		//existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
	}

	// Funcions d'ajuda
	protected boolean existeixElementAssert(String xpath, String msgNotFound) {
		return comprovaElement(xpath, null, 0L, msgNotFound, true, false);
	}
	protected boolean existeixElementAssert(String xpath, String msgNotFound, boolean continuar) {
		return comprovaElement(xpath, null, 0L, msgNotFound, true, continuar);
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
		boolean isPresent = false;
		try {
			long temps_esperat = new Date().getTime();
			while (!isPresent && (new Date().getTime()-temps_esperat) < TIMEOUT_PRESENT) {				
				if (driver.findElements(By.xpath(xpath)).size() > 0) {
					isPresent = true;
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
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
			Thread.sleep(5000);
			driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        } catch (InterruptedException e) {
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
			Thread.sleep(1000);
			driver.switchTo().alert().sendKeys(msg);
			Thread.sleep(1000);
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

	protected byte[] downloadFile(String xpath, String fitxer) {
		
		//System.setProperty ("jsse.enableSNIExtension", "false");
		
		FileDownloader downloader = new FileDownloader(driver);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(xpath)).size() > 0;
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("Enllaç al fitxer " + fitxer + " no existeix" , isPresent);
		
		byte[] downloadedFile = null;
		
		try {
			downloadedFile = downloader.downloadFile(driver.findElement(By.xpath(xpath)));
		} catch (SSLHandshakeException certEx) {
			certEx.printStackTrace();
			fail("No s'ha pogut descarregar el fitxer " + fitxer + ". Error de certificat.");
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

//			logger.debug("** --> " + getMD5Checksum(downloadedFile));
			
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
		
		//System.setProperty("jsse.enableSNIExtension", "false");
		
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
			//downloadedFile = downloader.postDownloaderWithRedirects(formAction, params);
			downloadedFile = downloader.postDownloadFile(formAction, params);
		} catch (Exception e) {
			e.printStackTrace();
			fail("No s'ha pogut descarregar l'arxiu. Error: " + e.getMessage());
		}
		
	
		assertThat(downloader.getHTTPStatusOfLastDownloadAttempt(), is(equalTo(200)));
		
		return downloadedFile;
	}
	
	protected void postDownloadFileHash(String formXpath, String md5) {
		byte[] downloadedFile = postDownloadFile(formXpath);
		
		try {	
//			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
//	        byte[] array = md.digest(downloadedFile);
//	        StringBuffer sb = new StringBuffer();
//	        for (int i = 0; i < array.length; ++i) {
//	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
//	       }
	        
			CheckFileHash fileToCheck = new CheckFileHash();
			fileToCheck.fileToCheck(downloadedFile);
			fileToCheck.hashDetails(md5, HashType.MD5);
			assertThat("El fitxer descarregat no té el hash esperat", fileToCheck.hasAValidHash(), is(equalTo(true)));
		} catch (Exception e) {
			fail("No s'ha pogut comprovar el fitxer descarregat");
		}
	}
	
	public static byte[] createChecksum(String filename) throws Exception {
     
		InputStream fis =  new FileInputStream(filename);
	     byte[] buffer = new byte[1024];
	     MessageDigest complete = MessageDigest.getInstance("MD5");
	     int numRead;
	     
	     do {
	    	 numRead = fis.read(buffer);
	    	 if (numRead > 0) {
	    		 complete.update(buffer, 0, numRead);
	    	 }
	     } while (numRead != -1);
	     
	     fis.close();
	     return complete.digest();
   }

   public static String getMD5Checksum(String filename) throws Exception {
     return getMD5Checksum(createChecksum(filename));
   }
   
   public static String getMD5Checksum(byte[] fileBytes) throws Exception {
		 String result = "";
		 for (int i=0; i < fileBytes.length; i++) {
		   result +=
		      Integer.toString( ( fileBytes[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		 return result;
	}
	
	protected String getMd5(byte[] hash) {
		// converting byte array to Hexadecimal String
		StringBuilder sb = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}
		
		return sb.toString();
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

}