package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpedientPestanyaTasques extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String grupo = carregarPropietat("test.base.group", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String user175 = carregarPropietat("test.base.usuari.configuracio.nom","No se pudo cargar el usuario curso 175");
	String user174 = carregarPropietat("test.base.usuari.feina.nom","No se pudo cargar el usuario curso 174");
	String user173 = carregarPropietat("test.base.usuari.disseny.nom","No se pudo cargar el usuario curso 173");
		
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
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void b_delegar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/delegar_tasca/1.png");

		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/definicioProces/tascaLlistat.html')]")).click();
		
		existeixElementAssert("//*[@id='registre']/tbody/tr", "No existía ninguna tarea");
		
		// Ponemos todas las tareas como delegables
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/a")).click();
			
			if (!driver.findElement(By.xpath("//*[@id='expressioDelegacio0']")).isSelected()) {
				driver.findElement(By.xpath("//*[@id='expressioDelegacio0']")).click();
			}
			
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
			
			existeixElementAssert("//*[@id='infos']/p", "No se guardó la tarea correctamente");
			screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/delegar_tasca/2-"+i+".png");
			i++;
		}
		
		// Comprobamos que todas se quedaron marcadas
		i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]/a")).click();
			checkboxIsSelectedAssert("//*[@id='expressioDelegacio0']", "No estaba seleccionado el delegate de la fila " + i);
						
			driver.findElement(By.xpath("//*[@id='command']/div[3]/button[2]")).click();
			screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/delegar_tasca/3-"+i+".png");
			i++;
		}
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/delegar_tasca/4.png");
	}
	
	@Test
	public void c_tramitar_delegar_tasca() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		consultarTareas(null, res[1], nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/tasca/info.html')]")).click();
		
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(),'Delegar la tasca')]", "La tarea no era delegable");
		
		driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(text(),'Delegar la tasca')]/img")).click();
		
		WebElement selectActor = driver.findElement(By.xpath("//*[@id='actorId0']"));
		for (WebElement option : selectActor.findElements(By.tagName("option"))) {
			if (option.getText().equals(properties.getProperty("test.base.usuari.configuracio.nom"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='comentari0']")).clear();
		
		String comentario = "El comentario de delegar la tarea";
		driver.findElement(By.xpath("//*[@id='comentari0']")).sendKeys(comentario);
		
		if (driver.findElement(By.xpath("//*[@id='supervisada0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='supervisada0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div/div[5]/button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se delegó la tarea correctamente");
		
		// Comprobamos el listado de tareas
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[1]/a/img", "No llegó la tarea delegada");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]")).click();
		String comentarioDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[3]")).getText();
		assertTrue("Error en el comentario", comentarioDel.equals(comentario));
		String supervisadaDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[4]")).getText();
		assertTrue("Error en el texto de supervisada", "No".equals(supervisadaDel));
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(), 'Aquesta tasca vos ha estat delegada')]", "No se encontró la cabecera de delegada");
		
		// Comprobamos el listado de tareas
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[2]/td[1]/a/img", "No existía la tarea que fue delegada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[2]/td[1]")).click();
		comentarioDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[3]")).getText();
		assertTrue("Error en el comentario", comentarioDel.equals(comentario));
		supervisadaDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[4]")).getText();
		assertTrue("Error en el texto de supervisada", "No".equals(supervisadaDel));
		
		
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(), 'Heu delegat aquesta tasca')]", "No se encontró la cabecera de delegada");
		
		// Cancelamos la delegación
		driver.findElement(By.xpath("//*[@id='command']/div[2]/button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se desdelegó la tarea");
	}
	
	@Test
	public void d_tramitar_supervisar_tasca() throws InterruptedException {
		
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		eliminarExpedient(null, null, nomTipusExp);
		
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		consultarTareas(null, res[1], nomTipusExp, false);
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/tasca/info.html')]")).click();
		
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(),'Delegar la tasca')]", "La tarea no era delegable");
		
		driver.findElement(By.xpath("//*[@id='content']/div/h3[contains(text(),'Delegar la tasca')]/img")).click();
		
		WebElement selectActor = driver.findElement(By.xpath("//*[@id='actorId0']"));
		for (WebElement option : selectActor.findElements(By.tagName("option"))) {
			if (option.getText().equals(properties.getProperty("test.base.usuari.configuracio.nom"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.xpath("//*[@id='comentari0']")).clear();
		
		String comentario = "El comentario de delegar la tarea";
		driver.findElement(By.xpath("//*[@id='comentari0']")).sendKeys(comentario);
		
		if (!driver.findElement(By.xpath("//*[@id='supervisada0']")).isSelected()) {
			driver.findElement(By.xpath("//*[@id='supervisada0']")).click();
		}
		
		driver.findElement(By.xpath("//*[@id='command']/div/div[5]/button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se delegó la tarea correctamente");
		
		// Comprobamos el listado de tareas
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[1]/td[1]/a/img", "No llegó la tarea delegada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]/td[1]")).click();
		String comentarioDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[3]")).getText();
		assertTrue("Error en el comentario", comentarioDel.equals(comentario));
		String supervisadaDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[4]")).getText();
		assertTrue("Error en el texto de supervisada", "Si".equals(supervisadaDel));
		
		
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(), 'Aquesta tasca vos ha estat delegada')]", "No se encontró la cabecera de delegada");
		
		// Comprobamos el listado de tareas
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr[2]/td[1]/a/img", "No existía la tarea que fue delegada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[2]/td[1]")).click();
		comentarioDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[3]")).getText();
		assertTrue("Error en el comentario", comentarioDel.equals(comentario));
		supervisadaDel = driver.findElement(By.xpath("//*[@id='content']/div/dl/dd[4]")).getText();
		assertTrue("Error en el texto de supervisada", "Si".equals(supervisadaDel));
		
		
		existeixElementAssert("//*[@id='content']/div/h3[contains(text(), 'Heu delegat aquesta tasca')]", "No se encontró la cabecera de delegada");
		
		// Cancelamos la delegación
		driver.findElement(By.xpath("//*[@id='command']/div[2]/button")).click();
		acceptarAlerta();
		existeixElementAssert("//*[@id='infos']/p", "No se desdelegó la tarea");
	}
	
	@Test
	public void e_reasignar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/1.png");

		consultarExpedientes(null, null, nomTipusExp);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/tasques.html')]")).click();
		
		existeixElementAssert("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]", "No había ninguna tarea para reasignar");
		
		String responsableOriginal = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
		driver.findElement(By.xpath("//img[@alt='Reassignar'][1]")).click();
		driver.findElement(By.xpath("//*[@id='expression0']")).sendKeys("user("+usuari+")");
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se reasignó la tarea al usuario correctamente");
		
		String responsable = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
		
		assertTrue("Error al asignar el responsable", responsableOriginal.equals(responsable));
	
		driver.findElement(By.xpath("//img[@alt='Reassignar'][1]")).click();
		driver.findElement(By.xpath("//*[@id='expression0']")).sendKeys("group("+grupo+")");
		
		driver.findElement(By.xpath("//*[@id='command']/div[3]/button[1]")).click();
		
		existeixElementAssert("//*[@id='infos']/p", "No se reasignó la tarea al grupo correctamente");
		
		String grupo = driver.findElement(By.xpath("//img[@alt='Reassignar'][1]/parent::a/parent::td/parent::tr/td[8]")).getText();
		String[] aGrupo = grupo.split(",");
		
		assertTrue("Error al asignar al grupo", aGrupo.length ==3 && (grupo.contains(user175) && grupo.contains(user174) && grupo.contains(user173)));
	}
	
	@Test
	public void f_suspendre_tasca() throws InterruptedException {
		carregarUrlConfiguracio();

		seleccionarEntorn(titolEntorn);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/1.png");

		consultarExpedientes(null, null, nomTipusExp);

		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/visualizar_tasques/2.png");

		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/tasques.html')]")).click();
		
		existeixElementAssert("//img[@alt='Suspendre'][1]", "No había ninguna tarea para suspender");
		
		String id = driver.findElement(By.xpath("//img[@alt='Suspendre'][1]/parent::a/parent::td/parent::tr/td[1]")).getText();
		
		String flagOriginal = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
		
		assertTrue("Error la tasca ya tenía el flag de suspendida", !flagOriginal.contains("S"));
		
		driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[12]/a")).click();
		acceptarAlerta();
		
		String flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
		
		assertTrue("Error la tasca no tenía el flag de suspendida", flagActual.contains("S"));
		
		driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[12]/a")).click();
		acceptarAlerta();
		
		flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
		assertTrue("Error la tasca tenía el flag de suspendida", !flagActual.contains("S"));
	}
		
	@Test
	public void g_cancellar_tasca() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		consultarExpedientes(null, null, nomTipusExp);
		
		assertTrue("No había ningún expediente", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//a[contains(@href,'/expedient/info.html')]")).click();

		
		driver.findElement(By.xpath("//*[@id='tabnav']//a[contains(@href,'/expedient/tasques.html')]")).click();
		
		existeixElementAssert("//img[@alt='Cancel·lar'][1]", "No había ninguna tarea para cancel·lar");
		
		String id = driver.findElement(By.xpath("//img[@alt='Cancel·lar'][1]/parent::a/parent::td/parent::tr/td[1]")).getText();
		
		String flagOriginal = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
		
		assertTrue("Error la tasca ya tenía el flag de cancel·lar", !flagOriginal.contains("C"));
		
		driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[13]/a")).click();
		acceptarAlerta();
		
		String flagActual = driver.findElement(By.xpath("//td[contains(text(),'"+id+"')]/parent::tr/td[9]")).getText();
		
		assertTrue("Error la tasca no tenía el flag de cancel·lar", flagActual.contains("C"));
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
