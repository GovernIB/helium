package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
		
		//importPar();
		//crearEnumeracio(false);	
		//crearEnumeracio(true);
		
		seleccionarDefProc();
		
		//crearVariables();
		//modificarVariable();
		//validacioVar();
		//esborrarVar();

		//crearAgrupacio();	
		//crearVarAgrupacio();
		//esborrarVarAgrupacio();
		//esborrarAgrupacio();

		//adjuntarDoc();
		//adjuntarDocPlantilla();
		//modificarDoc();
		//esborrarDoc();
		
		//crearTermini();
		//esborrarTermini();
		
		//crearVarTasca();
		//crearDocTasca();
		//esborrarVarTasca();
		//esborrarDocTasca();
       		
		//esborrarDefProc();
	}

	// TESTS A NIVELL D'UNA DEFINICIO DE PROCES
	// --------------------------------------------------------------------------------------------------------------

	public void importPar() throws InterruptedException {
		
/*		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuDefProces = driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuDefProces);
		actions.click();
		actions.build().perform();
		
		int versio = 0;
		
		WebElement elVersio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + properties.getProperty("deploy.definicio.proces.nom") + "')]/td[2]"));
		if (elVersio != null) {
			versio = Integer.parseInt(elVersio.getText().trim());
		}
		
		screenshotHelper.saveScreenshot("importPar/importPar_01.png");
*/
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuDeploy = driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/deploy.html')]"));
		
		actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();
		
		actions.moveToElement(menuDeploy);
		actions.click();
		actions.build().perform();
		
		// Deploy
		WebElement jbpmOption = driver.findElement(By.xpath("//option[@value='JBPM']"));
		jbpmOption.click();
		
		// tipus d'expedient
/*		WebElement selectTipusExpedient = driver.findElement(By.id("expedientTipusId0"));
		List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(properties.getProperty("deploy.tipus.expedient.nom"))) {
		    	option.click();
		    	break;
		    }
		}
*/
		WebElement arxiu = driver.findElement(By.id("arxiu0"));
		arxiu.sendKeys(properties.getProperty("deploy.arxiu.path.windows"));
		
		screenshotHelper.saveScreenshot("defproces/importPar/importPar01.png");
		
		WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
				
		screenshotHelper.saveScreenshot("defproces/importPar/importPar02.png");
		
		menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuDefProces = driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]"));
		
		actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuDefProces);
		actions.click();
		actions.build().perform();
		
		/*int novaVersio = 0;
		
		WebElement elVersio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'sion005')]/td[2]"));
		if (elVersio != null) {
			novaVersio = Integer.parseInt(elVersio.getText().trim());
		}*/
		
		// assertEquals("La versió resultant no és correcta", versio + 1, novaVersio);
	}

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
		String nomDefProc = getProperty("deploy.definicio.proces.nom");
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

	public void crearEnumeracio(boolean existeix) throws InterruptedException {
		// Crear una enumeració per poder definir una variable de tipus selecció			
		
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuEnum = driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]"));
		
		actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();
		
		actions.moveToElement(menuEnum);
		actions.click();
		actions.build().perform();

		if (existeix) {
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/error_exist1.png");
		}
		else {			
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum1.png");
		}
		
		// Crear enumeracio
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
 	    // Valors
		String codEnum = getProperty("defproc.enum.codi1");
		driver.findElement(By.id("codi0")).sendKeys(codEnum);
		driver.findElement(By.id("nom0")).sendKeys(getProperty("defproc.enum.nom1"));
		if (existeix) {
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/error_exist2.png");
		}
		else {			
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum2.png");
		}
		WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
		boto.click();

		if (existeix) {
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/error_exist3.png");
		}
		else {			
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum3.png");

			// Crear elements enumeració
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[3]/form/button")).click();
			actions.build().perform();
			driver.findElement(By.id("codi0")).sendKeys("S");
			driver.findElement(By.id("nom0")).sendKeys("Sí");
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum4.png");
			boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click();

			driver.findElement(By.id("codi0")).sendKeys("N");
			driver.findElement(By.id("nom0")).sendKeys("No");
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum5.png");
			boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click();
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum6.png");
		
			boto = driver.findElement(By.xpath("//button[@value='cancel']"));
			boto.click();
			screenshotHelper.saveScreenshot("defproces/variable/enumeracio/crea_enum7.png");
			
			// Comprovar que s'han creat els elements de l'enumeració
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'S')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			boolean isPresent2 = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'N')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (!isPresent || !isPresent2) {
				assertFalse("No s'ha pogut crear els elements de l'enumeració", isPresent);
			}		
		}
	}

	public void crearVariables() throws InterruptedException {
		// Crear variables en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/crea_var01.png");
		
  	    // Botó nova variable
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		// tipus string
		String codVar = getProperty("defproc.variable.codi1");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		WebElement jbpmOption = driver.findElement(By.xpath("//option[@value='STRING']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom1"));
		screenshotHelper.saveScreenshot("defproces/variable/crea_var02.png");
		// Botó crear variable
  	    WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
  	    boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var03.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable string", isPresent);
		}		

		// tipus date
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		codVar = getProperty("defproc.variable.codi2");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='DATE']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom2"));
		screenshotHelper.saveScreenshot("defproces/variable/crea_var04.png");
		// Botó crear variable
		boto = driver.findElement(By.xpath("//button[@value='submit']"));
		boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var05.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable data", isPresent);
		}		
	
		// tipus enumeracio
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		codVar = getProperty("defproc.variable.codi3");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='SELECCIO']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom3"));
		WebElement selectEnum = driver.findElement(By.id("enumeracio0"));
		List<WebElement> allOptions = selectEnum.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(properties.getProperty("defproc.enum.nom1"))) {
		    	option.click();
		    	break;
		    }
		}
		screenshotHelper.saveScreenshot("defproces/variable/crea_var06.png");
		// Botó crear variable
		boto = driver.findElement(By.xpath("//button[@value='submit']"));
		boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var07.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable seleccio", isPresent);
		}		
	
		// tipus registre
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		codVar = getProperty("defproc.variable.codi4");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='REGISTRE']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom4"));
		screenshotHelper.saveScreenshot("defproces/variable/crea_var08.png");
		// Botó crear variable
		boto = driver.findElement(By.xpath("//button[@value='submit']"));
		boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var09.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable registre", isPresent);
		}		
		else {
			// Crear elements de la variable registre
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[6]/form/button")).click();
			actions.build().perform();

			selectEnum = driver.findElement(By.id("membreId0"));
			allOptions = selectEnum.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    if (option.getText().equals(properties.getProperty("defproc.variable.codi1")+"/"+properties.getProperty("defproc.variable.nom1"))) {
			    	option.click();
			    	break;
			    }
			}
			screenshotHelper.saveScreenshot("defproces/variable/crea_var10.png");
			boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click();
			screenshotHelper.saveScreenshot("defproces/variable/crea_var11.png");
			boto = driver.findElement(By.xpath("//button[@value='cancel']"));
			boto.click();
		}

		// variable múltiple
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		codVar = getProperty("defproc.variable.codi5");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='STRING']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom5"));
		driver.findElement(By.id("multiple0")).click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var12.png");
		// Botó crear variable
  	    boto = driver.findElement(By.xpath("//button[@value='submit']"));
  	    boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var13.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable múltiple", isPresent);
		}		
		
		// variable oculta
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		codVar = getProperty("defproc.variable.codi6");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='STRING']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom6"));
		driver.findElement(By.id("ocult0")).click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var14.png");
		// Botó crear variable
  	    boto = driver.findElement(By.xpath("//button[@value='submit']"));
  	    boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var15.png");
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear la variable oculta", isPresent);
		}
		
		// crear variable que ja existeix
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
  	    screenshotHelper.saveScreenshot("defproces/variable/crea_var16.png");
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
		// tipus string
		codVar = getProperty("defproc.variable.codi1");
		driver.findElement(By.id("codi0")).sendKeys(codVar);
		jbpmOption = driver.findElement(By.xpath("//option[@value='STRING']"));
		jbpmOption.click();
		driver.findElement(By.id("etiqueta0")).sendKeys(getProperty("defproc.variable.nom1"));
		screenshotHelper.saveScreenshot("defproces/variable/crea_var17.png");
		// Botó crear variable
  	    boto = driver.findElement(By.xpath("//button[@value='submit']"));
  	    boto.click();
		screenshotHelper.saveScreenshot("defproces/variable/crea_var18.png");

		// tornar a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();
	}

	private void modificarVariable() {
		// Modificar el camp tipus d'una variable			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/mod_var1.png");

  	    // Obtenir nom variable i cercar-la
  	    String codVar = getProperty("defproc.variable.codi6");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, modificar-la
		if (isPresent) {
	  	    // guardar valor inicial
	  	    String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();

			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[1]/a")).click();
	  	    screenshotHelper.saveScreenshot("defproces/variable/mod_var2.png");

	  	    WebElement jbpmOption = driver.findElement(By.xpath("//option[@value='INTEGER']"));
			jbpmOption.click();

			screenshotHelper.saveScreenshot("defproces/variable/mod_var3.png");
			// Botó crear variable
			WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click(); 	    
	  	    
	  	    screenshotHelper.saveScreenshot("defproces/variable/mod_var4.png");
	  	    
	  	    // comprovar si s'ha modificat la variable
	  	    String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();
			assertNotEquals("No s'ha pogut canviar el tipus de la variable", valIni, valFin);
 	    } else {
			fail("La variable no existeix");
		}
	}
	
	private void validacioVar() {
		// Afegeix una validació a una variable			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/val_var1.png");

  	    // Obtenir nom variable i cercar-la
  	    String codVar = getProperty("defproc.variable.codi3");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, modificar-la
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[5]/form/button")).click();
			actions.build().perform();

			screenshotHelper.saveScreenshot("defproces/variable/val_var2.png");
			String valIni = "NOT(var_enum01='S' and var_dat01 is blank";
			driver.findElement(By.id("expressio0")).sendKeys(valIni);
			driver.findElement(By.id("missatge0")).sendKeys("S'ha d'especificar una data");

			screenshotHelper.saveScreenshot("defproces/variable/val_var3.png");
			// Botó crear 
			WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click(); 	    
	  	    
	  	    screenshotHelper.saveScreenshot("defproces/variable/val_var4.png");
	  	    
	  	    // comprovar si s'ha creat la validació
	  	    String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[1]")).getText().trim();
			assertNotEquals("No s'ha pogut crear la validadció de la variable", valIni, valFin);
 	    } else {
			fail("La variable no existeix");
		}
	}
	
	
	private void esborrarVar() {
		// Esborra una variable en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_var1.png");

  	    // Obtenir nom variable i cercar-la
  	    String codVar = getProperty("defproc.variable.codi6");
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
		  	    
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear l'agrupació", isPresent);
		}		
	}

	public void crearVarAgrupacio() throws InterruptedException {
		// Afegeix una variable una agrupació de variables 
		
		// Accedir a la fitxa de les agrupacions
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campAgrupacioLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/variable/crea_varagru1.png");	
  	    
  	    // Cercar agrupació
  	    String codAgr = getProperty("defproc.agrupacio.codi1");
  	    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("L'agrupació no existeix", isPresent);
		}		
		else {
			// Botó Assignar variable
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codAgr + "')]/td[4]/form/button")).click();	
			// Seleccionar variable
			WebElement selectVar = driver.findElement(By.id("id0"));
			List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    if (option.getText().equals(properties.getProperty("defproc.variable.codi1")+"/"+properties.getProperty("defproc.variable.nom1"))) {
			    	option.click();
			    	break;
			    }
			}
			screenshotHelper.saveScreenshot("defproces/variable/crea_varagru2.png");
			// Botó afegir variable
	  	    WebElement botoVar = driver.findElement(By.xpath("//button[@value='submit']"));
			botoVar.click();

			screenshotHelper.saveScreenshot("defproces/variable/crea_varagru3.png");			

			// Comprovar que s'ha creat
			String nomVar = getProperty("defproc.variable.nom1");
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (!isPresent) {
				assertFalse("No s'ha pogut crear la variable de l'agrupació", isPresent);
			}
		}
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
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si existeix, esborrar-la
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]/td[3]/a")).click();
			driver.switchTo().alert().accept();
	  	    screenshotHelper.saveScreenshot("defproces/variable/esborra_varagr3.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomVar + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar la variable de l'agrupació", isPresent);
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
		String nomDoc = getProperty("defproc.document.codi1");
		driver.findElement(By.id("codi0")).sendKeys(nomDoc);
		driver.findElement(By.id("nom0")).sendKeys(getProperty("defproc.document.nom1"));

		screenshotHelper.saveScreenshot("defproces/document/crea_doc3.png");
		
		// Botó crear document
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/document/crea_doc4.png");

		// Comprovar que s'ha creat		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear el document", isPresent);
		}		
	}

	public void adjuntarDocPlantilla() throws InterruptedException {
		// Adjunta un document tipus plantilla a una defició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/document/crea_docpl1.png");
		
  	    // Botó nou document
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		screenshotHelper.saveScreenshot("defproces/document/crea_docpl2.png");
		
		// Inicialitzar camps
		String nomDoc = getProperty("defproc.document.codi2");
		driver.findElement(By.id("codi0")).sendKeys(nomDoc);
		driver.findElement(By.id("nom0")).sendKeys(getProperty("defproc.document.nom2"));
		WebElement arxiu = driver.findElement(By.id("arxiuContingut0"));
		arxiu.sendKeys(properties.getProperty("defproc.document.arxiu"));
		driver.findElement(By.id("plantilla0")).click();

		screenshotHelper.saveScreenshot("defproces/document/crea_docpl3.png");
		
		// Botó crear document
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/document/crea_docpl4.png");

		// Comprovar que s'ha creat		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear el document", isPresent);
		}		
	}
	
	private void modificarDoc() {
		// Modifica la propietat camp amb data d'un document d'una definició de procés seleccionada amb seleccionarDefProc			
	
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();			
		
		screenshotHelper.saveScreenshot("defproces/document/mod_doc1.png");

		// Obtenir nom document i cercar-lo
		String nomDoc = getProperty("defproc.document.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, modificar-lo
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[1]/a")).click();
	  	    screenshotHelper.saveScreenshot("defproces/document/mod_doc2.png");
			
	  	    // guardar valor inicial
	  	    String valIni = driver.findElement(By.id("campData0")).getText().trim();

			// Seleccionar variable camp en la data
			WebElement selectVar = driver.findElement(By.id("campData0"));
			List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    if (option.getText().equals(properties.getProperty("defproc.variable.codi2")+"/"+properties.getProperty("defproc.variable.nom2"))) {
			    	option.click();
			    	break;
			    }
			}
			screenshotHelper.saveScreenshot("defproces/document/mod_doc3.png");
			// Botó guardar
			WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click(); 	    
	  	    
	  	    screenshotHelper.saveScreenshot("defproces/document/mod_doc4.png");
	  	    
	  	    // comprovar si s'ha modificat la variable
 	  	    //driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[1]/a")).click();
	  	    //String valFin = driver.findElement(By.id("campData0")).getText().trim();
	  	    //boto = driver.findElement(By.xpath("//button[@value='cancel']"));
			//boto.click(); 	    
	  	    //assertNotEquals("No s'ha pogut modificar el document", valIni, valFin);
		} else {
			fail("El document no existeix");
		}
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

	public void crearTermini() throws InterruptedException {
		// Crear un termini en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/termini/crea_termini1.png");
		
  	    // Botó nou termini
  	    driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

		// Inicialitzar camps
  	    String codTer = getProperty("defproc.termini.codi1");
		driver.findElement(By.id("codi0")).sendKeys(codTer);
		driver.findElement(By.id("nom0")).sendKeys(getProperty("defproc.termini.nom1"));
		driver.findElement(By.id("duradaPredefinida0")).click();
		driver.findElement(By.id("dies")).clear();
		driver.findElement(By.id("dies")).sendKeys("15");
		driver.findElement(By.id("laborable0")).click();
		
		screenshotHelper.saveScreenshot("defproces/termini/crea_termini2.png");

		// Botó crear termini
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/termini/crea_termini3.png");

		// Comprovar que s'ha creat		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTer + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut crear el termini", isPresent);
		}		
	}

	private void esborrarTermini() {
		// Esborra un termini d'una definició de procés seleccionada amb seleccionarDefProc			
	
		// Accedir a la fitxa dels terminis
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/terminiLlistat.html')]")).click();			
		
		screenshotHelper.saveScreenshot("defproces/termini/esborra_ter1.png");

		// Obtenir codi termini i cercar-lo
		String codTer = getProperty("defproc.termini.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTer + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Si existeix, esborrar-lo
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTer + "')]/td[4]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("defproces/termini/esborra_ter2.png");
		
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTer + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
			assertFalse("No s'ha pogut eliminar el termini", isPresent);
		} else {
			fail("El termini no existeix");
		}
	}

	public void crearVarTasca() throws InterruptedException {
		// Afegeix una variable a una tasca en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les tasques
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/tasca/crea_var1.png");

  	    String codTasca = getProperty("defproc.tasca.codi1");
  	    String codVar = getProperty("defproc.variable.codi1")+"/"+getProperty("defproc.variable.nom1");

  	    // Botó nova variable
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]/td[3]/form/button")).click();
		actions.build().perform();

		// Seleccionar variable
		WebElement selectVar = driver.findElement(By.id("campId0"));
		List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(codVar)) {
		    	option.click();
		    	break;
		    }
		}	

		screenshotHelper.saveScreenshot("defproces/tasca/crea_var2.png");

		// Botó crear variable
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/tasca/crea_var3.png");
		
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut assignar la variable a la tasca", isPresent);
		}		
	}	
	
	public void esborrarVarTasca() throws InterruptedException {
		// Esborra una variable d'una tasca en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les tasques
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/tasca/esborra_var1.png");

  	    // Obtenir codi tasca i cercar-la
  	    String codTasca = getProperty("defproc.tasca.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Botó variable
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]/td[3]/form/button")).click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("defproces/tasca/esborra_var2.png");
		
		// Obtenir codi i nom variable i cercar-la
  	    String codVar = getProperty("defproc.variable.codi1")+"/"+getProperty("defproc.variable.nom1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si existeix, esborrar-la
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[6]/a")).click();
			screenshotHelper.saveScreenshot("defproces/tasca/esborra_var3.png");
	  	    			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
			assertFalse("No s'ha pogut eliminar la variable de la tasca", isPresent);
		} else {
			fail("La variable no existeix en la tasca");
		}
	}	

	public void crearDocTasca() throws InterruptedException {
		// Afegeix un document a una tasca en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de tasques
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/tasca/crea_doc1.png");

  	    String codTasca = getProperty("defproc.tasca.codi1");
  	    String codDoc = getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1");

  	    // Botó document
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]/td[4]/form/button")).click();
		actions.build().perform();

		// Seleccionar document
		WebElement selectVar = driver.findElement(By.id("documentId0"));
		List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(codDoc)) {
		    	option.click();
		    	break;
		    }
		}	
		driver.findElement(By.id("readOnly0")).click();

		screenshotHelper.saveScreenshot("defproces/tasca/crea_doc2.png");

		// Botó crear document
  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
		
		screenshotHelper.saveScreenshot("defproces/tasca/crea_doc3.png");
		
		// Comprovar que s'ha creat
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (!isPresent) {
			assertFalse("No s'ha pogut assignar el document a la tasca", isPresent);
		}		
	}	

	public void esborrarDocTasca() throws InterruptedException {
		// Esborra un document d'una tasca en una definició de procés seleccionada amb seleccionarDefProc			
		
		// Accedir a la fitxa de les tasques
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/tascaLlistat.html')]")).click();			
			
  	    screenshotHelper.saveScreenshot("defproces/tasca/esborra_doc1.png");

  	    // Obtenir codi document i cercar-lo
  	    String codTasca = getProperty("defproc.tasca.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Botó document
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTasca + "')]/td[4]/form/button")).click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("defproces/tasca/esborra_doc2.png");
		
		// Obtenir codi i nom document i cercar-lo
  	    String codDoc = getProperty("defproc.document.codi1")+"/"+getProperty("defproc.document.nom1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si existeix, esborrar-lo
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]/td[3]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("defproces/tasca/esborra_doc3.png");
	  	    			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
			assertFalse("No s'ha pogut eliminar el document de la tasca", isPresent);
		} else {
			fail("El document no existeix en la tasca");
		}
	}	

}


