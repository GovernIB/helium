package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
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
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesFlux extends BaseTest {	
	
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String pathDefProc = carregarPropietatPath("tramsel_accio.deploy_flux.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export_flux.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExp = carregarPropietatPath("tipexp.tasca_dades.exp.export_flux.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	long waitTime = 1000*5;
	
//	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp);
		assignarPermisosTipusExpedient(codTipusExp, usuari, "DESIGN","CREATE","SUPERVISION","WRITE","MANAGE","DELETE","READ","ADMINISTRATION");
	}
	
//	@Test
	public void a_crear_dades() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		importarDadesTipExp(codTipusExp, exportTipExp);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_comprobar_flux_tasca() throws InterruptedException, ParseException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
				
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos sobre disseny entorn");
			
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']//a[contains(@href,'/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/1.png");
			
		// Tareas proceso principal
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDefProc + "')]/td[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		
		List<String> tareasPrincipal = new ArrayList<String>();
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {			
			tareasPrincipal.add(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText());
			i++;
		}
		
		assertTrue("No existían 4 tareas en el proceso principal", tareasPrincipal.size() == 4);
		
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuDisseny']//a[contains(@href,'/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
	
		screenshotHelper.saveScreenshot("NouExpedient/inici_any/1.png");
			
		// Tareas subproceso
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomSubDefProc + "')]/td[1]/a")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		
		List<String> tareasSubproceso = new ArrayList<String>();
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {			
			tareasSubproceso.add(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText());
			i++;
		}
		
		assertTrue("No existían 2 tareas en el subproceso", tareasSubproceso.size() == 2);
		
		eliminarExpedient(null, null, nomTipusExp);
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		consultarTareas(null, res[1], nomTipusExp, true);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(text(), '"+tareasPrincipal.get(0)+"')]", "No se encontró la tarea: "+tareasPrincipal.get(0)+"");
		
		// Cogemos la tarea ya que se asignó inicialmente a un grupo		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]", "No estaba el botón de agafar tarea");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		// Comprobamos que al finalizar cambie el título de la tarea según el nombre que le introducimos en "exp_nom"
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		String fechaTermini = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
		calendar.add(Calendar.DATE, 24);
		String fechafinTermini = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 3);
		String fecha5dias = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
		
		if (driver.findElement(By.xpath("//*[@id='correcte0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='correcte0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='exp_data0']")).clear();
		driver.findElement(By.xpath("//*[@id='exp_data0']")).sendKeys(fechaTermini);
		
		String titulo = "Un nombre para el expediente";
		driver.findElement(By.xpath("//*[@id='exp_nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='exp_nom0']")).sendKeys(titulo);
		
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		consultarTareas(null, titulo, nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a", "No se encontró la tarea: "+tareasPrincipal.get(1)+"");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/parent::tr/td[2]", "No se encontró la tarea: "+tareasPrincipal.get(1)+"");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/parent::tr/td[contains(a/text(), '"+titulo+"')]", "No se cambió el título del expediente");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a")).click();		
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Debe volver a la primera tarea		
		consultarTareas(null, titulo, nomTipusExp, true);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(text(), '"+tareasPrincipal.get(0)+"')]", "No se encontró la tarea: "+tareasPrincipal.get(0)+"");
		
		// Cogemos la tarea ya que se asignó inicialmente a un grupo		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]", "No estaba el botón de agafar tarea");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
		if (!driver.findElement(By.xpath("//*[@id='correcte0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='correcte0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='exp_data0']")).clear();
		driver.findElement(By.xpath("//*[@id='exp_data0']")).sendKeys(fechaTermini);
		
		driver.findElement(By.xpath("//*[@id='exp_nom0']")).clear();
		driver.findElement(By.xpath("//*[@id='exp_nom0']")).sendKeys(titulo);
		
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya una tarea con ese nombre	
		consultarTareas(null, titulo, nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a", "No se encontró la tarea: "+tareasPrincipal.get(1)+"");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/parent::tr/td[2]", "No se encontró la tarea: "+tareasPrincipal.get(1)+"");
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/parent::tr/td[contains(a/text(), '"+titulo+"')]", "No se cambió el título del expediente");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasPrincipal.get(1)+"')]/a")).click();		
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");

		// Comprobamos que se haya cambiado el estado a "Pendiente"
		consultarExpedientes(null, titulo, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		assertTrue("El expediente no estaba en estado 'Pendiente'", "Pendiente".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[5]")).getText()));
		
		// Comprobamos que se inició el exp_termini, exp_termini_lab y el timer
		driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/terminis.html')]")).click();
		
		assertTrue("No había 4 terminis", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() == 4);
				
		// Comprobamos los datos de cada termini
		String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[1]")).getText();
		String durada = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[2]")).getText();
		String dataIniTerminiNoLab = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[3]")).getText();
		String aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[4]")).getText();
		String dataFiTerminiNoLab = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[5]")).getText();
		String estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[6]")).getText();
		
		assertTrue("La duración de '"+nom+"' no era correcta", "25 dies laborables".equals(durada));
		assertTrue("El estado inicial de '"+nom+"' no era correcto", "Actiu".equals(estat));
		assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", dataIniTerminiNoLab.equals(fechaTermini));	
		assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());		
		
		nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[1]")).getText();
		durada = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[2]")).getText();
		String iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[3]")).getText();
		aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[4]")).getText();
		String datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[5]")).getText();
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[6]")).getText();
		
		assertTrue("El estado inicial de '"+nom+"' no era correcto", "Actiu".equals(estat));
		assertTrue("La duración de '"+nom+"' no era correcta", "25 dies naturals".equals(durada));
		assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", iniciat.equals(fechaTermini));
		assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());
		assertTrue("El campo de 'Data de fi del termini' de '"+nom+"' no era correcto", datafi.equals(fechafinTermini));		
				
		nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[1]")).getText();
		durada = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[2]")).getText();
		iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[3]")).getText();
		aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[4]")).getText();
		datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[5]")).getText();
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[6]")).getText();
		
		assertTrue("La duración de '"+nom+"' no era correcta", "25 dies naturals".equals(durada));
		assertTrue("El estado inicial de '"+nom+"' no era correcto", "Actiu".equals(estat));
		assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", iniciat.equals(fechaTermini));
		assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());
		assertTrue("El campo de 'Data de fi del termini' de '"+nom+"' no era correcto", datafi.equals(fechafinTermini));
		
		// Comprobamos que todos los botones estén desactivados
		// Play
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[7]/img", "El campo play inicial de '"+nom+"' no era correcto");
		WebElement play = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[7]/img"));
		assertTrue("El campo play inicial de '"+nom+"' no era correcto", play.getAttribute("src").contains("control_play.png"));
		
		// Pause
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[8]/img", "El campo pause inicial de '"+nom+"' no era correcto");
		WebElement pause = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[8]/img"));
		assertTrue("El campo pause inicial de '"+nom+"' no era correcto", pause.getAttribute("src").contains("control_pause.png"));
					
		// Stop
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[9]/a/img", "El campo stop inicial de '"+nom+"' no era correcto");
		WebElement stop = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[9]/a/img"));
		assertTrue("El campo stop inicial de '"+nom+"' no era correcto", stop.getAttribute("src").contains("control_stop_blue.png"));
		
		nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[1]")).getText();
		durada = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[2]")).getText();
		iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[3]")).getText();
		aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[4]")).getText();
		datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[5]")).getText();
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[6]")).getText();
		
		assertTrue("La duración de '"+nom+"' no era correcta", "5 dies naturals".equals(durada));
		assertTrue("El estado inicial de '"+nom+"' no era correcto", "Actiu".equals(estat));
		assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", iniciat.equals(fechaTermini));
		assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());
		assertTrue("El campo de 'Data de fi del termini' de '"+nom+"' no era correcto", datafi.equals(fecha5dias));		
		
		// Comprobamos los 25 días laborables a partir de una fecha
		String fechaTerminiNoLab = getFechaDiasLaborables(dataIniTerminiNoLab, 25);
		assertTrue("El campo de 'Data de fi del termini' de 'Termini lab' no era correcto : Inicio : " + dataIniTerminiNoLab + " - Fin esperado : " +dataFiTerminiNoLab+ " - Fin real : " + fechaTerminiNoLab, fechaTerminiNoLab.equals(dataFiTerminiNoLab));
		
		// Comprobamos que haya una nueva alerta
		driver.findElement(By.xpath("//*[@id='page-entorn-title']/h2/a")).click();
		
		WebElement selectTipusExpedient = driver.findElement(By.xpath("//*[@id='content']//select"));
		List<WebElement> options = selectTipusExpedient.findElements(By.tagName("option"));
		for (WebElement option : options) {
			if (option.getText().equals(nomTipusExp)) {
				option.click();
				break;
			}
		}
		Thread.sleep(waitTime);
		driver.findElement(By.xpath("//*[@id='content']/div/form/input")).click();
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[4]/text(),'Termini alertas')]", "Se creó una alerta del termini alertas");
		
		// Ejecutamos en Script
		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/eines.html')]")).click();
		driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(a/text(), \"Execució d'scripts\")]/a")).click();
		
		driver.findElement(By.xpath("//*[@id='script0']")).sendKeys("executionContext.getProcessInstance().signal();");
		driver.findElement(By.xpath("//button[contains(text(), 'Executar')]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se ejecutó el script correctamente");
		
		// Comprobamos que se haya cambiado el estado a "Siguiente"
		consultarExpedientes(null, titulo, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		assertTrue("El expediente no estaba en estado 'Siguiente'", "Siguiente".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[5]")).getText()));
		
		driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
				
		existeixElementAssert("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]", "No existía la variable timer");
		String valorTimer = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText();
		
		Thread.sleep(waitTime);
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		assertTrue("El timer no se ejecutó", !valorTimer.equals(driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText()));
		valorTimer = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText();
		
		Thread.sleep(waitTime);
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		assertTrue("El timer no se ejecutó", !valorTimer.equals(driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText()));
		valorTimer = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText();
		
		Thread.sleep(waitTime);
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/dades.html')]")).click();
		assertTrue("El timer no se ejecutó", !valorTimer.equals(driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText()));
		valorTimer = driver.findElement(By.xpath("//*[@id='codi']/tbody/tr/td[contains(text(), 'Timer')]/parent::tr/td[2]")).getText();
		
		// Finalizamos la tasca timer
		consultarTareas(null, titulo, nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(text(), '"+tareasPrincipal.get(3)+"')]", "No se encontró la tarea: "+tareasPrincipal.get(3)+"");
		
		// Cogemos la tarea ya que se asignó inicialmente a un grupo		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]", "No estaba el botón de agafar tarea");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//button[contains(text(), 'Agafar')]")).click();
		
		// Finalizamos la tarea timer
		driver.findElement(By.xpath("//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó la "+tareasPrincipal.get(3)+" correctamente");
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
		
		// Ejecutamos la tasca1_subproces 
		consultarTareas(null, titulo, nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(0)+"')]", "No se encontró la tarea: tasca1_subproces ");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '"+tareasSubproceso.get(0)+"')]/a")).click();
		driver.findElement(By.xpath("//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó la tasca1_subproces correctamente");
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
		
		// Ejecutamos la tasca2_subproces 
		consultarTareas(null, titulo, nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']//td[contains(a/text(), '"+tareasSubproceso.get(1)+"')]", "No se encontró la tarea: "+tareasSubproceso.get(1)+" ");
		driver.findElement(By.xpath("//*[@id='registre']//td[contains(a/text(), '"+tareasSubproceso.get(1)+"')]/a")).click();
		driver.findElement(By.xpath("//button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó la tasca1_subproces correctamente");
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
		
		// Comprobamos que haya hecho el "join" y se finalice el expediente
		consultarExpedientes(null, titulo, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		assertTrue("El expediente no estaba en estado 'Finalitzat'", "Finalitzat".equals(driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[5]")).getText()));
		
		// Comprobamos que los terminis se hayan cancelado
		driver.findElement(By.xpath("//*[@id='registre']//a[contains(@href,'/expedient/info.html')][1]")).click();
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/terminis.html')]")).click();
		
		assertTrue("No había 4 terminis", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() == 4);
				
		// Comprobamos los datos de cada termini
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini lab')]/td[6]")).getText();
		assertTrue("El estado inicial de 'Termini lab' no era correcto", "Cancel·lat".equals(estat));
		
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini nat')]/td[6]")).getText();
		assertTrue("El estado inicial de 'Termini nat' no era correcto", "Cancel·lat".equals(estat));
		
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini alertas')]/td[6]")).getText();
		assertTrue("El estado inicial de 'Termini alertas' no era correcto", "Cancel·lat".equals(estat));
		
		estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1]/text(), 'Termini automatic')]/td[6]")).getText();
		assertTrue("El estado inicial de 'Termini automatic' no era correcto", "Cancel·lat".equals(estat));		
	}

	private String getFechaDiasLaborables(String sFecha, int numDias) throws ParseException {
		actions.moveToElement(driver.findElement(By.id("menuConfiguracio")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//*[@id='menuConfiguracio']/ul/li[contains(a/text(), 'Festius')]/a")));
		actions.click();
		actions.build().perform();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(sFecha));
		int numLab = 1;
		String fechaTerminiNoLab = null;
		while (numLab < numDias) {
			calendar.add(Calendar.DATE, 1);
			fechaTerminiNoLab = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
			
			WebElement select = driver.findElement(By.xpath("//*[@id='content']/h3/form/select"));
			List<WebElement> options = select.findElements(By.tagName("option"));
			for (WebElement option : options) {
				if (option.getText().equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
					option.click();
					break;
				}
			}
			
			if (existeixElement("//*[@id='dia_"+fechaTerminiNoLab+"']")) {
				String clase = driver.findElement(By.xpath("//*[@id='dia_"+fechaTerminiNoLab+"']")).getAttribute("class");
				if (!"dia nolab".equals(clase) && !"dia festiu".equals(clase)) {
					numLab++;
				}
			}
		}
		return fechaTerminiNoLab;
	}

//	@Test
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
