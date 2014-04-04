package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CrearGrupoTests extends BaseTest {
	
//	@Override
//	protected void runTests() throws InterruptedException {
//		super.runTests();
//		
//		EntornTests.testEntornCrear(true);
//		EntornTests.testEntornPermisos(true);
//		EntornTests.testEntornSeleccionar(true);
//		crearTipoGrupoTest();
//		crearGrupoTest();
//		asignarMiembro();
//		borrarMiembro();
//		borrarGrupo();
//		borrarTipoGrupo();		
//		EntornTests.testEntornBorrar();
//	}
//
//	// TESTS 
//	// --------------------------------------------------------------------------------------------------------------
//	
//	public void crearTipoGrupoTest() throws InterruptedException {
//		WebElement menuGrupoArea = driver.findElement(By.id("menuOrganitzacio"));
//		WebElement menuTipoArea = driver.findElement(By.xpath("//a[contains(@href, '/helium/areaTipus/llistat.html')]"));
//
//		Actions actions = new Actions(driver);
//		actions.moveToElement(menuGrupoArea);
//		actions.build().perform();
//
//		actions.moveToElement(menuTipoArea);
//		actions.click();
//		actions.build().perform();
//		
//		String nombreTipoGrupo = getProperty("test.base.group.tipo");
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoGrupo + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//				
//		screenshotHelper.saveScreenshot("inicialitzar/tiposGruposExistents.png");
//		
//		if (!isPresent) {
//			// Si no existe el tipo de grupo, lo creamos
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
//			
//			driver.findElement(By.id("codi0")).sendKeys(nombreTipoGrupo);
//			driver.findElement(By.id("nom0")).sendKeys(nombreTipoGrupo);
//
//			driver.findElement(By.id("descripcio0")).sendKeys("La descripci�n del tipo de grupo");	
//			
//			screenshotHelper.saveScreenshot("inicialitzar/tiposGruposCrear_01.png");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//		}
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoGrupo + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//		
//		if (!isPresent) {
//			assertFalse("No s'ha pogut crear l'tipo de grupo", isPresent);
//		}
//	}
//	
//	public void crearGrupoTest() throws InterruptedException {
//		WebElement menuGrupoArea = driver.findElement(By.id("menuOrganitzacio"));
//		WebElement menuArea = driver.findElement(By.xpath("//a[contains(@href, '/helium/area/llistat.html')]"));
//		actions.moveToElement(menuGrupoArea);
//		actions.build().perform();
//
//		actions.moveToElement(menuArea);
//		actions.click();
//		actions.build().perform();
//		
//		String nombreGrupo = getProperty("test.base.group");
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreGrupo + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//				
//		screenshotHelper.saveScreenshot("inicialitzar/gruposExistents.png");
//		
//		if (!isPresent) {
//			// Si no existe el grupo, lo creamos
//			driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
//			
//			driver.findElement(By.id("codi0")).sendKeys(nombreGrupo);
//			driver.findElement(By.id("nom0")).sendKeys(nombreGrupo);	
//			
//			WebElement selectTipusExpedient = driver.findElement(By.id("tipus0"));
//			List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
//			for (WebElement option : allOptions) {
//			    if (option.getText().equals(getProperty("test.base.group.tipo"))) {
//			    	option.click();
//			    	break;
//			    }
//			}
//
//			driver.findElement(By.id("descripcio0")).sendKeys("La descripci�n");	
//			
//			screenshotHelper.saveScreenshot("inicialitzar/grupoCrear_01.png");
//			driver.findElement(By.xpath("//button[@value='submit']")).click();
//		}
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreGrupo + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//		
//		if (!isPresent) {
//			assertTrue("No s'ha pogut crear l'entorn de test", isPresent);
//		}
//	}
//	
//	public void asignarMiembro() throws InterruptedException {
//		// Miembros
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.group") + "')]/td[5]/form/button")).click();
//		actions.build().perform();		
//		driver.findElement(By.id("suggest_persona0")).clear();
//		driver.findElement(By.id("suggest_persona0")).sendKeys(getProperty("test.base.usuari.configuracio"));		
//		boolean isPresent = driver.findElements(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'"+getProperty("test.base.usuari.configuracio.nom")+"')]")).size() > 0;
//		
//		if (!isPresent) {
//			assertTrue("No existe ese usuario", isPresent);
//		}
//		
//		driver.findElement(By.xpath("//div[@class='ac_results']/ul/li[contains(text(),'"+getProperty("test.base.usuari.configuracio.nom")+"')]")).click();
//		driver.findElement(By.xpath("//button[@value='submit']")).click();	
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//		
//		if (!isPresent) {
//			assertTrue("No s'ha pogut asignar el permiso al usuario", isPresent);
//		}
//	}
//	
//	public void borrarMiembro() throws InterruptedException {
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.usuari.configuracio.nom") + "')]/td[3]/a")).click();
//		driver.switchTo().alert().accept();		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.usuari.configuracio.nom") + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//		
//		assertFalse("No s'ha pogut borrar el permiso al usuario", isPresent);
//		
//		driver.findElement(By.xpath("//button[@value='cancel']")).click();	
//	}
//
//	private void borrarGrupo() {
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.group") + "')]/td[6]/a")).click();
//		driver.switchTo().alert().accept();
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + getProperty("test.base.group") + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//				
//		screenshotHelper.saveScreenshot("inicialitzar/gruposExistents.png");
//		
//		assertFalse("No s'ha pogut borrar el grupo", isPresent);		
//	}
//	
//	private void borrarTipoGrupo() {
//		WebElement menuGrupoArea = driver.findElement(By.id("menuOrganitzacio"));
//		WebElement menuTipoArea = driver.findElement(By.xpath("//a[contains(@href, '/helium/areaTipus/llistat.html')]"));
//
//		Actions actions = new Actions(driver);
//		actions.moveToElement(menuGrupoArea);
//		actions.build().perform();
//
//		actions.moveToElement(menuTipoArea);
//		actions.click();
//		actions.build().perform();
//		
//		String nombreTipoGrupo = getProperty("test.base.group.tipo");
//		
//		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//		boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoGrupo + "')]")).size() > 0;
//		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//				
//		screenshotHelper.saveScreenshot("inicialitzar/tiposGruposExistents.png");
//		
//		if (isPresent) {
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoGrupo + "')]/td[3]/a")).click();
//			driver.switchTo().alert().accept();
//			
//			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//			isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nombreTipoGrupo + "')]")).size() > 0;
//			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//					
//			screenshotHelper.saveScreenshot("inicialitzar/gruposExistents.png");
//			
//			assertFalse("No s'ha pogut borrar el tipo de grupo", isPresent);	
//		}
//	}
}
