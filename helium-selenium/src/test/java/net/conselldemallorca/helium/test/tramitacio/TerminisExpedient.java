package net.conselldemallorca.helium.test.tramitacio;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TerminisExpedient extends BaseTest {

	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportTipExpProc = carregarPropietatPath("tipexp.tasca_dades_doc.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.mod.exp.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathExportDefProc = carregarPropietatPath("defproc.termini.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
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
		
		importarDadesDefPro(nomDefProc, pathExportDefProc);
		importarDadesTipExp(codTipusExp, exportTipExpProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}
	
	@Test
	public void b_visualizar_terminis() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("TerminisExpedient/visualizar_terminis/1.png");
		
		// Comprobamos que se han creado los terminis	
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'"+nomDefProc+"')]")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[5]/a")).click();
		
		Map<String, String[]> terminis = new HashMap<String, String[]>();
		
		int i = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+i+"]")) {
			//Codi, Nom, Durada
			String[] term = new String[3];
			term[0] = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[1]")).getText();
			term[1] = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[2]")).getText();
			term[2] = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+i+"]/td[3]")).getText();
			
			terminis.put(term[0], term);
			
			i++;
		}
		
		// Comprobamos que se han mostrado los terminis correspondientes		
		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		screenshotHelper.saveScreenshot("TerminisExpedient/visualizar_terminis/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[5]/a")).click();
		
		assertTrue("No había el mísmo número de terminis", driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).size() == terminis.size());
			
		int j = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+j+"]")) {
			// Comprobamos los datos de cada termini
			
			// Pantalla anterior: Codi, Nom, Durada
			// Pantalla actual: Nom, Durada, Iniciat el, Aturat el, Data de fi del termini, Estat
			String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText();
			String iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			String aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			String datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
			String estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			
			String[] termOr = terminis.get(nom);

			assertTrue("El nombre de '"+nom+"' no coincidía", nom.equals(termOr[1]));
			assertTrue("El estado inicial de '"+nom+"' no era correcto", "Pendent d'iniciar".equals(estat));
			assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", iniciat.isEmpty());
			assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());
			assertTrue("El campo de 'Data de fi del termini' inicial de '"+nom+"' no era correcto", datafi.isEmpty());
			
			screenshotHelper.saveScreenshot("TerminisExpedient/visualizar_terminis/3+"+j+".png");
			
			j++;
		}
		screenshotHelper.saveScreenshot("TerminisExpedient/visualizar_terminis/4.png");
	}
	
	@Test
	public void c_iniciar_terminis() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("TerminisExpedient/iniciar_terminis/1.png");
		
		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		screenshotHelper.saveScreenshot("TerminisExpedient/iniciar_terminis/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[5]/a")).click();
		
		assertTrue("No había ningún termini", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());
			
		String hoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		
		int j = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+j+"]")) {
			// Comprobamos los datos de cada termini	
			
			String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText();
			if (existeixElement("//*[@id='registre']/tbody/tr["+j+"]/td[9]/a/img")) {
				// Estaba iniciado. Lo paramos
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[9]/a/img")).click();
				acceptarAlerta();
				
				existeixElementAssert("//*[@id='infos']", "No paró el Termini '"+nom+"'");
			}
			String iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			String aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			String datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
			String estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			
			assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", iniciat.isEmpty());
			assertTrue("El campo de 'Aturat el' inicial de '"+nom+"' no era correcto", aturat.isEmpty());
			assertTrue("El campo de 'Data de fi del termini' inicial de '"+nom+"' no era correcto", datafi.isEmpty());
			
			// Play
			WebElement play = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img"));
			assertTrue("El campo play inicial de '"+nom+"' no era correcto", play.getAttribute("src").contains("control_play_blue.png"));
			
			// Pause
			WebElement pause = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[8]/img"));
			assertTrue("El campo pause inicial de '"+nom+"' no era correcto", pause.getAttribute("src").contains("control_pause.png"));
						
			// Stop
			WebElement stop = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[9]/img"));
			assertTrue("El campo stop inicial de '"+nom+"' no era correcto", stop.getAttribute("src").contains("control_stop.png"));
			
			// Pulsamos play
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img")).click();
			acceptarAlerta();			
			existeixElementAssert("//*[@id='infos']", "No inició el Termini '"+nom+"'");
			
			String durada = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[2]")).getText();
			
			datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
			assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", datafi.equals(hoy));
			
			estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			assertTrue("El campo 'estat' de '"+nom+"' no era correcto", "Actiu".equals(estat));
			
			iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			
			if ("Termini 1".equals(nom)) {
				// Sin duración
				assertTrue("La duración de '"+nom+"' no coincidía con lo esperado", "".equals(durada));
				assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", hoy.equals(iniciat));
			} else if ("Termini 2".equals(nom)) {				
				// Hace 3 meses
				assertTrue("La duración de '"+nom+"' no coincidía con lo esperado", "3 mesos".equals(durada));
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, -3);
				calendar.add(Calendar.DATE, -1);
				String fecha = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
				assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", fecha.equals(iniciat));
			} else if ("Termini 3".equals(nom)) {
				// Hace 2 días				
				assertTrue("La duración de '"+nom+"' no coincidía con lo esperado", "2 dies naturals".equals(durada));
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -1);
				String fecha = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
				assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", fecha.equals(iniciat));
			}
			
			screenshotHelper.saveScreenshot("TerminisExpedient/iniciar_terminis/3-"+j+".png");
			
			j++;
		}
		
		screenshotHelper.saveScreenshot("TerminisExpedient/iniciar_terminis/4.png");
	}
	
	@Test
	public void d_aturar_terminis() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("TerminisExpedient/aturar_terminis/1.png");
		
		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		screenshotHelper.saveScreenshot("TerminisExpedient/aturar_terminis/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[5]/a")).click();
		
		assertTrue("No había ningún termini", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());
		
		String hoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		
		int j = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+j+"]")) {
			// Comprobamos los datos de cada termini	
			
			String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText();
			if (existeixElement("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img")) {
				// Si no estaba iniciado, lo iniciamos
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img")).click();
				acceptarAlerta();
				
				existeixElementAssert("//*[@id='infos']", "No inició el Termini '"+nom+"'");
			}
			String estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			String iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			String aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			String datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
						
			assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", !iniciat.isEmpty());
			assertTrue("El campo de 'Data de fi del termini' inicial de '"+nom+"' no era correcto", !datafi.isEmpty());
			
			// Play
			WebElement play = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/img"));
			assertTrue("El campo play inicial de '"+nom+"' no era correcto", play.getAttribute("src").contains("control_play.png"));
			
			// Pause
			WebElement pause = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[8]/a/img"));
			assertTrue("El campo pause inicial de '"+nom+"' no era correcto", pause.getAttribute("src").contains("control_pause_blue.png"));
						
			// Stop
			WebElement stop = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[9]/a/img"));
			assertTrue("El campo stop inicial de '"+nom+"' no era correcto", stop.getAttribute("src").contains("control_stop_blue.png"));
			
			// Pulsamos pause
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[8]/a/img")).click();
			acceptarAlerta();			
			existeixElementAssert("//*[@id='infos']", "No pausó el Termini '"+nom+"'");
			
			iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", !iniciat.isEmpty());
			
			aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			assertTrue("El campo 'Aturat el' de '"+nom+"' no era correcto", !aturat.isEmpty() && hoy.equals(aturat));
			
			datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
			assertTrue("El campo 'Data de fi del termini' de '"+nom+"' no era correcto", !datafi.isEmpty());
			
			estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			assertTrue("El campo 'estat' de '"+nom+"' no era correcto", "Aturat".equals(estat));
			
			screenshotHelper.saveScreenshot("TerminisExpedient/aturar_terminis/3-"+j+".png");
			
			j++;
		}
		
		screenshotHelper.saveScreenshot("TerminisExpedient/aturar_terminis/4.png");
	}
	
	@Test
	public void e_cancelar_terminis() throws InterruptedException {
		carregarUrlConfiguracio(); 
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("TerminisExpedient/cancelar_terminis/1.png");
		
		consultarExpedientes(null, null, properties.getProperty("defproc.deploy.tipus.expedient.nom"));
		
		screenshotHelper.saveScreenshot("TerminisExpedient/cancelar_terminis/2.png");
		
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[1]//img[@src='/helium/img/information.png']")).click();
		
		driver.findElement(By.xpath("//*[@id='tabnav']/li[5]/a")).click();
		
		assertTrue("No había ningún termini", !driver.findElements(By.xpath("//*[@id='registre']/tbody/tr")).isEmpty());
			
		int j = 1;
		while (existeixElement("//*[@id='registre']/tbody/tr["+j+"]")) {
			// Comprobamos los datos de cada termini	
			
			String nom = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[1]")).getText();
			String estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			String iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			if (existeixElement("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img")) {
				// Si no estaba iniciado, lo iniciamos
				driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/a/img")).click();
				acceptarAlerta();
				
				existeixElementAssert("//*[@id='infos']", "No inició el Termini '"+nom+"'");
				
				iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			}
			String aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			String datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
						
			assertTrue("El campo de 'Iniciat el' inicial de '"+nom+"' no era correcto", !iniciat.isEmpty());
			assertTrue("El campo de 'Data de fi del termini' inicial de '"+nom+"' no era correcto", !datafi.isEmpty());
			
			// Play
			WebElement play = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[7]/img"));
			assertTrue("El campo play inicial de '"+nom+"' no era correcto", play.getAttribute("src").contains("control_play.png"));
			
			// Pause
			WebElement pause = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[8]/a/img"));
			assertTrue("El campo pause inicial de '"+nom+"' no era correcto", pause.getAttribute("src").contains("control_pause_blue.png"));
						
			// Stop
			WebElement stop = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[9]/a/img"));
			assertTrue("El campo stop inicial de '"+nom+"' no era correcto", stop.getAttribute("src").contains("control_stop_blue.png"));
			
			// Pulsamos stop
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[9]/a/img")).click();
			acceptarAlerta();			
			existeixElementAssert("//*[@id='infos']", "No paró el Termini '"+nom+"'");
			
			iniciat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[3]")).getText();
			assertTrue("El campo 'iniciat' de '"+nom+"' no era correcto", iniciat.isEmpty());
			
			aturat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[4]")).getText();
			assertTrue("El campo 'Aturat el' de '"+nom+"' no era correcto", aturat.isEmpty());
			
			datafi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[5]")).getText();
			assertTrue("El campo 'Data de fi del termini' de '"+nom+"' no era correcto", datafi.isEmpty());
			
			estat = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr["+j+"]/td[6]")).getText();
			assertTrue("El campo 'estat' de '"+nom+"' no era correcto", "Cancel·lat".equals(estat));
			
			screenshotHelper.saveScreenshot("TerminisExpedient/cancelar_terminis/3-"+j+".png");
			
			j++;
		}
		
		screenshotHelper.saveScreenshot("TerminisExpedient/cancelar_terminis/4.png");
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
