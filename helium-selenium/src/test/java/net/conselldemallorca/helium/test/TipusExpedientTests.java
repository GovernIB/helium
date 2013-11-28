package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class TipusExpedientTests extends BaseTest {

	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();

		// Seleccionar entorn
		EntornTests.testEntornSeleccionar(true);
		
		crearTipoExpedienteTest();
		modificarTipExp();
		assignarPermisosTipExp(true);
		assignarPermisosTipExp(false);
		DefinicioProcesTests.importPar(true);
		procesPrincipal();
		esborrarPermisosTipExp(getProperty("test.base.rol.configuracio"));

		// estats
		crearEstat();
		modificarEstat();
		esborrarEstat();

		// enumeracions
		crearEnumTipExp();
		modificarEnumTipExp();
		crearValEnumTipExp();
		modificarValEnumTipExp();
		ordenarValEnumTipExp();
		esborrarValEnumTipExp();
		esborrarEnumTipExp();
		
		esborrarTipExp();

		
		/*EntornTests.testEntornCrear(true);
		EntornTests.testEntornPermisos(true);
		EntornTests.testEntornSeleccionar(true);
		crearTipoExpedienteTest();
		asignarMiembro();
		borrarMiembro();
		borrarTipoExpedienteTest();
		EntornTests.testEntornBorrar();*/
	}

	// TESTS A NIVELL DE TIPUS D'EXPEDIENT
	// --------------------------------------------------------------------------------------------------------------

	public static void crearTipoExpedienteTest() throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		String codTipExp = getProperty("deploy.tipus.expedient.codi");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp1.png");

		if (!isPresent) {
			// Si no existe el tipo de grupo, lo creamos
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

			driver.findElement(By.id("codi0")).sendKeys(codTipExp);
			driver.findElement(By.id("nom0")).sendKeys(nombreTipoExpediente);
			//driver.findElement(By.id("expressioNumero0")).sendKeys("SE-${seq}${any}");

			driver.findElement(By.id("suggest_responsableDefecteCodi0")).clear();
			driver.findElement(By.id("suggest_responsableDefecteCodi0")).sendKeys(getProperty("test.base.usuari.configuracio"));
			isPresent = driver.findElements(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;

			if (!isPresent) {
				assertTrue("No existe ese usuario", isPresent);
			}

			driver.findElement(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).click();

			driver.findElement(By.id("teTitol0")).click();
			driver.findElement(By.id("demanaTitol0")).click();
			driver.findElement(By.id("teNumero0")).click();
			driver.findElement(By.id("demanaNumero0")).click();
			driver.findElement(By.id("reiniciarCadaAny0")).click();
			//driver.findElement(By.id("seleccionarAny0")).click();
			driver.findElement(By.xpath("//*[@id='seqMultiple']/div/button")).click();
			driver.findElement(By.id("seqany_0")).sendKeys("2013");
			driver.findElement(By.id("seqseq_0")).sendKeys("15");

			screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp2.png");
			
			driver.findElement(By.xpath("//button[@value='submit']")).click();

			screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp3.png");
		}

		// comprovar que s'ha creat
		menuDisseny = driver.findElement(By.id("menuDisseny"));
		menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha pogut crear el tipus d'expedient", isPresent);
		}
	}

	public static void seleccionarTipExp() throws InterruptedException {
		// Seleccionar un tipus d'expedient que s'utilitzarà per fer les proves
		
		// Accés a l'opció de menú
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("tipusExpedient/selecTipExp1.png");
		
		// Obtenir codi tipus expedient i cercar-lo
		String codTipExp = getProperty("deploy.tipus.expedient.codi");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha trobat el tips d'expedient", isPresent);
		}
		else
		{
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[1]/a")).click();
			screenshotHelper.saveScreenshot("tipusExpedient/selecTipExp2.png");
		}
	}

	public static void modificarTipExp() throws InterruptedException {
		// Modifica el camp Expressió per calcular el número d'un tipus d'expedient
		seleccionarTipExp();
		
		driver.findElement(By.xpath("html/body/div[1]/div[2]/div[3]/form[1]/button")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/modifTipExp1.png");		
  	    // guardar valor inicial
  	    String valIni = driver.findElement(By.id("expressioNumero0")).getText().trim();

		driver.findElement(By.id("expressioNumero0")).sendKeys("SE-${seq}${any}");

		screenshotHelper.saveScreenshot("tipusExpedient/modifTipExp2.png");
			
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		screenshotHelper.saveScreenshot("tipusExpedient/modifTipExp3.png");

		// comprovar si s'ha modificat la variable
/*		seleccionarTipExp();
		driver.findElement(By.xpath("html/body/div[1]/div[2]/div[3]/form[1]/button")).click();
  	    String valFin = driver.findElement(By.id("expressioNumero0")).getText().trim();
  	    driver.findElement(By.xpath("//button[@value='cancel']")).click();	  	
		assertNotEquals("No s'ha pogut modificar el tipus d'expedient", valIni, valFin);*/
	}
	
	private void esborrarTipExp() {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.codi");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("El tipus d'expedient no existeix", isPresent);
		}
		else {
			screenshotHelper.saveScreenshot("tipusExpedient/esborraTipExp1.png");			

			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[4]/a")).click();
			driver.switchTo().alert().accept();

			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			screenshotHelper.saveScreenshot("tipusExpedient/esborraTipExp2.png");

			assertFalse("No s'ha pogut esborrar el tipus d'expedient", isPresent);
		}
	}


	public static void assignarPermisosTipExp(boolean isUser) throws InterruptedException {		
		String userol = isUser ? properties.getProperty("test.base.usuari.configuracio") : properties.getProperty("test.base.rol.configuracio");
		String tipus = "crear/" + (isUser ? "usuari" : "rol");
		
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		String tipexp = getProperty("deploy.tipus.expedient.codi");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipexp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("tipusExpedient/permisos/" + tipus + "/tipusExpedients.png");
		
		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + tipexp + "')]/td[3]/form/button")).click();
			screenshotHelper.saveScreenshot("tipusExpedient/permisos/" + tipus + "/crea_01.png");
			
			driver.findElement(By.id("nom0")).sendKeys(userol);
			// marcar els permisos
	        if (isUser) { // TOTS
	    		driver.findElement(By.id("permisos00")).click();
	    		driver.findElement(By.id("permisos01")).click();
	    		driver.findElement(By.id("permisos02")).click();
	    		driver.findElement(By.id("permisos03")).click();
	    		driver.findElement(By.id("permisos04")).click();
	    		driver.findElement(By.id("permisos05")).click();
	    		driver.findElement(By.id("permisos06")).click();
	    		driver.findElement(By.id("permisos07")).click();
	    		driver.findElement(By.id("permisos08")).click();       	
	        }
	        else { //CREATE READ WRITE
	    		driver.findElement(By.id("permisos00")).click();
	    		driver.findElement(By.id("permisos07")).click();
	    		driver.findElement(By.id("permisos08")).click();       	     
	        }
	        screenshotHelper.saveScreenshot("tipusExpedient/permisos/" + tipus + "/crea_02.png");	        
	        driver.findElement(By.xpath("//button[@value='submit']")).click();
	        screenshotHelper.saveScreenshot("tipusExpedient/permisos/" + tipus + "/crea_03.png");
		} else {
			fail("El tipus d'expedient no existeix");
		}
	}	
		
	public void esborrarPermisosTipExp(String codUsu) throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tipusExpedient/esborraPermisos1.png");
		
		String codTipExp = getProperty("deploy.tipus.expedient.codi");
		
		// Botó permisos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[3]/form/button")).click();
		actions.build().perform();

		//String codUsu = getProperty("test.base.usuari.configuracio");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + codUsu + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si existeix l'usuari, esborrar-lo
		if (!isPresent) {
			assertFalse("L'usuari no existeix", isPresent);
		}
		else {
			screenshotHelper.saveScreenshot("tipusExpedient/esborraPermisos2.png");
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + codUsu + "')]/td[4]/a")).click();
			driver.switchTo().alert().accept();

			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + codUsu + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			screenshotHelper.saveScreenshot("tipusExpedient/esborraPermisos3.png");

			assertFalse("No s'ha pogut esborrar el tipus d'expedient", isPresent);		
		}
	}
	
	public static void procesPrincipal() throws InterruptedException {
		// Marcar com procés principal d'un tipus d'expedient una determinada definició de procés 
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		// Accedir a l'opció de menú Tipus d'expedient
		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		// Obtenir nom del tipus d'expedient i cercar-lo
		String codTipExp = getProperty("deploy.tipus.expedient.codi");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha trobat el tipus d'expedient", isPresent);
		}
		else
		{
			screenshotHelper.saveScreenshot("tipusExpedient/procPrincipal1.png");
            
			// Accedir al tipus d'expedient i a la Definició de procés			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codTipExp + "')]/td[1]/a")).click();
			driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/definicioProcesLlistat.html')]")).click();			
			
			screenshotHelper.saveScreenshot("tipusExpedient/procPrincipal2.png");
			
			// Obtenir nom de la definició de procés i cercar-lo
			String nomDefProces = getProperty("deploy.definicio.proces.nom");
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			boolean isPresent1 = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProces + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			if (!isPresent1) {
				assertFalse("No s'ha trobat la definició de procés", isPresent1);
			}
			else
			{
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProces + "')]/td[4]/form/button")).click();				

				screenshotHelper.saveScreenshot("tipusExpedient/procPrincipal3.png");
			}
		}
	}

	public void crearEstat() throws InterruptedException {
		// Crea un estat d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/estats.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/estats/crea1.png");
		
		String codEstat = getProperty("tipexp.estat.codi1");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			driver.findElement(By.id("codi0")).sendKeys(codEstat);
			driver.findElement(By.id("nom0")).sendKeys(getProperty("tipexp.estat.nom1"));

			screenshotHelper.saveScreenshot("tipusExpedient/estats/crea2.png");

			// Botó crear 
	  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
			botoDeploy.click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/estats/crea3.png");
			  	    
			// Comprovar que s'ha creat
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (!isPresent) {
				assertFalse("No s'ha pogut crear l'estat del tipus d'expedient", isPresent);
			}					
		}
	}
	
	public void modificarEstat() throws InterruptedException {
		// Modifica el nom d'un estat d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/estats.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/estats/modif1.png");
		
		String codEstat = getProperty("tipexp.estat.codi1");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]/td[2]")).getText().trim();			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]/td[1]/a")).click();
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys("Revisió documentació sol·licitud");

			screenshotHelper.saveScreenshot("tipusExpedient/estats/modif2.png");

			// Botó crear 
	  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
			botoDeploy.click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/estats/modif3.png");
			  	    
			// Comprovar que s'ha modificat
			String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]/td[2]")).getText().trim();
			assertNotEquals("No s'ha pogut modificar l'estat del tipus d'expedient", valIni, valFin);	
		}
		else {
			fail("L'estat del tipus d'expedient no existeix");
		}
	}	
	
	private void esborrarEstat() throws InterruptedException {
		// Esborra un estat d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/estats.html')]")).click();
		
		String codEstat = getProperty("tipexp.estat.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			screenshotHelper.saveScreenshot("tipusExpedient/estats/elimina1.png");			

			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]/td[3]/a")).click();
			driver.switchTo().alert().accept();

			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEstat + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			screenshotHelper.saveScreenshot("tipusExpedient/estats/elimina2.png");

			assertFalse("No s'ha pogut esborrar l'estat del tipus d'expedient", isPresent);
		}
		else {
			fail("L'estat del tipus d'expedient no existeix");
		}
	}	

	public void crearEnumTipExp() throws InterruptedException {
		// Crea una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/crea1.png");
		
		String codEnum = getProperty("tipexp.enum.codi1");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			driver.findElement(By.id("codi0")).sendKeys(codEnum);
			driver.findElement(By.id("nom0")).sendKeys(getProperty("tipexp.enum.nom1"));

			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/crea2.png");

			// Botó crear 
	  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
			botoDeploy.click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/crea3.png");
			  	    
			// Comprovar que s'ha creat
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (!isPresent) {
				assertFalse("No s'ha pogut crear l'enumeració del tipus d'expedient", isPresent);
			}					
		}
	}
	
	public void modificarEnumTipExp() throws InterruptedException {
		// Modifica el nom una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modif1.png");
		
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[2]")).getText().trim();			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[1]/a")).click();
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys("Tipus d'expedient");

			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modif2.png");

			// Botó crear 
	  	    WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
			botoDeploy.click();
			
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modif3.png");
			  	    
			// Comprovar que s'ha modificat
			String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[2]")).getText().trim();
			assertNotEquals("No s'ha pogut modificar l'enumeració del tipus d'expedient", valIni, valFin);	
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}
	
	public void crearValEnumTipExp() throws InterruptedException {
		// Assigna valors a una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/creaVal1.png");
		
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[3]/form/button")).click();
			actions.build().perform();
			driver.findElement(By.id("codi0")).sendKeys("A");
			driver.findElement(By.id("nom0")).sendKeys("Tipus A");
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/creaVal2.png");
			WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click();

			driver.findElement(By.id("codi0")).sendKeys("B");
			driver.findElement(By.id("nom0")).sendKeys("Tipus B");
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/creaVal3.png");
			boto = driver.findElement(By.xpath("//button[@value='submit']"));
			boto.click();
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/creaVal4.png");
			
			// Comprovar que s'han creat els elements de l'enumeració
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			boolean isPresent2 = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'B')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (!isPresent || !isPresent2) {
				assertFalse("No s'han pogut crear els elements de l'enumeració del tipus d'expedient", isPresent);
			}		

			boto = driver.findElement(By.xpath("//button[@value='cancel']"));
			boto.click();
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/creaVal5.png");
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}	
	
	public void modificarValEnumTipExp() throws InterruptedException {
		// Modifica valors d'una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modifVal1.png");

		// cercar enumeració
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[3]/form/button")).click();
			actions.build().perform();
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modifVal2.png");

			// cercar valor a modificar	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (isPresent) {
				// modificar valor
				String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[2]")).getText().trim();
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[1]/a")).click();
				driver.findElement(By.id("nom0")).clear();
				driver.findElement(By.id("nom0")).sendKeys("Tipus expedient A");
				screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modifVal3.png");
				WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
				boto.click();
				screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/modifVal4.png");

				// Comprovar que s'ha modificat
				String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[2]")).getText().trim();
				assertNotEquals("No s'ha pogut modificar el valor de l'enumeració del tipus d'expedient", valIni, valFin);	}
			else {
				fail("El valor de l'enumeració del tipus d'expedient no existeix");
			}
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}	
	

	public void ordenarValEnumTipExp() throws InterruptedException {
		// Ordena valors d'una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/ordreVal1.png");

		// cercar enumeració
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[3]/form/button")).click();
			actions.build().perform();
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/ordreVal2.png");

			// cercar valor a ordenar	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (isPresent) {
				// guardar ordre inicial
				String valIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[3]")).getText().trim();
				// ordenar valor
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[4]/a[2]")).click();
				screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/ordreVal3.png");

				// Comprovar que s'ha modificat l'ordre
				String valFin = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[3]")).getText().trim();
				assertNotEquals("No s'ha pogut ordenar el valor de l'enumeració del tipus d'expedient", valIni, valFin);	}
			else {
				fail("El valor de l'enumeració del tipus d'expedient no existeix");
			}
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}	

	public void esborrarValEnumTipExp() throws InterruptedException {
		// Esborra valors a una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/elimVal1.png");

		// cercar enumeració
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[3]/form/button")).click();
			actions.build().perform();
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/elimVal2.png");

			// cercar valor a esborrar	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			if (isPresent) {
				// esborrar valor
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]/td[5]/a")).click();
				driver.switchTo().alert().accept();
				screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/elimVal3.png");

				// comprovar que s'ha esborrat el valor
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'A')]")).size() > 0;
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				assertFalse("No s'ha pogut esborrar el valor de l'enumeració del tipus d'expedient", isPresent);
			}
			else {
				fail("El valor de l'enumeració del tipus d'expedient no existeix");
			}
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}	

	private void esborrarEnumTipExp() throws InterruptedException {
		// Esborra una enumeració d'un tipus d'expedient
		seleccionarTipExp();	
		driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/enumeracioLlistat.html')]")).click();
	
		String codEnum = getProperty("tipexp.enum.codi1");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/elimina1.png");			

			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]/td[4]/a")).click();
			driver.switchTo().alert().accept();

			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codEnum + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			screenshotHelper.saveScreenshot("tipusExpedient/enumeracio/elimina2.png");

			assertFalse("No s'ha pogut esborrar l'enumeració del tipus d'expedient", isPresent);
		}
		else {
			fail("L'enumeració del tipus d'expedient no existeix");
		}
	}	


	
// *******************************************************
/*
	private void borrarTipoExpedienteTest() {
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[6]/a")).click();
		screenshotHelper.saveScreenshot("tipusExpedient/esborraTipExp1.png");
		driver.switchTo().alert().accept();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("tipusExpedient/esborraTipExp2.png");

		assertFalse("No s'ha pogut esborrar el tipus d'expedient", isPresent);
	}

	public void asignarMiembro() throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		// Miembros
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[5]/form/button")).click();
		actions.build().perform();
		driver.findElement(By.id("suggest_persona0")).clear();
		driver.findElement(By.id("suggest_persona0")).sendKeys(getProperty("test.base.usuari.configuracio"));
		boolean isPresent = driver.findElements(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;

		if (!isPresent) {
			assertTrue("No existe ese usuario", isPresent);
		}

		driver.findElement(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).click();
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertTrue("No s'ha pogut asignar el permiso al usuario", isPresent);
		}
	}

	public void borrarMiembro() throws InterruptedException {
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[3]/a")).click();
		driver.switchTo().alert().accept();
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		assertFalse("No s'ha pogut esborrar el permiso al usuario", isPresent);

		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
*/
}
