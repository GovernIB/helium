package net.conselldemallorca.helium.test.disseny;

import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientDefProc extends BaseTest {
	
									//TEX.5 - Definicions de procés
										//TEX.5.1 - Assignar definició de procés inicial
										//TEX.5.2 - Desplegar Arxiu jBPM
										//TEX.5.3 - Desplegar arxiu d´exportació Helium
										//TEX.5.4 - Esborrar versió def. procés (Afegit. No consta al document de proves HEL3002 de 01/04/2014)
	
	String entorn 		= carregarPropietat("tipexp.definicio.proces.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.definicio.proces.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String nomTipusExp	= carregarPropietat("tipexp.definicio.proces.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.definicio.proces.tipus.expedient.titol", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	//Definiciao de proces a importar per asociar amb el tipus d´expedient
	String defProcNom	= carregarPropietat("informe.deploy.definicio.proces.nom", "Codi de la definicio de proces de proves no configurat al fitxer de properties");
	String defProcPath	= carregarPropietatPath("informe.deploy.definicio.proces.path", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");
	
	//Arxius de def. de proces, per importar desde dintre del tipus d´expecient
	String defProcJBPMPath		= carregarPropietatPath("tipexp.definicio.proces.path.jbpm", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");
	String defProcHELIUMPath	= carregarPropietatPath("tipexp.definicio.proces.path.helium", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");
	
	// XPATHS
	String pestanyaDefProc = "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/definicioProcesLlistat.html')]";
	String botoMarcarInicial = "//*[@id='registre']/tbody/tr/td/form[contains(@action, '/expedientTipus/definicioProcesInicial.html')]/button[contains(text(), 'inicial')]";
	String botoDesplegarArxiu = "//*[@id='content']/form/button[contains(text(), 'Desplegar')]";
	String botoDesplegarArxiu2 = "//*[@id='command']/div/div/button[contains(text(), 'Desplegar')]";
	String enllaçDefProc = "//*[@id='registre']/tbody/tr[contains(td/a, '"+defProcNom+"')]/td/a[contains(@href, '/definicioProces/info.html')]";
	String botoEsborrarVersioDefProc = "//*[@id='content']/div/form[contains(@action, '/definicioProces/delete.html')]/button[contains(text(), 'Esborrar')]";
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	}
	
	@Test
	public void a2_crear_exp_basic() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		crearTipusExpedient(nomTipusExp, codTipusExp, "tipusExpedient/defProces/a1_inicialitzacio");
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
	}
	
	@Test
	public void b1_desplegar_arxiu_helium() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		desplegarArxiuHeliumTipusExpedient(codTipusExp, pestanyaDefProc, botoDesplegarArxiu, defProcHELIUMPath, "Etiqueta Helium", botoDesplegarArxiu2, "tipusExpedient/defProces/b1_desplegar_helium");
	}
	
	@Test
	public void c1_assignar_definicio_proces_inicial() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDefProc)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/c1_1_asignar_defproc_inicial.png");
		
		driver.findElement(By.xpath(botoMarcarInicial)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/c1_2_asignar_defproc_resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut marcar el proces "+defProcNom+" com a inicial per el tipus d´expedient "+codTipusExp+".");
	}
	
	@Test
	public void d1_eliminar_versio_defproc_expedient() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDefProc)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/d1_1_eliminar_defproc_llistat.png");
		
		driver.findElement(By.xpath(enllaçDefProc)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/d1_2_eliminar_defproc.png");
		
		driver.findElement(By.xpath(botoEsborrarVersioDefProc)).click();
		
		if (isAlertPresent()) { acceptarAlerta(); }
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/d1_3_eliminar_defproc_resultat.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut esborrar la versió de la definicio de proces (helium) dins el tipus d'expedient.");
	}
	
	@Test
	public void e1_desplegar_arxiu_jbpm() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDefProc)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/e1_1_desplegar_jBMP_1_pipella_defproces.png");
		
		driver.findElement(By.xpath(botoDesplegarArxiu)).click();
		
		//Seleccionar Tipus d´arxiu
		for (WebElement option : driver.findElement(By.id("tipus0")).findElements(By.tagName("option"))) {
			if ("JBPM".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("arxiu0")).sendKeys(defProcJBPMPath);
		
		driver.findElement(By.id("etiqueta0")).sendKeys("Etiqueta JBPM");
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/e1_2_desplegar_jBMP_2_dades_arxiu.png");
		
		driver.findElement(By.xpath(botoDesplegarArxiu2)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/defProces/e1_3_desplegar_jBMP_3_resultat_importacio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "No s'ha pogut desplegar la definicio de proces (jBPM) dins el tipus d'expedient.");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarDefinicioProces(defProcNom);
		eliminarEntorn(entorn);
	}

}
