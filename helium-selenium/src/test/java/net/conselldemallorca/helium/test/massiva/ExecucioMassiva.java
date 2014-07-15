package net.conselldemallorca.helium.test.massiva;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExecucioMassiva extends BaseTest {
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
	public void c_programar_hora_execucio_i_programar_enviament_correu_i_aturar_tramitacio() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		// Iniciamos n expedientes
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		int numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		int numExpedients = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		assertFalse("Todos los expedientes estaban aturados", numAturados == numExpedients);
		
		// Programamos para dentro de 2 minutos
		Calendar calendar = Calendar.getInstance();
		driver.findElement(By.xpath("//*[@id='inici']")).click();
		
		WebElement minutes = driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div[2]/dl/dd[3]/div/a"));
		WebElement hours = driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div[2]/dl/dd[2]/div/a"));
		if (calendar.get(Calendar.MINUTE) > 57) {
			new Actions(driver).dragAndDropBy(hours, 6, hours.getLocation().y).click().perform();
		} else {
			new Actions(driver).dragAndDropBy(minutes, 6, minutes.getLocation().y).click().perform();				
		}
		driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div[3]/button[2]")).click();
		
		// Programamos el envío de correos
		if (!driver.findElement(By.xpath("//*[@id='correu']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='correu']")).click();
		}
		
		// Aturamos todos los expedientes		
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo");
		
		driver.findElement(By.xpath("//*[@id='aturarCommandMas']//button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		// Comprobamos que no se hayan cerrado los expedientes
		Thread.sleep(1000*10);
		consultarExpedientes(null, null, nomTipusExp);
		
		// Contamos los expedientes aturados
		numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		assertFalse("Todos los expedientes estaban aturados", numAturados == numExpedients);
		
		// Ya deben haberse cerrado los expedientes
		Thread.sleep(1000*150);
		consultarExpedientes(null, null, nomTipusExp);
		
		// Contamos los expedientes aturados
		numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		assertTrue("No se cerraron todos los expedientes correctamente", numAturados == numExpedients);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}
	
	@Test
	public void d_canvi_versio_proces() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		// Iniciamos n expedientes con la última versión
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();
		
		String versio = driver.findElement(By.xpath("//dd[contains(text(), '"+nomDefProc+"')]")).getText().trim();
		String versioSub = driver.findElement(By.xpath("//dd[contains(text(), '"+nomDefProc+"')]")).getText().trim();
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
		int numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		int numExpedients = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		assertFalse("Todos los expedientes estaban aturados", numAturados == numExpedients);
				
		// Cambiamos la versión del proceso y subproceso a la justo anterior
		WebElement selectProc = driver.findElement(By.xpath("//*[@id='definicioProcesId0']"));
		selectProc.findElements(By.tagName("option")).get(1).click();;
		String versioProc = selectProc.findElements(By.tagName("option")).get(1).getText().trim();
		assertTrue("El nombre de la versión del proceso no tenía un nombre adecuado", versioProc.startsWith(nomDefProc +" v."));
		
		WebElement selectSubProc = driver.findElement(By.xpath("//*[@id='subprocesId0']"));
		selectSubProc.findElements(By.tagName("option")).get(1).click();;
		String versioSubProc = selectSubProc.findElements(By.tagName("option")).get(1).getText().trim();
		assertTrue("El nombre de la versión del subproceso no tenía un nombre adecuado", versioSubProc.startsWith(nomSubDefProc +" v."));
		
		driver.findElement(By.xpath("//*[@id='canviVersioProcesCommand']//button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		// Esperamos unos segundos
		Thread.sleep(1000*5);
		
		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
			// Comprobamos que la versión haya cambiado
			consultarExpedientes(null, null, nomTipusExp);
			
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			// Versión del proceso
			String versioAct = driver.findElement(By.xpath("//dd[contains(text(), '"+nomDefProc+"')]")).getText().trim();
			assertFalse("No se cambió la versión del expediente: " + i, versio.equals(versioAct));
			assertTrue("No coincidía la versión del expediente: " + i, versioProc.equals(versioAct));
			
			WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='content']//select"));
			selectTipusExpedient.findElements(By.tagName("option")).get(1).click();
			
			// Versión del subproceso
			String versioSubAct = driver.findElement(By.xpath("//dd[contains(text(), '"+nomSubDefProc+"')]")).getText().trim();
			assertFalse("No se cambió la versión del subproceso del expediente: " + i, versioSub.equals(versioSubAct));
			assertTrue("No coincidía la versión del subproceso del expediente: " + i, versioSubProc.equals(versioSubAct));
		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}
	
	@Test
	public void e_executar_script() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProcScript);
		
		// Iniciamos n expedientes con la última versión
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		// Vemos el estado de los n expedientes, deben ser 'Iniciat'
		consultarExpedientes(null, null, nomTipusExp);		
		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
			String estado = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[5]")).getText();
			assertTrue("El estado del expediente '" + i + "' no era 'Pendiente'", "Pendiente".equals(estado));
		}
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
		// Ejecutamos un script con un signal
		driver.findElement(By.xpath("//*[@id='script0']")).sendKeys("executionContext.getProcessInstance().signal();");
		driver.findElement(By.xpath("//*[@id='scriptCommandMas']/div[3]/button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		// Vemos el estado de los n expedientes, deben ser 'Siguiente'
		// Esperamos 10 segundos
		Thread.sleep(1000*10);
		consultarExpedientes(null, null, nomTipusExp);
		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
			String estado = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[5]")).getText();
			assertTrue("El estado del expediente '" + i + "' no era 'Siguiente'", "Siguiente".equals(estado));
		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void f_executar_accio() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, accioPathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		// Iniciamos n expedientes con la última versión
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
		// Comprobamos que se ve únicamente la acción que hemos creado
		WebElement selectAccions = driver.findElement(By.xpath("//*[@id='accioId0']"));
		List<WebElement> options = selectAccions.findElements(By.tagName("option"));
		assertTrue("El número de acciones disponibles no era correcto", options.size() == 1);
		assertTrue("El texto de la acción no era correcto", "Enviar mensaje".equals(options.get(0).getText().trim()));
		
		// Ejecutamos una acción
		driver.findElement(By.xpath("//*[@id='execucioAccioCommand']//button[1]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		// Vemos las variables de los n expedientes, deben tener una variable llamada 'menssage' con el texto 'El valor de la variable 'message' no era el esperado'
		// Esperamos 10 segundos
		Thread.sleep(1000*10);		
		for (int i = 1; i <= numExpedientesTramMasiva; i++) {
			consultarExpedientes(null, null, nomTipusExp);
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]//a[contains(@href,'/expedient/info.html')]")).click();
			
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(text(), 'Dades')]")).click();
			existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]","No se encontró la variable 'message'");
			
			String mensaje = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(),'message')]/parent::tr/td[2]")).getText().trim();
			assertTrue("El valor de la variable 'message' no era el esperado", "Se ha ejecutado la acción".equals(mensaje));
		}
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void h_reindexar_expedients() throws InterruptedException {
		
	}

	@Test
	public void i_consultar_estat_execucións_massives() throws InterruptedException {
		
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
