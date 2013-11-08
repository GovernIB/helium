package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class InicialitzarTests extends BaseTest {
	@Override
	protected void runTests() throws InterruptedException {
		testEntornTest();
	}
	
	// TESTS 
	// --------------------------------------------------------------------------------------------------------------
	
	public static void testEntornTest() throws InterruptedException {
		
		String entorn = properties.getProperty("entorn.nom");
		String usuari = properties.getProperty("test.base.usuari.configuracio");
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				
		screenshotHelper.saveScreenshot("inicialitzar/entornsExistents.png");
		
		if (!isPresent) {
			// Entorn no existeix. Cream entorn
			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			
			driver.findElement(By.id("codi0")).sendKeys(entorn);
			driver.findElement(By.id("nom0")).sendKeys(entorn);
						
			screenshotHelper.saveScreenshot("inicialitzar/entornCrear_01.png");
			driver.findElement(By.xpath("//button[@value='submit']")).click();

			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			screenshotHelper.saveScreenshot("inicialitzar/entornCrear_02.png");
			
			assertTrue("No s'ha pogut crear l'entorn de test", isPresent);
		}
		// Comprovam permisos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
		screenshotHelper.saveScreenshot("inicialitzar/entornPermisos_03.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		if (!isPresent) {
			// No té permisos. Els assignam
			driver.findElement(By.id("nom0")).sendKeys(usuari);
			driver.findElement(By.id("permisos00")).click();
			driver.findElement(By.id("permisos01")).click();
			driver.findElement(By.id("permisos02")).click();
			driver.findElement(By.id("permisos03")).click();
			screenshotHelper.saveScreenshot("inicialitzar/entornPermisos_04.png");
			
			driver.findElement(By.xpath("//button[@value='submit']")).click();
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + usuari + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			screenshotHelper.saveScreenshot("inicialitzar/entornPermisos_05.png");
			
			assertTrue("No s'han pogut assignar permisos a l'entorn de test", isPresent);
		}
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
}
