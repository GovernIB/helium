package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class DefinicioProcesTests extends BaseTest {

	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();

		EntornTests.testEntornSeleccionar(true);
		seleccionarDefProc();
				
		//crearVariable();
		//crearAgrupacio();	
		//adjuntarDoc();

		esborrarVarAgrupacio();
		//esborrarAgrupacio();
		//esborrarVar();	
		//esborrarDoc();
	}

	// TESTS A NIVELL D'UNA DEFINICIO DE PROCES
	// --------------------------------------------------------------------------------------------------------------


	public void seleccionarDefProc() throws InterruptedException {
		// Seleccionar una definició de procés que s'utilitzarà per fer les proves
		
		// Accés a l'opció de menú
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuDefProc = driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuDefProc);
		actions.click();
		actions.build().perform();
		
		// Obtenir nom de la defició de procés i cercar-lo
		String nomDefProc = getProperty("definicio.proces.nom");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha trobat la defició de procés", isPresent);
		}
		else
		{
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();

			screenshotHelper.saveScreenshot("defproces/selecDefProc.png");
		}
	}
	

	public void crearVariable() throws InterruptedException {
		// Crear una variable tipus string en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/crea_var1.png");
		
  	    // Botó nova variable
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		screenshotHelper.saveScreenshot("defproces/variable/crea_var2.png");
		
		// Inicialitzar camps
		driver.findElement(By.id("codi0")).sendKeys(getProperty("defproc.variable.codi1"));
		WebElement jbpmOption = driver.findElement(By.xpath("//option[@value='STRING']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom1"));

		screenshotHelper.saveScreenshot("defproces/variable/crea_var3.png");

		// Botó crear variable
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/variable/crea_var4.png");
	}

	private void esborrarVar() {
		// Esborra una variable tipus string en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_var1.png");

  	    // Obtenir nom variable i cercar-la
  	    String codVar = getProperty("defproc.variable.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, esborrar-la
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[7]/a")).click();
			driver.switchTo().alert().accept();
	  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_var2.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar la variable", isPresent);
		} else {
			fail("La variable no existeix");
		}
	}

	public void crearAgrupacio() throws InterruptedException {
		// Crear una agrupació de variables en una definició de procés seleccionada amb seleccionarDefProc
		// Afegeix una variable creada anteriorment
		
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar1.png");
		
  	    // Botó nova agrupció
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar2.png");
		
		// Inicialitzar camps
		String codAgr = getProperty("defproc.agrupacio.codi1");
		driver.findElement(By.id("codi0")).sendKeys(codAgr);
		driver.findElement(By.id("nom0")).sendKeys("Nom agrupació variables 01");

		screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar3.png");

		// Botó crear agrupacio
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar4.png");
		
		// Botó Assignar variable
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]/td[4]/form/button")).click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar5.png");
		// Seleccionar variable
		WebElement selectVar = driver.findElement(By.id("id0"));
		List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(properties.getProperty("defproc.variable.codi1")+"/"+properties.getProperty("defproc.variable.nom1"))) {
		    	option.click();
		    	break;
		    }
		}	

		// Botó afegir variable
  	    WebElement botoVar = driver.findElement(By.xpath("//button[@value='submit']"));
		botoVar.click();

		screenshotHelper.saveScreenshot("defproces/variable/crea_agruvar6.png");
	}

	private void esborrarAgrupacio() {
		// Esborra una agrupació de variables en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_agruvar1.png");

  	    // Obtenir nom agrupacio i cercar-la
  	    String codAgr = getProperty("defproc.agrupacio.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, esborrar-la
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]/td[5]/a")).click();
			driver.switchTo().alert().accept();
	  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_agruvar2.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar l'agrupació", isPresent);
		} else {
			fail("L'agrupació no existeix");
		}
	}	
	
	private void esborrarVarAgrupacio() {
		// Esborra una variable d'una agrupació de variables, en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_varagr1.png");

  	    // Obtenir codi agrupacio i cercar-la
  	    String codAgr = getProperty("defproc.agrupacio.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Botó variable
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]/td[4]/form/button")).click();
		screenshotHelper.saveScreenshot("defproces/variable/esborra_varagr2.png");
		
		// Obtenir codi variable i cercar-la
  	    String nomVar = getProperty("defproc.variable.nom1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent2 = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si existeix, esborrar-la
		if (isPresent2) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[3]/a")).click();
			driver.switchTo().alert().accept();
	  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_varagr3.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			boolean isPresent3 = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar la variable de l'agrupació", isPresent3);
		} else {
			fail("La variable no existeix en l'agrupació");
		}
	}	
	
	public void adjuntarDoc() throws InterruptedException {
		// Adjunta un document a una defició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/document/crea_doc1.png");
		
  	    // Botó nou document
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		screenshotHelper.saveScreenshot("defproces/document/crea_doc2.png");
		
		// Inicialitzar camps
		driver.findElement(By.id("codi0")).sendKeys(getProperty("defproc.document.codi1"));
		driver.findElement(By.id("nom0")).sendKeys("Nom document 01");

		screenshotHelper.saveScreenshot("defproces/document/crea_doc3.png");
		
		// Botó crear document
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/document/crea_doc4.png");
	}


	private void esborrarDoc() {
		// Esborra un document d'una definició de procés seleccionada amb seleccionarDefProc			
	
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();			
		
		screenshotHelper.saveScreenshot("defproces/document/esborra_doc1.png");

		// Obtenir nom document i cercar-lo
		String nomDoc = getProperty("defproc.document.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, esborrar-lo
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[5]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("defproces/document/esborra_doc2.png");
		
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
			assertFalse("No s'ha pogut eliminar el document", isPresent);
		} else {
			fail("El document no existeix");
		}
	}


}


