package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.fail;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TipusExpedientTramits extends BaseTest {
	
							//TEX.11 - Integració amb tramits (no provar)
								//TEX.11.1 - Activar /desactivar
								//TEX.11.2 - Mapeig de variables: Crear / Eliminar
								//TEX.11.3 - Mapeig de documents: Crear / Eliminar
								//TEX.11.4 - Mapeig de documents adjunts: Crear / Eliminar
	
	String entorn 		= carregarPropietat("tipexp.tramits.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn	= carregarPropietat("tipexp.tramits.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	
	String usuari 		= carregarPropietat("test.base.usuari.disseny", "Usuari feina de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin  = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");

	String nomTipusExp	= carregarPropietat("tipexp.tramits.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp	= carregarPropietat("tipexp.tramits.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String pathTipExp	= carregarPropietatPath("tipexp.tramits.arxiu.path", "Ruta de l´arxiu del tipus d´expedient exportat no configurat al fitxer de properties");

	String codDefProc	= carregarPropietat("tipexp.tramits.definicio.proces.nom", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");

	// XPATHS
	String pestanyaTramits	= "//*[@id='tabnav']/li/a[contains(@href, '/expedientTipus/sistra.html')]";
	String botoGuardarTram	= "//*[@id='command']//div[@class='buttonHolder']/button[text() = 'Guardar']";
	
	String botoMapeigVars	= "//*[@id='variables']/div/button";	
	String botoMapeigDocs	= "//*[@id='documents']/div/button";
	String botoMapeigAdjs	= "//*[@id='adjunts.html']/div/button";
	
	String botoAfegir		= "//*[@id='command']/div[@class='buttonHolder']/button[text() = 'Afegir']";
	
	String linkEliminVar	= "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/sistraVariableEsborrar.html')]";
	String linkEliminDoc	= "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/sistraDocumentEsborrar.html')]";
	String linkEliminAdj	= "//*[@id='registre']/tbody/tr/td/a[contains(@href, '/expedientTipus/sistraAdjuntEsborrar.html')]";
		
	
	
	@Test
	public void a1_inicialitzacio() {
		
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
	
		crearTipusExpedient(nomTipusExp, codTipusExp);		
		importarDadesTipExp(codTipusExp, pathTipExp);		
		
		//crearDocumentDefProc(codDefProc);
		
		assignarPermisosTipusExpedient(codTipusExp, usuariAdmin, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
		assignarPermisosTipusExpedient(codTipusExp, usuari, "CREATE", "DESIGN", "MANAGE", "READ", "DELETE");
	}
	
	@Test
	public void b1_eliminar_mapeig_variables() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/b1_1_eliminar_mapeig_vars-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigVars)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/b1_2_eliminar_mapeig_vars-estat_inicial.png");

		int contadorEliminats = 1;
		while (existeixElement(linkEliminVar)) {
			
			driver.findElement(By.xpath(linkEliminVar)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/tramits/b1_3_"+contadorEliminats+"_eliminar_mapeig_vars-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar el mapeig de variable per el tipus d´expedient "+codTipusExp+".");
			
			contadorEliminats++;
		}
	}
	
	@Test
	public void c1_eliminar_mapeig_documents() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/c1_1_eliminar_mapeig_docs-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigDocs)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/c1_2_eliminar_mapeig_docs-estat_inicial.png");

		int contadorEliminats = 1;
		while (existeixElement(linkEliminDoc)) {
			
			driver.findElement(By.xpath(linkEliminDoc)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/tramits/c1_3_"+contadorEliminats+"_eliminar_mapeig_docs-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar el document mapejat per el tramit del tipus d´expedient "+codTipusExp+".");
			
			contadorEliminats++;
		}
	}
	
	@Test
	public void d1_eliminar_mapeig_adjunt() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/d1_1_eliminar_mapeig_adjs-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigAdjs)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/d1_2_eliminar_mapeig_adjs-estat_inicial.png");

		int contadorEliminats = 1;
		while (existeixElement(linkEliminAdj)) {
			
			driver.findElement(By.xpath(linkEliminAdj)).click();
			
			if (isAlertPresent()) {acceptarAlerta();}
			
			screenshotHelper.saveScreenshot("tipusExpedient/tramits/d1_3_"+contadorEliminats+"_eliminar_mapeig_adjs-resultat_eliminacio.png");
			
			existeixElementAssert("//*[@class='missatgesOk']", "Error al borrar el doc. adjunt mapejat per el tramit del tipus d´expedient "+codTipusExp+".");
			
			contadorEliminats++;
		}
	}
	
	@Test
	public void e1_desactivar_tramit() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/e1_1_desactivar_tramit-pipella_tramits.png");
		
		driver.findElement(By.id("actiu0")).click();
						
		driver.findElement(By.xpath(botoGuardarTram)).click();

		//Tornam a clicar damunt la pipella per recarregar la pagina i comprovar que s´ha deshabilitat
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/e1_2_desactivar_tramit-resultat_desactivacio.png");
		
		if (checkboxSelected("//*[@id='actiu0']", true)) {fail("El check actiu no hauria de estar seleccionat"); }	
	}
	
	@Test
	public void f1_activar_tramit() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/f1_1_crear_tramit-pipella_tramits.png");
		
		driver.findElement(By.id("actiu0")).click();
		
		driver.findElement(By.id("codiTramit0")).sendKeys("TraSel1");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/f1_2_crear_tramit-dades_emplenades.png");
		
		driver.findElement(By.xpath(botoGuardarTram)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/f1_3_crear_tramit-resultat_insercio.png");
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al crear el tramit TraSel1 per el tipus d´expedient "+codTipusExp+".");	
	}
	
	@Test
	public void g1_crear_mapeig_variables() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
	
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_1_crear_mapeig_vars-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigVars)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_2_crear_mapeig_vars-estat_inicial.png");
		
		//---------------------------//
		
		for (WebElement option : driver.findElement(By.id("codiHelium0")).findElements(By.tagName("option"))) {
			if ("v1/Variable 1".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("codiSistra0")).sendKeys("Var_1_Sis");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_1_crear_mapeig_vars-dades_variable.png");
		
		driver.findElement(By.xpath(botoAfegir)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al mapejar la variable v1/Variable 1 per el tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_2_crear_mapeig_vars-resultat_insercio.png");
		
		//---------------------------//
		
		for (WebElement option : driver.findElement(By.id("codiHelium0")).findElements(By.tagName("option"))) {
			if ("v2/Variable 2".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("codiSistra0")).sendKeys("Var_2_Sis");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_3_crear_mapeig_vars-dades_variable.png");
		
		driver.findElement(By.xpath(botoAfegir)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al mapejar la variable v2/Variable 2 per el tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_4_crear_mapeig_vars-resultat_insercio.png");
		
		//------------------------------//
		
		for (WebElement option : driver.findElement(By.id("codiHelium0")).findElements(By.tagName("option"))) {
			if ("v3/Variable 3".equals(option.getText())) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("codiSistra0")).sendKeys("Var_3_Sis");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_5_crear_mapeig_vars-dades_variable.png");
		
		driver.findElement(By.xpath(botoAfegir)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al mapejar la variable v3/Variable 3 per el tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/g1_3_6_crear_mapeig_vars-resultat_insercio.png");
	}
	
	@Test
	public void h1_crear_mapeig_documents() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
	
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/h1_1_crear_mapeig_docs-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigDocs)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/h1_2_crear_mapeig_docs-estat_inicial.png");
		
		for (WebElement option : driver.findElement(By.id("codiHelium0")).findElements(By.tagName("option"))) {
			if ("CodDocDefPro".equals(option.getAttribute("value"))) {
				option.click();
				break;
			}
		}
		
		driver.findElement(By.id("codiSistra0")).sendKeys("Cod_Doc_Sistra");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/h1_3_crear_mapeig_docs-dades_documents.png");
		
		driver.findElement(By.xpath(botoAfegir)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al mapejar la variable CodDocDefPro per el tramit del tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/h1_4_crear_mapeig_docs-resultat_insercio.png");
	}
	
	@Test
	public void i1_crear_mapeig_adjunts() {
		
		carregarUrlDisseny();
		
		seleccionarEntorn(titolEntorn);
		
		seleccionarTipExp(codTipusExp);
		
		driver.findElement(By.xpath(pestanyaTramits)).click();
	
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/i1_1_crear_mapeig_adju-pipella_tramits.png");
		
		driver.findElement(By.xpath(botoMapeigAdjs)).click();
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/i1_2_crear_mapeig_adju-estat_inicial.png");
				
		driver.findElement(By.id("codiHelium0")).sendKeys("SitraCodiAdjunt");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/i1_3_crear_mapeig_adju-dades_documents.png");
		
		driver.findElement(By.xpath(botoAfegir)).click();
		
		existeixElementAssert("//*[@class='missatgesOk']", "Error al mapejar el doc adjunt SitraCodiAdjunt per el tramit del tipus d´expedient "+codTipusExp+".");
		
		screenshotHelper.saveScreenshot("tipusExpedient/tramits/i1_4_crear_mapeig_adju-resultat_insercio.png");
	}
	
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}
}
