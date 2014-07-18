package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Accions extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String tipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	static String entornActual;
	
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
	public void a_executar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("accions/executar/1.png");

		iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
		if (existeixElement("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]")) {
			driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr//img[@src='/helium/img/cross.png']")).click();
			acceptarAlerta();
		}
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Expedient')]")).click();		
		driver.findElement(By.xpath("//button[contains(text(),'Enviar mensaje')]")).click();
		acceptarAlerta();		
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la acción correctamente");
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
		existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]","No se encontró la variable 'message'");
		
		String mensaje = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr/td[2]")).getText().trim();
		assertTrue("El valor de la variable 'message' no era el esperado", "Se ha ejecutado la acción".equals(mensaje));
				
		eliminarExpedient(null, null, tipusExp);
	}

	@Test
	public void b_comprobar_publica() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("accions/ocultar/1.png");

		iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		// Ponemos la acción como pública
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/helium/definicioProces/accioLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'mensaje')]/a")).click();
		
		if (!driver.findElement(By.xpath("//*[@id='publica0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='publica0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");		
		
		// Cambiamos los permisos del usuario a READ para que no se muestre a no ser que sea una acción pública
		assignarPermisosTipusExpedient(codTipusExp, usuari, "SUPERVISION");
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		existeixElementAssert("//button[contains(text(),'Enviar mensaje')]", "No se mostró la acción");		
		
		// Ponemos la acción como no pública				
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/helium/definicioProces/accioLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'mensaje')]/a")).click();
		
		if (driver.findElement(By.xpath("//*[@id='publica0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='publica0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");		
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		noExisteixElementAssert("//button[contains(text(),'Enviar mensaje')]", "No se ocultó la acción");
		
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		eliminarExpedient(null, null, tipusExp);
	}

	@Test
	public void c_comprobar_ocultar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);

		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("accions/ocultar/1.png");

		// Ocultamos la acción
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/helium/definicioProces/accioLlistat.html')]")).click();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(),'mensaje')]/a")).click();
		
		if (!driver.findElement(By.xpath("//*[@id='oculta0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='oculta0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se modificó la acción correctamente");
		
		screenshotHelper.saveScreenshot("accions/ocultar/2.png");
		
		iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		noExisteixElementAssert("//button[contains(text(),'Enviar mensaje')]", "No se ocultó la acción");
		
		eliminarExpedient(null, null, tipusExp);
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
