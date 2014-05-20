package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesDocs extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietat("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.configuracio", "Usuari configuració de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietat("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
//	String nomTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.nom", "Nom del tipus d'expedient de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	
//	@Test
	public void a_inicialitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefinicioProcesEntorn(nomDefProc, pathDefProc);
		importarDadesEntorn(entorn, pathExportEntorn);
	}
	
//	@Test
	public void b_crearDocumentSensePlantilla() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearDoc(carregarPropietat("defproc.document.noplant.codi", "Codi del document sense plantilla no configurat al fitxer de properties"),
				carregarPropietat("defproc.document.noplant.nom", "Nom del document sense plantilla no configurat al fitxer de properties"),
				false, null, null, false, null, null, null, null, null);
	}
	
//	@Test
	public void c_crearDocumentAmbPlantilla() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		crearDoc(carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties"),
				carregarPropietat("defproc.document.plant.nom", "Nom del document amb plantilla no configurat al fitxer de properties"),
				true, 
				carregarPropietat("defproc.document.plant.path", "Nom del document amb plantilla no configurat al fitxer de properties"),
				null, false, null, null, null, null, null);
	}
	
//	@Test
	public void d_definirCampAmbData() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
	}
	
//	@Test
	public void e_adjuntaAutomaticament() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
	}
	
//	@Test
	public void f_modificarDocument() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		// Modificar el camp tipus d'una variable			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/campLlistat.html')]")).click();			
	    screenshotHelper.saveScreenshot("defproces/variable/modificar/1_variablesInici.png");

	    // Obtenir nom variable i cercar-la
	    String codVar = carregarPropietat("defproc.variable.string.codi", "Codi de la variable string no configurat al fitxer de properties");
	    existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]", "No existeix la variable a modificar");

  	    // guardar valor inicial
  	    String nomIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipIni = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[1]/a")).click();
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/2_variableOriginal.png");

  	    driver.findElement(By.xpath("//*[@id='tipus0']/option[@value='INTEGER']")).click();
		driver.findElement(By.id("etiqueta0")).clear();
		driver.findElement(By.id("etiqueta0")).sendKeys("Etiqueta modificada");
		screenshotHelper.saveScreenshot("defproces/variable/modificar/3_variableModificada.png");
		driver.findElement(By.xpath("//button[@value='submit']")).click(); 	    
  	    screenshotHelper.saveScreenshot("defproces/variable/modificar/4_variablesFi.png");
  	    
  	    // comprovar si s'ha modificat la variable
  	    String nomFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[2]")).getText().trim();
  	    String tipFi = driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codVar + "')]/td[3]")).getText().trim();
		assertNotEquals("No s'ha pogut canviar el nom de la variable", nomIni, nomFi);
		assertEquals("No s'ha canviar correctament el nom de la variable", "Etiqueta modificada", nomFi);
		assertNotEquals("No s'ha pogut canviar el tipus de la variable", tipIni, tipFi);
		assertEquals("No s'ha canviar correctament el tipus de la variable", "INTEGER", tipFi);
	}
//	private void modificarDoc() {
//	// Modifica la propietat camp amb data d'un document d'una definici� de proc�s seleccionada amb seleccionarDefProc			
//
//	// Accedir a la fitxa dels documents
//	driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();			
//	
//	screenshotHelper.saveScreenshot("defproces/document/mod_doc1.png");
//
//	// Obtenir nom document i cercar-lo
//	String nomDoc = getProperty("defproc.document.codi1");
//	driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
//	boolean isPresent = driver.findElements(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]")).size() > 0;
//	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
//
//	// Si existeix, modificar-lo
//	if (isPresent) {
//		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[1]/a")).click();
//  	    screenshotHelper.saveScreenshot("defproces/document/mod_doc2.png");
//		
//  	    // guardar valor inicial
//  	    String valIni = driver.findElement(By.id("campData0")).getText().trim();
//
//		// Seleccionar variable camp en la data
//		WebElement selectVar = driver.findElement(By.id("campData0"));
//		List<WebElement> allOptions = selectVar.findElements(By.tagName("option"));
//		for (WebElement option : allOptions) {
//		    if (option.getText().equals(properties.getProperty("defproc.variable.codi2")+"/"+properties.getProperty("defproc.variable.nom2"))) {
//		    	option.click();
//		    	break;
//		    }
//		}
//		screenshotHelper.saveScreenshot("defproces/document/mod_doc3.png");
//		// Bot� guardar
//		WebElement boto = driver.findElement(By.xpath("//button[@value='submit']"));
//		boto.click(); 	    
//  	    
//  	    screenshotHelper.saveScreenshot("defproces/document/mod_doc4.png");
//  	    
//  	    // comprovar si s'ha modificat la variable
//	  	    //driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + nomDoc + "')]/td[1]/a")).click();
//  	    //String valFin = driver.findElement(By.id("campData0")).getText().trim();
//  	    //boto = driver.findElement(By.xpath("//button[@value='cancel']"));
//		//boto.click(); 	    
//  	    //assertNotEquals("No s'ha pogut modificar el document", valIni, valFin);
//	} else {
//		fail("El document no existeix");
//	}
//}
	
