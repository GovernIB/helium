package net.conselldemallorca.helium.test.disseny;

import static org.junit.Assert.assertTrue;
import net.conselldemallorca.helium.test.util.BaseTest;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DefinicioProcesDocs extends BaseTest {

	String entorn = carregarPropietat("defproc.entorn.nom", "Nom de l'entorn de proves no configurat al fitxer de properties");
	String titolEntorn = carregarPropietat("defproc.entorn.titol", "Titol de l'entorn de proves no configurat al fitxer de properties");
	String pathExportEntorn = carregarPropietatPath("defproc.export.entorn.arxiu.path", "Ruta de l'exportació de l'entorn de proves no configurat al fitxer de properties");
	String usuari = carregarPropietat("test.base.usuari.disseny", "Usuari disseny de l'entorn de proves no configurat al fitxer de properties");
	String usuariAdmin = carregarPropietat("test.base.usuari.configuracio", "Usuari configuracio de l'entorn de proves no configurat al fitxer de properties");
	String nomDefProc = carregarPropietat("defproc.deploy.definicio.proces.nom", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String pathDefProc = carregarPropietatPath("defproc.deploy.arxiu.path", "Nom de la definició de procés de proves no configurat al fitxer de properties");
	String codTipusExp = carregarPropietat("defproc.deploy.tipus.expedient.codi", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");
	String arxiuContingut_hash = carregarPropietat("defproc.deploy.arxiu.contingut.hash", "Codi del tipus d'expedient de proves no configurat al fitxer de properties");

	@Test
	public void a0_inicialitzacio() {
		carregarUrlConfiguracio();
		crearEntorn(entorn, titolEntorn);
		assignarPermisosEntorn(entorn, usuariAdmin, "DESIGN", "ORGANIZATION", "READ", "ADMINISTRATION");
		assignarPermisosEntorn(entorn, usuari, "DESIGN", "ORGANIZATION", "READ");
	}
	
	@Test
	public void a1_inicialitzacio() {
		carregarUrlConfiguracio();
		//saveEntornActual();
		marcarEntornDefecte(titolEntorn);
		seleccionarEntorn(titolEntorn);
		desplegarDefinicioProcesEntorn(nomDefProc, pathDefProc);
		seleccionarDefinicioProces(nomDefProc);
		crearVar("campData", "campData", TipusVar.DATE, null, false, false, false, null);
		importarDadesEntorn(entorn, pathExportEntorn);
	}
	
	@Test
	public void a2_inicialitzacio() {
		carregarUrlDisseny();
		marcarEntornDefecte(titolEntorn);
	}	
	
	@Test
	public void b_crearDocumentSensePlantilla() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		crearDoc(carregarPropietat("defproc.document.noplant.codi", "Codi del document sense plantilla no configurat al fitxer de properties"),
				carregarPropietat("defproc.document.noplant.nom", "Nom del document sense plantilla no configurat al fitxer de properties"),
				false, null, null, false, null, null, null, null, null, null);
	}
	
	@Test
	public void c_crearDocumentAmbPlantilla() {
		carregarUrlDisseny();
		
		seleccionarDefinicioProces(nomDefProc);
		crearDoc(carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties"),
				carregarPropietat("defproc.document.plant.nom", "Nom del document amb plantilla no configurat al fitxer de properties"),
				true, 
				carregarPropietatPath("defproc.document.plant.path", "Plantilla de proves no configurat al fitxer de properties"),
				null, false, null, null, null, null, null, 
				carregarPropietat("defproc.document.plant.hash", "Hash de la plantilla no configurat al fitxer de properties"));
	}
	
	@Test
	public void d_definirCampAmbData() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		modificarDoc(carregarPropietat("defproc.document.noplant.codi", "Codi del document sense plantilla no configurat al fitxer de properties"), 
				"Nom modificat", 
				"Descripció modificada", 
				true, 
				carregarPropietatPath("defproc.document.plant2.path", "Plantilla de proves 2 no configurat al fitxer de properties"), 
				null, false, 
				"campData/campData", 
				null, null, null, null, 
				carregarPropietat("defproc.document.plant2.hash", "Hash de la plantilla no configurat al fitxer de properties"));
	}
	
	@Test
	public void e_adjuntaAutomaticament() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		modificarDoc(carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties"), 
				null, 
				null, 
				null, 
				carregarPropietatPath("defproc.document.plant2.path", "Plantilla de proves 2 no configurat al fitxer de properties"), 
				"odt", 
				true, 
				null, 
				"odt,docx", 
				"application/vnd.oasis.opendocument.text", 
				"codiCustodia", 
				null,
				carregarPropietat("defproc.document.plant2.hash", "Hash de la plantilla no configurat al fitxer de properties"));
	}
	
	@Test
	public void f_descarregarPlantilla() {
		carregarUrlConfiguracio();
		seleccionarEntorn(titolEntorn);
		seleccionarDefinicioProces(nomDefProc);
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("defproces/document/descarregar/1_documentsInici.png");
		
		//comprovar document existeix i té plantilla
		String codDoc = carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]", "No existeix el document a descarregar plantilla");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]/td[4]/a/img", "El document no té plantilla");

		downloadFile("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]/td[4]/a", "plantilla.odt");
	}
	
	@Test
	public void g_eliminarDocument() {
		carregarUrlDisseny();
		seleccionarDefinicioProces(nomDefProc);
		// Esborra una variable en una definició de procés			
	
		// Accedir a la fitxa dels documents
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();
		screenshotHelper.saveScreenshot("defproces/document/esborra/1_documentsInici.png");

	    // Obtenir nom document i cercar-lo
		String codDoc = carregarPropietat("defproc.document.plant.codi", "Codi del document amb plantilla no configurat al fitxer de properties");
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]", "No existeix el document a esborrar");

		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codDoc + "')]/td[5]/a")).click();

		if (isAlertPresent()) acceptarAlerta();

		existeixElementAssert("//*[@id='infos']/p", "No se pudo borrar el documento.");
	}
		
	@Test
	public void z0_finalitzacio() {
		carregarUrlConfiguracio();
		eliminarDefinicioProces(nomDefProc);
		eliminarTipusExpedient(codTipusExp);
		eliminarEnumeracio("enumsel");
		eliminarDomini("enumerat");
		eliminarEntorn(entorn);
	}
	

	
	// Funcions ajuda
	// ----------------------------------------------------------------------------------------
	private void crearDoc(String codi, String nom, boolean esPlantilla, String pathPlantilla, String extensioSortida, boolean adjuntar, String campData, String extensionsPermeses, String contentType, String codiCustodia, String tipusDoc, String md5Plantilla) {
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
		checkboxSelected("//*[@id='plantilla0']", "És plantilla no sa gravat correctament", esPlantilla);
		checkboxSelected("//*[@id='adjuntarAuto0']", "Adjuntar automàticament no s'ha gravat correctament", adjuntar);
		if (esPlantilla) {
			if (pathPlantilla != null) existeixElementAssert("//*[@id='iconsFileInput_arxiuContingut0']/a[1]/img", "L'arxiu de la plantilla no s'ha gravat correctament");
			
			/*if (md5Plantilla != null) {
				try {
					byte[] arxiuContingut = downloadFile("//*[@id='iconsFileInput_arxiuContingut0']/a[1]", "");
					String hashFile = getMD5Checksum(arxiuContingut);
					System.out.println(hashFile);
					System.out.println(arxiuContingut_hash);
					assertTrue("Hash de l'arxiu contingut no ha estat l´esperat: "+hashFile+" diferent de " + arxiuContingut_hash, arxiuContingut_hash.equals(hashFile));
				}catch (Exception ex) {}
			}*/
			
			//TODO: Revisar funcion de comprobacion MD5 con el metodo de descarga de archivos HTTPS
			
			if (extensioSortida != null) existeixElementAssert("//*[@id='convertirExtensio0' and @value='" + extensioSortida + "']", "L'extensió de sortida no s'ha gravat correctament");
		}
		if (campData != null) driver.findElement(By.xpath("//*[@id='campData0']/option[normalize-space(text())='" + campData + "']")).click();
		if (extensionsPermeses != null) existeixElementAssert("//*[@id='extensionsPermeses0' and @value='" + extensionsPermeses + "']", "Les extensions permeses no s'han gravat correctament"); 
		if (contentType != null) existeixElementAssert("//*[@id='contentType0' and @value='" + contentType + "']", "El content type no s'ha gravat correctament");
		if (codiCustodia != null) existeixElementAssert("//*[@id='custodiaCodi0' and @value='" + codiCustodia + "']", "El codi de custòdia no s'ha gravat correctament");
		if (tipusDoc != null) existeixElementAssert("//*[@id='tipusDocPortasignatures0' and @value='" + tipusDoc + "']", "El tipus de document de protasigantures no s'ha gravat correctament");
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
	
	private void modificarDoc(String codi, String nom, String descripcio, Boolean esPlantilla, String pathPlantilla, String extensioSortida, Boolean adjuntar, String campData, String extensionsPermeses, String contentType, String codiCustodia, String tipusDoc, String md5Plantilla) {
		// Accedir a la fitxa de les variables
		driver.findElement(By.xpath("//a[contains(@href, '/helium/definicioProces/documentLlistat.html')]")).click();	
			
		// Comprovar que existeix
		existeixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]", "defproces/document/modificar/" + codi + "/03_mod_doc.png", "El document a modificar no existeix");
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]")).click();
		
  	    // Paràmetres de la variable
		if (nom != null) {
			driver.findElement(By.id("nom0")).clear();
			driver.findElement(By.id("nom0")).sendKeys(nom);
		}
		if (descripcio != null) {
			driver.findElement(By.id("descripcio0")).clear();
			driver.findElement(By.id("descripcio0")).sendKeys(descripcio);
		}
		if (esPlantilla != null) {
			if (!checkboxSelected("//*[@id='plantilla0']", "", esPlantilla))
				driver.findElement(By.id("plantilla0")).click();
		}
		if (pathPlantilla != null) {
			if (noExisteixElement("//*[@id='iconsFileInput_arxiuContingut0' and @style='display:none']")) {
				driver.findElement(By.xpath("//*[@id='iconsFileInput_arxiuContingut0']/a[2]")).click();
			}
			driver.findElement(By.id("arxiuContingut0")).sendKeys(pathPlantilla);
		}
		if (extensioSortida != null) {
			driver.findElement(By.id("convertirExtensio0")).clear();
			driver.findElement(By.id("convertirExtensio0")).sendKeys(extensioSortida);
		}
		if (adjuntar != null) {
			if (!checkboxSelected("//*[@id='adjuntarAuto0']", "", adjuntar))
				driver.findElement(By.id("adjuntarAuto0")).click();
		}
		if (campData != null) driver.findElement(By.xpath("//*[@id='campData0']/option[normalize-space(text())='" + campData + "']")).click();
		if (extensionsPermeses != null) {
			driver.findElement(By.id("extensionsPermeses0")).clear();
			driver.findElement(By.id("extensionsPermeses0")).sendKeys(extensionsPermeses);
		}
		if (contentType != null) {
			driver.findElement(By.id("contentType0")).clear();
			driver.findElement(By.id("contentType0")).sendKeys(contentType);
		}
		if (codiCustodia != null) {
			driver.findElement(By.id("custodiaCodi0")).clear();
			driver.findElement(By.id("custodiaCodi0")).sendKeys(codiCustodia);
		}
		if (tipusDoc != null) {
			driver.findElement(By.id("tipusDocPortasignatures0")).clear();
			driver.findElement(By.id("tipusDocPortasignatures0")).sendKeys(tipusDoc);
		}
		
		screenshotHelper.saveScreenshot("defproces/document/modificar/" + codi + "/02_mod_doc.png");
		
		// Modifica document
		driver.findElement(By.xpath("//button[@value='submit']")).click();

		// Comprovar paràmetres
		driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'" + codi + "')]/td[1]")).click();
		if (nom != null) existeixElementAssert("//*[@id='nom0' and @value='" + nom + "']", "El nom no s'ha gravat correctament");
		if (descripcio != null) existeixElementAssert("//*[@id='descripcio0' and normalize-space(text())='" + descripcio + "']", "La descripcio no s'ha gravat correctament");
		if (esPlantilla != null) checkboxSelectedAssert("//*[@id='plantilla0']", "És plantilla no sa gravat correctament", esPlantilla);
		if (adjuntar != null) checkboxSelectedAssert("//*[@id='adjuntarAuto0']", "Adjuntar automàticament no s'ha gravat correctament", adjuntar);
		if (pathPlantilla != null) existeixElementAssert("//*[@id='iconsFileInput_arxiuContingut0']/a[1]/img", "L'arxiu de la plantilla no s'ha gravat correctament");
		
		//if (md5Plantilla != null) downloadFileHash("//*[@id='iconsFileInput_arxiuContingut0']/a[1]", md5Plantilla, "plantilla.odt");
		
		/*if (md5Plantilla != null) {
			try {
				byte[] arxiuContingut = downloadFile("//*[@id='iconsFileInput_arxiuContingut0']/a[1]", "");
				assertTrue("Hash de l'arxiu contingut no ha estat l´esperat." , arxiuContingut_hash.equals(getMD5Checksum(arxiuContingut)));
			}catch (Exception ex) {}
		}*/
		
		//TODO: Revisar funcion de comprobacion MD5 con el metodo de descarga de archivos HTTPS
		
		if (extensioSortida != null) existeixElementAssert("//*[@id='convertirExtensio0' and @value='" + extensioSortida + "']", "L'extensió de sortida no s'ha gravat correctament");
		if (campData != null) driver.findElement(By.xpath("//*[@id='campData0']/option[normalize-space(text())='" + campData + "']")).click();
		if (extensionsPermeses != null) existeixElementAssert("//*[@id='extensionsPermeses0' and @value='" + extensionsPermeses + "']", "Les extensions permeses no s'han gravat correctament"); 
		if (contentType != null) existeixElementAssert("//*[@id='contentType0' and @value='" + contentType + "']", "El content type no s'ha gravat correctament");
		if (codiCustodia != null) existeixElementAssert("//*[@id='custodiaCodi0' and @value='" + codiCustodia + "']", "El codi de custòdia no s'ha gravat correctament");
		if (tipusDoc != null) existeixElementAssert("//*[@id='tipusDocPortasignatures0' and @value='" + tipusDoc + "']", "El tipus de document de protasigantures no s'ha gravat correctament");
		
		driver.findElement(By.xpath("//button[@value='cancel']")).click();
	}
	
	private void eliminarEnumeracionsTest() {
		actions.moveToElement(driver.findElement(By.id("menuDisseny")));
		actions.build().perform();
		actions.moveToElement(driver.findElement(By.xpath("//a[contains(@href, '/helium/enumeracio/llistat.html')]")));
		actions.click();
		actions.build().perform();
		
		if (existeixElement("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]")) {
			driver.findElement(By.xpath("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]/td[4]/a")).click();
			acceptarAlerta();
		}
		noExisteixElementAssert("//*[@id='registre']/tbody/tr[contains(td[1],'enumsel')]", "No s'han pogut eliminar l'enumeració");
	}
}
