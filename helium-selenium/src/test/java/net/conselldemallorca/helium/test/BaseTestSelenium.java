package net.conselldemallorca.helium.test;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.conselldemallorca.helium.helper.ScreenshotHelper;
import net.consellemallorca.helium.util.CheckFileHash;
import net.consellemallorca.helium.util.FileDownloader;
import net.consellemallorca.helium.util.HashType;

/** Mètode base amb mètodes comuns per inicialitzar el webdriver, comprovar
 * elements o facilitar la navegació.
 */
@Ignore("Classe base per a la execució dels tests amb Selenium")
public class BaseTestSelenium extends BaseTest{

	protected static  WebDriver driver;
	protected static ScreenshotHelper screenshotHelper;
	protected static Actions actions;
	
	private static final int TIMEOUT_PRESENT = 10000;
	
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
		BaseTest.setUpBeforeClass();
		// Configurarem el driver depenent del navegador
		String browser = propietats.getProperty("test.browser");
				
		String driverName = "";
		if ("chrome".equals(browser)) {
			driverName = propietats.getProperty("webdriver.chrome.driver");
			assertNotNull("Driver per chrome no configurat al fitxer de properties", driverName);
			System.setProperty("webdriver.chrome.driver", driverName);
			
			DesiredCapabilities capability = DesiredCapabilities.chrome();
			capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			
			driver = new ChromeDriver(capability);
			driverConfig("CR");
		} else if ("iexplorer".equals(browser)) {
			driverName = propietats.getProperty("webdriver.ie.driver");
			assertNotNull("Driver per iexplorer no configurat al fitxer de properties", driverName);
			System.setProperty("webdriver.ie.driver", driverName);
			driver = new InternetExplorerDriver();
			driverConfig("IE");
		} else { //"firefox":
			FirefoxProfile fp = new FirefoxProfile();
			fp.setAcceptUntrustedCertificates( true );
			fp.setPreference( "security.enable_java", true ); 
			fp.setPreference( "plugin.state.java", 2 );
			fp.setPreference( "intl.accept_languages","ca");
//			plugin.state.java = 0 --> never activate
//			plugin.state.java = 1 --> ask to activate
//			plugin.state.java = 2 --> always activate
			String binari = propietats.getProperty("webdriver.firefox.binary");
			if (binari != null) {
				File pathToBinary = new File(binari);
				FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
				driver = new FirefoxDriver(ffBinary, fp);
			} else {
				driver = new FirefoxDriver(fp);
			}
			driverConfig("Firefox");
		}
		// Configuram l'element actions
		actions = new Actions(driver);
	}

	@Before
	public void setUp() throws Exception {		
		// Cancel·la totes les alertes presents
		if(isAlertPresent())
			rebutjarAlerta();
	}

	@After
	public void tearDown() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		BaseTest.tearDownAfterClass();
		driver.quit();
	}

	protected static void driverConfig(String prefix) {
		assertNotNull("No s'ha pogut inicialitzar el driver", driver);
		screenshotHelper = new ScreenshotHelper(driver, prefix);
	}

	// Funcions d'ajuda
	protected boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	protected WebElement existeixElementAssert(By by) {
		String msgNotFound = "No existeix l'element esperat " + by.toString();
		comprovaElement(by, null, 0L, msgNotFound, true, false);
		return driver.findElement(by);
	}
	protected WebElement existeixElementAssert(By by, String msgNotFound) {
		comprovaElement(by, null, 0L, msgNotFound, true, false);
		return driver.findElement(by);
	}
	protected WebElement existeixElementAssert(By by, String msgNotFound, boolean continuar) {
		comprovaElement(by, null, 0L, msgNotFound, true, continuar);
		return driver.findElement(by);
	}
	protected WebElement existeixElementAssert(By by, String screenShot, String msgNotFound) {
		comprovaElement(by, screenShot, 0L, msgNotFound, true, false);
		return driver.findElement(by);
	}
	protected WebElement existeixElementAssert(By by, String screenShot, long waitTime, String msgNotFound) {
		comprovaElement(by, screenShot, waitTime, msgNotFound, true, false);
		return driver.findElement(by);
	}
	protected boolean existeixElement(By by) {
		return comprovaElement(by, null, 0L, null, true, true);
	}
	protected boolean existeixElement(By by, String screenShot) {
		return comprovaElement(by, screenShot, 0L, null, true, true);
	}
	protected boolean existeixElement(By by, String screenShot, long waitTime) {
		return comprovaElement(by, screenShot, waitTime, null, true, true);
	}
	protected boolean noExisteixElementAssert(By by, String msgNotFound) {
		return comprovaElement(by, null, 0L, msgNotFound, false, false);
	}
	protected boolean noExisteixElementAssert(By by, String screenShot, String msgNotFound) {
		return comprovaElement(by, screenShot, 0L, msgNotFound, false, false);
	}
	protected boolean noExisteixElementAssert(By by, String screenShot, long waitTime, String msgNotFound) {
		return comprovaElement(by, screenShot, waitTime, msgNotFound, false, false);
	}
	protected boolean noExisteixElement(By by) {
		return comprovaElement(by, null, 0L, null, false, true);
	}
	protected boolean noExisteixElement(By by, String screenShot) {
		return comprovaElement(by, screenShot, 0L, null, false, true);
	}
	protected boolean noExisteixElement(By by, String screenShot, long waitTime) {
		return comprovaElement(by, screenShot, waitTime, null, false, true);
	}
	
	protected boolean comprovaElement(By by, String screenShot, long waitTime, String msgNotFound, boolean existeix, boolean continuarTest) {
		boolean condicio = false;
		boolean isPresent = false;
		try {
			long temps_esperat = new Date().getTime();
			while (!condicio && (new Date().getTime()-temps_esperat) < TIMEOUT_PRESENT) {
				try {
					driver.findElement(by);
					if (driver.findElement(by).isDisplayed())
						isPresent = true;
				} catch (Exception e) {
					isPresent = false;
				}
				condicio = isPresent == existeix;
				if (!condicio) {
					Thread.sleep(100);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error comprovant element("+by.toString()+"): "+ex.getMessage());
		}
		
		if (screenShot != null) {
			if (waitTime > 0L)
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			screenshotHelper.saveScreenshot(screenShot);
		}
		if (!continuarTest) {
			if (existeix) assertTrue(msgNotFound, isPresent);
			else assertFalse(msgNotFound, isPresent);
		}
		return (existeix ? isPresent : !isPresent);
	}
	
	protected boolean checkboxSelectedAssert(By by, String msgNotFound, boolean selected) {
		return comprovaCheckbox(by, msgNotFound, selected, false);
	}
	protected boolean checkboxSelected(By by, boolean selected) {
		return comprovaCheckbox(by, null, selected, true);
	}
	protected boolean checkboxSelected(By by, String msgNotFound, boolean selected) {
		return comprovaCheckbox(by, msgNotFound, selected, true);
	}
	protected boolean comprovaCheckbox(By by, String msgNotFound, boolean selected, boolean continuarTest) {
		boolean isChecked = driver.findElement(by).isSelected();
		if (!continuarTest) {
			if (selected) assertTrue(msgNotFound, isChecked);
			else assertFalse(msgNotFound, isChecked);
		}
		return (selected ? isChecked : !isChecked);
	}
	protected void checkboxIsSelectedAssert(By by, String msg) {
		assertTrue(msg, driver.findElement(by).isSelected());
	}
	
	protected void modalObertaAssert(String modal) {
		existeixElementAssert(By.xpath("//iframe[contains(@src, '" + modal + "')]"), 
						null,
						"No s'ha obert la finestra modal de " + modal);
	}
	protected void modalObertaAssert(String modal, String screenShot) {
		existeixElementAssert(By.xpath("//iframe[contains(@src, '" + modal + "')]"), 
						screenShot,
						500,
						"No s'ha obert la finestra modal de " + modal);
	}
	protected boolean modalOberta(String modal) {
		return existeixElement(By.xpath("//iframe[contains(@src, '" + modal + "')]"), 
						null);
	}
	protected boolean modalOberta(String modal, String screenShot) {
		return existeixElement(By.xpath("//iframe[contains(@src, '" + modal + "')]"), 
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
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	
	protected void rebutjarAlerta() {
		try {
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {
			fail("Error rebutjant alert.");
		}
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

	protected byte[] downloadFile(By by, String fitxer) {
				
		FileDownloader downloader = new FileDownloader(driver);
//		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(by).size() > 0;
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("Enllaç al fitxer " + fitxer + " no existeix" , isPresent);
		
		byte[] downloadedFile = null;
		
		try {
			downloadedFile = downloader.downloadFile(driver.findElement(by));
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
	
	protected byte[] downloadFileFromForm(By by, String fitxer) {
		
		FileDownloader downloader = new FileDownloader(driver);
//		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(by).size() > 0;
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		assertTrue("Enllaç al fitxer " + fitxer + " no existeix" , isPresent);
		
		byte[] downloadedFile = null;
		try {
			downloadedFile = downloader.downloadFileFromForm(driver.findElement(by));
		} catch (Exception e) {
			e.printStackTrace();
			fail("No s'ha pogut descarregar el fitxer " + fitxer);
		}
		
		assertThat(downloader.getHTTPStatusOfLastDownloadAttempt(), is(equalTo(200)));
		
		return downloadedFile;
	}
	
	protected void downloadFileHash(By by, String md5, String fitxer) {
		
		byte[] downloadedFile = downloadFile(by, fitxer);
		
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
		
		//System.setProperty("jsse.enableSNIExtension", "false");
		
		FileDownloader downloader = new FileDownloader(driver);
//		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath(formXpath)).size() > 0;
//		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
	
	// Mètodes d'ajuda a la navegació.
	
	/** Carrega la pàgina inicial d'helium. */
	protected void carregarPaginaIncial() {
		driver.get(propietats.getTestBaseUrl() + "/index.html");
		boolean seycon = propietats.getPropertyBool("test.base.url.inici.seycon", false);
		if (seycon) {
			String user = propietats.getProperty("test.usuari.codi");
			String pass = propietats.getProperty("test.usuari.password");
			driver.findElement(By.xpath("//*[@id='j_username']")).sendKeys(user);
			driver.findElement(By.xpath("//*[@id='j_password']")).sendKeys(pass);
			driver.findElement(By.xpath("//*[@id='usuariclau']/form/p[3]/input")).click();
		}
		// Espera que es carregui la pàgina
		waitPaginaCarregada();
		// Comprova que existeixi el logo
		existeixElementAssert(By.cssSelector("img[alt=\"Helium\"]"), "La pàgina d'inici no s'ha carregat correctament.");
	}		
	
	/** Mètode que espera que la pàgina estigui totalment carregada i que no hi hagi connexions $.ajax
	 * obertes. És útil quan s'entra a una pàgina amb taules que carreguen dades.
	 */
	protected void waitPaginaCarregada() {
		// Espera un segon
		try{ new WebDriverWait(driver, 2).wait();} catch(Exception e){};
		// Espera a que la pàgina estigui totalment carregada
	    (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	            JavascriptExecutor js = (JavascriptExecutor) d;
	            String readyState = js.executeScript("return document.readyState").toString();
	            return (Boolean) readyState.equals("complete");
	        }
	    });
	    // Espera que acabin les connexions ajax
	    (new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver d) {
	            JavascriptExecutor js = (JavascriptExecutor) d;
	            Boolean res = (Boolean) js.executeScript("return !!window.jQuery && window.jQuery.active == 0");
	            return res;
	        }
	    });
	}	
	
	
	/** Mètode per esborrar un expedient segons el títol i el tipus. */
	protected boolean esborrarExpedient(String titol) {
		boolean esborrat = false;
		
		// Filtra per trobar resultats
		driver.findElement(By.id("titol")).clear();
		driver.findElement(By.id("titol")).sendKeys(titol);
		driver.findElement(By.id("consultar")).click();
		waitPaginaCarregada();
		try {
			// Prem els botons d'esborrar i acceptar la confirmació
			driver.findElement(By.id("taulaDades")).findElement(By.cssSelector(".btn.btn-primary")).click();
			driver.findElement(By.id("taulaDades")).findElement(By.cssSelector(".fa.fa-trash-o")).click();
			if (isAlertPresent())
				acceptarAlerta();
			waitPaginaCarregada();		
		} catch(Exception e) {
			// No existeix segurament
		}
		// Neteja el formulari de cerca
		driver.findElement(By.id("netejar")).click();
		waitPaginaCarregada();		

		return esborrat;		
	}
}