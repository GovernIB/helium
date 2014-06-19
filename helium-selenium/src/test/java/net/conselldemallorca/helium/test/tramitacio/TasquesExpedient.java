package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
public class TasquesExpedient extends BaseTest {
	
	String entorn = carregarPropietat("entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.definicio.subproces.main.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietat("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathAgafarAlliberar = carregarPropietat("tramsel_accio.deploy_agafar_alliberar.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietat("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String tipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	static String entornActual;
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_visualizar_tasques() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
				
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos sobre disseny entorn");
			
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']/ul/li[2]/a")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/1.png");
		
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
			
		// Tareas proceso principal
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		
		List<String> tareasPrincipal = new ArrayList<String>();
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {			
			tareasPrincipal.add(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText());
			i++;
		}
		
		assertTrue("No existían 2 tareas en el proceso principal", tareasPrincipal.size() == 2);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']/ul/li[2]/a")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/1.png");
		
		assertEquals("No s'ha pogut seleccionar l'entorn de forma directe.", entorn, driver.findElement(By.xpath("//div[@id='page-entorn-title']/h2/span")).getText().trim());
			
		// Tareas proceso principal
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomSubDefProc + "')]/td[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']/li[2]/a")).click();
		
		List<String> tareasSubproceso = new ArrayList<String>();
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {			
			tareasSubproceso.add(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText());
			i++;
		}
		
		assertTrue("No existían 2 tareas en el subproceso", tareasPrincipal.size() == 2);
		
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
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(0)+"')]/a", "No se encontró la tarea: "+tareasPrincipal.get(0)+"");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(0)+"')]/a")).click();
		
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
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(0)+"')]/a", "No se encontró la tarea: "+tareasSubproceso.get(0)+"");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(0)+"')]/a")).click();
		
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
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(1)+"')]/a", "No se encontró la tarea: "+tareasSubproceso.get(1)+"");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(1)+"')]/a")).click();
		
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
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a", "No se encontró la tarea: "+tareasPrincipal.get(1)+"");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
	}
	
	@Test
	public void c_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathAgafarAlliberar);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void d_agafar_alliberar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();

		screenshotHelper.saveScreenshot("TasquesExpedient/agafar_tasca/1.png");

		// Iniciamos el expediente y lo asignamos directamente a un grupo
		
		iniciarExpediente(nomDefProc,codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']/ul/li[2]/a")));
		actions.click();
		actions.build().perform();
		
		assertTrue("No había ninguna tarea para agafar", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		existeixElementAssert("//*[@id='registre']/tbody/tr/td[8]/form/button[contains(text(),'Agafar')][1]", "No había ninguna tarea para agafar");
		
		// Cogemos el nombre de la tarea y expediente 
		String tasca = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[8]/form/button[contains(text(),'Agafar')][1]/parent::form/parent::td/parent::tr/td[2]")).getText();
		String expedient = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[8]/form/button[contains(text(),'Agafar')][1]/parent::form/parent::td/parent::tr/td[3]")).getText();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[8]/form/button[contains(text(),'Agafar')][1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se cogió la tarea correctamente");
		
		// Comprobamos que aparezca en nuestras tareas
		actions.moveToElement(driver.findElement(By.id("menuTasques")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuTasques']/ul/li[1]/a")));
		actions.click();
		actions.build().perform();
		
		if ("Mostrar filtre".equals(driver.findElement(By.xpath("//*[@id='botoFiltres']")).getText().trim()))
			driver.findElement(By.xpath("//*[@id='botoFiltres']")).click();

		driver.findElement(By.xpath("//*[@id='nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='nom0']")).sendKeys(tasca);
		
		driver.findElement(By.xpath("//*[@id='expedient0']")).clear();
		driver.findElement(By.xpath("//*[@id='expedient0']")).sendKeys(expedient);
		
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
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[2]/a", "No existía la tarea agafada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[2]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[6]/a")).click();
		
		existeixElementAssert("//*[@id='page-title']/h2/span[contains(text(), '"+expedient+"')]", "El nombre del expediente no coincidía");
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2]/text(),'"+tasca+"')]","No se encontró la tarea agafada");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2]/text(),'"+tasca+"')]/td[contains(text(),'Usuari Administrador')]","El responsable no coincidía");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[2]/text(),'"+tasca+"')]/td/a/img[@alt='Alliberar']","No existía la imagen de alliberar");

		screenshotHelper.saveScreenshot("TasquesExpedient/agafar_tasca/3.png");		

		// Liberamos
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2]/text(),'"+tasca+"')]/td/a/img[@alt='Alliberar']")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se liberó la tarea al grupo correctamente");
		
		String grupoOriginal = "Usuari Administrador, Mariona Mestre";
		String grupo = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[2]/text(),'"+tasca+"')]/td[8]")).getText();
		
		assertTrue("Error al asignar al grupo", grupoOriginal.equals(grupo));
	}
	
	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		// Selecció directe
		actions.moveToElement(driver.findElement(By.id("menuEntorn")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//li[@id='menuEntorn']/ul[@class='llista-entorns']/li[contains(., '" + entorn + "')]/a")));
		actions.click();
		actions.build().perform();
		
		eliminarExpedient(null, null, tipusExp);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
