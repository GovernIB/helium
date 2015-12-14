package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientDocuments extends BaseTest {
	
						//TEX.8 - Documents
							//TEX.8.1 - Llistat de documents
							//TEX.8.2 - Accés a la modificació de document a la definició de procés
	
	String entorn 		= carregarPropietat("tipexp.documents.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.documents.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String nomTipusExp	= carregarPropietat("tipexp.documents.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.documents.tipus.expedient.titol", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
	//Definició de proces a importar per asociar amb el tipus d´expedient
	String defProcNom	= carregarPropietat("tipexp.documents.definicio.proces.nom", "Codi de la definicio de proces de proves no configurat al fitxer de properties");
	String defProcPath	= carregarPropietatPath("tipexp.documents.definicio.proces.path", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");

	String provaDocPath	= carregarPropietatPath("tipexp.documents.path.doc.proves", "Ruta de l´arxiu de definicio de proces exportada no configurat al fitxer de properties");

	// XPATHS
	String pestanyaDefProc 	= "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/definicioProcesLlistat.html')]";
	String pestanyaDocs		= "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/documentLlistat.html')]";
	String botoDesplegarArxiu = "//*[@id='content']/form/button[contains(text(), 'Desplegar')]";
	String botoDesplegarArxiu2 = "//*[@id='command']/div/div/button[contains(text(), 'Desplegar')]";

	String linkDoc1 = "//*[@id='registre']/tbody/tr[contains(td/a, 'DocSel1')]/td/a";
	String linkDoc1_m = "//*[@id='registre']/tbody/tr[contains(td/a, 'DocSel1Mod')]/td/a";
	String linkDoc2 = "//*[@id='registre']/tbody/tr[contains(td/a, 'DocSel2')]/td/a";
	String linkDoc3 = "//*[@id='registre']/tbody/tr[contains(td/a, 'DocSel3')]/td/a";

	String botoModificarArxiu = "//*[@id='command']/div/button[contains(text(), 'Modificar')]";
	String enllaçDocumentAdjuntat = "//*[@id='iconsFileInput_arxiuContingut0']/a[contains(@href, '/expedientTipus/documentDownload.html')]";


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
		
		crearTipusExpedient(nomTipusExp, codTipusExp, "tipusExpedient/documents/a1_crear_tipexp_basic");
		
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		
		desplegarArxiuHeliumTipusExpedient(codTipusExp, pestanyaDefProc, botoDesplegarArxiu, defProcPath, "Proves Docs", botoDesplegarArxiu2);
	}
	
	@Test
	public void b1_llistat_i_modificacio_documents() {
	
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaDocs)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_1_llistat-pipella_documents.png");
		
		//Seleccionar Def. Proces
		for (WebElement option : driver.findElement(By.name("definicioProcesId")).findElements(By.tagName("option"))) {
			if (defProcNom.equals(option.getText())) {
				option.click();
				//screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_2_llistat-selecciona_defProces.png");
				break;
			}
		}
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_3_llistat-docs_defProc.png");
		
		//Comprovam el llistat de documents importats
		existeixElementAssert(linkDoc1, "No s´ha trobat el document 'Document proves selenium TEX8 1' incluit dins l´arxiu d´exportació: "+defProcPath);
		existeixElementAssert(linkDoc2, "No s´ha trobat el document 'Document proves selenium TEX8 2' incluit dins l´arxiu d´exportació: "+defProcPath);
		existeixElementAssert(linkDoc3, "No s´ha trobat el document 'Document proves selenium TEX8 3' incluit dins l´arxiu d´exportació: "+defProcPath);
		
		//Modificam el primer document
		driver.findElement(By.xpath(linkDoc1)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_4_modificacio-dades_inicials.png");
		
		driver.findElement(By.id("codi0")).sendKeys("Mod");
		driver.findElement(By.id("nom0")).sendKeys("Mod");
		driver.findElement(By.id("descripcio0")).sendKeys("Mod");
		driver.findElement(By.id("arxiuContingut0")).sendKeys(provaDocPath);
		driver.findElement(By.id("plantilla0")).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_5_modificacio-dades_modificades.png");
		
		driver.findElement(By.xpath(botoModificarArxiu)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al modificar el document DocSel1 desde el tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_6_modificacio-resultat.png");
		
		//Comprovam les modificacions fetes
		driver.findElement(By.xpath(linkDoc1_m)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_7_modificacio-comprobacio_canvis.png");
		
		if (!"DocSel1Mod".equals(driver.findElement(By.id("codi0")).getAttribute("value"))) { fail("El codi del document (DocSel1Mod) del tipus d´expedient no s´ha modificat!"); }
		if (!"Document proves selenium TEX8 1Mod".equals(driver.findElement(By.id("nom0")).getAttribute("value"))) { fail("El codi del document (DocSel1Mod) del tipus d´expedient no s´ha modificat!"); }
		if (!"Mod".equals(driver.findElement(By.id("descripcio0")).getAttribute("value"))) { fail("El codi del document (DocSel1Mod) del tipus d´expedient no s´ha modificat!"); }
		if (!driver.findElement(By.id("plantilla0")).isSelected()) { fail("El check de es Plantilla del document amb codi DocSel1Mod hauria de estar marcat!");	}
		
		downloadFile(enllaçDocumentAdjuntat, "informe.pdf");
		
		screenshotHelper.saveScreenshot("tipusExpedient/documents/b1_8_descarrega_informe.png");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarDefinicioProces(defProcNom);
		eliminarEntorn(entorn);
	}

}