package net.conselldemallorca.helium.test.massiva;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	String exportTipExpProcMassiva = carregarPropietatPath("tramsel_massiva.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpMasProc = carregarPropietatPath("tramas_massivo.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	int numExpedientesTramMasiva = Integer.parseInt(carregarPropietat("tramas.num_expedientes_tram_masiva", "Número de espedientes para las pruebas de tramitación masiva al fitxer de properties"));
	
	//XPATHS
	String botoExecMassiva = "//*[@id='page-entorn-menu']/div/a";
	
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

		Thread.sleep(2000);
		
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);

		// Los volvemos a desplegar para tener 2 versiones diferentes
		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		// Desplegamos el tipo de expediente de nuevo
		importarDadesTipExp(codTipusExp, exportTipExpProcMassiva);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void c_programar_hora_execucio_i_programar_enviament_correu_i_aturar_tramitacio() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		// Iniciamos n expedientes
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		int numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		int numExpedients = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size();
		assertFalse("Todos los expedientes estaban aturados", numAturados == numExpedients);
		
		driver.findElement(By.xpath("//*[@id='inici']")).click();
		Thread.sleep(1000*10);
		
		// Programamos para dentro de 5 minutos
		programarEjecucionMasiva(5);
		
		// Marcamos el check de envío de correos
		if (!driver.findElement(By.xpath("//*[@id='correu']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='correu']")).click();
		}
		
		// Guardamos la programación
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo");
		driver.findElement(By.xpath("//*[@id='aturarCommandMas']//button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		// Comprobamos que no se hayan cerrado los expedientes
		consultarExpedientes(null, null, nomTipusExp);
		
		// Contamos los expedientes aturados
		numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
		assertFalse("Todos los expedientes estaban parados", numAturados == numExpedients);
		
		// Mmiramos cada minuto a ver si se han parado, daremos un maximo de 10 min aprox. 
		int intentos =0;
		while (numAturados==0 && intentos<10) {
			Thread.sleep(1000*60);
			consultarExpedientes(null, null, nomTipusExp);
			// Contamos los expedientes parados
			numAturados = driver.findElements(By.xpath("//img[@src = '/helium/img/stop.png']")).size();
			intentos++;
		}
		
		assertTrue("No se ceró el expediente en el tiempo previsto", numAturados == numExpedients);
		
		// Eliminamos los expedientes
		eliminarExpedient(null, null, nomTipusExp);
	}

	@Test
	public void d_canvi_versio_proces() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
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
		
		esperaFinExecucioMassiva(botoExecMassiva);
		
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
		esperaFinExecucioMassiva(botoExecMassiva);
		
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
		esperaFinExecucioMassiva(botoExecMassiva);
		
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
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, accioPathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProcMassiva);
		
		// Iniciamos n expedientes con la última versión
		List<String[]> expedientes = new ArrayList<String[]>();
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			String[] expediente = iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
			expedientes.add(expediente);
		}

		// Eliminamos los expedientes en Lucene
		for (String[] expediente : expedientes) {
			String script = ""
					+ "String processInstanceId = net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().getExpedientAmbEntornITipusINumero(net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().getEntornActual().getId(), \""+codTipusExp+"\", \""+expediente[0]+"\").getProcessInstanceId();"
					+ "net.conselldemallorca.helium.jbpm3.integracio.Jbpm3HeliumBridge.getInstanceService().luceneDeleteExpedient(processInstanceId);";
			consultarExpedientes(null, null, nomTipusExp);
			
			driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
			driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
			driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(a/text(), \"Execució d'scripts\")]/a")).click();
			
			driver.findElement(By.xpath("//*[@id='script0']")).sendKeys(script);
			driver.findElement(By.xpath("//button[contains(text(), 'Executar')]")).click();
			acceptarAlerta();
			existeixElementAssert("//*[@id='infos']/p", "No se ejecutó el script correctamente del expediente: " + expediente);			
		}
		
		// Reindexamos
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
		driver.findElement(By.xpath("//button[contains(text(), 'Reindexar')]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
		
		esperaFinExecucioMassiva(botoExecMassiva);
		
		// Comprobamos que aparezcan
		actions.moveToElement(driver.findElement(By.id("menuConsultes")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConsultes']//a[contains(@href, '/expedient/consultaDisseny.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='expedientTipusId0']")).findElements(By.tagName("option")).get(1).click();
		driver.findElement(By.xpath("//*[@id='consultaId0']")).findElements(By.tagName("option")).get(1).click();
		
		driver.findElement(By.xpath("//button[contains(text(), 'Consultar')]")).click();

		for (String[] expediente : expedientes) {
			existeixElementAssert("//td[contains(a/text(),'"+expediente[0]+"')]", "No se encontró el expediente: " + expediente);			
		}
	}

	@Test
	public void i_consultar_estat_execucións_massives() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, accioPathDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProcMassiva);
		
		// Iniciamos n expedientes con la última versión
		List<String[]> expedientes = new ArrayList<String[]>();
		for (int i = 0; i < numExpedientesTramMasiva; i++) {
			expedientes.add(iniciarExpediente(codTipusExp,"SE-"+i+"/2014", "Expedient de prova Selenium " + (new Date()).getTime() ));
		}
		
		consultarExpedientes(null, null, nomTipusExp);
		
		driver.findElement(By.xpath("//*[@id='massivaInfoForm']/button[2]")).click();
		
		driver.findElement(By.xpath("//*[@id='inici']")).click();
		Thread.sleep(1000*10);
		
		// Programamos para dentro de 2 minutos
		programarEjecucionMasiva(2);
		
		// Ejecutamos que ature los expedientes 
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo para finalizar correctamente");
		
		driver.findElement(By.xpath("//*[@id='aturarCommandMas']//button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		// Estado pendiente
		Thread.sleep(1000*10);
		for (String[] expediente : expedientes) {
			assertTrue("El expediente '"+expediente+"' no estaba en estado 'pendiente'",estadoExpedientExecucioMassiva(expediente[1], botoExecMassiva) == 0);
		}
		actions.sendKeys(Keys.ESCAPE);
		
		Thread.sleep(1000*140);
		// Estado finalizado
		for (String[] expediente : expedientes) {
			assertTrue("El expediente '"+expediente+"' no estaba en estado 'finalizado'",estadoExpedientExecucioMassiva(expediente[1], botoExecMassiva) == 1);
		}
		actions.sendKeys(Keys.ESCAPE);
		
		// Ejecutamos de nuevo que ature los expedientes para que de error
		driver.findElement(By.xpath("//*[@id='motiu0']")).sendKeys("El motivo para que de error");
		
		driver.findElement(By.xpath("//*[@id='aturarCommandMas']//button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó la operación masiva correctamente");
				
		// Estado error
		Thread.sleep(1000*10);
		for (String[] expediente : expedientes) {
			assertTrue("El expediente '"+expediente+"' no estaba en estado 'error'",estadoExpedientExecucioMassiva(expediente[1], botoExecMassiva) == 2);
		}
		actions.sendKeys(Keys.ESCAPE);
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
	
	private void programarEjecucionMasiva(int minut) {

		long timeInMillis = System.currentTimeMillis() + (minut*60000);
		Calendar calendarFin = Calendar.getInstance();
		calendarFin.setTimeInMillis(timeInMillis);
		//calendarFin.add(Calendar.MINUTE, minut);
		
		driver.findElement(By.xpath("//*[@id='inici']")).clear();
		driver.findElement(By.xpath("//*[@id='inici']")).sendKeys(Keys.TAB);
		driver.findElement(By.xpath("//*[@id='inici']")).sendKeys(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calendarFin.getTime()));
		driver.findElement(By.xpath("//*[@id='inici']")).sendKeys(Keys.TAB);
		driver.findElement(By.xpath("//*[@id='inici']")).click();
		try { Thread.sleep(2000); } catch (Exception tEx) {}
		driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/div[3]/button[2]")).click();
	}
}
