package net.conselldemallorca.helium.test.tramitacio;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesSubprocessExpedient extends BaseTest {	
	
	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.definicio.subproces.main.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietat("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	static String entornActual;
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorno(entorn);

		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_pas_proces_subproces() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorno(entorn);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
					
		String[] res = iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();		
		if (res[1] != null)
			driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(res[1]);
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(tipusExp)) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/2.png");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[5]/button[1]")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a", "No se encontró la tarea: 01 - Entrada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Subproceso')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya una tarea del subproceso		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();		
		if (res[1] != null)
			driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(res[1]);
		
		selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
		options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(tipusExp)) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/2.png");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[5]/button[1]")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a", "No se encontró la tarea: tasca1_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya la 2ª tarea del subproceso		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();		
		if (res[1] != null)
			driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(res[1]);
		
		selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
		options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(tipusExp)) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/2.png");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[5]/button[1]")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a", "No se encontró la tarea: tasca2_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que vuelva al proceso principal	
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();		
		if (res[1] != null)
			driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(res[1]);
		
		selectTipusExpedient = driver.findElement(By.xpath("//*[@id='tipusExpedient0']"));
		options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(tipusExp)) {
				option.click();
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/2.png");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id='command']/div[2]/div[5]/button[1]")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a", "No se encontró la tarea: 02 - Tancament");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
	}
	
	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorno(entorn);
		
		eliminarExpedient(null, null, tipusExp);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