//	@Test
	public void g_descarregarPlantilla() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
	}
	
//	@Test
	public void h_eliminarDocument() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuDisseny']", "No te permisos de disseny a Helium");
		seleccionarDefinicioProces(nomDefProc);
		// Esborra una variable en una definició de procés			
	
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("defproces/document/esborra/1_documentsInici.png");

	    // Obtenir nom document i cercar-lo
		String codDoc = carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]", "No existeix el document a esborrar");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]/td[5]/a")).click();
		acceptarAlerta();

		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]", "defproces/document/esborra/2_documentsFi.png", "No s'ha pogut esborrar el document");
	}
	
//	@Test
	public void z_finalitzacio() {
		carregarUrlConfiguracio();
		existeixElementAssert("//li[@id='menuConfiguracio']", "No te permisos de configuració a Helium");
		eliminarDefinicioProces(nomDefProc);
		eliminarTipusExpedient(codTipusExp);
		eliminarEntorn(entorn);
	}

	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	private void crearDoc(String codi, String nom, boolean esPlantilla, String pathPlantilla, String extensioSortida, boolean adjuntar, String campData, String extensionsPermeses, String contentType, String codiCustodia, String tipusDoc) {
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();	
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/document/" + (esPlantilla ? "ambPlantilla" : "sensePlantilla") + "/01_crea_doc.png", "El document a crear ja existeix");
				
  	    // Botó nou document
		driver.findElement(By.xpath("//div[@id='content']/form/button[@class='submitButton']")).click();
  	    // Paràmetres de la variable
  	    driver.findElement(By.id("codi0")).clear();
		driver.findElement(By.id("codi0")).sendKeys(codi);
		driver.findElement(By.id("nom0")).clear();
		driver.findElement(By.id("nom0")).sendKeys(nom);
		driver.findElement(By.id("descripcio0")).clear();
		driver.findElement(By.id("descripcio0")).sendKeys("Document " +(esPlantilla ? "amb plantilla" : "sense plantilla"));

		if (esPlantilla) {
			driver.findElement(By.id("plantilla0")).click();
			if (pathPlantilla != null) driver.findElement(By.id("arxiuContingut0")).sendKeys(pathPlantilla);
			if (extensioSortida != null) driver.findElement(By.id("convertirExtensio0")).sendKeys(extensioSortida);
			if (!adjuntar) driver.findElement(By.id("adjuntarAuto0")).click();
		}
		
		if (campData != null) driver.findElement(By.xpath("//*[@id='campData0']/option[normalize-space(text())='" + campData + "']")).click();
		
		if (extensionsPermeses != null) driver.findElement(By.id("extensionsPermeses0")).sendKeys(extensionsPermeses);
		if (contentType != null) driver.findElement(By.id("contentType0")).sendKeys(contentType);
		if (codiCustodia != null) driver.findElement(By.id("custodiaCodi0")).sendKeys(codiCustodia);
		if (tipusDoc != null) driver.findElement(By.id("tipusDocPortasignatures0")).sendKeys(tipusDoc);
		
		screenshotHelper.saveScreenshot("defproces/document/" + (esPlantilla ? "ambPlantilla" : "sensePlantilla") + "/02_crea_doc.png");
		
		// Crear document
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		// Comprovar que s'ha creat
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/document/" + (esPlantilla ? "ambPlantilla" : "sensePlantilla") + "/03_crea_doc.png", "El document no s'ha pogut crear");

		// Comprovar paràmetres
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]")).click();
		checkboxSelected("//*[@id='plantilla0']", "", esPlantilla);
		checkboxSelected("//*[@id='adjuntarAuto0']", "", adjuntar);
		if (esPlantilla) {
			if (pathPlantilla != null) existeixElementAssert("//*[@id='iconsFileInput_arxiuContingut0']/a[1]/img", "L'arxiu de la plantilla no s'ha gravat correctament");
			if (extensioSortida != null) existeixElementAssert("//*[@id='convertirExtensio0' and @value='" + extensioSortida + "']", "L'extensió de sortida no s'ha gravat correctament");
		}

		if (campData != null) driver.findElement(By.xpath("//*[@id='campData0']/option[normalize-space(text())='" + campData + "']")).click();
		
		if (extensionsPermeses != null) existeixElementAssert("//*[@id='extensionsPermeses0' and @value='" + extensionsPermeses + "']", "Les extensions permeses no s'han gravat correctament"); 
		if (contentType != null) existeixElementAssert("//*[@id='contentType0' and @value='" + contentType + "']", "El content type no s'ha gravat correctament");
		if (codiCustodia != null) existeixElementAssert("//*[@id='custodiaCodi0' and @value='" + codiCustodia + "']", "El codi de custòdia no s'ha gravat correctament");
		if (tipusDoc != null) existeixElementAssert("//*[@id='tipusDocPortasignatures0' and @value='" + tipusDoc + "']", "El tipus de document de protasigantures no s'ha gravat correctament");
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
		
	}
}
