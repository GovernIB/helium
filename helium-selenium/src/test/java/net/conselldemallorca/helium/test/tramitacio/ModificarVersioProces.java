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
public class ModificarVersioProces extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	
	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void a_modificarVersioExp() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
		
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		consultarExpedientes(res[0], res[1], nomTipusExp);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();	
					
		// Empezamos a cambiar la versión
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='content']/div/h3[3]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='definicioProcesId0']")).click();
		WebElement selectDef = driver.findElement(By.xpath("//*[@id='definicioProcesId0']"));
		List<WebElement> allOptions = selectDef.findElements(By.tagName("option"));
		String versionCambiada = null;
		for (WebElement option : allOptions) {
			if (option.equals(allOptions.get(0))) {
				versionCambiada = option.getText().trim();
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='canviVersioProcesCommand']/div[3]/button")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']","No se cambió la versión");
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/3.png");
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/info.html')]")).click();
		
		// Vemos la versión
		WebElement element = driver.findElement(By.xpath("//*[@id='content']/dl"));
		
		List<WebElement> claves = element.findElements(By.xpath("dt"));
		List<WebElement> valores = element.findElements(By.xpath("dd"));

		String versionNueva = null;
		for (int i=0; i < claves.size() ; i++) {
			if ("Definició de procés".equals(claves.get(i).getText())) {
				versionNueva = valores.get(i).getText().trim();
				break;
			}
		}	
		assertTrue("La versión no ha cambiado", versionNueva.equals(versionCambiada));	
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/4.png");
		
		eliminarExpedient(res[0], res[1]);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/5.png");
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
