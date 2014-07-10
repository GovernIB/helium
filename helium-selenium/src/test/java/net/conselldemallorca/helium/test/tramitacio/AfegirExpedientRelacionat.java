package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AfegirExpedientRelacionat extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	
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
	public void b_afegirExpedientRelacionat() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/1.png");
		
		String[] res_dest = iniciarExpediente(codTipusExp,"SE-21/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		String[] res_orig = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );

		consultarExpedientes(res_orig[0], res_orig[1], nomTipusExp);

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();

		driver.findElement(By.xpath("//*[@action='/helium/expedient/relacionar.html']//button")).click();

		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).clear();
		driver.findElement(By.xpath("//*[@id='suggest_expedientIdDesti0']")).sendKeys(res_dest[1]);
		driver.findElement(By.xpath("//*[@class='ac_results']/ul/li[1]")).click();

		driver.findElement(By.xpath("//*[@id='relacionarCommand']//button[1]")).click();
		
		assertTrue("El expediente no se relacionó de forma correcta", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() > 0);
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/3.png");		
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/cross.png']")).click();
		acceptarAlerta();
		
		String textoInfo = driver.findElement(By.xpath("//*[@id='infos']/p")).getText();
		assertTrue("No se borró el expediente relacionado",!textoInfo.isEmpty());
		
		screenshotHelper.saveScreenshot("tramitar/AfegirExpedientRelacionat/4.png");
		
		eliminarExpedient(res_orig[0], res_orig[1]);
		
		eliminarExpedient(res_dest[0], res_dest[1]);
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
			
		// Eliminar la def de proceso
		eliminarDefinicioProces(nomDefProc);
		eliminarDefinicioProces(nomSubDefProc);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
