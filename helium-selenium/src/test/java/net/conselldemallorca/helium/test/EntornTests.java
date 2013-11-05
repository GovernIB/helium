package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class EntornTests extends BaseTest{
	
	@Override
	protected void runTests() throws InterruptedException {
		super.runTests();
		
		testEntornCrear(true);
		testEntornPermisos(true);
		testEntornPermisos(false);
		testEntornSeleccionar(true);
		testEntornSeleccionar(false);
		testEntornDefault();
		testEntornEsborrarPermisos(true);
		testEntornEsborrarPermisos(false);
		//testEntornReindexar();
		testEntornCanviTitol();
		testEntornCanviActiu();
		testEntornBorrar();
		testEntornCrear(false);
		testEntornBorrar();	
	}
	
	// TESTS A NIVELL D'ENTORN
	// --------------------------------------------------------------------------------------------------------------
	
	public static void testEntornCrear(boolean actiu) throws InterruptedException {
    // Crear un entorn actiu. Si existeix, primer l'esborra
		
		//screenshotHelper.saveScreenshot("entorns/crear/entornsExistents_00.png");
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/crear/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("entorns/crear/entornsExistents_02.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar l'entorn", isPresent);
		}
		// Entorn no existeix. Cream entorn
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
			
		driver.findElement(By.id("codi0")).sendKeys(entorn);
		driver.findElement(By.id("nom0")).sendKeys(entorn);
		if (!actiu) 				
			driver.findElement(By.id("actiu0")).click();
		
		screenshotHelper.saveScreenshot("entorns/crear/entornCrear" + (actiu ? "Actiu" : "Inactiu") + "_01.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
		screenshotHelper.saveScreenshot("entorns/crear/entornCrear" + (actiu ? "Actiu" : "Inactiu") + "_02.png");
			
		assertTrue("No s'ha pogut crear l'entorn", isPresent);
	}
	
	public static void testEntornSeleccionar(boolean directe) throws InterruptedException {
	// Seleccionar l'entorn de proves	
		
		String entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();
		WebElement menuEntorn = driver.findElement(By.id("menuEntorn"));

		screenshotHelper.saveScreenshot("entorns/seleccionar/" + (directe ? "directe" : "form") + "/entornActual.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			if (directe) {
				Actions actions = new Actions(driver);
				actions.moveToElement(menuEntorn);
				actions.build().perform();
	
				actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
				actions.click();
				actions.build().perform();
			
				screenshotHelper.saveScreenshot("entorns/seleccionar/" + (directe ? "directe" : "form") + "/entornActiu.png");
			
				assertEquals("No s'ha pogut seleccionar l'entorn.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
				
//				menuEntorn = driver.findElement(By.id("menuEntorn"));
//				actions = new Actions(driver);
//				actions.moveToElement(menuEntorn);
//				actions.build().perform();
//	
//				actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entornActual + "')]/a")));
//				actions.click();
//				actions.build().perform();
				
			} else {
				menuEntorn.findElement(By.tagName("a")).click();
				screenshotHelper.saveScreenshot("entorns/seleccionar/" + (directe ? "directe" : "form") + "/entorns.png");
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[3]/form/button")).click();
				
				screenshotHelper.saveScreenshot("entorns/seleccionar/" + (directe ? "directe" : "form") + "/entornActiu.png");
				
				assertEquals("No s'ha pogut seleccionar l'entorn.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
				
				menuEntorn = driver.findElement(By.id("menuEntorn"));
				menuEntorn.findElement(By.tagName("a")).click();
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[3]/form/button")).click();
			}
		} else {
			fail("No existeix l'entorn de proves");
		}
	}
	
	public void testEntornDefault() throws InterruptedException {
	// Marcar entorn per defecte
		
		String entornActual = driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim();

		screenshotHelper.saveScreenshot("entorns/default/entornActual.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		if (isPresent) {
			driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
			String src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a/img")).getAttribute("src");
			
			if (src.endsWith("star.png")) {
				fail("L'entorn ja est‡† marcat per defecte");
			} else {
				
				screenshotHelper.saveScreenshot("entorns/default/entornDefecte_01.png");
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a")).click();
				
				driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
				screenshotHelper.saveScreenshot("entorns/default/entornDefecte_02.png");
				
				src = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entorn + "')]/td[1]/a/img")).getAttribute("src");
				assertTrue("No s'ha pogut marcar l'entorn per defecte.", src.endsWith("star.png"));
				
				driver.findElement(By.id("menuEntorn")).findElement(By.tagName("a")).click();
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + entornActual + "')]/td[1]/a")).click();
			}
		} else {
			fail("No existeix l'entorn de proves");
		}
	}
		

	public static void testEntornBorrar() throws InterruptedException {
    // Esborrar un entorn
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/borrar/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[6]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("entorns/borrar/entornsExistents_02.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'ha pogut eliminar l'entorn", isPresent);
		} else {
			fail("Entorn no existeix");
		}
	}
	
	
	public void testEntornCanviTitol() throws InterruptedException {
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/titol/1entornsExistents_01.png");
		String titolEntorn = getProperty("entorn.titol");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			fail("L'entorn no existeix");
		} else {	
			String titol = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[2]")).getText().trim();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
			screenshotHelper.saveScreenshot("entorns/titol/2canviTitol.png");
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(titolEntorn);

			screenshotHelper.saveScreenshot("entorns/titol/3canviTitol.png");
			driver.findElement(By.xpath("//button[@value='submit']")).click();
	
			screenshotHelper.saveScreenshot("entorns/titol/4entornsExistents_02.png");
			String nouTitol = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[2]")).getText().trim();
			assertNotEquals("No s'ha pogut canviar el tÌtol de l'entorn de test", titol, nouTitol);
		}
	}
	
	
	public void testEntornCanviActiu() throws InterruptedException {
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/actiu/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (!isPresent) {
			fail("Entorn no existeix");
		} else {
			String actiu = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[3]")).getText().trim();
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[1]/a")).click();
			driver.findElement(By.id("actiu0")).click();
			
			screenshotHelper.saveScreenshot("entorns/actiu/entornCanvi.png");
			driver.findElement(By.xpath("//button[@value='submit']")).click();
	
			screenshotHelper.saveScreenshot("entorns/actiu/entornsExistents_02.png");
			String nouActiu = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[3]")).getText().trim();
			assertNotEquals("No s'ha pogut canviar l'estat actiu de l'entorn de test", actiu, nouActiu);
		}
	}
	
	public void testEntornReindexar() throws InterruptedException {
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/borrar/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[5]/form/button")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("entorns/borrar/entornsExistents_02.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@class='missatgesOk']")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertTrue("No s'ha pogut reindexar l'entorn", isPresent);
		} else {
			fail("Entorn no existeix");
		}
	}
	
	public static void testEntornPermisos(boolean isUser) throws InterruptedException {
		String userol = isUser ? properties.getProperty("test.base.usuari.configuracio") : properties.getProperty("test.base.rol.configuracio");
		String tipus = "crear/" + (isUser ? "usuari" : "rol");
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
			screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_01.png");
			
			// Crear els permis READ
			updatePermisos(isUser, userol, false, false, false, true, tipus, 3, new String[]{"+READ"}, true);
			// ORGANIZATION, DESIGN i READ
			updatePermisos(isUser, userol, true, false, true, false, tipus, 4, new String[]{"+READ", "+DESIGN", "+ORGANIZATION"}, false);
			// ADMINISTRATION
			updatePermisos(isUser, userol, false, true, false, false, tipus, 5, new String[]{"+ADMINISTRATION"}, true);
			// DESIGN i READ
			updatePermisos(isUser, userol, false, false, true, true, tipus, 6, new String[]{"+DESIGN", "+READ"}, true);
			// TOTS
			updatePermisos(isUser, userol, true, true, false, false, tipus, 7, new String[]{"+DESIGN", "+READ", "+ORGANIZATION", "+ADMINISTRATION"}, false);
		} else {
			fail("Entorn no existeix");
		}
	}
	
	private static void updatePermisos(
			boolean isUser, 
			String userol, 
			boolean organization, 
			boolean administration, 
			boolean design, 
			boolean read, 
			String tipus,
			int pos,
			String[] permisosEsperats,
			boolean borraActuals) {
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		if (isPresent && borraActuals) {
			// eliminam els permisos actuals
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]/td[4]/a")).click();
			driver.switchTo().alert().accept();
			screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_02.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			assertFalse("No s'han pogut eliminar els permisos", isPresent);
		}
		
		driver.findElement(By.id("nom0")).sendKeys(userol);
		if (design)	
			driver.findElement(By.xpath("//input[@value='DESIGN']")).click();
		if (organization)	
			driver.findElement(By.xpath("//input[@value='ORGANIZATION']")).click();
		if (read)			
			driver.findElement(By.xpath("//input[@value='READ']")).click();
		if (administration)			
			driver.findElement(By.xpath("//input[@value='ADMINISTRATION']")).click();
		if (!isUser)
			driver.findElement(By.id("usuari0")).click();
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_afegir_0" + pos + ".png");
		
		driver.findElement(By.xpath("//button[@value='submit']")).click();
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_0" + pos + ".png");
		assertTrue("No s'han pogut assignar permisos a l'entorn de test", isPresent);
		
		String[] permisos = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]/td[3]")).getText().trim().split(" ");
		
		Arrays.sort(permisos);
		Arrays.sort(permisosEsperats);
		assertArrayEquals("No s'han assignar els permisos correctament", permisosEsperats, permisos);
	}
	
	public void testEntornEsborrarPermisos(boolean isUser) throws InterruptedException {
		
		String userol = isUser ? properties.getProperty("test.base.usuari.configuracio") : properties.getProperty("test.base.rol.configuracio");
		String tipus = "borrar/" + (isUser ? "usuari" : "rol");
		
		WebElement menuConfiguracio = driver.findElement(By.id("menuConfiguracio"));
		WebElement menuEntorn = driver.findElement(By.xpath("//a[contains(@href, '/helium/entorn/llistat.html')]"));

		Actions actions = new Actions(driver);
		actions.moveToElement(menuConfiguracio);
		actions.build().perform();

		actions.moveToElement(menuEntorn);
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/entornsExistents_01.png");
		
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]")).size() > 0;
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		if (isPresent) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + entorn + "')]/td[4]/form/button")).click();
			screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_01.png");
			
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]")).size() > 0;
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			
			if (isPresent) {
				// eliminam els permisos actuals
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]/td[4]/a")).click();
				driver.switchTo().alert().accept();
				screenshotHelper.saveScreenshot("entorns/permisos/" + tipus + "/permisos_02.png");
				
				driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
				isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2],'" + userol + "')]")).size() > 0;
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				
				assertFalse("No s'han pogut eliminar els permisos", isPresent);
			} else {
				fail(userol + " no t√© permisos per aquuest entorn.");
			}
		} else {
			fail("Entorn no existeix");
		}
	}

}
