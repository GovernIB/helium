package net.conselldemallorca.helium.test.tramitacio;

import java.util.Date;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasquesSubprocessExpedient extends BaseTest {	
	
	String entorn = carregarPropietat("tramsel.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("tramsel.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomSubDefProc = carregarPropietat("defproc.deploy.definicio.subproces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.definicio.subproces.main.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathSubDefProc = carregarPropietatPath("defproc.subproces.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String exportDefProc = carregarPropietatPath("defproc.tasca_dades.exp.export.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
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

		desplegarDefinicioProcesEntorn(nomTipusExp, nomSubDefProc, pathSubDefProc);
		desplegarDefinicioProcesEntorn(nomTipusExp, nomDefProc, pathDefProc);
		
		importarDadesDefPro(nomDefProc, exportDefProc);
		importarDadesDefPro(nomSubDefProc, exportDefProc);
		
		screenshotHelper.saveScreenshot("tramitar/dadesexpedient/crear_dades/1.png");
	}

	@Test
	public void b_pas_proces_subproces() throws InterruptedException {
		carregarUrlConfiguracio();
		
		seleccionarEntorn(titolEntorn);
		
		screenshotHelper.saveScreenshot("tramitar/modificarInfoExp/1.png");
					
		String[] res = iniciarExpediente(codTipusExp,"SE-22/2014", "Expedient de prova Selenium " + (new Date()).getTime() );
				
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/1.png");

		consultarTareas(res[0], res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a", "No se encontró la tarea: 01 - Entrada");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '01 - Entrada')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Subproceso')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya una tarea del subproceso		
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a", "No se encontró la tarea: tasca1_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca1_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que haya la 2ª tarea del subproceso		
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a", "No se encontró la tarea: tasca2_subproces");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), 'tasca2_subproces')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
		
		// Comprobamos que vuelva al proceso principal	
		consultarTareas(null, res[1], nomTipusExp, false);
		
		existeixElementAssert("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a", "No se encontró la tarea: 02 - Tancament");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr/td[contains(a/text(), '02 - Tancament')]/a")).click();
		
		screenshotHelper.saveScreenshot("ExpedientPestanyaTasques/tramitar_delegar_tasca/3.png");
						
		driver.findElement(By.xpath("//*[@id='formFinalitzar']/div/button[contains(text(), 'Finalitzar')]")).click();
		acceptarAlerta();
		
		existeixElementAssert("//*[@id='infos']/p", "No se finalizó correctamente");
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
