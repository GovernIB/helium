package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CrearTipoExpedienteTests extends BaseTest {

	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();

		EntornTests.testEntornCrear(true);
		EntornTests.testEntornPermisos(true);
		EntornTests.testEntornSeleccionar(true);
		crearTipoExpedienteTest();
		asignarMiembro();
		borrarMiembro();
		borrarTipoExpedienteTest();
		EntornTests.testEntornBorrar();
	}

	// TESTS
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

		screenshotHelper.saveScreenshot("inicialitzar/tipoExpedientExistents.png");

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

			driver.findElement(By.xpath("//button[@value='submit']")).click();

			screenshotHelper.saveScreenshot("inicialitzar/tipoExpedientCrear_01.png");
		}

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			assertFalse("No s'ha pogut crear l'tipo de expedient", isPresent);
		}
	}

	private void borrarTipoExpedienteTest() {
		String nombreTipoExpediente = getProperty("deploy.tipus.expedient.nom");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]/td[6]/a")).click();
		driver.switchTo().alert().accept();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoExpediente + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		screenshotHelper.saveScreenshot("inicialitzar/tipoExpedient.png");

		assertFalse("No s'ha pogut borrar el grupo", isPresent);
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

		assertFalse("No s'ha pogut borrar el permiso al usuario", isPresent);

		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
}
