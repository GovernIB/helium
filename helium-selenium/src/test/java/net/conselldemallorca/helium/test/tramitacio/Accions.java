package net.conselldemallorca.helium.test.tramitacio;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Accions extends BaseTest {

	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	static String entornActual;

	@Test
	public void a_executar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesExpedient(nomDefProc, exportDefProc);
		
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
		
		screenshotHelper.saveScreenshot("accions/executar/2.png");

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		driver.findElement(By.xpath("//button[contains(text(),'acc_prueba')]")).click();
		
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la acción correctamente");
				
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("accions/executar/3.png");
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("accions/executar/4.png");
	}

	@Test
	public void b_comprobar_publica() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesExpedient(nomDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("accions/ocultar/1.png");

		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		// Ponemos la acción como pública
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[7]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'acc_prueba')]/a")).click();
		
		if (!driver.findElement(By.xpath("//*[@id='publica0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='publica0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");		
		
		// Cambiamos los permisos del usuario a READ para que no se muestre a no ser que sea una acción pública
		assignarPermisosTipusExpedient(codTipusExp, usuari, "SUPERVISION");
		
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
		
		screenshotHelper.saveScreenshot("accions/ocultar/3.png");

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		existeixElementAssert("//button[contains(text(),'acc_prueba')]", "No se mostró la acción");		
		
		// Ponemos la acción como no pública				
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[7]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'acc_prueba')]/a")).click();
		
		if (driver.findElement(By.xpath("//*[@id='publica0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='publica0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");		
		
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='numero0']")).clear();
		driver.findElement(By.xpath("//*[@id='numero0']")).sendKeys(res[0]);
		
		driver.findElement(By.xpath("//*[@id='titol0']")).clear();
		driver.findElement(By.xpath("//*[@id='titol0']")).sendKeys(res[1]);
		
		selectTipusExpedient = driver.findElement(By.xpath("//*[@id='expedientTipus0']"));
		options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(properties.getProperty("defproc.deploy.tipus.expedient.nom"))) {
				option.click();
				break;
			}
		}
						
		screenshotHelper.saveScreenshot("accions/ocultar/3.png");		

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		noExisteixElementAssert("//button[contains(text(),'acc_prueba')]", "No se ocultó la acción");
		
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("accions/ocultar/4.png");
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("accions/ocultar/5.png");
	}

	@Test
	public void c_comprobar_ocultar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesExpedient(nomDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("accions/ocultar/1.png");

		// Ocultamos la acción
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']/li[7]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'acc_prueba')]/a")).click();
		
		if (!driver.findElement(By.xpath("//*[@id='oculta0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='oculta0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");
		
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
		
		screenshotHelper.saveScreenshot("accions/ocultar/3.png");

		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[6]/button[1]")).click();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[6]/a/img")).click();

		noExisteixElementAssert("//button[contains(text(),'acc_prueba')]", "No se ocultó la acción");
		
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("accions/ocultar/4.png");
		
		eliminarDefinicioProces(nomDefProc);
		
		screenshotHelper.saveScreenshot("accions/ocultar/5.png");
	}
}
