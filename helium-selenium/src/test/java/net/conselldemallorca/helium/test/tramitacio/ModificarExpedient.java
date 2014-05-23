package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModificarExpedient extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	
	static String entornActual;

	@Test
	public void a_modificarInfoExp() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesDefPro(nomDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("accions/executar/1.png");
		
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();

		driver.findElement(By.xpath("//*[@id='numero0']")).clear();
		driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(res[0]);
		
		driver.findElement(By.xpath("//*[@id='titol0']")).clear();
		driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(res[1]);
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();
		String numero = "SE-8-9-1000";
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		// Empezamos a modificar los datos
		driver.findElement(By.xpath("//*[@id='content']/form[1]/button")).click();

		if (driver.findElements(By.xpath("//*[@id='numero0']")).size() > 0) {
			driver.findElement(By.xpath("//*[@id='numero0']")).clear();
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(numero);
		} else {
			numero = null;
		}
		
		String titulo = "Un título";
		if (driver.findElements(By.xpath("//*[@id='titol0']")).size() > 0) {
			driver.findElement(By.xpath("//*[@id='titol0']")).clear();
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(titulo);
		} else {
			titulo = null;
		}
		driver.findElement(By.xpath("//*[@id='dataInici0']")).clear();
		driver.findElement(By.xpath("//*[@id='dataInici0']")).click();
		driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/table/tbody/tr[3]/td[2]")).click();
		driver.findElement(By.xpath("//*[@id='dataInici0']")).sendKeys(Keys.ESCAPE);
		if (driver.findElement(By.id("suggest_responsableCodi0_delete")) != null)
			driver.findElement(By.id("suggest_responsableCodi0_delete")).click();
		driver.findElement(By.xpath("//*[@id='suggest_responsableCodi0']")).clear();
		driver.findElement(By.xpath("//*[@id='suggest_responsableCodi0']")).sendKeys("admin");
		driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();
		driver.findElement(By.xpath("//*[@id='comentari0']")).clear();
		driver.findElement(By.xpath("//*[@id='comentari0']")).sendKeys("Un comentario");
		
		driver.findElement(By.xpath("//*[@id='geoReferencia0']")).clear();
		driver.findElement(By.xpath("//*[@id='geoReferencia0']")).sendKeys("geoReferencia");
		driver.findElement(By.xpath("//*[@id='grupCodi0']")).clear();
		driver.findElement(By.xpath("//*[@id='grupCodi0']")).sendKeys("grupCodi");

		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();

		// Comprobamos

		existeixElementAssert("//*[@id='infos']", "No se guardó la información");

		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/2.png");
		
		String comentari = driver.findElement(By.xpath("//*[@class='missatgesComment']")).getText();
		assertTrue("Error al comprobar el comentario", comentari.endsWith("Un comentario"));

		WebElement element = driver.findElement(By.xpath("//*[@id='content']/dl"));

		List<WebElement> claves = element.findElements(By.xpath("dt"));
		List<WebElement> valores = element.findElements(By.xpath("dd"));
		for (int i = 0; i < claves.size(); i++) {
			boolean result = true;
			if ("Número".equals(claves.get(i).getText()) && numero != null)
				result = numero.equals(valores.get(i).getText());
			else if ("Títol".equals(claves.get(i).getText()) && titulo != null)
				result = titulo.equals(valores.get(i).getText());
			else if ("Iniciat per".equals(claves.get(i).getText()))
				result = !valores.get(i).getText().isEmpty();
			else if ("Iniciat el".equals(claves.get(i).getText()))
				result = !valores.get(i).getText().isEmpty();
			else if ("Persona responsable".equals(claves.get(i).getText()))
				result = !valores.get(i).getText().isEmpty();
			else if ("Georeferència".equals(claves.get(i).getText()))
				result = "geoReferencia".equals(valores.get(i).getText());

			assertTrue("Error al comprobar el campo " + claves.get(i).getText(), result);
		}

		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/3.png");
		
		eliminarExpedient(numero, titulo);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/4.png");
	}
}
