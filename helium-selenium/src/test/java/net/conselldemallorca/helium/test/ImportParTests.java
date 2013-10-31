package net.conselldemallorca.helium.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class ImportParTests extends BaseTest{
	
//	public WebElement fluentWait(final By locator){
//		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
//				.withTimeout(30, TimeUnit.SECONDS)
//				.pollingEvery(5, TimeUnit.SECONDS)
//				.ignoring(NoSuchElementException.class);
//
//		WebElement element = wait.until(
//				new Function<WebDriver, WebElement>() {
//					public WebElement apply(WebDriver driver) {
//						return driver.findElement(locator);
//					}
//				}
//				);
//		return element;
//	}
	

	@Override
	protected void runTests() throws InterruptedException {
		testImportPar();
	}
	
	// TESTS 
	// --------------------------------------------------------------------------------------------------------------
	
	public void testImportPar() throws InterruptedException {
		
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
		
		WebElement selectTipusExpedient = driver.findElement(By.id("expedientTipusId0"));
		List<WebElement> allOptions = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
		    if (option.getText().equals(properties.getProperty("deploy.tipus.expedient.nom"))) {
		    	option.click();
		    	break;
		    }
		}
		
		WebElement arxiu = driver.findElement(By.id("arxiu0"));
		arxiu.sendKeys(properties.getProperty("deploy.arxiu.path.windows"));
		
		screenshotHelper.saveScreenshot("importPar/importPar_02.png");
		
		WebElement botoDeploy = driver.findElement(By.xpath("//button[@value='submit']"));
		botoDeploy.click();
				
		screenshotHelper.saveScreenshot("importPar/importPar_03.png");
		
		menuDisseny = driver.findElement(By.id("menuDisseny"));
		WebElement menuDefProces = driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]"));
		
		actions = new Actions(driver);
		actions.moveToElement(menuDisseny);
		actions.build().perform();

		actions.moveToElement(menuDefProces);
		actions.click();
		actions.build().perform();
		
		int novaVersio = 0;
		
		WebElement elVersio = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'sion005')]/td[2]"));
		if (elVersio != null) {
			novaVersio = Integer.parseInt(elVersio.getText().trim());
		}
		
		// assertEquals("La versió resultant no és correcta", versio + 1, novaVersio);
	}
}
