package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class TipusExpedientTests extends BaseTest {

	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();

		EntornTests.testEntornSeleccionar(true);
		//crearTipoExpedienteTest();
		//assignarPermisosTipExp();
		procesPrincipal();
		//esborrarPermisosTipExp();
		//esborrarTipExp();

		//EntornTests.testEntornCrear(true);
		//EntornTests.testEntornPermisos(true);
		//EntornTests.testEntornSeleccionar(true);
		//crearTipoExpedienteTest();
		//asignarMiembro();
		//borrarMiembro();
		//borrarTipoExpedienteTest();
		//EntornTests.testEntornBorrar();
	}

	// TESTS A NIVELL DE TIPUS D'EXPEDIENT
	// --------------------------------------------------------------------------------------------------------------

	public void crearTipoExpedienteTest() throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp1.png");

		if (!isPresent) {
			// Si no existe el tipo de grupo, lo creamos
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();

			driver.findElement(By.id("codi0")).sendKeys(nombreTipoExpediente);
			driver.findElement(By.id("nom0")).sendKeys(nombreTipoExpediente);
			driver.findElement(By.id("expressioNumero0")).sendKeys("SE-$(seq)$(any)");

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
			driver.findElement(By.id("seleccionarAny0")).click();

			screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp2.png");
			
			driver.findElement(By.xpath("//button[@value='submit']")).click();

			screenshotHelper.saveScreenshot("tipusExpedient/creaTipExp3.png");
		}

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha pogut crear el tipus d'expedient", isPresent);
		}
	}

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
	
	
	private void esborrarTipExp() {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();

		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");

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

	public void assignarPermisosTipExp() throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tipusExpedient/assignaPermisos1.png");
		
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		// Botó permisos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[3]/form/button")).click();
		actions.build().perform();
				
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		// Si ja existeix l'usuari, seleccionar-lo
		if (!isPresent) {
			driver.findElement(By.id("nom0")).sendKeys(getProperty("test.base.usuari.configuracio"));
		}
		else {
			driver.findElement(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).click();
		}
		
		// marcar els permisos
		driver.findElement(By.id("permisos00")).click();
		driver.findElement(By.id("permisos01")).click();
		driver.findElement(By.id("permisos02")).click();
		driver.findElement(By.id("permisos03")).click();
		driver.findElement(By.id("permisos04")).click();
		driver.findElement(By.id("permisos05")).click();
		driver.findElement(By.id("permisos06")).click();
		driver.findElement(By.id("permisos07")).click();
		driver.findElement(By.id("permisos08")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/assignaPermisos2.png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("tipusExpedient/assignaPermisos3.png");
		
		if (!isPresent) {
			assertTrue("No s'han pogut assignar els permisos a l'usuari", isPresent);
		}
	}
	
	public void esborrarPermisosTipExp() throws InterruptedException {
		WebElement menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuTipExpedient = driver.findElement(By.xpath("//a[contains(@href, '/helium/expedientTipus/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuTipExpedient);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tipusExpedient/esborraPermisos1.png");
		
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		// Botó permisos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[3]/form/button")).click();
		actions.build().perform();

		String codUsu = getProperty("test.base.usuari.configuracio");
		
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
	
	public void procesPrincipal() throws InterruptedException {
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
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha trobat el tipus d'expedient", isPresent);
		}
		else
		{
			screenshotHelper.saveScreenshot("tipusExpedient/procPrincipal1.png");
            
			// Accedir al tipus d'expedient i a la Definició de procés			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[1]/a")).click();
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
}
