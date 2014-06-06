package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpedientPestanyaTasques extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	@Test
	public void a_iniciar_expedient() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/iniciar_expedient/1.png");

		crearTipusExpedientTest(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, properties.getProperty("defproc.termini.exp.export.arxiu.path"));
		iniciarExpediente(nomDefProc, codTipusExp, "SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime());

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/iniciar_expedient/2.png");
	}
	
//	@Test
	public void b_delegar_tasca() throws InterruptedException {
		
	}
	
	@Test
	public void c_reasignar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/1.png");

		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();

		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[6]/a")).click();
		
		existeixElementAssert("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]", "No había ninguna tarea para reasignar");
		
		if (existeixElement("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")) {
			String responsableOriginal = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
			driver.findElement(By.xpath("//img[@alt='Reassignar'][1]")).click();
			driver.findElement(By.xpath("//*[@id='expression0']")).sendKeys("user(admin)");
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
			
			existeixElementAssert("//*[@id='infos']/p", "No se reasignó la tarea al usuario correctamente");
			
			String responsable = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
			
			assertTrue("Error al asignar el responsable", responsableOriginal.equals(responsable));
		
			driver.findElement(By.xpath("//img[@alt='Reassignar'][1]")).click();
			driver.findElement(By.xpath("//*[@id='expression0']")).sendKeys("group(admin)");
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
			
			existeixElementAssert("//*[@id='infos']/p", "No se reasignó la tarea al grupo correctamente");
			
//			String grupoOriginal = "";
//			String grupo = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
			
//			assertTrue("Error al asignar el responsable", grupoOriginal.equals(grupo));
		}
	}
	
	@Test
	public void d_suspendre_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/1.png");

		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();

		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[6]/a")).click();
		
		existeixElementAssert("//img[@alt='Suspendre'][1]", "No había ninguna tarea para suspender");
		
		if (existeixElement("//img[@alt='Suspendre'][1]")) {
			String id = driver.findElement(By.xpath("//img[@alt='Suspendre'][1]/parent::a/parent::td/parent::tr/td[1]")).getText();
			
			String flagOriginal = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
			
			assertTrue("Error la tasca ya tenía el flag de suspendida", !flagOriginal.contains("S"));
			
			driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[12]/a")).click();
			acceptarAlerta();
			
			String flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
			
			assertTrue("Error la tasca no tenía el flag de suspendida", flagActual.contains("S"));
			
			driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[12]/a")).click();
			acceptarAlerta();
			
			flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
			assertTrue("Error la tasca tenía el flag de suspendida", !flagActual.contains("S"));
		}
	}
		
	@Test
	public void e_cancellar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/1.png");

		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();

		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[6]/a")).click();
		
		existeixElementAssert("//img[@alt='Cancel·lar'][1]", "No había ninguna tarea para cancel·lar");
		
		if (existeixElement("//img[@alt='Cancel·lar'][1]")) {
			String id = driver.findElement(By.xpath("//img[@alt='Cancel·la'][1]/parent::a/parent::td/parent::tr/td[1]")).getText();
			
			String flagOriginal = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
			
			assertTrue("Error la tasca ya tenía el flag de cancel·lar", !flagOriginal.contains("C"));
			
			driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[13]/a")).click();
			acceptarAlerta();
			
			String flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
			
			assertTrue("Error la tasca no tenía el flag de cancel·lar", flagActual.contains("C"));
		}
	}
	
	@Test
	public void z_finalizar_expedient() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		eliminarExpedient(null, null);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("terminisexpedient/finalizar_documents/1.png");	
	}
}
