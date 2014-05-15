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
public class AfegirExpedientRelacionat extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("entorn.nom", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.mod.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;

	@Test
	public void a_afegirExpedientRelacionat() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesExpedient(nomDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/1.png");
		
		String[] res_dest = iniciarExpediente(nomDefProc,codTipusExp,"SE-21/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		String[] res_orig = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );

		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();

		driver.findElement(By.xpath("//*[@id='numero0']")).clear();
		if (res_orig[0] != null)
			driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(res_orig[0]);
		
		driver.findElement(By.xpath("//*[@id='titol0']")).clear();
		if (res_orig[1] != null)
			driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(res_orig[1]);
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(nomTipusExp)) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/2.png");

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//*[@id='content']/form[2]/button")).click();

		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).clear();
		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).sendKeys("Expedient");
		driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();

		driver.findElement(By.xpath("//*[@id='relacionarCommand']/div/div[3]/button[1]")).click();
		
		assertTrue("El expediente no se relacionó de forma correcta", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() > 0);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/3.png");		
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[5]/a/img")).click();
		acceptarAlerta();
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		assertTrue("No se borró el expediente relacionado",!textoInfo.isEmpty());
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/4.png");
		
		eliminarExpedient(res_orig[0], res_orig[1]);
		
		eliminarExpedient(res_dest[0], res_dest[1]);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/5.png");
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/6.png");
	}
}
