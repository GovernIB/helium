package net.conselldemallorca.helium.test.massiva;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecucioMassivaVariables extends BaseTest {
	String entorn = carregarPropietat("tramas.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramas.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main_direct.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProcScript = carregarPropietatPath("defproc.deploy.expexe_executar_script.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String accioPathDefProc = carregarPropietatPath("tramsel_accio.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tramsel_accio.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpMasProc = carregarPropietatPath("tramas_massivo.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	int numExpedientesTramMasiva = Integer.parseInt(carregarPropietat("tramas.num_expedientes_tram_masiva", "Número de espedientes para las pruebas de tramitación masiva al fitxer de properties"));
			
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

		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);

		// Los volvemos a desplegar para tener 2 versiones diferentes
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void g_modificar_variable() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, accioPathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		// Vamos las variables disponibles
		
		// Iniciamos los expedientes
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
//		// Ejecutamos una modificación de variable
//		
//		WebElement selectVar = driver.findElement(By.xpath("//*[@id='var0']"));
//		List<WebElement> optionsVar = selectVar.findElements(By.tagName("option"));
//		for (WebElement var : optionsVar) {
//			if ("exp_nom".equals(var.getAttribute("value"))) {
//				var.click();
//			}
//		}
//		
//		driver.findElement(By.xpath("//*[@id='modificarVariablesMasCommand']//button[1]")).click();
//		acceptarAlerta();
//		driver.findElement(By.xpath("//*[@id='exp_nom0']")).sendKeys("Un texto");
//		driver.findElement(By.xpath("//*[@id='command']//button[1]")).click();
//		acceptarAlerta();
//		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
//		
//		// Vemos las variables de los n expedientes
//		// Esperamos 10 segundos
//		Thread.sleep(1000*10);		
//		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
//			consultarExpedientes(null, null, nomTipusExp);
//			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]//img[@src='/helium/img/information.png']")).click();
//			
//			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
//			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'Nombre del expediente')]","No se encontró la variable 'Nombre del expediente'");
//			
//			String mensaje = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr/td[2]")).getText().trim();
//			assertTrue("El valor de la variable no era el esperado", "Un texto".equals(mensaje));
//		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void z_limpiar() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		// Eliminar el tipo de expediente
		eliminarTipusExpedient(codTipusExp);
		
		eliminarEntorn(entorn);
		
		screenshotHelper.saveScreenshot("TasquesDadesTasca/finalizar_expedient/1.png");	
	}
}
